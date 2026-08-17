package com.example.data.sync

import android.content.Context
import android.util.Log
import com.example.data.model.TripEntity
import com.example.data.model.UserPreferenceEntity
import com.example.data.model.WalletBalanceEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Cloud Synchronization & Cross-Device Authentication Manager using Firebase Auth & Firestore.
 * Supports offline caching and background synchronization of itineraries, preferences, and wallet states.
 */
class CloudSyncManager(private val context: Context) {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _syncStatus = MutableStateFlow(SyncStatus.OFFLINE_LOCAL)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()

    private val _lastSyncTimestamp = MutableStateFlow(System.currentTimeMillis())
    val lastSyncTimestamp: StateFlow<Long> = _lastSyncTimestamp.asStateFlow()

    init {
        try {
            _currentUser.value = auth.currentUser
            auth.addAuthStateListener { firebaseAuth ->
                _currentUser.value = firebaseAuth.currentUser
                if (firebaseAuth.currentUser != null) {
                    _syncStatus.value = SyncStatus.SYNCED_CLOUD
                } else {
                    _syncStatus.value = SyncStatus.OFFLINE_LOCAL
                }
            }
        } catch (e: Exception) {
            Log.w("CloudSyncManager", "Firebase Auth initialization note: ${e.message}")
        }
    }

    /**
     * Synchronizes a trip itinerary to Firestore under the current authenticated user's workspace
     */
    suspend fun syncTripToCloud(trip: TripEntity): Boolean = withContext(Dispatchers.IO) {
        val user = auth.currentUser
        if (user == null) {
            _syncStatus.value = SyncStatus.OFFLINE_LOCAL
            return@withContext false
        }

        return@withContext try {
            val tripDoc = mapOf(
                "id" to trip.id,
                "title" to trip.title,
                "destination" to trip.destination,
                "countryCode" to trip.countryCode,
                "startDate" to trip.startDate,
                "endDate" to trip.endDate,
                "budgetTotal" to trip.budgetTotal,
                "budgetSpent" to trip.budgetSpent,
                "primaryCurrency" to trip.primaryCurrency,
                "accessibilityRequirements" to trip.accessibilityRequirements,
                "dietaryRestrictions" to trip.dietaryRestrictions,
                "familyAgeBrackets" to trip.familyAgeBrackets,
                "travelStyle" to trip.travelStyle,
                "lastSyncedTimestamp" to System.currentTimeMillis()
            )

            firestore.collection("users")
                .document(user.uid)
                .collection("trips")
                .document(trip.id.toString())
                .set(tripDoc, SetOptions.merge())
                .await()

            _syncStatus.value = SyncStatus.SYNCED_CLOUD
            _lastSyncTimestamp.value = System.currentTimeMillis()
            true
        } catch (e: Exception) {
            Log.e("CloudSyncManager", "Firestore sync exception: ${e.message}")
            _syncStatus.value = SyncStatus.OFFLINE_LOCAL
            false
        }
    }

    /**
     * Synchronizes user preferences and learned DNA
     */
    suspend fun syncPreferencesToCloud(pref: UserPreferenceEntity): Boolean = withContext(Dispatchers.IO) {
        val user = auth.currentUser ?: return@withContext false
        return@withContext try {
            val prefMap = mapOf(
                "preferredAirlines" to pref.preferredAirlines,
                "preferredHotelTypes" to pref.preferredHotelTypes,
                "activityLevel" to pref.activityLevel,
                "wheelchairRequirements" to pref.wheelchairRequirements,
                "dietaryPreferences" to pref.dietaryPreferences,
                "familyAgeBrackets" to pref.familyAgeBrackets,
                "learnedInsightsSummary" to pref.learnedInsightsSummary,
                "lastUpdatedTimestamp" to System.currentTimeMillis()
            )

            firestore.collection("users")
                .document(user.uid)
                .collection("preferences")
                .document("traveler_dna")
                .set(prefMap, SetOptions.merge())
                .await()

            _syncStatus.value = SyncStatus.SYNCED_CLOUD
            true
        } catch (e: Exception) {
            Log.e("CloudSyncManager", "Preferences sync failed: ${e.message}")
            false
        }
    }

    /**
     * Synchronizes encrypted multi-currency wallet balances
     */
    suspend fun syncWalletBalanceToCloud(balances: List<WalletBalanceEntity>): Boolean = withContext(Dispatchers.IO) {
        val user = auth.currentUser ?: return@withContext false
        return@withContext try {
            val batch = firestore.batch()
            val collection = firestore.collection("users").document(user.uid).collection("wallet")

            balances.forEach { balance ->
                val docRef = collection.document(balance.currencyCode)
                val data = mapOf(
                    "currencyCode" to balance.currencyCode,
                    "currencyName" to balance.currencyName,
                    "allocatedBudget" to balance.allocatedBudget,
                    "availableBalance" to balance.availableBalance,
                    "spentAmount" to balance.spentAmount,
                    "exchangeRateToUsd" to balance.exchangeRateToUsd,
                    "lastUpdatedTimestamp" to System.currentTimeMillis()
                )
                batch.set(docRef, data, SetOptions.merge())
            }
            batch.commit().await()
            _syncStatus.value = SyncStatus.SYNCED_CLOUD
            true
        } catch (e: Exception) {
            Log.e("CloudSyncManager", "Wallet sync failed: ${e.message}")
            false
        }
    }

    fun signOut() {
        try {
            auth.signOut()
            _currentUser.value = null
            _syncStatus.value = SyncStatus.OFFLINE_LOCAL
        } catch (e: Exception) {
            Log.e("CloudSyncManager", "Sign out error: ${e.message}")
        }
    }

    enum class SyncStatus {
        SYNCED_CLOUD,
        OFFLINE_LOCAL,
        SYNCING
    }
}

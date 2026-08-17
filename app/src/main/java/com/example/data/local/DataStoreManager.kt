package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "marco_preferences")

/**
 * DataStore Preferences implementation for managing user configurations,
 * theme options, audio playback preferences, and offline caching settings.
 */
class DataStoreManager(private val context: Context) {

    companion object {
        val KEY_THEME_MODE = stringPreferencesKey("theme_mode") // "ELEGANT_DARK", "ANTIQUE_PARCHMENT", "SYSTEM"
        val KEY_VOICE_PROFILE = stringPreferencesKey("voice_profile_id")
        val KEY_VOICE_SPEED = floatPreferencesKey("voice_speed")
        val KEY_AUTO_VOICE_PLAY = booleanPreferencesKey("auto_voice_play")
        val KEY_OFFLINE_MODE = booleanPreferencesKey("offline_mode_active")
        val KEY_AI_MODEL = stringPreferencesKey("ai_model_name")
        val KEY_CUSTOM_API_KEY = stringPreferencesKey("custom_gemini_key")
        val KEY_EMERGENCY_CONTACT = stringPreferencesKey("emergency_contact_phone")
        val KEY_REDUCED_MOTION = booleanPreferencesKey("reduced_motion")
        val KEY_HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")

        @Volatile
        private var INSTANCE: DataStoreManager? = null

        fun getInstance(context: Context): DataStoreManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DataStoreManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    val themeModeFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_THEME_MODE] ?: "ELEGANT_DARK"
    }

    val voiceProfileFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_VOICE_PROFILE] ?: "marco_polo"
    }

    val offlineModeFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_OFFLINE_MODE] ?: true
    }

    val emergencyContactFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_EMERGENCY_CONTACT] ?: "+1-800-555-0199"
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_THEME_MODE] = mode
        }
    }

    suspend fun setVoiceProfile(profileId: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_VOICE_PROFILE] = profileId
        }
    }

    suspend fun setOfflineMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_OFFLINE_MODE] = enabled
        }
    }

    suspend fun setEmergencyContact(contactPhone: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_EMERGENCY_CONTACT] = contactPhone
        }
    }

    suspend fun setCustomApiKey(apiKey: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_CUSTOM_API_KEY] = apiKey
        }
    }
}

package com.example.data.repository

import com.example.data.local.TravelDao
import com.example.data.model.ChatMessageEntity
import com.example.data.model.ConnectedAccountEntity
import com.example.data.model.CurrencyRateEntity
import com.example.data.model.EmergencyAlertEntity
import com.example.data.model.ExpenseEntity
import com.example.data.model.GroupMemoryEntity
import com.example.data.model.ProactiveSuggestionEntity
import com.example.data.model.TripActivityEntity
import com.example.data.model.TripEntity
import com.example.data.model.TripFeedbackEntity
import com.example.data.model.UserPreferenceEntity
import com.example.data.model.VendorCallLogEntity
import com.example.data.model.WalletBalanceEntity
import com.example.data.model.WalletTransactionEntity
import com.example.data.security.WalletSecurityManager
import kotlinx.coroutines.flow.Flow

class TravelRepository(private val travelDao: TravelDao) {

    val allTrips: Flow<List<TripEntity>> = travelDao.getAllTrips()
    val connectedAccounts: Flow<List<ConnectedAccountEntity>> = travelDao.getAllConnectedAccounts()
    val allAlerts: Flow<List<EmergencyAlertEntity>> = travelDao.getAllAlerts()
    val allVendorCalls: Flow<List<VendorCallLogEntity>> = travelDao.getAllVendorCalls()
    val chatMessages: Flow<List<ChatMessageEntity>> = travelDao.getChatMessages()
    val userPreferences: Flow<UserPreferenceEntity?> = travelDao.getUserPreferences()
    val allTripFeedbacks: Flow<List<TripFeedbackEntity>> = travelDao.getAllTripFeedbacks()
    val proactiveSuggestions: Flow<List<ProactiveSuggestionEntity>> = travelDao.getAllProactiveSuggestions()
    val allWalletBalances: Flow<List<WalletBalanceEntity>> = travelDao.getAllWalletBalances()
    val allWalletTransactions: Flow<List<WalletTransactionEntity>> = travelDao.getAllWalletTransactions()
    val allCurrencyRates: Flow<List<CurrencyRateEntity>> = travelDao.getAllCurrencyRates()

    fun getTripById(tripId: Long): Flow<TripEntity?> = travelDao.getTripById(tripId)
    fun getActivitiesForTrip(tripId: Long): Flow<List<TripActivityEntity>> = travelDao.getActivitiesForTrip(tripId)
    fun getExpensesForTrip(tripId: Long): Flow<List<ExpenseEntity>> = travelDao.getExpensesForTrip(tripId)
    fun getMemoriesForTrip(tripId: Long): Flow<List<GroupMemoryEntity>> = travelDao.getMemoriesForTrip(tripId)
    fun getFeedbacksForTrip(tripId: Long): Flow<List<TripFeedbackEntity>> = travelDao.getFeedbacksForTrip(tripId)
    fun getWalletBalancesForTrip(tripId: Long): Flow<List<WalletBalanceEntity>> = travelDao.getWalletBalancesForTrip(tripId)
    fun getWalletTransactionsForTrip(tripId: Long): Flow<List<WalletTransactionEntity>> = travelDao.getWalletTransactionsForTrip(tripId)

    suspend fun getUserPreferencesSync(): UserPreferenceEntity? = travelDao.getUserPreferencesSync()
    suspend fun saveUserPreferences(preferences: UserPreferenceEntity) = travelDao.insertOrUpdateUserPreferences(preferences)

    suspend fun insertTripFeedback(feedback: TripFeedbackEntity): Long = travelDao.insertTripFeedback(feedback)
    suspend fun deleteTripFeedback(feedbackId: Long) = travelDao.deleteTripFeedback(feedbackId)

    suspend fun updateProactiveSuggestions(suggestions: List<ProactiveSuggestionEntity>) {
        travelDao.clearProactiveSuggestions()
        travelDao.insertProactiveSuggestions(suggestions)
    }

    suspend fun insertTrip(trip: TripEntity): Long = travelDao.insertTrip(trip)
    suspend fun updateTrip(trip: TripEntity) = travelDao.updateTrip(trip)
    suspend fun deleteTrip(tripId: Long) = travelDao.deleteTrip(tripId)

    suspend fun insertActivity(activity: TripActivityEntity): Long = travelDao.insertActivity(activity)
    suspend fun insertActivities(activities: List<TripActivityEntity>) = travelDao.insertActivities(activities)
    suspend fun updateActivity(activity: TripActivityEntity) = travelDao.updateActivity(activity)
    suspend fun deleteActivity(activityId: Long) = travelDao.deleteActivity(activityId)
    suspend fun clearActivitiesForTrip(tripId: Long) = travelDao.clearActivitiesForTrip(tripId)

    suspend fun insertAccount(account: ConnectedAccountEntity): Long = travelDao.insertAccount(account)
    suspend fun updateAccount(account: ConnectedAccountEntity) = travelDao.updateAccount(account)
    suspend fun deleteAccount(accountId: Long) = travelDao.deleteAccount(accountId)

    suspend fun insertExpense(expense: ExpenseEntity): Long = travelDao.insertExpense(expense)
    suspend fun deleteExpense(expenseId: Long) = travelDao.deleteExpense(expenseId)

    suspend fun insertVendorCall(callLog: VendorCallLogEntity): Long = travelDao.insertVendorCall(callLog)

    suspend fun insertAlert(alert: EmergencyAlertEntity): Long = travelDao.insertAlert(alert)

    suspend fun insertMemory(memory: GroupMemoryEntity): Long = travelDao.insertMemory(memory)
    suspend fun incrementMemoryLikes(memoryId: Long) = travelDao.incrementMemoryLikes(memoryId)

    suspend fun insertChatMessage(message: ChatMessageEntity): Long = travelDao.insertChatMessage(message)
    suspend fun clearChatMessages() = travelDao.clearChatMessages()

    // Marco Wallet Balances & Encrypted Transactions
    suspend fun insertWalletBalance(balance: WalletBalanceEntity): Long = travelDao.insertWalletBalance(balance)
    suspend fun updateWalletBalance(balance: WalletBalanceEntity) = travelDao.updateWalletBalance(balance)
    suspend fun insertWalletTransaction(transaction: WalletTransactionEntity): Long = travelDao.insertWalletTransaction(transaction)
    suspend fun deleteWalletTransaction(transactionId: Long) = travelDao.deleteWalletTransaction(transactionId)
    suspend fun insertCurrencyRates(rates: List<CurrencyRateEntity>) = travelDao.insertCurrencyRates(rates)

    suspend fun checkAndSeedInitialData() {
        val first = travelDao.getFirstTrip()
        if (first != null) return

        // 1. Seed Trips
        val trip1Id = travelDao.insertTrip(
            TripEntity(
                title = "Maui & Haleakalā Luxury & Stargazing",
                destination = "Maui, Hawaii, USA",
                countryCode = "US",
                startDate = "Oct 12, 2026",
                endDate = "Oct 19, 2026",
                budgetTotal = 4800.0,
                budgetSpent = 2340.0,
                primaryCurrency = "USD",
                travelersCount = 3,
                childrenCount = 1,
                accessibilityRequirements = "Step-Free Ramp, ADA Heated Pool Chair Lift, Stroller Friendly",
                dietaryRestrictions = "Gluten-Free & Nut-Free Aware (Celiac Safe Dining)",
                familyAgeBrackets = "Toddler (3yo) & 2 Adults",
                travelStyle = "Timeshare Exchange + Airline Miles",
                timeshareExchangeDetails = "RCI 2-Bedroom Oceanfront Villa (Grand Champions Maui)",
                isOfflineSynced = true,
                heroThemeIndex = 0
            )
        )

        val trip2Id = travelDao.insertTrip(
            TripEntity(
                title = "Swiss Alps & Lake Geneva Express",
                destination = "Interlaken & Geneva, Switzerland",
                countryCode = "CH",
                startDate = "Dec 05, 2026",
                endDate = "Dec 14, 2026",
                budgetTotal = 6200.0,
                budgetSpent = 1180.0,
                primaryCurrency = "CHF",
                travelersCount = 2,
                childrenCount = 0,
                accessibilityRequirements = "Wheelchair train transit ramps confirmed, Step-free chalets",
                dietaryRestrictions = "Vegetarian & Farm-to-Table",
                familyAgeBrackets = "2 Adults (Senior Friendly Pacing)",
                travelStyle = "Chase UR Points & Panoramic Rail",
                timeshareExchangeDetails = "Interval International Alpine Chalet Swap",
                isOfflineSynced = true,
                heroThemeIndex = 1
            )
        )

        // 2. Seed Activities for Trip 1
        val activities = listOf(
            TripActivityEntity(
                tripId = trip1Id,
                dayNumber = 1,
                timeSlot = "08:30 AM",
                title = "Delta Nonstop Flight DL 482 (SFO -> OGG)",
                category = "FLIGHT",
                location = "San Francisco Intl (SFO) Gate 71A",
                confirmationCode = "DL-99482A",
                notes = "First Class upgrades cleared via SkyMiles. Pre-ordered toddler breakfast meal.",
                cost = 0.0,
                currency = "USD",
                accessibilityBadge = "Priority Family Boarding & Wheelchair Assistance",
                vendorName = "Delta Air Lines",
                vendorPhone = "+1-800-221-1212",
                isCompleted = true,
                latitude = 20.8986,
                longitude = -156.4305
            ),
            TripActivityEntity(
                tripId = trip1Id,
                dayNumber = 1,
                timeSlot = "01:30 PM",
                title = "Check-in: Grand Champions Oceanfront Villas",
                category = "TIMESHARE",
                location = "3750 Wailea Alanui Dr, Wailea, HI",
                confirmationCode = "RCI-WAIL-8832",
                notes = "AI Voice Agent verified heated pool open daily 7am-10pm with poolside ramp.",
                cost = 249.0,
                currency = "USD",
                accessibilityBadge = "Step-free Ground Floor Villa",
                vendorName = "Grand Champions Front Desk",
                vendorPhone = "+1-808-879-1595",
                isCompleted = true,
                latitude = 20.6865,
                longitude = -156.4421
            ),
            TripActivityEntity(
                tripId = trip1Id,
                dayNumber = 2,
                timeSlot = "09:00 AM",
                title = "Molokini Crater Eco-Snorkel & Marine Sanctuary",
                category = "ATTRACTION",
                location = "Maalaea Harbor Slip 42",
                confirmationCode = "PRD-SNRK-50",
                notes = "Includes child flotation vests and glass-bottom viewing chamber.",
                cost = 180.0,
                currency = "USD",
                accessibilityBadge = "Accessible Boarding Gangway",
                vendorName = "Pride of Maui Eco Charters",
                vendorPhone = "+1-808-242-0955",
                isCompleted = false,
                latitude = 20.7932,
                longitude = -156.5100
            ),
            TripActivityEntity(
                tripId = trip1Id,
                dayNumber = 2,
                timeSlot = "06:30 PM",
                title = "Sunset Dinner at Merriman's Kapalua",
                category = "DINING",
                location = "1 Bay Dr, Lahaina, HI",
                confirmationCode = "OPNTBL-819",
                notes = "Farm-to-table tasting menu. Requested quiet corner table with high chair.",
                cost = 220.0,
                currency = "USD",
                accessibilityBadge = "Full ADA Ramp Access",
                vendorName = "Merriman's Kapalua",
                vendorPhone = "+1-808-669-6400",
                isCompleted = false,
                latitude = 20.9996,
                longitude = -156.6667
            ),
            TripActivityEntity(
                tripId = trip1Id,
                dayNumber = 3,
                timeSlot = "03:30 AM",
                title = "Haleakalā National Park Sunrise & Stargazing",
                category = "CAMPGROUND",
                location = "Haleakalā Summit Visitor Center",
                confirmationCode = "REC-GOV-94812",
                notes = "Recreation.gov summit permit synced offline. Pack warm fleece layers for toddler.",
                cost = 35.0,
                currency = "USD",
                accessibilityBadge = "Paved Observation Deck",
                vendorName = "National Park Service (Recreation.gov)",
                vendorPhone = "+1-808-572-4400",
                isCompleted = false,
                latitude = 20.7097,
                longitude = -156.2533
            ),
            TripActivityEntity(
                tripId = trip1Id,
                dayNumber = 3,
                timeSlot = "12:00 PM",
                title = "Upcountry Maui Lavender Farm & Picnic",
                category = "FAMILY_KIDS",
                location = "1100 Waipoli Rd, Kula, HI",
                confirmationCode = "AKL-6712",
                notes = "Stroller-friendly walking paths, sensory aromatic garden, lavender ice cream.",
                cost = 45.0,
                currency = "USD",
                accessibilityBadge = "Gentle Grade Paths",
                vendorName = "Ali'i Kula Lavender Farm",
                vendorPhone = "+1-808-878-3004",
                isCompleted = false,
                latitude = 20.7511,
                longitude = -156.3211
            )
        )
        travelDao.insertActivities(activities)

        // 3. Seed Connected Accounts (Airlines, Hotels, Timeshare, Credit Cards, Camping)
        val accounts = listOf(
            ConnectedAccountEntity(
                categoryType = "AIRLINE",
                providerName = "Delta SkyMiles",
                accountNumberMasked = "DL •••• 4891",
                balanceValue = "84,250",
                unitLabel = "miles",
                tierStatus = "Platinum Medallion",
                rewardsEstimatedValuationUsd = 1011.0,
                lastSyncTime = "Synced 5m ago"
            ),
            ConnectedAccountEntity(
                categoryType = "HOTEL",
                providerName = "Marriott Bonvoy",
                accountNumberMasked = "MB •••• 9924",
                balanceValue = "162,000",
                unitLabel = "pts",
                tierStatus = "Titanium Elite",
                rewardsEstimatedValuationUsd = 1377.0,
                lastSyncTime = "Synced 10m ago"
            ),
            ConnectedAccountEntity(
                categoryType = "TIMESHARE",
                providerName = "RCI Weeks Exchange",
                accountNumberMasked = "RCI •••• 7731",
                balanceValue = "1 Week / 38",
                unitLabel = "TPU Trading Power",
                tierStatus = "RCI Platinum Member",
                rewardsEstimatedValuationUsd = 2400.0,
                exchangePowerScore = 38,
                lastSyncTime = "Synced 1h ago"
            ),
            ConnectedAccountEntity(
                categoryType = "TIMESHARE",
                providerName = "Interval International (II)",
                accountNumberMasked = "II •••• 2048",
                balanceValue = "2 Exchange Certificates",
                unitLabel = "Deposit Credits",
                tierStatus = "Gold Exchange",
                rewardsEstimatedValuationUsd = 1800.0,
                exchangePowerScore = 42,
                lastSyncTime = "Synced 2h ago"
            ),
            ConnectedAccountEntity(
                categoryType = "CREDIT_CARD",
                providerName = "Chase Sapphire Ultimate Rewards",
                accountNumberMasked = "UR •••• 1042",
                balanceValue = "142,800",
                unitLabel = "UR pts",
                tierStatus = "Sapphire Reserve (1.5x)",
                rewardsEstimatedValuationUsd = 2856.0,
                lastSyncTime = "Synced 15m ago"
            ),
            ConnectedAccountEntity(
                categoryType = "CREDIT_CARD",
                providerName = "Amex Membership Rewards",
                accountNumberMasked = "MR •••• 5590",
                balanceValue = "118,500",
                unitLabel = "MR pts",
                tierStatus = "Centurion Concierge Access",
                rewardsEstimatedValuationUsd = 2370.0,
                lastSyncTime = "Synced 30m ago"
            ),
            ConnectedAccountEntity(
                categoryType = "CAMPING",
                providerName = "Recreation.gov & National Parks Pass",
                accountNumberMasked = "NPS •••• 8812",
                balanceValue = "America the Beautiful Pass",
                unitLabel = "Active Pass",
                tierStatus = "Lifetime Senior / Family",
                rewardsEstimatedValuationUsd = 80.0,
                lastSyncTime = "Synced 1d ago"
            ),
            ConnectedAccountEntity(
                categoryType = "OTA",
                providerName = "Expedia OneKey & VIP Access",
                accountNumberMasked = "EXP •••• 3341",
                balanceValue = "$315.00",
                unitLabel = "OneKeyCash",
                tierStatus = "Gold VIP",
                rewardsEstimatedValuationUsd = 315.0,
                lastSyncTime = "Synced 4h ago"
            )
        )
        travelDao.insertAccounts(accounts)

        // 4. Seed Expenses
        val expenses = listOf(
            ExpenseEntity(
                tripId = trip1Id,
                title = "RCI Timeshare Exchange Fee & Resort Upgrade",
                category = "Timeshare Fees",
                amountOriginal = 249.0,
                originalCurrency = "USD",
                amountUsd = 249.0,
                exchangeRate = 1.0,
                paidBy = "Chase Sapphire Reserve",
                date = "Oct 12, 2026",
                notes = "Covered by travel credit"
            ),
            ExpenseEntity(
                tripId = trip1Id,
                title = "Alamo Luxury SUV Rental with Toddler Booster",
                category = "Transit",
                amountOriginal = 385.0,
                originalCurrency = "USD",
                amountUsd = 385.0,
                exchangeRate = 1.0,
                paidBy = "Amex Platinum",
                date = "Oct 12, 2026",
                notes = "Includes primary rental insurance waiver"
            ),
            ExpenseEntity(
                tripId = trip1Id,
                title = "Molokini Snorkel Tickets (Family Pass)",
                category = "Activities",
                amountOriginal = 360.0,
                originalCurrency = "USD",
                amountUsd = 360.0,
                exchangeRate = 1.0,
                paidBy = "Chase Sapphire Reserve",
                date = "Oct 13, 2026",
                notes = "3x points earned"
            ),
            ExpenseEntity(
                tripId = trip1Id,
                title = "Organic Island Grocery & Baby Supplies (Safeway Kahului)",
                category = "Food",
                amountOriginal = 142.50,
                originalCurrency = "USD",
                amountUsd = 142.50,
                exchangeRate = 1.0,
                paidBy = "Chase Sapphire Reserve",
                date = "Oct 12, 2026",
                notes = "Stocked kitchen in villa"
            )
        )
        for (expense in expenses) {
            travelDao.insertExpense(expense)
        }

        // 5. Seed Vendor Voice Call Logs
        travelDao.insertVendorCall(
            VendorCallLogEntity(
                tripId = trip1Id,
                vendorName = "Grand Champions Front Desk & Concierge",
                vendorPhone = "+1-808-879-1595",
                inquiryTopic = "Verify Heated Pool Schedule & ADA Pool Lift Access",
                status = "COMPLETED",
                audioTranscript = "AI Agent: 'Hello! Calling on behalf of guest reservation RCI-WAIL-8832. Could you verify if the north heated pool will be open October 12-19, and if it has a hydraulic lift or ramp?'\nHotel Representative: 'Aloha! Yes, both our north and south heated swimming pools are open daily from 7:00 AM to 10:00 PM throughout October. The north pool is equipped with a certified ADA chair lift and step-free gentle slope entrance.'\nAI Agent: 'Mahalo! That is noted and saved to the guest itinerary.'",
                callSummaryOutcome = "Pool confirmed open daily 7am-10pm with ADA chair lift & step-free entrance.",
                confirmedDetails = "North Pool heated to 84°F; no construction during stay."
            )
        )
        travelDao.insertVendorCall(
            VendorCallLogEntity(
                tripId = trip1Id,
                vendorName = "Haleakalā National Park Ranger Station",
                vendorPhone = "+1-808-572-4400",
                inquiryTopic = "Inquire About High Altitude Health Advisory for Young Children",
                status = "COMPLETED",
                audioTranscript = "AI Agent: 'Hi Ranger, calling regarding the sunrise summit access with a 3-year old child. Are there heated restrooms and oxygen stations at the visitor center?'\nPark Ranger: 'Good morning! The Summit Visitor Center at 9,740 ft has heated indoor restrooms, but we recommend dressing in multiple heavy windproof layers. Keep visits under 45 minutes for young kids to adjust easily.'\nAI Agent: 'Thank you! Added family warm-layer reminder to itinerary.'",
                callSummaryOutcome = "Heated indoor rest area confirmed; advised warm fleece and 45-min summit limit for toddler.",
                confirmedDetails = "Heated restrooms open 24/7; summit gate code verified."
            )
        )

        // 6. Seed Emergency Alerts & Safety
        val alerts = listOf(
            EmergencyAlertEntity(
                tripId = trip1Id,
                alertType = "WEATHER_ALERT",
                title = "Haleakalā Summit High-Wind Advisory",
                description = "Summit temperatures currently 38°F with wind gusts up to 25mph. Pack insulated coats and thermal beanies.",
                severity = "WARNING",
                actionContact = "Summit Weather Line: 808-572-4400",
                location = "Haleakalā Summit, Maui",
                timestamp = "Updated 20m ago"
            ),
            EmergencyAlertEntity(
                tripId = trip1Id,
                alertType = "MEDICAL_ASSISTANCE",
                title = "Nearest 24/7 Pediatric & Emergency Hospital",
                description = "Maui Memorial Medical Center (Level III Trauma & Full Pediatric ER). 221 Mahalani St, Wailuku.",
                severity = "INFO",
                actionContact = "Hospital ER: +1-808-244-9056 | Police/Fire SOS: 911",
                location = "Wailuku, Maui (25 mins from Wailea)",
                timestamp = "Active 24/7"
            ),
            EmergencyAlertEntity(
                tripId = trip1Id,
                alertType = "FLIGHT_UPDATE",
                title = "Delta Flight DL 482 On Time",
                description = "Gate assigned 71A at SFO. Baggage drop open. No ATC delay reported.",
                severity = "INFO",
                actionContact = "Delta Gate Services: +1-800-221-1212",
                location = "SFO Intl Airport",
                timestamp = "Live Flight Radar"
            )
        )
        travelDao.insertAlerts(alerts)

        // 7. Seed Group Memories
        val memories = listOf(
            GroupMemoryEntity(
                tripId = trip1Id,
                authorName = "Sarah Jenkins",
                caption = "Sunset over Makena Beach! The kids were mesmerized by the sea turtles near the reef. 🌺🌅",
                locationTag = "Makena Cove, Maui",
                timestamp = "Yesterday at 6:45 PM",
                photoGradientColor = 0xFFF59E0B,
                likesCount = 8,
                aiTag = "Golden Hour & Wildlife"
            ),
            GroupMemoryEntity(
                tripId = trip1Id,
                authorName = "David Kim",
                caption = "Morning coffee on the villa lanai. Best RCI exchange redemption we have ever done! ☕🏝️",
                locationTag = "Grand Champions Villas",
                timestamp = "Today at 7:15 AM",
                photoGradientColor = 0xFF0284C7,
                likesCount = 5,
                aiTag = "Resort Living"
            ),
            GroupMemoryEntity(
                tripId = trip1Id,
                authorName = "Alex Rivera",
                caption = "Upcountry lavender fields smell unbelievable! Super easy stroller access everywhere. 💜",
                locationTag = "Ali'i Kula Lavender",
                timestamp = "Today at 2:30 PM",
                photoGradientColor = 0xFF8B5CF6,
                likesCount = 6,
                aiTag = "Family Adventure"
            )
        )
        for (memory in memories) {
            travelDao.insertMemory(memory)
        }

        // 8. Seed Chat Messages
        val messages = listOf(
            ChatMessageEntity(
                tripId = trip1Id,
                sender = "CONCIERGE_AI",
                text = "Aloha! I am your 24/7 AI Travel Agent. I've analyzed your traveler profile (Delta SkyTeam, oceanfront timeshares, balanced family pace) and synced your itinerary. How may I assist your Maui getaway today?",
                timestamp = System.currentTimeMillis() - 100000,
                actionChipText = "Pool & amenities verified open",
                suggestedActionJson = ""
            )
        )
        for (msg in messages) {
            travelDao.insertChatMessage(msg)
        }

        // 9. Seed User Preferences (Stated + Learned Insights)
        travelDao.insertOrUpdateUserPreferences(
            UserPreferenceEntity(
                id = 1,
                preferredAirlines = "Delta Air Lines, ANA, United, Singapore Airlines",
                preferredHotelTypes = "Oceanfront Timeshare Villa, Boutique Luxury, Historic Ryokan",
                activityLevel = "Balanced & Moderate (Sensory & Rest Intervals)",
                preferredTravelStyle = "Smart Points Maximizer & Scenic Culture",
                dietaryPreferences = "Gluten-Free, Nut-Free, Toddler High-Chair Friendly",
                wheelchairRequirements = "Step-Free Ramp, Roll-in Shower, ADA Pool Chair Lift",
                familyAgeBrackets = "Toddler (3yo), Teen (15yo), 2 Adults",
                sensoryAndMobilityNotes = "Paved stroller paths, quiet low-sensory rest windows, elevator priority",
                pacingPreference = "Morning Activities (8am-12pm) with Leisure Afternoons",
                preferredTransit = "Scenic Rail & Private Luxury SUV",
                preferredClimate = "Tropical Coast & Alpine Scenic",
                learnedInsightsSummary = "AI Learned Profile: 96% affinity for step-free morning outdoor scenery, toddler-safe villas with full kitchens, and verified gluten/nut-safe dining. Consistently awards 5-stars to ADA-compliant spacious multi-bedroom timeshare villas over standard hotels. High Delta SkyMiles loyalty synergy.",
                totalTripsAnalyzed = 2,
                lastUpdatedTimestamp = System.currentTimeMillis()
            )
        )

        // 10. Seed Past Trip Feedback
        val feedbacks = listOf(
            TripFeedbackEntity(
                tripId = trip1Id,
                tripTitle = "Maui & Haleakala Luxury & Stargazing",
                destination = "Maui, Hawaii",
                rating = 5,
                likedAspects = "Oceanfront villa kitchen, Delta First Class upgrade, heated pool with ramp",
                dislikedAspects = "High altitude cold at summit",
                feedbackNotes = "The RCI 2-bedroom villa gave our toddler great sleep space, and morning lavender farm was peaceful and stroller-perfect. Keep recommending spacious suites.",
                dateSubmitted = "Recent",
                learnedActionableTakeaway = "Prioritize villas with full kitchens and calm morning schedules."
            ),
            TripFeedbackEntity(
                tripId = trip2Id,
                tripTitle = "Swiss Alps & Lake Geneva Express",
                destination = "Interlaken & Geneva, Switzerland",
                rating = 5,
                likedAspects = "Panoramic Glacier Express train, step-free ramps, quiet alpine chalets",
                dislikedAspects = "Cobblestone streets near old town",
                feedbackNotes = "Scenic rail travel was 10x more relaxing than driving. Chase UR point transfer to Hyatt Zurich worked seamlessly.",
                dateSubmitted = "Prior Trip",
                learnedActionableTakeaway = "Favor scenic high-speed rail over crowded car rentals."
            )
        )
        for (fb in feedbacks) {
            travelDao.insertTripFeedback(fb)
        }

        // 11. Seed Proactive Suggestions (Tailored future travel suggestions)
        val suggestions = listOf(
            ProactiveSuggestionEntity(
                title = "Kyoto & Hakone Zen Onsen Ryokan",
                destination = "Kyoto & Hakone, Japan",
                countryCode = "JP",
                durationDays = 8,
                estimatedBudget = 4200.0,
                currency = "USD",
                matchScorePercent = 98,
                rationale = "98% Match based on your high ratings for scenic panoramic rail, quiet villas with gardens, and Delta/ANA flight upgrades.",
                suggestedAirline = "Delta / ANA Nonstop (SkyTeam)",
                suggestedLodging = "Gora Kadan Ryokan & Timeshare Suite",
                activityPace = "Morning Zen Gardens & Afternoon Tea",
                heroTag = "Top AI Recommendation",
                pointsSavingsUsd = 2100.0,
                season = "Spring Sakura / Autumn Peak"
            ),
            ProactiveSuggestionEntity(
                title = "Banff & Canadian Rockies Scenic Rail",
                destination = "Banff & Lake Louise, Canada",
                countryCode = "CA",
                durationDays = 6,
                estimatedBudget = 3600.0,
                currency = "CAD",
                matchScorePercent = 95,
                rationale = "95% Match: Leverages your America the Beautiful & Parks pass affinity, stroller-paved lakeside trails, and mountain chalets.",
                suggestedAirline = "Delta / WestJet SkyMiles",
                suggestedLodging = "Fairmont Chateau Lake Louise & Alpine Lodge",
                activityPace = "Gentle Glacier Walks & Thermal Hot Springs",
                heroTag = "Nature & Scenic Rail",
                pointsSavingsUsd = 1450.0,
                season = "Summer / Fall Foliage"
            ),
            ProactiveSuggestionEntity(
                title = "Amalfi Coast & Capri Cliffside Villa",
                destination = "Positano & Amalfi, Italy",
                countryCode = "IT",
                durationDays = 7,
                estimatedBudget = 5100.0,
                currency = "EUR",
                matchScorePercent = 92,
                rationale = "92% Match: Perfect for your RCI Interval International trading power swap + organic Mediterranean farm-to-table dining.",
                suggestedAirline = "Air France / Delta via Paris",
                suggestedLodging = "Interval International Cliffside Villa",
                activityPace = "Private Boat Charter & Lemon Grove Walks",
                heroTag = "Timeshare Exchange Match",
                pointsSavingsUsd = 2800.0,
                season = "May - September"
            )
        )
        travelDao.insertProactiveSuggestions(suggestions)

        // 12. Seed Marco Secure Multi-Currency Wallet Balances
        val walletBalances = listOf(
            WalletBalanceEntity(
                tripId = trip1Id,
                currencyCode = "USD",
                currencySymbol = "$",
                currencyName = "US Dollar (Primary)",
                allocatedBudget = 3200.0,
                availableBalance = 1682.50,
                spentAmount = 1517.50,
                exchangeRateToUsd = 1.0,
                encryptedAccountDetails = WalletSecurityManager.encryptString("Chase Sapphire Reserve •••• 1042 (Primary) | Amex Platinum •••• 5590")
            ),
            WalletBalanceEntity(
                tripId = trip1Id,
                currencyCode = "EUR",
                currencySymbol = "€",
                currencyName = "Euro (European Reserves)",
                allocatedBudget = 800.0,
                availableBalance = 650.0,
                spentAmount = 150.0,
                exchangeRateToUsd = 1.085,
                encryptedAccountDetails = WalletSecurityManager.encryptString("Revolut Multi-Currency Vault •••• 9920")
            ),
            WalletBalanceEntity(
                tripId = trip1Id,
                currencyCode = "JPY",
                currencySymbol = "¥",
                currencyName = "Japanese Yen (Transit & Suica)",
                allocatedBudget = 50000.0,
                availableBalance = 38500.0,
                spentAmount = 11500.0,
                exchangeRateToUsd = 0.0067,
                encryptedAccountDetails = WalletSecurityManager.encryptString("Apple Wallet Digital Suica IC #8841-29")
            ),
            WalletBalanceEntity(
                tripId = trip1Id,
                currencyCode = "CHF",
                currencySymbol = "CHF",
                currencyName = "Swiss Franc (Alpine Escrow)",
                allocatedBudget = 600.0,
                availableBalance = 600.0,
                spentAmount = 0.0,
                exchangeRateToUsd = 1.142,
                encryptedAccountDetails = WalletSecurityManager.encryptString("Swiss Bankers Travel Cash •••• 4182")
            )
        )
        travelDao.insertWalletBalances(walletBalances)

        // 13. Seed Marco Wallet Transactions (With Loyalty Savings Attribution)
        val walletTransactions = listOf(
            WalletTransactionEntity(
                tripId = trip1Id,
                title = "Delta SkyTeam First Class Upgrades (2 Adults + Infant)",
                category = "Flights",
                amountOriginal = 312.0,
                currencyCode = "USD",
                amountUsd = 312.0,
                exchangeRate = 1.0,
                paymentMethod = "Delta SkyMiles + Chase UR",
                loyaltyProgramApplied = "Delta Platinum Medallion",
                loyaltySavingsUsd = 1450.0,
                dateString = "Oct 12, 2026",
                timestamp = System.currentTimeMillis() - (86400000L * 3),
                notes = "Saved $1,450 vs cash fare by redeeming 55,000 SkyMiles & Medallion certificates.",
                encryptedReceiptHash = WalletSecurityManager.encryptString("TX-DL-99482-AUTH-TOKEN-788192")
            ),
            WalletTransactionEntity(
                tripId = trip1Id,
                title = "Grand Champions 2-Bedroom Oceanfront Villa (7 Nights)",
                category = "Lodging",
                amountOriginal = 249.0,
                currencyCode = "USD",
                amountUsd = 249.0,
                exchangeRate = 1.0,
                paymentMethod = "RCI Weeks Exchange",
                loyaltyProgramApplied = "RCI Platinum (38 TPU)",
                loyaltySavingsUsd = 2650.0,
                dateString = "Oct 12, 2026",
                timestamp = System.currentTimeMillis() - (86400000L * 2),
                notes = "Retail rack rate $3,899 booked for $249 exchange fee with RCI Trading Power points.",
                encryptedReceiptHash = WalletSecurityManager.encryptString("TX-RCI-8832-AUTH-TOKEN-449102")
            ),
            WalletTransactionEntity(
                tripId = trip1Id,
                title = "Alamo Luxury AWD SUV Rental + Child Booster Seat",
                category = "Transit",
                amountOriginal = 385.0,
                currencyCode = "USD",
                amountUsd = 385.0,
                exchangeRate = 1.0,
                paymentMethod = "Amex Platinum Card",
                loyaltyProgramApplied = "National Executive Elite Tier Match",
                loyaltySavingsUsd = 190.0,
                dateString = "Oct 12, 2026",
                timestamp = System.currentTimeMillis() - 86400000L,
                notes = "Free vehicle class upgrade and complimentary car seat waiver.",
                encryptedReceiptHash = WalletSecurityManager.encryptString("TX-ALAMO-8891-AUTH-TOKEN-110293")
            ),
            WalletTransactionEntity(
                tripId = trip1Id,
                title = "Merriman's Oceanfront Farm-to-Table Dinner",
                category = "Dining",
                amountOriginal = 220.0,
                currencyCode = "USD",
                amountUsd = 220.0,
                exchangeRate = 1.0,
                paymentMethod = "Chase Sapphire Reserve",
                loyaltyProgramApplied = "Chase Dining 10x Points",
                loyaltySavingsUsd = 66.0,
                dateString = "Oct 13, 2026",
                timestamp = System.currentTimeMillis() - (3600000L * 12),
                notes = "Earned 2,200 Ultimate Rewards points (equivalent to $66 future Hyatt stay value).",
                encryptedReceiptHash = WalletSecurityManager.encryptString("TX-MERR-901-AUTH-TOKEN-339102")
            ),
            WalletTransactionEntity(
                tripId = trip1Id,
                title = "Molokini Crater Eco-Snorkel & Marine Sanctuary Pass",
                category = "Activities",
                amountOriginal = 360.0,
                currencyCode = "USD",
                amountUsd = 360.0,
                exchangeRate = 1.0,
                paymentMethod = "Chase Sapphire Reserve",
                loyaltyProgramApplied = "Chase Travel Portal 1.5x Multiplier",
                loyaltySavingsUsd = 120.0,
                dateString = "Oct 13, 2026",
                timestamp = System.currentTimeMillis() - (3600000L * 6),
                notes = "Family ticket package redeemed with partial points rebate.",
                encryptedReceiptHash = WalletSecurityManager.encryptString("TX-PRIDE-991-AUTH-TOKEN-778819")
            ),
            WalletTransactionEntity(
                tripId = trip1Id,
                title = "Organic Island Provisions & Toddler Essentials",
                category = "Groceries",
                amountOriginal = 142.50,
                currencyCode = "USD",
                amountUsd = 142.50,
                exchangeRate = 1.0,
                paymentMethod = "Apple Pay (Amex)",
                loyaltyProgramApplied = "Amex 4x Supermarkets",
                loyaltySavingsUsd = 22.80,
                dateString = "Oct 12, 2026",
                timestamp = System.currentTimeMillis() - (3600000L * 24),
                notes = "Stocked kitchen in villa, avoiding $300+ in dining out costs.",
                encryptedReceiptHash = WalletSecurityManager.encryptString("TX-SAFEWAY-441-AUTH-TOKEN-899120")
            )
        )
        travelDao.insertWalletTransactions(walletTransactions)

        // 14. Seed Currency Exchange Rates with volatility and fee tips
        val currencyRates = listOf(
            CurrencyRateEntity(
                currencyCode = "EUR",
                baseCurrency = "USD",
                rateAgainstBase = 0.921,
                inverseRate = 1.085,
                dayChangePercent = -0.18,
                countryFlag = "🇪🇺",
                feeAvoidanceTip = "Pay in local Euros (€) to bypass dynamic currency conversion markup (save 3-5%)."
            ),
            CurrencyRateEntity(
                currencyCode = "JPY",
                baseCurrency = "USD",
                rateAgainstBase = 149.25,
                inverseRate = 0.0067,
                dayChangePercent = +0.42,
                countryFlag = "🇯🇵",
                feeAvoidanceTip = "Yen is at multi-year favorable value vs USD; ideal time to preload digital Suica passes."
            ),
            CurrencyRateEntity(
                currencyCode = "GBP",
                baseCurrency = "USD",
                rateAgainstBase = 0.785,
                inverseRate = 1.274,
                dayChangePercent = +0.05,
                countryFlag = "🇬🇧",
                feeAvoidanceTip = "Contactless transit taps in London automatically cap fares at daily limits."
            ),
            CurrencyRateEntity(
                currencyCode = "CHF",
                baseCurrency = "USD",
                rateAgainstBase = 0.875,
                inverseRate = 1.142,
                dayChangePercent = -0.12,
                countryFlag = "🇨🇭",
                feeAvoidanceTip = "Swiss Rail Passes & half-fare cards save 50% on all mountain funiculars."
            ),
            CurrencyRateEntity(
                currencyCode = "CAD",
                baseCurrency = "USD",
                rateAgainstBase = 1.358,
                inverseRate = 0.736,
                dayChangePercent = +0.10,
                countryFlag = "🇨🇦",
                feeAvoidanceTip = "US Credit cards with 0% foreign transaction fee waive all Canadian exchange penalties."
            ),
            CurrencyRateEntity(
                currencyCode = "AUD",
                baseCurrency = "USD",
                rateAgainstBase = 1.520,
                inverseRate = 0.658,
                dayChangePercent = -0.25,
                countryFlag = "🇦🇺",
                feeAvoidanceTip = "Tipping is not customary in Australia; prices display all taxes inclusive."
            )
        )
        travelDao.insertCurrencyRates(currencyRates)
    }
}

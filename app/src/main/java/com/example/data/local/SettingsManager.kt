package com.example.data.local

import android.content.Context
import android.content.SharedPreferences

data class VoiceProfile(
    val id: String,
    val name: String,
    val epithet: String,
    val description: String,
    val tone: String,
    val languageRegion: String,
    val iconEmoji: String
)

val AvailableVoiceProfiles = listOf(
    VoiceProfile(
        id = "marco_polo",
        name = "Marco (Venetian Explorer)",
        epithet = "Silk Road Voyager & Warm Storyteller",
        description = "Eloquent, worldly, and observant. Speaks with classic cadence and rich navigational phrasing.",
        tone = "Sophisticated, Warm & Literary",
        languageRegion = "English (Global Explorer)",
        iconEmoji = "📜"
    ),
    VoiceProfile(
        id = "aurora_concierge",
        name = "Aurora (Luxury Concierge)",
        epithet = "5-Star Hospitality Specialist",
        description = "Crisp, ultra-attentive, and refined. Focused on VIP perks, seamless reservations, and comfort.",
        tone = "Polished, Elegant & Proactive",
        languageRegion = "English (US Refined)",
        iconEmoji = "✨"
    ),
    VoiceProfile(
        id = "atlas_navigator",
        name = "Atlas (Expedition Navigator)",
        epithet = "Wilderness & Trail Wayfarer",
        description = "Direct, rugged, and safety-conscious. Expert on off-grid GPS, elevation changes, and weather radar.",
        tone = "Crisp, Pragmatic & Reassuring",
        languageRegion = "English (Pacific Northwest)",
        iconEmoji = "🧭"
    ),
    VoiceProfile(
        id = "solaris_family",
        name = "Soleil (Family & Accessibility Guide)",
        epithet = "Universal Logistics & Sensory Planner",
        description = "Gentle, patient, and joyful. Specialized in toddler pacing, wheelchair ramps, and allergy-safe dining.",
        tone = "Empathetic, Bright & Cheerful",
        languageRegion = "English (International Warm)",
        iconEmoji = "☀️"
    ),
    VoiceProfile(
        id = "meridian_points",
        name = "Meridian (Points & Miles Strategist)",
        epithet = "Aviation Arbitrage & Status Hacker",
        description = "Analytical, energetic, and value-focused. Breaks down redemption ratios and upgrade windows.",
        tone = "Analytical, Dynamic & Strategic",
        languageRegion = "English (Metropolitan)",
        iconEmoji = "💳"
    )
)

data class AppSettingsState(
    val customGeminiApiKey: String = "",
    val customOpenAiApiKey: String = "",
    val customAnthropicApiKey: String = "",
    val selectedVoiceId: String = "marco_polo",
    val voiceSpeed: Float = 1.0f,
    val voicePitch: Float = 1.0f,
    val autoPlayVoiceReplies: Boolean = false,
    val defaultAiModel: String = "gemini-3.5-flash",
    val temperature: Float = 0.7f,
    val unitsSystem: String = "Imperial (mi, °F)", // Imperial (mi, °F), Metric (km, °C)
    val defaultCurrency: String = "USD",
    val offlineCacheEnabled: Boolean = true,
    val autoSyncEmergencyAlerts: Boolean = true,
    val sensoryReducedMotion: Boolean = false,
    val hapticFeedbackEnabled: Boolean = true,
    val dynamicWeatherRebooking: Boolean = true
)

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("marco_app_settings", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_GEMINI_KEY = "custom_gemini_api_key"
        private const val KEY_OPENAI_KEY = "custom_openai_api_key"
        private const val KEY_ANTHROPIC_KEY = "custom_anthropic_api_key"
        private const val KEY_VOICE_ID = "selected_voice_id"
        private const val KEY_VOICE_SPEED = "voice_speed"
        private const val KEY_VOICE_PITCH = "voice_pitch"
        private const val KEY_AUTO_VOICE = "auto_play_voice"
        private const val KEY_AI_MODEL = "default_ai_model"
        private const val KEY_TEMPERATURE = "ai_temperature"
        private const val KEY_UNITS = "units_system"
        private const val KEY_CURRENCY = "default_currency"
        private const val KEY_OFFLINE_CACHE = "offline_cache_enabled"
        private const val KEY_EMERGENCY_SYNC = "emergency_sync_enabled"
        private const val KEY_REDUCED_MOTION = "sensory_reduced_motion"
        private const val KEY_HAPTIC = "haptic_feedback_enabled"
        private const val KEY_DYNAMIC_REBOOK = "dynamic_weather_rebooking"

        @Volatile
        private var INSTANCE: SettingsManager? = null

        fun getInstance(context: Context): SettingsManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SettingsManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    fun loadSettings(): AppSettingsState {
        return AppSettingsState(
            customGeminiApiKey = prefs.getString(KEY_GEMINI_KEY, "") ?: "",
            customOpenAiApiKey = prefs.getString(KEY_OPENAI_KEY, "") ?: "",
            customAnthropicApiKey = prefs.getString(KEY_ANTHROPIC_KEY, "") ?: "",
            selectedVoiceId = prefs.getString(KEY_VOICE_ID, "marco_polo") ?: "marco_polo",
            voiceSpeed = prefs.getFloat(KEY_VOICE_SPEED, 1.0f),
            voicePitch = prefs.getFloat(KEY_VOICE_PITCH, 1.0f),
            autoPlayVoiceReplies = prefs.getBoolean(KEY_AUTO_VOICE, false),
            defaultAiModel = prefs.getString(KEY_AI_MODEL, "gemini-3.5-flash") ?: "gemini-3.5-flash",
            temperature = prefs.getFloat(KEY_TEMPERATURE, 0.7f),
            unitsSystem = prefs.getString(KEY_UNITS, "Imperial (mi, °F)") ?: "Imperial (mi, °F)",
            defaultCurrency = prefs.getString(KEY_CURRENCY, "USD") ?: "USD",
            offlineCacheEnabled = prefs.getBoolean(KEY_OFFLINE_CACHE, true),
            autoSyncEmergencyAlerts = prefs.getBoolean(KEY_EMERGENCY_SYNC, true),
            sensoryReducedMotion = prefs.getBoolean(KEY_REDUCED_MOTION, false),
            hapticFeedbackEnabled = prefs.getBoolean(KEY_HAPTIC, true),
            dynamicWeatherRebooking = prefs.getBoolean(KEY_DYNAMIC_REBOOK, true)
        )
    }

    fun saveSettings(settings: AppSettingsState) {
        prefs.edit().apply {
            putString(KEY_GEMINI_KEY, settings.customGeminiApiKey.trim())
            putString(KEY_OPENAI_KEY, settings.customOpenAiApiKey.trim())
            putString(KEY_ANTHROPIC_KEY, settings.customAnthropicApiKey.trim())
            putString(KEY_VOICE_ID, settings.selectedVoiceId)
            putFloat(KEY_VOICE_SPEED, settings.voiceSpeed)
            putFloat(KEY_VOICE_PITCH, settings.voicePitch)
            putBoolean(KEY_AUTO_VOICE, settings.autoPlayVoiceReplies)
            putString(KEY_AI_MODEL, settings.defaultAiModel)
            putFloat(KEY_TEMPERATURE, settings.temperature)
            putString(KEY_UNITS, settings.unitsSystem)
            putString(KEY_CURRENCY, settings.defaultCurrency)
            putBoolean(KEY_OFFLINE_CACHE, settings.offlineCacheEnabled)
            putBoolean(KEY_EMERGENCY_SYNC, settings.autoSyncEmergencyAlerts)
            putBoolean(KEY_REDUCED_MOTION, settings.sensoryReducedMotion)
            putBoolean(KEY_HAPTIC, settings.hapticFeedbackEnabled)
            putBoolean(KEY_DYNAMIC_REBOOK, settings.dynamicWeatherRebooking)
            apply()
        }
    }
}

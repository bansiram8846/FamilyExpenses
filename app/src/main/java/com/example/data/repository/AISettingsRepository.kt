package com.example.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "familywise_ai_settings")

enum class OperatingMode {
  HYBRID,
  OFFLINE
}

data class AISettingsState(
  val isOnboardingCompleted: Boolean = false,
  val householdName: String = "Sharma Family",
  val primaryUserName: String = "Rajesh",
  val primaryUserRelation: String = "Self",
  val onlineAIEnabled: Boolean = true,
  val operatingMode: OperatingMode = OperatingMode.HYBRID,
  val hasAcceptedTerms: Boolean = true,
  val dailyQuestionsUsed: Int = 0,
  val dailyQuotaLimit: Int = 50,
  val activeProvider: String = "Google Gemini 1.5 Flash",
  val gatewayRouter: String = "Community Free Proxy",
  val proxyEndpoint: String = "https://api.familywise.internal/v1/gemini",
  val pinLockEnabled: Boolean = false,
  val appPin: String = "1234"
)

class AISettingsRepository(private val context: Context) {

  private object Keys {
    val IS_ONBOARDING_COMPLETED = booleanPreferencesKey("is_onboarding_completed")
    val HOUSEHOLD_NAME = stringPreferencesKey("household_name")
    val PRIMARY_USER_NAME = stringPreferencesKey("primary_user_name")
    val PRIMARY_USER_RELATION = stringPreferencesKey("primary_user_relation")
    val ONLINE_AI_ENABLED = booleanPreferencesKey("online_ai_enabled")
    val OPERATING_MODE = stringPreferencesKey("operating_mode")
    val HAS_ACCEPTED_TERMS = booleanPreferencesKey("has_accepted_terms")
    val DAILY_QUESTIONS_USED = intPreferencesKey("daily_questions_used")
    val PROXY_ENDPOINT = stringPreferencesKey("proxy_endpoint")
    val PIN_LOCK_ENABLED = booleanPreferencesKey("pin_lock_enabled")
    val APP_PIN = stringPreferencesKey("app_pin")
  }

  val settingsFlow: Flow<AISettingsState> = context.dataStore.data.map { prefs ->
    val modeStr = prefs[Keys.OPERATING_MODE] ?: OperatingMode.HYBRID.name
    val mode = try {
      OperatingMode.valueOf(modeStr)
    } catch (e: Exception) {
      OperatingMode.HYBRID
    }

    AISettingsState(
      isOnboardingCompleted = prefs[Keys.IS_ONBOARDING_COMPLETED] ?: false,
      householdName = prefs[Keys.HOUSEHOLD_NAME] ?: "Sharma Family",
      primaryUserName = prefs[Keys.PRIMARY_USER_NAME] ?: "Rajesh",
      primaryUserRelation = prefs[Keys.PRIMARY_USER_RELATION] ?: "Self",
      onlineAIEnabled = prefs[Keys.ONLINE_AI_ENABLED] ?: true,
      operatingMode = mode,
      hasAcceptedTerms = prefs[Keys.HAS_ACCEPTED_TERMS] ?: true,
      dailyQuestionsUsed = prefs[Keys.DAILY_QUESTIONS_USED] ?: 0,
      proxyEndpoint = prefs[Keys.PROXY_ENDPOINT] ?: "https://api.familywise.internal/v1/gemini",
      pinLockEnabled = prefs[Keys.PIN_LOCK_ENABLED] ?: false,
      appPin = prefs[Keys.APP_PIN] ?: "1234"
    )
  }

  suspend fun completeOnboarding(
    householdName: String,
    primaryUserName: String,
    primaryUserRelation: String,
    operatingMode: OperatingMode
  ) {
    context.dataStore.edit {
      it[Keys.IS_ONBOARDING_COMPLETED] = true
      it[Keys.HOUSEHOLD_NAME] = householdName.trim()
      it[Keys.PRIMARY_USER_NAME] = primaryUserName.trim()
      it[Keys.PRIMARY_USER_RELATION] = primaryUserRelation.trim()
      it[Keys.OPERATING_MODE] = operatingMode.name
      it[Keys.ONLINE_AI_ENABLED] = (operatingMode == OperatingMode.HYBRID)
    }
  }

  suspend fun resetOnboarding() {
    context.dataStore.edit {
      it[Keys.IS_ONBOARDING_COMPLETED] = false
    }
  }

  suspend fun setOnlineAIEnabled(enabled: Boolean) {
    context.dataStore.edit { it[Keys.ONLINE_AI_ENABLED] = enabled }
  }

  suspend fun setOperatingMode(mode: OperatingMode) {
    context.dataStore.edit { it[Keys.OPERATING_MODE] = mode.name }
  }

  suspend fun setHasAcceptedTerms(accepted: Boolean) {
    context.dataStore.edit { it[Keys.HAS_ACCEPTED_TERMS] = accepted }
  }

  suspend fun incrementQuestionsUsed() {
    context.dataStore.edit { prefs ->
      val current = prefs[Keys.DAILY_QUESTIONS_USED] ?: 0
      prefs[Keys.DAILY_QUESTIONS_USED] = (current + 1).coerceAtMost(50)
    }
  }

  suspend fun resetQuestionsUsed() {
    context.dataStore.edit { it[Keys.DAILY_QUESTIONS_USED] = 0 }
  }

  suspend fun setProxyEndpoint(endpoint: String) {
    context.dataStore.edit { it[Keys.PROXY_ENDPOINT] = endpoint }
  }

  suspend fun setPinLock(enabled: Boolean, pin: String) {
    context.dataStore.edit {
      it[Keys.PIN_LOCK_ENABLED] = enabled
      it[Keys.APP_PIN] = pin
    }
  }
}

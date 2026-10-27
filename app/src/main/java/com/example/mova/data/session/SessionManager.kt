package com.example.mova.data.session

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import androidx.datastore.preferences.core.booleanPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.sessionDataStore by preferencesDataStore(name = "mova_session")

/**
 * Persistent session manager backed by DataStore Preferences.
 *
 * Persists:
 * - the currently logged-in user's ID across app restarts,
 * - onboarding flag,
 * - dark mode toggle
 */
@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val TAG = "SessionManager"
        private val KEY_USER_ID = stringPreferencesKey("user_id")
        private val KEY_ONBOARDED = booleanPreferencesKey("onboarding_complete")
        private val KEY_DARK_MODE = booleanPreferencesKey("dark_mode")
        private val KEY_LARGE_TEXT = booleanPreferencesKey("large_text")
        private val KEY_HIGH_CONTRAST = booleanPreferencesKey("high_contrast")
    }

    // Internal scope for collecting DataStore flows into StateFlows
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Onboarding
    suspend fun isOnboardingComplete(): Boolean {
        return context.sessionDataStore.data.first()[KEY_ONBOARDED] ?: false
    }

    suspend fun setOnboardingComplete(complete: Boolean) {
        Log.d(TAG, "Onboarding complete -> $complete")
        context.sessionDataStore.edit { prefs ->
            prefs[KEY_ONBOARDED] = complete
        }
    }

    /**
     * Flow of the current user ID. Emits null if not logged in.
     * Screens can collect this to react to log in/logout events.
     */
    val userIdFlow: Flow<String?> = context.sessionDataStore.data.map { prefs ->
        prefs[KEY_USER_ID]
    }

    /**
     * One-shot read (suspends until DataStore returns the first value).
     * This is used in ViewModels if the ID is needed immediately.
     */
    suspend fun getUserId(): String? {
        val id = context.sessionDataStore.data.first()[KEY_USER_ID]
        Log.d(TAG, "getUserId() -> $id")
        return id
    }

    /**
     * Persist the user ID. Call this on successful login/registration.
     */
    suspend fun setUserId(userId: String) {
        Log.d(TAG, "Session started for user: $userId")
        context.sessionDataStore.edit { prefs ->
            prefs[KEY_USER_ID] = userId
        }
    }

    /**
     * Clears the session. Call this on logout.
     */
    suspend fun clear() {
        Log.d(TAG, "Session cleared")
        context.sessionDataStore.edit { prefs ->
            prefs.clear() }
    }

    /**
     * Convenience check used by the nav layer to decide start destination.
     */
    suspend fun isLoggedIn(): Boolean = getUserId() != null

    // Dark mode (controlled by user's device preference)
    /**
     * Reacts to dark mode flag,
     * emits current value immediately and on every change
     * */
    private val _darkMode = MutableStateFlow(false)
    val darkMode: StateFlow<Boolean> = _darkMode.asStateFlow()

    // Large Text
    private val _largeText = MutableStateFlow(false)
    val largeText: StateFlow<Boolean> = _largeText.asStateFlow()

    // High Contrast
    private val _highContrast = MutableStateFlow(false)
    val highContrast: StateFlow<Boolean> = _highContrast.asStateFlow()

    init {
        // Primes all the StateFlows with the persisted values when app starts
        scope.launch {
            val prefs = context.sessionDataStore.data.first()

            _darkMode.value = prefs[KEY_DARK_MODE] ?: false
            _largeText.value = prefs[KEY_LARGE_TEXT] ?: false
            _highContrast.value = prefs[KEY_HIGH_CONTRAST] ?: false

            Log.d(TAG, "Prefs loaded -> dark: ${_darkMode.value}, largeText: ${_largeText.value}, highContrast: ${_highContrast.value}")
        }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        Log.d(TAG, "darkMode -> $enabled")
        _darkMode.value = enabled
        context.sessionDataStore.edit { it[KEY_DARK_MODE] = enabled }
    }

    suspend fun setLargeText(enabled: Boolean) {
        Log.d(TAG, "largeText -> $enabled")
        _largeText.value = enabled
        context.sessionDataStore.edit { it[KEY_LARGE_TEXT] = enabled }
    }

    suspend fun setHighContrast(enabled: Boolean) {
        Log.d(TAG, "highContrast -> $enabled")
        _highContrast.value = enabled
        context.sessionDataStore.edit { it[KEY_HIGH_CONTRAST] = enabled }
    }
}
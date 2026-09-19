package com.example.mova.data.session

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import androidx.datastore.preferences.core.booleanPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.sessionDataStore by preferencesDataStore(name = "mova_session")

/**
 * Persistent session manager backed by DataStore Preferences.
 *
 * Persists the currently logged-in user's ID across app restarts.
 */
@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val TAG = "SessionManager"
        private val KEY_USER_ID = stringPreferencesKey("user_id")
        private val KEY_ONBOARDED = booleanPreferencesKey("onboarding_complete")
    }

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
}
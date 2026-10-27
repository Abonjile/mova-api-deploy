/**
 * ChallengesViewModel
 * Holds challenge data for the Challenges screen and performs join and check-in
 * actions. The screen only observes state. Network work happens here, so the UI
 * survives rotation without refetching.
 */
package com.example.mova.ui.screens.challenges

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mova.data.remote.RetrofitClient
import com.example.mova.data.remote.model.ChallengeDto
import com.example.mova.data.remote.model.UserIdRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChallengesViewModel : ViewModel() {

    private val api = RetrofitClient.apiService

    // Private mutable state, exposed as read-only StateFlow. The screen can observe the
    // data but can't change it directly, so all changes go through this class.
    private val _challenges = MutableStateFlow<List<ChallengeDto>>(emptyList())
    val challenges: StateFlow<List<ChallengeDto>> = _challenges.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // A user-facing message. Null means there is no error to show.
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * Loads the user's challenges. Errors are shown on screen, not only in Logcat, so the
     * user can tell an empty list apart from a failed request.
     */
    fun fetchUserChallenges(userId: String) {
        if (userId.isBlank()) return // nothing to fetch until the user is known
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = api.getUserChallenges(userId)
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    _challenges.value = body
                } else {
                    _errorMessage.value = "Couldn't load challenges (HTTP ${response.code()})"
                }
            } catch (e: Exception) {
                // Covers network failures and JSON parse errors. The full exception goes
                // to Logcat for debugging, and the user sees a plain message.
                Log.e(TAG, "Fetch failed", e)
                _errorMessage.value = "Can't reach the server. Check the connection."
            } finally {
                _isLoading.value = false // always reset, even when an error occurred
            }
        }
    }

    /** Joins a challenge, then refreshes the list so the new entry appears. */
    fun joinChallenge(userId: String, challengeId: String) {
        viewModelScope.launch {
            try {
                val response = api.joinChallenge(challengeId, UserIdRequest(userId))
                if (response.isSuccessful) fetchUserChallenges(userId)
                else Log.e(TAG, "Join failed: ${response.code()}")
            } catch (e: Exception) {
                Log.e(TAG, "Join exception", e)
            }
        }
    }

    /**
     * Records today's check-in. The list is refreshed afterwards, so progress is taken
     * from the server rather than calculated on the phone.
     */
    fun checkIn(userId: String, challengeId: String) {
        viewModelScope.launch {
            try {
                val response = api.checkInChallenge(challengeId, UserIdRequest(userId))
                if (response.isSuccessful) fetchUserChallenges(userId)
                else _errorMessage.value = "Check-in failed (HTTP ${response.code()})"
            } catch (e: Exception) {
                _errorMessage.value = "Can't reach the server."
            }
        }
    }

    companion object {
        // A constant tag keeps Logcat filtering consistent.
        private const val TAG = "ChallengesViewModel"
    }
}
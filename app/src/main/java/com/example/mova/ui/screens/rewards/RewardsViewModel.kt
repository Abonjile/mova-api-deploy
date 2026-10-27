/**
 * RewardsViewModel
 * Loads streaks, trophies and claimable rewards, and handles claiming. After a claim the
 * data is reloaded, so the button and points come from the server rather than being
 * changed locally.
 */
package com.example.mova.ui.screens.rewards

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mova.data.remote.RetrofitClient
import com.example.mova.data.remote.model.ClaimRewardRequest
import com.example.mova.data.remote.model.RewardDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RewardsViewModel : ViewModel() {

    // The loaded rewards data. Null until the first successful fetch.
    private val _rewardData = MutableStateFlow<RewardDto?>(null)
    val rewardData: StateFlow<RewardDto?> = _rewardData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * Loads the user's streak, consistency, rewards and trophies. Errors are kept in state
     * so the screen can show them.
     */
    fun fetchRewards(userId: String) {
        if (userId.isBlank()) return
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = RetrofitClient.apiService.getRewards(userId)
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    _rewardData.value = body
                } else {
                    _errorMessage.value = "Failed to load rewards: HTTP ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Network error"
                Log.e(TAG, "Network error: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Claims one reward and refreshes the screen, so the button turns to "Claimed".
     * The server enforces the one-claim rule, so a double tap can't award points twice.
     */
    fun claimReward(userId: String, rewardId: String) {
        if (userId.isBlank() || rewardId.isBlank()) return
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.claimReward(
                    ClaimRewardRequest(userId, rewardId)
                )
                if (response.isSuccessful) {
                    fetchRewards(userId)
                } else {
                    _errorMessage.value = "Failed to claim reward: HTTP ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Network error"
            }
        }
    }

    companion object {
        private const val TAG = "RewardsViewModel"
    }
}
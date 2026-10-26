package com.example.mova.ui.screens.rewards

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mova.data.remote.RetrofitClient
import com.example.mova.data.remote.model.RewardDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for tracking user milestone badges,
 * streak counters, and reward claim statuses.
 */
class RewardsViewModel : ViewModel() {

    // State holder for user rewards and streak details
    private val _rewardsData = MutableStateFlow<RewardDto?>(null)
    val rewardsData: StateFlow<RewardDto?> = _rewardsData.asStateFlow()

    // Loading indicator state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * Fetches user-specific reward milestones and streak data from the backend API.
     *
     * @param userId Unique identifier for the authenticated user profile.
     */
    fun fetchUserRewards(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Retrieving streak count and earned badges from REST endpoint
                val response = RetrofitClient.apiService.getUserRewards(userId)
                if (response.isSuccessful && response.body() != null) {
                    _rewardsData.value = response.body()
                } else {
                    Log.e("RewardsViewModel", "Failed to retrieve rewards: ${response.errorBody()}")
                }
            } catch (e: Exception) {
                Log.e("RewardsViewModel", "Network error: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
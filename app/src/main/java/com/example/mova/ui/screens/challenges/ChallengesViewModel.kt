package com.example.mova.ui.screens.challenges

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mova.data.remote.RetrofitClient
import com.example.mova.data.remote.model.ChallengeDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing active fitness challenges state,
 * fetching backend challenge programs, and handling user progress updates.
 */
class ChallengesViewModel : ViewModel() {

    // UI state holder for the list of active challenges
    private val _challenges = MutableStateFlow<List<ChallengeDto>>(emptyList())
    val challenges: StateFlow<List<ChallengeDto>> = _challenges.asStateFlow()

    // Loading indicator state for network operations
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        // Fetch active challenges automatically when the ViewModel initializes
        fetchActiveChallenges()
    }

    /**
     * Calls the REST API service via Retrofit to retrieve active challenges
     * for the current user profile.
     */
    fun fetchActiveChallenges() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Fetching active 7, 10, or 30-day challenge records from backend
                val response = RetrofitClient.apiService.getActiveChallenges()
                if (response.isSuccessful && response.body() != null) {
                    _challenges.value = response.body()!!
                } else {
                    Log.e("ChallengesViewModel", "Failed to fetch challenges: ${response.errorBody()}")
                }
            } catch (e: Exception) {
                Log.e("ChallengesViewModel", "Network exception occurred: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
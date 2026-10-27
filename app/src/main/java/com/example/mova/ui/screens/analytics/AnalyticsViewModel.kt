/**
 * AnalyticsViewModel
 * Loads analytics for the selected period and saves goal edits. The current period is
 * tracked here, so a goal update always targets the period the user is looking at.
 */
package com.example.mova.ui.screens.analytics

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mova.data.remote.RetrofitClient
import com.example.mova.data.remote.model.AnalyticsDto
import com.example.mova.data.remote.model.GoalUpdateRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AnalyticsViewModel : ViewModel() {

    private val api = RetrofitClient.apiService

    private val _analyticsData = MutableStateFlow<AnalyticsDto?>(null)
    val analyticsData: StateFlow<AnalyticsDto?> = _analyticsData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // The period the screen is showing. Updates and reloads read this value, so they can't
    // accidentally target a different period.
    private val _currentPeriod = MutableStateFlow("Weekly")
    val currentPeriod: StateFlow<String> = _currentPeriod.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /** Loads analytics for a period, and records that period as the current one. */
    fun loadAnalytics(userId: String, period: String = "Weekly") {
        if (userId.isBlank()) return
        _currentPeriod.value = period
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = api.getAnalytics(userId, period)
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    _analyticsData.value = body
                } else {
                    _errorMessage.value = "Couldn't load analytics (HTTP ${response.code()})"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Analytics fetch failed", e)
                _errorMessage.value = "Can't reach the server."
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Saves the goal for the current period, then reloads. Validation runs before the request
     * is sent, so the user gets an immediate message instead of waiting on a 400 response.
     */
    fun updateFullGoal(userId: String, goalTitle: String, targetCalories: Int) {
        if (userId.isBlank()) return
        val title = goalTitle.trim()
        if (title.isEmpty() || targetCalories <= 0) {
            _errorMessage.value = "A goal needs a title and a calorie target above 0."
            return
        }

        val period = _currentPeriod.value
        viewModelScope.launch {
            try {
                val response = api.updateAnalyticsGoal(
                    userId, period, GoalUpdateRequest(title, targetCalories)
                )
                if (response.isSuccessful) {
                    // Reload, so the card shows the saved values rather than what was typed.
                    loadAnalytics(userId, period)
                } else {
                    _errorMessage.value = "Couldn't save goal (HTTP ${response.code()})"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Goal update failed", e)
                _errorMessage.value = "Can't reach the server."
            }
        }
    }

    companion object {
        private const val TAG = "AnalyticsViewModel"
    }
}
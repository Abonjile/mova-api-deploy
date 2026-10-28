/**
 * =====================================================================================
 * VIEWMODEL: AnalyticsViewModel
 * =====================================================================================
 * Loads analytics for the selected period and saves goal changes. Errors are sent as an
 * enum, so the screen can translate them.
 * =====================================================================================
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
import java.io.IOException

/** Problems the screen can show. Each one maps to a string in strings.xml. */
enum class AnalyticsError { NETWORK, LOAD_FAILED, SAVE_FAILED, INVALID_GOAL }

class AnalyticsViewModel : ViewModel() {

    private val api = RetrofitClient.apiService

    private val _analyticsData = MutableStateFlow<AnalyticsDto?>(null)
    val analyticsData: StateFlow<AnalyticsDto?> = _analyticsData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // The period on screen. Goal saves read this, so they always hit the right period.
    private val _currentPeriod = MutableStateFlow("Weekly")
    val currentPeriod: StateFlow<String> = _currentPeriod.asStateFlow()

    private val _error = MutableStateFlow<AnalyticsError?>(null)
    val error: StateFlow<AnalyticsError?> = _error.asStateFlow()

    fun loadAnalytics(userId: String, period: String = "Weekly") {
        if (userId.isBlank()) return
        _currentPeriod.value = period
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = api.getAnalytics(userId, period)
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    _analyticsData.value = body
                } else {
                    _error.value = AnalyticsError.LOAD_FAILED
                }
            } catch (e: IOException) {
                _error.value = AnalyticsError.NETWORK
            } catch (e: Exception) {
                Log.e(TAG, "Analytics fetch failed", e)
                _error.value = AnalyticsError.LOAD_FAILED
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Saves the goal for the period on screen, then reloads so the card shows saved values. */
    fun updateFullGoal(userId: String, goalTitle: String, targetCalories: Int) {
        if (userId.isBlank()) return
        val title = goalTitle.trim()
        if (title.isEmpty() || targetCalories <= 0) {
            _error.value = AnalyticsError.INVALID_GOAL
            return
        }

        val period = _currentPeriod.value
        viewModelScope.launch {
            try {
                val response = api.updateAnalyticsGoal(
                    userId, period, GoalUpdateRequest(title, targetCalories)
                )
                if (response.isSuccessful) {
                    loadAnalytics(userId, period)
                } else {
                    _error.value = AnalyticsError.SAVE_FAILED
                }
            } catch (e: IOException) {
                _error.value = AnalyticsError.NETWORK
            } catch (e: Exception) {
                Log.e(TAG, "Goal update failed", e)
                _error.value = AnalyticsError.SAVE_FAILED
            }
        }
    }

    companion object {
        private const val TAG = "AnalyticsViewModel"
    }
}
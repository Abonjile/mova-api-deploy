package com.example.mova.ui.screens.analytics

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mova.data.remote.RetrofitClient
import com.example.mova.data.remote.model.AnalyticsDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel managing user performance metrics, calorie progress tracking,
 * and trend graphs for weekly or monthly intervals.
 */
class AnalyticsViewModel : ViewModel() {

    // State holder for analytics metrics and performance trends
    private val _analyticsData = MutableStateFlow<AnalyticsDto?>(null)
    val analyticsData: StateFlow<AnalyticsDto?> = _analyticsData.asStateFlow()

    // Loading state for asynchronous API calls
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * Fetches user analytics data based on the selected period filter (Weekly or Monthly).
     *
     * @param period Filter criteria: "Weekly" or "Monthly"
     */
    fun loadAnalytics(period: String = "Weekly") {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Requesting calories burned and training volume distributions
                val response = RetrofitClient.apiService.getAnalytics(period)
                if (response.isSuccessful && response.body() != null) {
                    _analyticsData.value = response.body()
                } else {
                    Log.e("AnalyticsViewModel", "Error loading analytics: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("AnalyticsViewModel", "Exception during analytics fetch: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
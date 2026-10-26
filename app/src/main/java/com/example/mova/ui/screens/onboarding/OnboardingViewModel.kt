package com.example.mova.ui.screens.onboarding

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mova.data.models.User
import com.example.mova.data.repository.UserRepository
import com.example.mova.data.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Wraps the onboarding save flow:
 *  1. Persist the updated profile to the API (unless skipping)
 *  2. Mark onboarding complete in DataStore
 *  3. Emit `saved = true` only after both succeed, so the screen can navigate safely
 */
@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    companion object {
        private const val TAG = "OnboardingViewModel"
    }

    data class UiState(
        val isSaving: Boolean = false,
        val error: String? = null,
        val saved: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    /**
     * Save onboarding data, then mark onboarding complete.
     * On success, sets `saved = true`; the screen's LaunchedEffect will navigate.
     */
    fun save(user: User, skip: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }

            val userId = sessionManager.getUserId()
            if (userId == null) {
                _uiState.update { it.copy(isSaving = false, error = "Session expired") }
                return@launch
            }

            if (skip) {
                // No profile fields to save; just mark onboarding as complete
                Log.d(TAG, "Skipping onboarding — marking complete")
                sessionManager.setOnboardingComplete(true)
                _uiState.update { it.copy(isSaving = false, saved = true) }
                return@launch
            }

            userRepository.updateUserProfile(userId, user)
                .onSuccess {
                    Log.d(TAG, "Onboarding data saved")
                    sessionManager.setOnboardingComplete(true)
                    _uiState.update { it.copy(isSaving = false, saved = true) }
                }
                .onFailure { e ->
                    Log.e(TAG, "Failed to save onboarding", e)
                    _uiState.update {
                        it.copy(isSaving = false, error = e.message ?: "Save failed")
                    }
                }
        }
    }
}
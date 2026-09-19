package com.example.mova.ui.screens.profile

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

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    companion object {
        private const val TAG = "UserViewModel"
    }

    // UI State
    data class UiState(
        val isLoading: Boolean = false,
        val user: User? = null,
        val error: String? = null,
        val isSaving: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        Log.d(TAG, "UserViewModel initialized")
        loadUser()
    }

    /**
     * Load user from repository
     */
    fun loadUser() {
        viewModelScope.launch {
            val userId = sessionManager.getUserId()
            if (userId == null) {
                Log.w(TAG, "No active session — cannot load user")
                _uiState.update { it.copy(isLoading = false, error = "Not logged in") }
                return@launch
            }

            Log.d(TAG, "Loading user...")
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val user = userRepository.getUser(userId)
                if (user != null) {
                    Log.d(TAG, "User loaded: ${user.fullName}")
                    _uiState.update { it.copy(isLoading = false, user = user) }
                } else {
                    Log.w(TAG, "User not found")
                    _uiState.update {
                        it.copy(isLoading = false, error = "User not found")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading user: ${e.message}")
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Unknown error")
                }
            }
        }
    }

    /**
     * Update user profile
     */
    fun updateUserProfile(user: User) {
        viewModelScope.launch {
            val userId = sessionManager.getUserId() ?: run {
                Log.w(TAG, "Cannot update — no session")
                return@launch
            }
            Log.d(TAG, "Updating user: ${user.fullName}")
            _uiState.update { it.copy(isSaving = true, error = null) }

            val result = userRepository.updateUserProfile(userId, user)

            result.onSuccess { updatedUser ->
                Log.d(TAG, "User updated successfully")
                _uiState.update {
                    it.copy(isSaving = false, user = updatedUser)
                }
            }.onFailure { error ->
                Log.e(TAG, "Failed to update user: ${error.message}")
                _uiState.update {
                    it.copy(isSaving = false, error = error.message)
                }
            }
        }
    }

    /**
     * Update user settings
     */
    fun updateSettings(
        language: String,
        notificationsEnabled: Boolean,
        biometricEnabled: Boolean
    ) {
        viewModelScope.launch {
            val userId = sessionManager.getUserId() ?: run {
                Log.w(TAG, "Cannot update settings — no session")
                return@launch
            }

            Log.d(TAG, "Updating settings: language=$language")

            userRepository.updateSettings(userId, language, notificationsEnabled, biometricEnabled)
                .onSuccess { updatedUser ->
                    Log.d(TAG, "Settings updated successfully")
                    _uiState.update { it.copy(user = updatedUser) }
                }
                .onFailure { error ->
                    Log.e(TAG, "Failed to update settings: ${error.message}")
                    _uiState.update { it.copy(error = error.message) }
                }
        }
    }

    /**
     * Sync offline data
     */
    fun syncData() {
        viewModelScope.launch {
            val userId = sessionManager.getUserId() ?: return@launch
            Log.d(TAG, "Syncing data...")
            userRepository.syncOfflineData(userId)
            loadUser() // Refresh after sync
        }
    }
}
package com.example.mova.ui.screens.authentication

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mova.data.repository.AuthRepository
import com.example.mova.data.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Shared ViewModel for Login and Register screens.
 * Both screens use the same UiState shape and resetState() call.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    companion object {
        private const val TAG = "AuthViewModel"
    }

    /**
     * UI state for both auth screens.
     *  - isLoading: shows spinner on Continue button
     *  - error: shows inline error message
     *  - success: triggers navigation via LaunchedEffect
     *  - isNewRegistration true if success came from registration (route to onboarding),
     *      false if from login (route straight to main app)
     */
    data class UiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val success: Boolean = false,
        val isNewRegistration: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // EMAIL / PASSWORD

    /**
     * Register a new account.
     * Called from RegisterScreen on Continue tap (after validation passes).
     */
    fun register(fullName: String, email: String, password: String) {
        viewModelScope.launch {
            Log.d(TAG, "register() called for: $email")
            _uiState.update { it.copy(isLoading = true, error = null, success = false) }

            authRepository.register(fullName, email, password)
                .onSuccess { response ->
                    val userId = response.user?.userId
                    // A successful HTTP response without a userId,
                    // is treated as a failure since we cannot establish a session.
                    if (userId == null) {
                        Log.e(TAG, "Registration succeeded but no userId returned")
                        _uiState.update {
                            it.copy(isLoading = false, error = "Registration response missing userId")
                        }
                        return@onSuccess
                    }

                    sessionManager.setUserId(userId)
                    sessionManager.setOnboardingComplete(false)
                    Log.d(TAG, "Registration success, session started for $userId")

                    // Mark as NEW registration so NavGraph routes to onboarding
                    _uiState.update {
                        it.copy(isLoading = false, success = true, isNewRegistration = true)
                    }
                }
                .onFailure { e ->
                    Log.e(TAG, "Registration failed", e)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Registration failed. Please try again."
                        )
                    }
                }
        }
    }

    /**
     * Login with email + password.
     *
     * On success:
     *      *  - Persists the returned userId in SessionManager
     *      *  - Emits UiState with success=true and isNewRegistration=false
     *      *    so the NavGraph routes directly to the main app (skipping onboarding).
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            Log.d(TAG, "login() called for: $email")
            // Reset error/success flags before starting a new request.
            _uiState.update { it.copy(isLoading = true, error = null, success = false) }

            authRepository.login(email, password)
                .onSuccess { response ->
                    val userId = response.user?.userId
                    // Same defensive check as register(): no userId → cannot start session.
                    if (userId == null) {
                        Log.e(TAG, "Login succeeded but no userId returned")
                        _uiState.update {
                            it.copy(isLoading = false, error = "Login response missing userId")
                        }
                        return@onSuccess
                    }

                    sessionManager.setUserId(userId)
                    Log.d(TAG, "Login success, session started for $userId")

                    // Existing user: go straight to main app (not onboarding)
                    _uiState.update {
                        it.copy(isLoading = false, success = true, isNewRegistration = false)
                    }
                }
                .onFailure { e ->
                    Log.e(TAG, "Login failed", e)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Login failed. Check your credentials."
                        )
                    }
                }
        }
    }


    // SSO STUBS
    // TODO: Wire real Google / Apple / Biometric flows in a future phase.

    fun signInWithGoogle() {
        Log.d(TAG, "Google SSO requested (not yet implemented)")
        // TODO: Launch Credential Manager → get idToken → authRepository.signInWithGoogle(idToken)
        _uiState.update { it.copy(error = "Google sign-in coming soon") }
    }

    fun signInWithApple() {
        Log.d(TAG, "Apple SSO requested (not yet implemented)")
        // TODO: Launch Apple Sign-In → get identityToken → authRepository.signInWithApple(identityToken)
        _uiState.update { it.copy(error = "Apple sign-in coming soon") }
    }

    fun signInWithBiometric() {
        Log.d(TAG, "Biometric auth requested (not yet implemented)")
        // TODO: Use BiometricPrompt → on success, restore cached session
        _uiState.update { it.copy(error = "Biometric sign-in coming soon") }
    }

    // UTILITY

    /**
     * Clears loading/error/success.
     *
     * Call this after a navigation event,
     * so returning to the screen doesn't immediately re-trigger the effect.
     */
    fun resetState() {
        Log.d(TAG, "resetState()")
        _uiState.update { UiState() }
    }
}
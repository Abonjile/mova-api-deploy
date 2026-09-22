package com.example.mova.ui.screens.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mova.data.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {

    // -1 / null state until we read from DataStore
    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId.asStateFlow()

    init {
        viewModelScope.launch {
            _userId.value = sessionManager.getUserId()
        }
    }

    /**
     * Fallback getter for one-shot use (e.g. inside onClick lambdas).
     * Returns "test_user_123" only if there's truly no session. This keeps
     * the feature working in preview / dev.
     */
    suspend fun requireUserId(): String =
        sessionManager.getUserId() ?: "test_user_123"
}
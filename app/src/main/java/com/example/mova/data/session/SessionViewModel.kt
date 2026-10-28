package com.example.mova.ui.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mova.data.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * =====================================================================================
 * VIEWMODEL: SessionViewModel
 * =====================================================================================
 * Exposes the logged-in user's ID as a StateFlow. It updates automatically when the
 * session changes, so screens always use the current account.
 * =====================================================================================
 */
@HiltViewModel
class SessionViewModel @Inject constructor(
    sessionManager: SessionManager
) : ViewModel() {

    // null until the DataStore value loads, or when nobody is logged in.
    val userId: StateFlow<String?> = sessionManager.userIdFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )
}
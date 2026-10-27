package com.example.mova.ui.screens.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * =====================================================================================
 * VIEWMODEL: LanguageViewModel
 * =====================================================================================
 * Manages the current language selection state and triggers runtime locale updates.
 * =====================================================================================
 */
class LanguageViewModel : ViewModel() {

    private val _currentLanguage = MutableStateFlow(LanguageManager.getCurrentLocaleTag())
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    /**
     * Updates the application language and persists state via AppCompatDelegate.
     *
     * @param languageCode The target language code ("en", "zu", or "st").
     */
    fun changeLanguage(languageCode: String) {
        if (languageCode.isBlank()) return
        LanguageManager.setLocale(languageCode)
        _currentLanguage.value = languageCode
    }
}
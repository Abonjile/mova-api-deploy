package com.example.mova.ui.screens.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * VIEWMODEL: LanguageViewModel
 * Holds which language the radio buttons show as selected, and applies a choice.
 * The actual translation happens in the resources, not here.
 */
class LanguageViewModel : ViewModel() {

    private val _selected = MutableStateFlow(LanguageManager.currentSelection())
    val selected: StateFlow<String> = _selected.asStateFlow()

    /** Applies the chosen language. Android then recreates the activity with the new resources. */
    fun select(tag: String) {
        if (tag == _selected.value) return
        _selected.value = tag
        LanguageManager.setLanguage(tag)
    }
}
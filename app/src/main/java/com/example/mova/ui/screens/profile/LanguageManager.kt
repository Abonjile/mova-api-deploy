package com.example.mova.ui.screens.profile

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

/**
 * =====================================================================================
 * UTILITY: LanguageManager
 * =====================================================================================
 * Handles runtime locale switching for the application using AppCompatDelegate
 * supporting English ("en"), isiZulu ("zu"), and Sesotho ("st").
 * =====================================================================================
 */
object LanguageManager {

    /**
     * Sets the application locale dynamically at runtime and triggers recomposition.
     *
     * @param languageCode The ISO language tag ("en", "zu", or "st").
     */
    fun setLocale(languageCode: String) {
        if (languageCode.isBlank()) return
        val appLocale = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

    /**
     * Gets the current active application locale tag.
     */
    fun getCurrentLocaleTag(): String {
        val locales = AppCompatDelegate.getApplicationLocales()
        return if (!locales.isEmpty) {
            locales.get(0)?.language ?: "en"
        } else {
            "en"
        }
    }
}
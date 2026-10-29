package com.example.mova.ui.screens.profile

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

/**
 * =====================================================================================
 * UTILITY: LanguageManager
 * =====================================================================================
 * Switches the app's language using AppCompat. Android loads the matching
 * values-<code> resources automatically, and it stores the choice so it survives a
 * restart.
 * =====================================================================================
 */
object LanguageManager {

    // Language tags that match the folders in res/ and locales_config.xml.
    const val ENGLISH = "en"
    const val ZULU = "zu"
    const val SESOTHO = "st"

    // Special value meaning "follow the device language".
    const val SYSTEM = "system"

    /**
     * Sets the app language. An empty locale list tells AppCompat to stop overriding
     * the language and return control to the device.
     */
    fun setLanguage(tag: String) {
        val locales = if (tag == SYSTEM) {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(tag)
        }
        AppCompatDelegate.setApplicationLocales(locales)
    }

    /**
     * Returns the language the user chose, or SYSTEM if no app language is set.
     * Used to show the correct radio button when the screen opens.
     */
    fun currentSelection(): String {
        val locales = AppCompatDelegate.getApplicationLocales()
        return if (locales.isEmpty) SYSTEM else locales.get(0)?.language ?: SYSTEM
    }
}
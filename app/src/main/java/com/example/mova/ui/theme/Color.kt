package com.example.mova.ui.theme

import androidx.compose.ui.graphics.Color

// MOVA BRAND PALETTE
// Values aligned with design mockups from Part 1

// --- Primary brand: navy blue (used for headers, primary text) ---
val Primary = Color(0xFF1E3A8A)            // MOVA Navy
val PrimaryDark = Color(0xFF152C6A)         // Darker navy for pressed states
val PrimaryLight = Color(0xFF5BA4F5)        // Light blue (buttons in auth mockup)

// --- Secondary brand: teal (used for accents, success states) ---
val Secondary = Color(0xFF2DD4BF)           // MOVA Teal
val SecondaryDark = Color(0xFF14B8A6)
val SecondaryLight = Color(0xFF5EEAD4)

// --- Backgrounds ---
val Background = Color(0xFFF8FAFC)          // Off-white
val BackgroundDark = Color(0xFF0F0F1A)
val Surface = Color(0xFFFFFFFF)
val SurfaceVariant = Color(0xFFF1F5F9)      // Light gray (cards)

// --- Text ---
val TextPrimary = Color(0xFF0F172A)         // Near-black
val TextSecondary = Color(0xFF64748B)       // Gray
val TextLight = Color(0xFFFFFFFF)
val TextDark = Color(0xFF1F2937)            // Used in auth screens
val TextGray = Color(0xFF6B7280)            // Used in auth screens

// --- Accent & Status ---
val AccentGreen = Color(0xFF10B981)
val AccentRed = Color(0xFFEF4444)
val AccentYellow = Color(0xFFF59E0B)

// BRAND ALIASES
// Screens can use either the semantic name (Primary) or the
// brand name (MOVA Blue). These point to the same values.

val MovaBlue = Primary            // 0xFF1E3A8A
val MovaTeal = Secondary          // 0xFF2DD4BF
val MovaOffWhite = Background     // 0xFFF8FAF
val MovaGray = TextSecondary      // 0xFF64748B
val MovaLightGray = SurfaceVariant // 0xFFF1F5F9
val MovaBlack = TextPrimary       // 0xFF0F172A
val MovaBlueLight = PrimaryLight  // 0xFF5BA4F5 — for auth buttons

// Error color for form validation
val MovaError = AccentRed
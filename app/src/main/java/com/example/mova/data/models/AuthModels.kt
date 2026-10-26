package com.example.mova.data.models

/**
 * Request body for POST /api/auth/register and POST /api/auth/login.
 *
 * The same class handles both endpoints because the backend accepts
 * a superset of fields. For SSO, `ssoProvider` and `ssoProviderId`
 * are populated instead of `password`.
 */
data class AuthRequest(
    val fullName: String? = null,
    val email: String,
    val password: String? = null,
    val ssoProvider: String? = null,       // "GOOGLE" | "APPLE" | null
    val ssoProviderId: String? = null      // Provider-specific user ID
)

/**
 * Response from /api/auth/register and /api/auth/login.
 * Matches the backend's current response shape:
 *   { "message": "...", "user": { ... } }
 *
 * `token` is reserved for future JWT support.
 */
data class AuthResponse(
    val message: String,
    val user: User? = null,
    val token: String? = null
)

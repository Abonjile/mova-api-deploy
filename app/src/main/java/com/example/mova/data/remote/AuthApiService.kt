package com.example.mova.data.remote

import com.example.mova.data.models.AuthRequest
import com.example.mova.data.models.AuthResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    /**
     * Register a new user.
     * Backend route: POST /api/auth/register
     * Backend handler: src/routes/authRoutes.js (router.post('/register', ...))
     */
    @POST("api/auth/register")
    suspend fun register(@Body request: AuthRequest): AuthResponse

    /**
     * Login an existing user.
     * Backend route: POST /api/auth/login
     * Backend handler: src/routes/authRoutes.js (router.post('/login', ...))
     */
    @POST("api/auth/login")
    suspend fun login(@Body request: AuthRequest): AuthResponse
}
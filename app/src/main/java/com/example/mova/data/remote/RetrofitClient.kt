package com.example.mova.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton object responsible for initializing and providing the Retrofit network client.
 */
object RetrofitClient {
    // Base URL for the backend API (update to your local emulator IP or production server URL)
    private const val BASE_URL = "http://10.0.2.2:3000"

    /**
     * Lazy-initialized Retrofit API service instance.
     */
    val apiService: MovaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MovaApiService::class.java)
    }
}

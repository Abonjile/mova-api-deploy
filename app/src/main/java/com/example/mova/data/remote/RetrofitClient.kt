package com.example.mova.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Creates and manages the Retrofit client used to communicate with the server
object RetrofitClient {

    // TODO: Replace with actual server URL
    private const val BASE_URL = "http://10.0.2.2:3000/" // Android emulator localhost

    // Intercepts HTTP requests and reponses so they can be viewed in Logcat
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        // Logs the full request and response body for debugging purposes
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Configures the HTTP client used by Retrofit
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor) // adds the logging interceptor to monitor network requests
        .connectTimeout(30, TimeUnit.SECONDS) // maximum time allowed to establish a connection with the server
        .readTimeout(30, TimeUnit.SECONDS) // maximum time allowed to receive data from the server
        .writeTimeout(30, TimeUnit.SECONDS) // maximum time allowed to send data to the server
        .build() // Creates the configured OkHttp client

    // Configures Retrofit to communicate with the backend API
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL) // Sets the base URL used for all API requests.
        .client(okHttpClient) // Tells Retrofit to use the configured OKhttp client
        .addConverterFactory(GsonConverterFactory.create()) // Converts JSON responses from the server into Kotlin objects
        .build() // Creates the Retrofit instance

    // Creates the UserApiService only when it is 1st needed
    val userApiService: UserApiService by lazy {
        retrofit.create(UserApiService::class.java)
    }
}
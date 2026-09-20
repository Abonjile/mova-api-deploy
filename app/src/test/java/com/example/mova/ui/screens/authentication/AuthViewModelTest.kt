package com.example.mova.ui.screens.authentication

import com.example.mova.data.models.AuthResponse
import com.example.mova.data.models.User
import com.example.mova.data.repository.AuthRepository
import com.example.mova.data.session.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Before
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.assertNull
import org.junit.Assert.assertNotNull
import io.mockk.mockk
import io.mockk.coEvery
import io.mockk.coVerify
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    // The curoutines executes after advanceUntilIdle() is called
    private val dispatcher = StandardTestDispatcher()
    private lateinit var authRepository: AuthRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        // Restore Main so other test classes are unaffected
        Dispatchers.setMain(dispatcher)
        authRepository = mockk()
        sessionManager = mockk(relaxed = true)
        viewModel = AuthViewModel(authRepository, sessionManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `register success emits success and isNewRegistration`() = runTest(dispatcher) {
        val response = AuthResponse(
            message = "ok",
            user = User(userId = "usr_1", fullName = "Test", email = "t@t.com")
        )
        coEvery { authRepository.register(any(), any(), any()) } returns Result.success(response)

        viewModel.register("Test", "t@t.com", "password123")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.success)
        // New sign-ups must be flagged so the UI can route to onboarding
        assertTrue(state.isNewRegistration)
        assertFalse(state.isLoading)
        // The session must be populated with the returned user id
        coVerify { sessionManager.setUserId("usr_1") }
        // New users start onboarding as incomplete
        coVerify { sessionManager.setOnboardingComplete(false) }
    }

    @Test
    fun `register failure emits error and keeps success false`() = runTest(dispatcher) {
        coEvery { authRepository.register(any(), any(), any()) } returns
                Result.failure(Exception("Email already exists"))

        viewModel.register("Test", "t@t.com", "password123")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.success)
        // The error message from the exception is propagated to the UI state
        assertEquals("Email already exists", state.error)
    }

    @Test
    fun `login success sets isNewRegistration to false`() = runTest(dispatcher) {
        val response = AuthResponse(
            message = "ok",
            user = User(userId = "usr_1", fullName = "Test", email = "t@t.com")
        )
        coEvery { authRepository.login(any(), any()) } returns Result.success(response)

        viewModel.login("t@t.com", "password123")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.success)
        // Existing users skip the onboarding flow
        assertFalse(state.isNewRegistration)
    }

    @Test
    fun `login with missing userId surfaces error`() = runTest(dispatcher) {
        // Backend is simulated and returns success, but without a user payload
        coEvery { authRepository.login(any(), any()) } returns
                Result.success(AuthResponse(message = "ok", user = null))

        viewModel.login("t@t.com", "password123")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        // If user id is missing it will be treated as a failure
        assertFalse(state.success)
        assertNotNull(state.error)
    }

    @Test
    fun `resetState clears all flags`() = runTest(dispatcher) {
        viewModel.resetState()
        val state = viewModel.uiState.value
        // resetState should return the UI state to a clean baseline
        assertFalse(state.success)
        assertNull(state.error)
        assertFalse(state.isLoading)
    }
}
package com.example.mova.ui.screens.authentication

import com.example.mova.MainDispatcherRule
import com.example.mova.data.models.AuthResponse
import com.example.mova.data.models.User
import com.example.mova.data.repository.AuthRepository
import com.example.mova.data.session.SessionManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    // Replaces the main dispatcher with a test dispatcher so coroutines run deterministically
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var authRepository: AuthRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        // Creates a strict mock for the auth repository where unstubbed calls fail
        authRepository = mockk()
        // creates a relaxed mock for the session manager, unstubbed calls returns defaults
        sessionManager = mockk(relaxed = true)
        // Instantiates the ViewModel under test with the mocked dependencies
        viewModel = AuthViewModel(authRepository, sessionManager)
    }

    @Test
    fun `register success emits success and isNewRegistration`() = runTest(mainDispatcherRule.testScheduler) {
        val response = AuthResponse(
            message = "ok",
            user = User(userId = "usr_1", fullName = "Test", email = "t@t.com")
        )
        // Stubs the repository to return a successful registration response
        coEvery { authRepository.register(any(), any(), any()) } returns Result.success(response)

        viewModel.register("Test", "t@t.com", "password123")
        // waits for all coroutines launched by register to complete
        advanceUntilIdle()

        val state = viewModel.uiState.value
        // Verifies that the success flag was set
        assertTrue(state.success)
        // Verifies that the flow marks this as a new registration
        assertTrue(state.isNewRegistration)
        // Verifies that loading has finished
        assertFalse(state.isLoading)
        // Verifies that the user ID was persisted to the session
        coVerify { sessionManager.setUserId("usr_1") }
        // Verifies that onboarding is marked incomplete for new registrations
        coVerify { sessionManager.setOnboardingComplete(false) }
    }

    @Test
    fun `register failure emits error`() = runTest(mainDispatcherRule.testScheduler) {
        // Simulates a registration failure, for example a duplicate email
        coEvery { authRepository.register(any(), any(), any()) } returns
                Result.failure(Exception("Email already exists"))

        viewModel.register("Test", "t@t.com", "password123")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        // Verifies that success was not set on failure
        assertFalse(state.success)
        // Verifies that the error message is surfaced from the exception
        assertEquals("Email already exists", state.error)
    }

    @Test
    fun `login success sets isNewRegistration to false`() = runTest(mainDispatcherRule.testScheduler) {
        val response = AuthResponse(
            message = "ok",
            user = User(userId = "usr_1", fullName = "Test", email = "t@t.com")
        )
        // Stubs the repository to return a successful login response
        coEvery { authRepository.login(any(), any()) } returns Result.success(response)

        viewModel.login("t@t.com", "password123")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        // Verifies that the success flag was set
        assertTrue(state.success)
        // Verifies that login is not treated as a new registration
        assertFalse(state.isNewRegistration)
    }

    @Test
    fun `login with missing userId surfaces error`() = runTest(mainDispatcherRule.testScheduler) {
        // Simulates a login response that is missing the user object
        coEvery { authRepository.login(any(), any()) } returns
                Result.success(AuthResponse(message = "ok", user = null))

        viewModel.login("t@t.com", "password123")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        // Verifies that success was not set when userId is missing
        assertFalse(state.success)
        // Verifies that an error was surfaced to the UI
        assertNotNull(state.error)
    }

    @Test
    fun `resetState clears all flags`() = runTest(mainDispatcherRule.testScheduler) {
        viewModel.resetState()
        val state = viewModel.uiState.value
        // Verifies that all state flags and error are cleared back to defaults
        assertFalse(state.success)
        assertNull(state.error)
        assertFalse(state.isLoading)
    }
}
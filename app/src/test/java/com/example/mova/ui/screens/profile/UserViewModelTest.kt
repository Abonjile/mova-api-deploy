package com.example.mova.ui.screens.profile

import com.example.mova.MainDispatcherRule
import com.example.mova.data.models.User
import com.example.mova.data.repository.UserRepository
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
class UserViewModelTest {

    // Replaces the main dispatcher with a test dispatcher so coroutines run deterministically
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var userRepository: UserRepository
    private lateinit var sessionManager: SessionManager

    @Before
    fun setup() {
        // Creates a strict mock for the repository, where unstubbed calls will fail
        userRepository = mockk()
        // Creates a relaxed mock for the session manager, unstubbed calls will return defaults
        sessionManager = mockk(relaxed = true)
    }

    @Test
    fun `init loads user from session`() = runTest {
        val user = User(userId = "usr_1", fullName = "Rita", email = "r@mova.com")
        // stubs the session to return a logged-in user ID
        coEvery { sessionManager.getUserId() } returns "usr_1"
        // stubs the repository to return the user for that ID
        coEvery { userRepository.getUser("usr_1") } returns user

        val viewModel = UserViewModel(userRepository, sessionManager)
        // waits for all coroutines launched during ViewModel init to complete
        advanceUntilIdle()

        // Verifies that the UI state was populated with the loaded user
        assertEquals("Rita", viewModel.uiState.value.user?.fullName)
        // Verifies that loading has finished
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `loadUser with no session surfaces error`() = runTest {
        // simulates no active session
        coEvery { sessionManager.getUserId() } returns null

        val viewModel = UserViewModel(userRepository, sessionManager)
        advanceUntilIdle()

        // Verifies that the correct error message is shown when not logged in
        assertEquals("Not logged in", viewModel.uiState.value.error)
    }

    @Test
    fun `addObjective appends and calls update`() = runTest {
        val user = User(userId = "usr_1", fullName = "Rita", objectives = emptyList())
        coEvery { sessionManager.getUserId() } returns "usr_1"
        coEvery { userRepository.getUser("usr_1") } returns user
        // stubs the update to return the user with the new objective appended
        coEvery { userRepository.updateUserProfile(any(), any()) } returns Result.success(
            user.copy(objectives = listOf("Burn 500 cal"))
        )

        val viewModel = UserViewModel(userRepository, sessionManager)
        advanceUntilIdle()

        viewModel.addObjective("Burn 500 cal")
        advanceUntilIdle()

        // Verifies that the repository update was called with the correct user ID
        coVerify { userRepository.updateUserProfile("usr_1", any()) }
    }

    @Test
    fun `addObjective ignores blank strings`() = runTest {
        val user = User(userId = "usr_1", objectives = emptyList())
        coEvery { sessionManager.getUserId() } returns "usr_1"
        coEvery { userRepository.getUser("usr_1") } returns user

        val viewModel = UserViewModel(userRepository, sessionManager)
        advanceUntilIdle()

        // attempts to add a blank objective, which should be ignored
        viewModel.addObjective("   ")
        advanceUntilIdle()

        // Verifies that no repository update was triggered for blank input
        coVerify(exactly = 0) { userRepository.updateUserProfile(any(), any()) }
    }

    @Test
    fun `removeObjective removes existing`() = runTest {
        val user = User(userId = "usr_1", objectives = listOf("A", "B"))
        coEvery { sessionManager.getUserId() } returns "usr_1"
        coEvery { userRepository.getUser("usr_1") } returns user
        // stubs the update to return the user with 'B' removed
        coEvery { userRepository.updateUserProfile(any(), any()) } returns Result.success(
            user.copy(objectives = listOf("A"))
        )

        val viewModel = UserViewModel(userRepository, sessionManager)
        advanceUntilIdle()

        viewModel.removeObjective("B")
        advanceUntilIdle()

        // Verifies that the repository update was called with the correct user ID
        coVerify { userRepository.updateUserProfile("usr_1", any()) }
    }

    @Test
    fun `logout clears session and invokes callback`() = runTest {
        coEvery { sessionManager.getUserId() } returns "usr_1"
        // Returns null user to keep the init path simple for this test
        coEvery { userRepository.getUser(any()) } returns null

        val viewModel = UserViewModel(userRepository, sessionManager)
        advanceUntilIdle()

        // Captures whether the logout callback was invoked
        var called = false
        viewModel.logout { called = true }
        advanceUntilIdle()

        // Verifies that the session was cleared
        coVerify { sessionManager.clear() }
        // Verifies that the callback was invoked after logout
        assertTrue(called)
    }
}
package com.example.mova.ui.screens.profile

import com.example.mova.data.models.User
import com.example.mova.data.repository.UserRepository
import com.example.mova.data.session.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import io.mockk.mockk
import io.mockk.coEvery
import io.mockk.coVerify
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserViewModelTest {

    // Test dispatcher that only runs coroutines when virtual clock ticks
    private val dispatcher = StandardTestDispatcher()
    private lateinit var userRepository: UserRepository
    private lateinit var sessionManager: SessionManager

    @Before
    fun setup() {
        // Replace Main with the test dispatcher so viewModelScope uses it
        Dispatchers.setMain(dispatcher)
        userRepository = mockk()
        // unstubbed calls return defaults instead of throwing
        sessionManager = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        // Restore the real Main dispatcher to avoid leaking state between tests
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads user from session`() = runTest(dispatcher) {
        val user = User(userId = "usr_1", fullName = "Rita", email = "r@mova.com")
        coEvery { sessionManager.getUserId() } returns "usr_1"
        coEvery { userRepository.getUser("usr_1") } returns user

        val viewModel = UserViewModel(userRepository, sessionManager)
        // wait for the init-triggered coroutine to finish before asserting
        advanceUntilIdle()

        Assert.assertEquals("Rita", viewModel.uiState.value.user?.fullName)
        Assert.assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `loadUser with no session surfaces error`() = runTest(dispatcher) {
        // ViewModel should short-circuit with an error if there's no logged-in user
        coEvery { sessionManager.getUserId() } returns null

        val viewModel = UserViewModel(userRepository, sessionManager)
        advanceUntilIdle()

        Assert.assertEquals("Not logged in", viewModel.uiState.value.error)
    }

    @Test
    fun `addObjective appends and calls update`() = runTest(dispatcher) {
        val user = User(userId = "usr_1", fullName = "Rita", objectives = emptyList())
        coEvery { sessionManager.getUserId() } returns "usr_1"
        coEvery { userRepository.getUser("usr_1") } returns user
        // Repository returns the updated user objective appended
        coEvery { userRepository.updateUserProfile(any(), any()) } returns Result.success(
            user.copy(objectives = listOf("Burn 500 cal"))
        )

        val viewModel = UserViewModel(userRepository, sessionManager)
        advanceUntilIdle()
        viewModel.addObjective("Burn 500 cal")
        advanceUntilIdle()

        // confirm the objective was persisted through the repository
        coVerify { userRepository.updateUserProfile("usr_1", any()) }
    }

    @Test
    fun `addObjective ignores blank strings`() = runTest(dispatcher) {
        val user = User(userId = "usr_1", objectives = emptyList())
        coEvery { sessionManager.getUserId() } returns "usr_1"
        coEvery { userRepository.getUser("usr_1") } returns user

        val viewModel = UserViewModel(userRepository, sessionManager)
        advanceUntilIdle()
        viewModel.addObjective("   ")
        advanceUntilIdle()

        // A blank input must not trigger any persistence call
        coVerify(exactly = 0) { userRepository.updateUserProfile(any(), any()) }
    }

    @Test
    fun `removeObjective removes existing`() = runTest(dispatcher) {
        val user = User(userId = "usr_1", objectives = listOf("A", "B"))
        coEvery { sessionManager.getUserId() } returns "usr_1"
        coEvery { userRepository.getUser("usr_1") } returns user
        // The repository returns the user, with 'B' removed
        coEvery { userRepository.updateUserProfile(any(), any()) } returns Result.success(
            user.copy(objectives = listOf("A"))
        )

        val viewModel = UserViewModel(userRepository, sessionManager)
        advanceUntilIdle()
        viewModel.removeObjective("B")
        advanceUntilIdle()

        coVerify { userRepository.updateUserProfile("usr_1", any()) }
    }

    @Test
    fun `logout clears session and invokes callback`() = runTest(dispatcher) {
        coEvery { sessionManager.getUserId() } returns "usr_1"
        coEvery { userRepository.getUser(any()) } returns null

        val viewModel = UserViewModel(userRepository, sessionManager)
        var called = false
        viewModel.logout { called = true }
        advanceUntilIdle()

        // Session must be cleared and completion callback fired
        coVerify { sessionManager.clear() }
        Assert.assertTrue(called)
    }
}
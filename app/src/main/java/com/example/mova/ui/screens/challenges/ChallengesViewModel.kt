/**
 * =====================================================================================
 * VIEWMODEL: ChallengesViewModel
 * =====================================================================================
 * Holds the programmes the user has joined, the full catalogue, and the actions to join
 * or check in. Errors are sent as an enum, and the screen turns each one into a string,
 * so the messages translate.
 * =====================================================================================
 */
package com.example.mova.ui.screens.challenges

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mova.data.remote.RetrofitClient
import com.example.mova.data.remote.model.ChallengeDto
import com.example.mova.data.remote.model.ProgramDto
import com.example.mova.data.remote.model.UserIdRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException

/** Problems the screen can show. Each one maps to a string in strings.xml. */
enum class ChallengeError { NETWORK, LOAD_FAILED, ACTION_FAILED, ALREADY_DONE }

class ChallengesViewModel : ViewModel() {

    private val api = RetrofitClient.apiService

    // Programmes the user has joined, with progress.
    private val _joined = MutableStateFlow<List<ChallengeDto>>(emptyList())
    val joined: StateFlow<List<ChallengeDto>> = _joined.asStateFlow()

    // Every active programme, including ones not yet joined.
    private val _catalogue = MutableStateFlow<List<ProgramDto>>(emptyList())
    val catalogue: StateFlow<List<ProgramDto>> = _catalogue.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<ChallengeError?>(null)
    val error: StateFlow<ChallengeError?> = _error.asStateFlow()

    /** Loads both lists. Called on open and every time the screen is shown again. */
    fun refresh(userId: String) {
        if (userId.isBlank()) return
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val mine = api.getUserChallenges(userId)
                val programs = api.getCatalogue(userId)

                val mineBody = mine.body()
                val programsBody = programs.body()

                if (mine.isSuccessful && mineBody != null) {
                    _joined.value = mineBody
                } else {
                    _error.value = ChallengeError.LOAD_FAILED
                }

                if (programs.isSuccessful && programsBody != null) {
                    _catalogue.value = programsBody
                }
            } catch (e: IOException) {
                _error.value = ChallengeError.NETWORK
            } catch (e: Exception) {
                Log.e(TAG, "Refresh failed", e)
                _error.value = ChallengeError.LOAD_FAILED
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Joins a programme, then reloads both lists so it moves into the tabs. */
    fun join(userId: String, challengeId: String) =
        runAction(userId) { api.joinChallenge(challengeId, UserIdRequest(userId)) }

    /** Manual check-in for today. The server allows one per day. */
    fun checkIn(userId: String, challengeId: String) =
        runAction(userId) { api.checkInChallenge(challengeId, UserIdRequest(userId)) }

    private fun runAction(userId: String, call: suspend () -> Response<ResponseBody>) {
        viewModelScope.launch {
            try {
                val response = call()
                when {
                    response.isSuccessful -> refresh(userId)
                    response.code() == 409 -> _error.value = ChallengeError.ALREADY_DONE
                    else -> _error.value = ChallengeError.ACTION_FAILED
                }
            } catch (e: IOException) {
                _error.value = ChallengeError.NETWORK
            } catch (e: Exception) {
                Log.e(TAG, "Action failed", e)
                _error.value = ChallengeError.ACTION_FAILED
            }
        }
    }

    companion object {
        private const val TAG = "ChallengesViewModel"
    }
}
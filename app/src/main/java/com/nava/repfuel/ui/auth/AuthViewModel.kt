package com.nava.repfuel.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.nava.repfuel.data.auth.AuthRepository
import com.nava.repfuel.data.auth.SignInResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    object SignedOut : AuthUiState
    object SigningIn : AuthUiState
    data class SignedIn(val user: FirebaseUser) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(
        repository.currentUser?.let { AuthUiState.SignedIn(it) } ?: AuthUiState.SignedOut
    )
    val uiState: StateFlow<AuthUiState> = _uiState

    fun signInWithGoogle() {
        if (_uiState.value is AuthUiState.SigningIn) return
        _uiState.value = AuthUiState.SigningIn
        viewModelScope.launch {
            _uiState.value = when (val result = repository.signInWithGoogle()) {
                is SignInResult.Success -> AuthUiState.SignedIn(result.user)
                is SignInResult.Failure -> AuthUiState.Error(result.message)
                SignInResult.Cancelled -> AuthUiState.SignedOut
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            repository.signOut()
            _uiState.value = AuthUiState.SignedOut
        }
    }
}

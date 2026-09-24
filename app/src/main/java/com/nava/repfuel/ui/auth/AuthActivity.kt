package com.nava.repfuel.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.nava.repfuel.MainActivity
import com.nava.repfuel.R
import com.nava.repfuel.data.auth.AuthRepository
import com.nava.repfuel.ui.theme.RepFeulTheme

/**
 * Entry point of the app (see AndroidManifest). Owns the authentication flow only.
 *
 * On launch it defers to [AuthViewModel]'s initial state, which is seeded from
 * [AuthRepository.currentUser] — i.e. Firebase's own persisted session, not an
 * in-memory flag. If a session already exists (including after the app was
 * killed or removed from Recents), this activity immediately hands off to
 * [MainActivity] without ever rendering sign-in UI.
 *
 * [AuthUiState]/[AuthViewModel] are intentionally provider-agnostic: adding a
 * phone/OTP flow later means adding a new sign-in method to [AuthRepository]
 * and a new screen/branch here (or a nested nav graph), not touching
 * [MainActivity] or the startup logic at all.
 */
class AuthActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(
            AuthRepository(
                context = applicationContext,
                webClientId = getString(R.string.default_web_client_id),
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RepFeulTheme {
                val authState by authViewModel.uiState.collectAsState()

                LaunchedEffect(authState) {
                    if (authState is AuthUiState.SignedIn) {
                        goToMainActivity()
                    }
                }

                if (authState !is AuthUiState.SignedIn) {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        SignInScreen(
                            state = authState,
                            onSignInClick = authViewModel::signInWithGoogle,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }

    private fun goToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

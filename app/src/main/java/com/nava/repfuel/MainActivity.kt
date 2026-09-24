package com.nava.repfuel

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nava.repfuel.data.auth.AuthRepository
import com.nava.repfuel.ui.auth.AuthActivity
import com.nava.repfuel.ui.auth.AuthUiState
import com.nava.repfuel.ui.auth.AuthViewModel
import com.nava.repfuel.ui.auth.AuthViewModelFactory
import com.nava.repfuel.ui.theme.RepFuelTheme

/**
 * The authenticated application. Holds no sign-in UI or provider-specific logic —
 * see [AuthActivity] for that. The only auth-related things this activity does are:
 * (1) bail out to [AuthActivity] if it is ever reached without a valid session, and
 * (2) trigger sign-out and hand back to [AuthActivity] when the user asks to.
 * Both checks read the persisted session from [AuthRepository] on each call, never
 * a locally cached flag.
 */
class MainActivity : ComponentActivity() {

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
            RepFuelTheme {
                val authState by authViewModel.uiState.collectAsState()

                LaunchedEffect(authState) {
                    if (authState !is AuthUiState.SignedIn) {
                        goToAuthActivity()
                    }
                }

                if (authState is AuthUiState.SignedIn) {
                    val user = (authState as AuthUiState.SignedIn).user
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        HomeScreen(
                            name = user.displayName ?: "Android",
                            uid = user.uid,
                            onSignOutClick = authViewModel::signOut,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }

    private fun goToAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}

@Composable
fun HomeScreen(name: String, uid: String, onSignOutClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = "Hello $name!")
        Text(text = "UID: $uid")
        Button(onClick = onSignOutClick, modifier = Modifier.padding(top = 16.dp)) {
            Text("Sign out")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    RepFuelTheme {
        HomeScreen(name = "Android", uid = "preview-uid", onSignOutClick = {})
    }
}

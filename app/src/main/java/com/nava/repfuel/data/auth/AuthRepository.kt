package com.nava.repfuel.data.auth

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

sealed class SignInResult {
    data class Success(val user: FirebaseUser) : SignInResult()
    data class Failure(val message: String) : SignInResult()
    object Cancelled : SignInResult()
}

/**
 * Wraps Credential Manager's "Sign in with Google" flow and exchanges the
 * resulting Google ID token for a Firebase session.
 *
 * [webClientId] must be the *Web application* OAuth client ID from the
 * Firebase project (auto-generated as `R.string.default_web_client_id`
 * once google-services.json is added with Google sign-in enabled).
 */
class AuthRepository(
    private val context: Context,
    private val webClientId: String,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
) {
    private val credentialManager by lazy { CredentialManager.create(context) }

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    /**
     * Backed by FirebaseAuth's own on-disk session cache, so it reflects the
     * true persisted session, not any in-memory/activity state — valid across
     * process death and after the app is swiped from Recents.
     */
    fun isAuthenticated(): Boolean = auth.currentUser != null

    suspend fun signInWithGoogle(filterByAuthorizedAccounts: Boolean = true): SignInResult {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(webClientId)
            .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            val response = credentialManager.getCredential(context, request)
            handleCredential(response.credential)
        } catch (e: NoCredentialException) {
            // No previously-authorized Google account on this device; fall back to the
            // full account chooser instead of surfacing an error.
            if (filterByAuthorizedAccounts) {
                signInWithGoogle(filterByAuthorizedAccounts = false)
            } else {
                SignInResult.Failure(e.message ?: "No Google account available")
            }
        } catch (e: GetCredentialCancellationException) {
            SignInResult.Cancelled
        } catch (e: GetCredentialException) {
            SignInResult.Failure(e.message ?: "Google sign-in failed")
        }
    }

    private suspend fun handleCredential(credential: Credential): SignInResult {
        if (credential !is CustomCredential ||
            credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            return SignInResult.Failure("Unexpected credential type: ${credential.type}")
        }

        val idToken = try {
            GoogleIdTokenCredential.createFrom(credential.data).idToken
        } catch (e: GoogleIdTokenParsingException) {
            return SignInResult.Failure("Invalid Google ID token: ${e.message}")
        }

        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        return try {
            val result = auth.signInWithCredential(firebaseCredential).await()
            val user = result.user
            if (user != null) SignInResult.Success(user) else SignInResult.Failure("No Firebase user returned")
        } catch (e: Exception) {
            SignInResult.Failure(e.message ?: "Firebase sign-in failed")
        }
    }

    suspend fun signOut() {
        auth.signOut()
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
    }
}

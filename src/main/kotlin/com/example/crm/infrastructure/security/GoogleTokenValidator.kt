package com.example.crm.infrastructure.security

import com.example.crm.exception.GoogleAuthenticationException
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.time.Instant

data class GoogleTokenPayload(
    val sub: String,
    val email: String,
    val name: String?,
    val picture: String?
)

interface GoogleIdTokenVerifierClient {
    fun verify(credential: String): GoogleIdToken?
}

@Component
class DefaultGoogleIdTokenVerifierClient(config: GoogleAuthConfig) : GoogleIdTokenVerifierClient {
    private val verifier = GoogleIdTokenVerifier.Builder(NetHttpTransport(), GsonFactory.getDefaultInstance())
        .setAudience(listOf(config.clientId))
        .setIssuers(listOf("accounts.google.com", "https://accounts.google.com"))
        .build()

    override fun verify(credential: String): GoogleIdToken? = verifier.verify(credential)
}

@Service
class GoogleTokenValidator(
    private val config: GoogleAuthConfig,
    private val verifierClient: GoogleIdTokenVerifierClient
) {

    fun validate(credential: String): GoogleTokenPayload {
        if (config.clientId.isBlank()) {
            throw IllegalStateException("Google client id is not configured")
        }
        if (credential.isBlank()) {
            throw GoogleAuthenticationException("Google credential is required")
        }

        val token = verifierClient.verify(credential)
            ?: throw GoogleAuthenticationException("Invalid Google ID token")

        val payload = token.payload
        validatePayload(payload)

        val email = payload.email ?: throw GoogleAuthenticationException("Google token email is missing")
        val sub = payload.subject ?: throw GoogleAuthenticationException("Google token subject is missing")
        return GoogleTokenPayload(
            sub = sub,
            email = email,
            name = payload["name"] as? String,
            picture = payload["picture"] as? String
        )
    }

    private fun validatePayload(payload: GoogleIdToken.Payload) {
        val issuer = payload.issuer
        if (issuer != "accounts.google.com" && issuer != "https://accounts.google.com") {
            throw GoogleAuthenticationException("Invalid Google token issuer")
        }

        val audience = payload.audience
        if (audience != config.clientId) {
            throw GoogleAuthenticationException("Invalid Google token audience")
        }

        val expirationTimeSeconds = payload.expirationTimeSeconds
            ?: throw GoogleAuthenticationException("Invalid Google token expiration")
        if (Instant.ofEpochSecond(expirationTimeSeconds).isBefore(Instant.now())) {
            throw GoogleAuthenticationException("Google token expired")
        }
    }
}

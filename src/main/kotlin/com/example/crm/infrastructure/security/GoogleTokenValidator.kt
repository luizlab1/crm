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
        check(config.clientId.isNotBlank()) { "Google client id is not configured" }
        credential.takeIf { it.isNotBlank() } ?: unauthorized("Google credential is required")

        val token = verifierClient.verify(credential)
            ?: unauthorized("Invalid Google ID token")

        val payload = token.payload
        validatePayload(payload)

        val email = payload.email ?: unauthorized("Google token email is missing")
        val sub = payload.subject ?: unauthorized("Google token subject is missing")
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
            unauthorized("Invalid Google token issuer")
        }

        val audience = payload.audience
        if (audience != config.clientId) {
            unauthorized("Invalid Google token audience")
        }

        val expirationTimeSeconds = payload.expirationTimeSeconds
            ?: unauthorized("Invalid Google token expiration")
        if (Instant.ofEpochSecond(expirationTimeSeconds).isBefore(Instant.now())) {
            unauthorized("Google token expired")
        }
    }

    private fun unauthorized(message: String): Nothing = throw GoogleAuthenticationException(message)
}

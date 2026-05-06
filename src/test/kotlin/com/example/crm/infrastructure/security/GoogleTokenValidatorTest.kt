package com.example.crm.infrastructure.security

import com.example.crm.exception.GoogleAuthenticationException
import com.example.crm.support.shouldBe
import com.example.crm.support.shouldThrow
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import java.time.Instant

class GoogleTokenValidatorTest {

    @Test
    fun `it should validate google token payload`() {
        val verifierClient = mockk<GoogleIdTokenVerifierClient>()
        val token = mockk<GoogleIdToken>()
        val payload = mockk<GoogleIdToken.Payload>()
        every { verifierClient.verify("credential") } returns token
        every { token.payload } returns payload
        every { payload.issuer } returns "https://accounts.google.com"
        every { payload.audience } returns "google-client-id"
        every { payload.expirationTimeSeconds } returns Instant.now().plusSeconds(300).epochSecond
        every { payload.email } returns "user@crm.com"
        every { payload.subject } returns "sub-123"
        every { payload["name"] } returns "CRM User"
        every { payload["picture"] } returns "https://image"

        val validator = GoogleTokenValidator(
            config = GoogleAuthConfig(clientId = "google-client-id"),
            verifierClient = verifierClient
        )

        val result = validator.validate("credential")
        result.sub shouldBe "sub-123"
        result.email shouldBe "user@crm.com"
    }

    @Test
    fun `it should throw unauthorized when token is invalid`() {
        val verifierClient = mockk<GoogleIdTokenVerifierClient>()
        every { verifierClient.verify(any()) } returns null
        val validator = GoogleTokenValidator(GoogleAuthConfig(clientId = "google-client-id"), verifierClient)

        shouldThrow<GoogleAuthenticationException> {
            validator.validate("invalid")
        }
    }
}

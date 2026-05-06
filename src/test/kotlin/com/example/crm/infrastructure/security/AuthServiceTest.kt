package com.example.crm.infrastructure.security

import com.example.crm.entity.UserEntity
import com.example.crm.service.UserService
import com.example.crm.support.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test

class AuthServiceTest {

    @Test
    fun `it should generate internal jwt when authenticating with google`() {
        val userService = mockk<UserService>()
        val googleTokenValidator = mockk<GoogleTokenValidator>()
        val jwtService = JwtService(JwtConfig())

        every { googleTokenValidator.validate("google-id-token") } returns GoogleTokenPayload(
            sub = "sub-123",
            email = "google@crm.com",
            name = "Google User",
            picture = "https://img"
        )
        every { userService.getByEmail("google@crm.com") } returns UserEntity(
            id = 8,
            tenantId = 3,
            email = "google@crm.com",
            passwordHash = "hash",
            isActive = true
        )

        val service = AuthService(userService, jwtService, googleTokenValidator)
        val token = service.authenticateWithGoogle("google-id-token")
        val claims = jwtService.parseClaims(token)

        claims.subject shouldBe "google@crm.com"
        claims["tenantId"] shouldBe 3
        claims["userId"] shouldBe 8
    }
}

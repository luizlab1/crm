package com.example.crm.infrastructure.security

import com.example.crm.entity.UserEntity
import com.example.crm.repository.UserRepository
import com.example.crm.support.shouldBe
import com.example.crm.support.shouldBeNull
import com.example.crm.support.shouldNotBeNull
import com.example.crm.support.shouldThrow
import com.example.crm.infrastructure.web.config.GlobalExceptionHandler
import com.example.crm.domain.exception.RequestValidationException
import com.example.crm.domain.exception.ValidationError
import io.jsonwebtoken.Claims
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.mock.web.MockFilterChain
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

class SecurityAndExceptionTest {

    @Test
    fun `it should generate and parse jwt subject`() {
        val service = JwtService(JwtConfig())
        val token = service.generateToken("user@crm.com", mapOf("tenant" to "1"))
        service.parseSubject(token) shouldBe "user@crm.com"
    }

    @Test
    fun `it should authenticate when bearer token is valid`() {
        val jwtService = mockk<JwtService>()
        val claims = mockk<Claims>()
        every { claims.subject } returns "user@crm.com"
        every { claims["userId"] } returns 1L
        every { claims["tenantId"] } returns 1L
        every { jwtService.parseClaims("valid-token") } returns claims

        val request = MockHttpServletRequest().apply { addHeader("Authorization", "Bearer valid-token") }
        val response = MockHttpServletResponse()
        val chain = MockFilterChain()

        SecurityContextHolder.clearContext()
        JwtAuthenticationFilter(jwtService).doFilter(request, response, chain)

        val auth = SecurityContextHolder.getContext().authentication.shouldNotBeNull()
        auth.name shouldBe "user@crm.com"
    }

    @Test
    fun `it should clear authentication when bearer token is invalid`() {
        val jwtService = mockk<JwtService>()
        every { jwtService.parseClaims(any()) } throws IllegalArgumentException("invalid")

        val request = MockHttpServletRequest().apply { addHeader("Authorization", "Bearer invalid-token") }
        val response = MockHttpServletResponse()
        val chain = MockFilterChain()

        SecurityContextHolder.getContext().authentication =
            org.springframework.security.authentication.UsernamePasswordAuthenticationToken("old", null)

        JwtAuthenticationFilter(jwtService).doFilter(request, response, chain)
        SecurityContextHolder.getContext().authentication.shouldBeNull()
    }

    @Test
    fun `it should load user details when user exists`() {
        val repository = mockk<UserRepository>()
        val user = UserEntity(id = 1, tenantId = 1, email = "user@crm.com", passwordHash = "hash")
        every { repository.findByEmail("user@crm.com") } returns user

        val userDetails = UserDetailsServiceImpl(repository).loadUserByUsername("user@crm.com")
        userDetails.username shouldBe "user@crm.com"
        userDetails.password shouldBe "hash"
    }

    @Test
    fun `it should throw when user does not exist`() {
        val repository = mockk<UserRepository>()
        every { repository.findByEmail("none@crm.com") } returns null

        shouldThrow<UsernameNotFoundException> {
            UserDetailsServiceImpl(repository).loadUserByUsername("none@crm.com")
        }
    }

    @Test
    fun `it should map exception statuses correctly`() {
        val handler = GlobalExceptionHandler()
        val notFound = handler.handleNotFound(NoSuchElementException("nao achei"))
        val badRequest = handler.handleBadRequest(IllegalArgumentException("invalido"))
        val generic = handler.handleGeneric(RuntimeException("erro"))

        notFound.body?.status shouldBe 404
        badRequest.body?.status shouldBe 400
        generic.body?.status shouldBe 500

        val validation = handler.handleValidation(
            RequestValidationException(
                listOf(ValidationError(field = "name", message = "Nome e obrigatorio", code = "NotBlank"))
            )
        )
        validation.body?.errors?.size shouldBe 1
    }
}

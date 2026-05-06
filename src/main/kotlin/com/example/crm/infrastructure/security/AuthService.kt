package com.example.crm.infrastructure.security

import com.example.crm.entity.UserEntity
import com.example.crm.service.UserService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class AuthService(
    private val userService: UserService,
    private val jwtService: JwtService,
    private val googleTokenValidator: GoogleTokenValidator
) {
    private val passwordEncoder = BCryptPasswordEncoder()

    fun authenticateWithEmailPassword(email: String, password: String): String? {
        val user = userService.getByEmail(email) ?: return null
        val stored = user.passwordHash.replaceFirst("^\\$2b\\$".toRegex(), "\$2a\$")
        val matches = passwordEncoder.matches(password, stored)
        if (!matches) {
            return null
        }
        return generateInternalToken(user)
    }

    @Transactional
    fun authenticateWithGoogle(credential: String): String {
        val payload = googleTokenValidator.validate(credential)
        val user = userService.getByEmail(payload.email) ?: createGoogleUser(payload.email)
        return generateInternalToken(user)
    }

    private fun createGoogleUser(email: String): UserEntity {
        val randomPassword = passwordEncoder.encode(UUID.randomUUID().toString())
            ?: throw IllegalStateException("Could not generate password hash for Google user")
        return userService.create(
            user = UserEntity(
                tenantId = DEFAULT_TENANT_ID,
                email = email,
                passwordHash = randomPassword,
                isActive = true
            ),
            personData = null,
            contacts = emptyList(),
            addressRequests = emptyList()
        )
    }

    private fun generateInternalToken(user: UserEntity): String =
        jwtService.generateToken(
            user.email,
            mapOf("email" to user.email, "userId" to user.id, "tenantId" to user.tenantId)
        )

    companion object {
        private const val DEFAULT_TENANT_ID = 1L
    }
}

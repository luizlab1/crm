package com.example.crm.infrastructure.web.controller

import com.example.crm.infrastructure.security.JwtService
import com.example.crm.application.port.input.UserUseCase
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

data class AuthRequest(val email: String, val password: String)
data class AuthResponse(val token: String)

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(private val jwtService: JwtService, private val userUseCase: UserUseCase) {

    @PostMapping("/token")
    fun token(@RequestBody req: AuthRequest): ResponseEntity<AuthResponse> {
        val user = userUseCase.getByEmail(req.email) ?: return ResponseEntity.status(401).build()

        // Verify password using BCryptPasswordEncoder. Some bcrypt implementations
        // produce $2b$ prefixes; normalize to $2a$ for compatibility with older libs.
        val stored = user.passwordHash.replaceFirst("^\\$2b\\$".toRegex(), "\$2a\$")
        val encoder = BCryptPasswordEncoder()
        if (!encoder.matches(req.password, stored)) {
            return ResponseEntity.status(401).build()
        }

        val token = jwtService.generateToken(user.email, mapOf("email" to user.email, "userId" to user.id))
        return ResponseEntity.ok(AuthResponse(token))
    }
}

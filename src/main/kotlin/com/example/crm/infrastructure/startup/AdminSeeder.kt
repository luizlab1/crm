package com.example.crm.infrastructure.startup

import com.example.crm.domain.model.User
import com.example.crm.domain.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class AdminSeeder(private val userRepository: UserRepository) {
    private val log = LoggerFactory.getLogger(AdminSeeder::class.java)

    @EventListener(ApplicationReadyEvent::class)
    fun seed() {
        val email = "admin@saas.com"
        val existing = userRepository.findByEmail(email)
        // Password hash generated earlier (BCrypt $2a$12)
        val passwordHash = "\$2a\$12\$Av.rZS5isVIBXlOBk.PbuuAGgGnA1J7HuG3r1ghah2vbFXDHkbczO"

        if (existing != null) {
            // Force update password hash to the known value to ensure signin works for the demonstration admin
            val updated = existing.copy(passwordHash = passwordHash)
            userRepository.save(updated)
            log.info("Ensured admin password hash is set for: {}", email)
            return
        }

        val admin = User(
            tenantId = 1,
            personId = null,
            email = email,
            passwordHash = passwordHash,
            isActive = true
        )

        userRepository.save(admin)
        log.info("Seeded admin user: {}", email)
    }
}

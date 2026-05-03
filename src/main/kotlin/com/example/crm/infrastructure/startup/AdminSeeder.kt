package com.example.crm.infrastructure.startup

import com.example.crm.entity.UserEntity
import com.example.crm.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class AdminSeeder(private val userRepository: UserRepository) {
    private val log = LoggerFactory.getLogger(AdminSeeder::class.java)

    @EventListener(ApplicationReadyEvent::class)
    @Transactional
    fun seed() {
        val email = "admin@saas.com"
        val passwordHash = "\$2a\$12\$Av.rZS5isVIBXlOBk.PbuuAGgGnA1J7HuG3r1ghah2vbFXDHkbczO"
        val existing = userRepository.findByEmail(email)

        if (existing != null) {
            existing.passwordHash = passwordHash
            userRepository.save(existing)
            log.info("Ensured admin password hash is set for: {}", email)
            return
        }

        userRepository.save(UserEntity(tenantId = 1, email = email, passwordHash = passwordHash, isActive = true))
        log.info("Seeded admin user: {}", email)
    }
}

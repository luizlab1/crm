package com.example.crm.infrastructure.startup

import com.example.crm.entity.UserEntity
import com.example.crm.repository.UserRepository
import com.example.crm.support.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Test

class AdminSeederTest {

    @Test
    fun `it should update existing admin password hash`() {
        val repository = mockk<UserRepository>()
        val existing = UserEntity(id = 1, tenantId = 1, email = "admin@saas.com", passwordHash = "old")

        every { repository.findByEmail("admin@saas.com") } returns existing
        every { repository.save(any()) } answers { firstArg() }

        AdminSeeder(repository).seed()

        verify(exactly = 1) {
            repository.save(match { it.id == 1L && it.email == "admin@saas.com" && it.passwordHash.startsWith("\$2a\$") })
        }
    }

    @Test
    fun `it should create admin user when it does not exist`() {
        val repository = mockk<UserRepository>()
        val saved = slot<UserEntity>()

        every { repository.findByEmail("admin@saas.com") } returns null
        every { repository.save(capture(saved)) } answers { firstArg() }

        AdminSeeder(repository).seed()

        saved.captured.email shouldBe "admin@saas.com"
        saved.captured.tenantId shouldBe 1
    }
}

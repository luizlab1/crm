package com.example.crm.application.usecase

import com.example.crm.application.port.input.SettingsSaasPlanBenefitInput
import com.example.crm.application.port.input.SettingsSaasPlanUpsertInput
import com.example.crm.domain.exception.RequestValidationException
import com.example.crm.domain.model.PlanCategory
import com.example.crm.domain.model.SettingsSaasPlan
import com.example.crm.domain.model.SettingsSaasPlanBenefit
import com.example.crm.domain.repository.SettingsSaasPlanRepository
import com.example.crm.support.shouldBe
import com.example.crm.support.shouldThrow
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class SettingsSaasPlanUseCaseTest {

    @Test
    fun `it should create plan with benefits and return complete structure`() {
        val repository = mockk<SettingsSaasPlanRepository>()
        val useCase = SettingsSaasPlanUseCaseImpl(repository)

        every { repository.save(any()) } answers {
            val plan = firstArg<SettingsSaasPlan>()
            plan.copy(
                id = 10,
                benefits = plan.benefits.mapIndexed { index, benefit ->
                    benefit.copy(id = (index + 1).toLong())
                }
            )
        }

        val created = useCase.create(
            tenantId = 1,
            input = SettingsSaasPlanUpsertInput(
                name = " Essencial ",
                description = " Plano para autonomos ",
                category = PlanCategory.PROFESSIONAL_AUTONOMOUS,
                benefits = listOf(
                    SettingsSaasPlanBenefitInput(" Atendimento prioritario "),
                    SettingsSaasPlanBenefitInput(" Relatorios mensais ")
                )
            )
        )

        created.id shouldBe 10
        created.tenantId shouldBe 1
        created.name shouldBe "Essencial"
        created.benefits.size shouldBe 2
        created.benefits.first().description shouldBe "Atendimento prioritario"
    }

    @Test
    fun `it should replace benefits on update`() {
        val repository = mockk<SettingsSaasPlanRepository>()
        val useCase = SettingsSaasPlanUseCaseImpl(repository)
        val existing = SettingsSaasPlan(
            id = 10,
            tenantId = 1,
            name = "Essencial",
            category = PlanCategory.PROFESSIONAL_AUTONOMOUS,
            benefits = listOf(
                SettingsSaasPlanBenefit(id = 1, description = "Beneficio antigo")
            )
        )

        every { repository.findByIdAndTenantId(10, 1) } returns existing
        every { repository.save(any()) } answers { firstArg() }

        val updated = useCase.update(
            id = 10,
            tenantId = 1,
            input = SettingsSaasPlanUpsertInput(
                name = "Essencial Plus",
                category = PlanCategory.BUSINESS,
                benefits = listOf(SettingsSaasPlanBenefitInput("Novo beneficio"))
            )
        )

        updated.name shouldBe "Essencial Plus"
        updated.category shouldBe PlanCategory.BUSINESS
        updated.benefits.size shouldBe 1
        updated.benefits.first().description shouldBe "Novo beneficio"
    }

    @Test
    fun `it should fail and bubble exception when save fails`() {
        val repository = mockk<SettingsSaasPlanRepository>()
        val useCase = SettingsSaasPlanUseCaseImpl(repository)

        every { repository.save(any()) } throws RuntimeException("db error")

        shouldThrow<RuntimeException> {
            useCase.create(
                tenantId = 1,
                input = SettingsSaasPlanUpsertInput(
                    name = "Plano",
                    category = PlanCategory.BUSINESS,
                    benefits = listOf(SettingsSaasPlanBenefitInput("Beneficio"))
                )
            )
        }
    }

    @Test
    fun `it should block operation with divergent tenant`() {
        val repository = mockk<SettingsSaasPlanRepository>()
        val useCase = SettingsSaasPlanUseCaseImpl(repository)

        every { repository.findByIdAndTenantId(10, 2) } returns null

        shouldThrow<NoSuchElementException> {
            useCase.getById(10, 2)
        }

        verify(exactly = 1) { repository.findByIdAndTenantId(10, 2) }
    }

    @Test
    fun `it should validate payload and return field errors`() {
        val repository = mockk<SettingsSaasPlanRepository>()
        val useCase = SettingsSaasPlanUseCaseImpl(repository)

        val ex = shouldThrow<RequestValidationException> {
            useCase.create(
                tenantId = 1,
                input = SettingsSaasPlanUpsertInput(
                    tenantId = 99,
                    name = " ",
                    category = null,
                    benefits = listOf(SettingsSaasPlanBenefitInput(" "))
                )
            )
        }

        ex.errors.size shouldBe 4
    }
}

package com.example.crm.infrastructure.web.dto.response

import com.example.crm.domain.model.ItemType
import com.example.crm.support.shouldBe
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

class ResponseDtosTest {

    private val now = OffsetDateTime.parse("2026-01-01T10:15:30+00:00")
    private val uuid: UUID = UUID.fromString("11111111-1111-1111-1111-111111111111")

    @Test
    fun `it should create all response dtos`() {
        AddressResponse(1, "Rua A", "10", null, "Centro", 1, "13000-000", BigDecimal("-10"), BigDecimal("20"), true, now, now).id shouldBe 1
        AppointmentResponse(1, uuid, "SCHEDULED", now, null, null, 1000, null, now, now).status shouldBe "SCHEDULED"
        CityResponse(1, 1, "Campinas", 3509502, now, now).city shouldBe "Campinas"
        CountryResponse(1, "BR", "BRA", "Brasil", now, now).iso2 shouldBe "BR"
        CustomerResponse(1, uuid, 1, null, "Maria", "m@crm.com", null, null, true, now, now).fullName shouldBe "Maria"
        ItemCategoryResponse(1, 1, "Categoria", "Descrição", true, 0, true, ItemType.entries.toSet(), now, now)
            .name shouldBe "Categoria"
        ItemResponse(
            1, uuid, 1, null, ItemType.SERVICE, "Consultoria", null, true, emptyList(),
            null, null, emptyList(), emptyList(), emptyList(), now, now
        ).type shouldBe ItemType.SERVICE

        val orderItem = OrderItemResponse(1, 2, 1, 1000, 1000, now)
        OrderResponse(1, uuid, 1, 2, 3, "DRAFT", 1000, 0, 1000, "BRL", null, listOf(orderItem), now, now).items.size shouldBe 1

        LeadResponse(1, uuid, 1, 2, null, "NEW", "WHATSAPP", 5000, null, now, now).status shouldBe "NEW"
        LeadMessageResponse(1, 1, "Olá", "CHAT", 10, now).channel shouldBe "CHAT"

        PageResponse(content = listOf("a"), page = 0, size = 20, totalElements = 1, totalPages = 1).totalElements shouldBe 1
        PermissionResponse(1, "READ", null, true, now, now).code shouldBe "READ"

        val contact = ContactResponse(1, "EMAIL", "m@crm.com", true, true, now, now)
        val person = PersonResponse(
            id = 1,
            tenantId = 1,
            code = uuid,
            isActive = true,
            physical = PersonPhysicalResponse("Maria", "123", LocalDate.parse("1990-01-01")),
            legal = PersonLegalResponse("Empresa", null, "12345678000199"),
            contacts = listOf(contact),
            createdAt = now,
            updatedAt = now
        )
        person.contacts.first().type shouldBe "EMAIL"

        val step = PipelineFlowStepResponse(1, 1, "S1", "Start", null, "START", false, now, now)
        PipelineFlowResponse(1, 1, "FLOW", "Flow", null, true, listOf(step), now, now).steps.first().code shouldBe "S1"

        RoleResponse(1, "ADMIN", null, true, now, now).name shouldBe "ADMIN"
        ScheduleResponse(1, uuid, 1, 2, 3, null, true, now, now).appointmentId shouldBe 3
        SettingsSaasPlanResponse(
            id = 1,
            tenantId = 1,
            name = "Essencial",
            description = "Plano",
            category = com.example.crm.domain.model.PlanCategory.PROFESSIONAL_AUTONOMOUS,
            benefits = listOf(SettingsSaasPlanBenefitResponse(id = 1, description = "Atendimento prioritario")),
            createdAt = now,
            updatedAt = now
        ).benefits.size shouldBe 1
        StateResponse(1, 1, "SP", "São Paulo", 35, now, now).acronym shouldBe "SP"
        TenantResponse(1, null, uuid, "Tenant", "BUSINESS", true, now, now).name shouldBe "Tenant"
        UnitOfMeasureResponse(1, "UN", "Unidade", "un", true, now, now).code shouldBe "UN"
        UserResponse(1, 1, null, uuid, "u@crm.com", true, now, now).email shouldBe "u@crm.com"
        WorkerResponse(1, uuid, 1, 2, 3, true, now, now).personId shouldBe 2
    }
}

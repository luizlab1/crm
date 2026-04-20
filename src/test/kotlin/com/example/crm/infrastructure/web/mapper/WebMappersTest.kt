package com.example.crm.infrastructure.web.mapper

import com.example.crm.domain.model.*
import com.example.crm.support.shouldBe
import com.example.crm.support.shouldBeNull
import com.example.crm.infrastructure.web.dto.request.*
import io.mockk.mockk
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.OffsetDateTime

class WebMappersTest {

    private val now: OffsetDateTime = OffsetDateTime.parse("2026-01-01T10:15:30+00:00")
    private val photoResolver = mockk<EntityPhotoResolver>(relaxed = true)

    @Test
    fun `it should map person request and response`() {
        val mapper = PersonWebMapper()
        val request = PersonRequest(
            tenantId = 10,
            physical = PersonPhysicalRequest("Maria", "12345678900", LocalDate.parse("1990-01-01")),
            contacts = listOf(ContactRequest("EMAIL", "maria@crm.com", true, true))
        )

        val domain = mapper.toDomain(request)
        domain.physical?.fullName shouldBe "Maria"
        domain.contacts.first().type shouldBe "EMAIL"

        val response = mapper.toResponse(
            domain.copy(id = 1, code = java.util.UUID.fromString("11111111-1111-1111-1111-111111111111"), createdAt = now, updatedAt = now)
        )
        response.id shouldBe 1
        response.physical?.fullName shouldBe "Maria"
        response.contacts.first().contactValue shouldBe "maria@crm.com"
    }

    @Test
    fun `it should map lead and lead message`() {
        val mapper = LeadWebMapper()
        val request = LeadRequest(tenantId = 1, flowId = 2, status = "NEW", source = "WHATSAPP")

        val domain = mapper.toDomain(request)
        domain.tenantId shouldBe 1
        domain.source shouldBe "WHATSAPP"

        val message = mapper.messageToDomain(99, LeadMessageRequest("Olá", "CHAT", 7))
        message.leadId shouldBe 99
        message.message shouldBe "Olá"

        val messageResponse = mapper.toResponse(message.copy(id = 5, createdAt = now))
        messageResponse.id shouldBe 5
        messageResponse.channel shouldBe "CHAT"
    }

    @Test
    fun `it should map order items correctly`() {
        val mapper = OrderWebMapper()
        val request = OrderRequest(
            tenantId = 1,
            customerId = 2,
            userId = 3,
            subtotalCents = 1000,
            totalCents = 900,
            items = listOf(OrderItemRequest(itemId = 77, quantity = 2, unitPriceCents = 500, totalPriceCents = 1000))
        )

        val domain = mapper.toDomain(request)
        domain.items.size shouldBe 1
        domain.items.first().itemId shouldBe 77

        val response = mapper.toResponse(
            domain.copy(id = 10, code = java.util.UUID.fromString("22222222-2222-2222-2222-222222222222"), createdAt = now, updatedAt = now)
        )
        response.id shouldBe 10
        response.items.first().quantity shouldBe 2
    }

    @Test
    fun `it should map pipeline flow steps`() {
        val mapper = PipelineFlowWebMapper()
        val request = PipelineFlowRequest(
            tenantId = 1,
            code = "FLOW-1",
            name = "Fluxo",
            steps = listOf(PipelineFlowStepRequest(1, "S1", "Inicio", stepType = "START", isTerminal = false))
        )

        val domain = mapper.toDomain(request)
        domain.steps.first().code shouldBe "S1"

        val response = mapper.toResponse(domain.copy(id = 3, createdAt = now, updatedAt = now))
        response.id shouldBe 3
        response.steps.first().stepType shouldBe "START"
    }

    @Test
    fun `it should map simple request and response models`() {
        val customer = CustomerWebMapper(photoResolver).toResponse(
            Customer(tenantId = 1, fullName = "Ana", createdAt = now, updatedAt = now)
        )
        customer.fullName shouldBe "Ana"

        val tenant = TenantWebMapper(photoResolver).toDomain(
            TenantRequest(name = "Tenant A", category = "BUSINESS")
        )
        tenant.name shouldBe "Tenant A"

        val user = UserWebMapper(photoResolver).toDomain(
            UserRequest(tenantId = 1, email = "u@c.com", passwordHash = "hash")
        )
        user.email shouldBe "u@c.com"

        val worker = WorkerWebMapper(photoResolver).toDomain(WorkerRequest(tenantId = 1))
        worker.tenantId shouldBe 1

        val item = ItemWebMapper().toDomain(
            ItemRequest(tenantId = 1, type = ItemType.SERVICE, name = "Consultoria")
        )
        item.type shouldBe ItemType.SERVICE

        val itemCategory = ItemCategoryWebMapper(photoResolver).toDomain(
            ItemCategoryRequest(tenantId = 1, name = "Categoria", showOnSite = false)
        )
        itemCategory.name shouldBe "Categoria"
        itemCategory.showOnSite shouldBe false

        val address = AddressWebMapper().toDomain(
            AddressRequest("Rua A", "10", null, "Centro", 1, "12345-000", BigDecimal("-10.5"), BigDecimal("20.1"))
        )
        address.neighborhood shouldBe "Centro"

        val appointment = AppointmentWebMapper().toDomain(AppointmentRequest(scheduledAt = now, totalCents = 3000))
        appointment.totalCents shouldBe 3000

        val schedule = ScheduleWebMapper().toDomain(ScheduleRequest(tenantId = 1, customerId = 2, appointmentId = 3))
        schedule.appointmentId shouldBe 3

        val role = RoleWebMapper().toDomain(RoleRequest(name = "ADMIN"))
        role.name shouldBe "ADMIN"

        val permission = PermissionWebMapper().toDomain(PermissionRequest(code = "USER_READ"))
        permission.code shouldBe "USER_READ"
    }

    @Test
    fun `it should map response only models`() {
        val country = CountryWebMapper().toResponse(Country(id = 1, iso2 = "BR", iso3 = "BRA", country = "Brasil", createdAt = now, updatedAt = now))
        val state = StateWebMapper().toResponse(State(id = 2, countryId = 1, acronym = "SP", state = "São Paulo", createdAt = now, updatedAt = now))
        val city = CityWebMapper().toResponse(City(id = 3, stateId = 2, city = "Campinas", createdAt = now, updatedAt = now))
        val uom = UnitOfMeasureWebMapper().toResponse(UnitOfMeasure(id = 4, code = "UN", name = "Unidade", symbol = "un", createdAt = now, updatedAt = now))

        country.iso2 shouldBe "BR"
        state.acronym shouldBe "SP"
        city.city shouldBe "Campinas"
        uom.code shouldBe "UN"
    }

    @Test
    fun `it should keep null person details when not provided`() {
        val mapper = PersonWebMapper()
        val domain = mapper.toDomain(PersonRequest(tenantId = 7, physical = null, legal = null))

        domain.physical.shouldBeNull()
        domain.legal.shouldBeNull()
    }
}

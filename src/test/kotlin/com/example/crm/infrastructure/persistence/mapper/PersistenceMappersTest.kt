package com.example.crm.infrastructure.persistence.mapper

import com.example.crm.domain.model.*
import com.example.crm.support.shouldBe
import com.example.crm.infrastructure.persistence.entity.CityJpaEntity
import com.example.crm.infrastructure.persistence.entity.CountryJpaEntity
import com.example.crm.infrastructure.persistence.entity.StateJpaEntity
import com.example.crm.infrastructure.persistence.entity.UnitOfMeasureJpaEntity
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

class PersistenceMappersTest {

    private val now: OffsetDateTime = OffsetDateTime.parse("2026-01-01T10:15:30+00:00")

    @Test
    fun `it should map simple models to entity and back`() {
        val tenantMapper = TenantPersistenceMapper()
        val userMapper = UserPersistenceMapper()
        val workerMapper = WorkerPersistenceMapper()
        val itemMapper = ItemPersistenceMapper()
        val itemCategoryMapper = ItemCategoryPersistenceMapper()
        val addressMapper = AddressPersistenceMapper()
        val appointmentMapper = AppointmentPersistenceMapper()
        val scheduleMapper = SchedulePersistenceMapper()
        val roleMapper = RolePersistenceMapper()
        val permissionMapper = PermissionPersistenceMapper()

        val tenant = Tenant(id = 1, name = "Tenant", category = "BUSINESS", createdAt = now, updatedAt = now)
        tenantMapper.toDomain(tenantMapper.toEntity(tenant)).id shouldBe tenant.id

        val user = User(id = 2, tenantId = 1, email = "a@crm.com", passwordHash = "hash", createdAt = now, updatedAt = now)
        userMapper.toDomain(userMapper.toEntity(user)).email shouldBe user.email

        val worker = Worker(id = 3, tenantId = 1, personId = 9, userId = 2, createdAt = now, updatedAt = now)
        workerMapper.toDomain(workerMapper.toEntity(worker)).personId shouldBe worker.personId

        val item = Item(
            id = 4, tenantId = 1, categoryId = 5, type = ItemType.SERVICE,
            name = "Item", sku = "SKU-1", createdAt = now, updatedAt = now
        )
        itemMapper.toDomain(itemMapper.toEntity(item)).name shouldBe item.name

        val itemCategory = ItemCategory(
            id = 5,
            tenantId = 1,
            name = "Cat",
            description = "Categoria de teste",
            showOnSite = false,
            sortOrder = 7,
            createdAt = now,
            updatedAt = now
        )
        val mappedItemCategory = itemCategoryMapper.toDomain(itemCategoryMapper.toEntity(itemCategory))
        mappedItemCategory.showOnSite shouldBe itemCategory.showOnSite
        mappedItemCategory.description shouldBe itemCategory.description
        mappedItemCategory.sortOrder shouldBe itemCategory.sortOrder

        val address = Address(
            id = 6,
            street = "Rua A",
            number = "10",
            neighborhood = "Centro",
            cityId = 100,
            postalCode = "12345-000",
            latitude = BigDecimal("-10.1"),
            longitude = BigDecimal("20.2"),
            createdAt = now,
            updatedAt = now
        )
        addressMapper.toDomain(addressMapper.toEntity(address)).postalCode shouldBe address.postalCode

        val appointment = Appointment(id = 7, status = "DONE", scheduledAt = now, totalCents = 500, createdAt = now, updatedAt = now)
        appointmentMapper.toDomain(appointmentMapper.toEntity(appointment)).status shouldBe appointment.status

        val schedule = Schedule(id = 8, tenantId = 1, customerId = 2, appointmentId = 7, description = "Agendado", createdAt = now, updatedAt = now)
        scheduleMapper.toDomain(scheduleMapper.toEntity(schedule)).description shouldBe schedule.description

        val role = Role(id = 9, name = "ADMIN", description = "desc", createdAt = now, updatedAt = now)
        roleMapper.toDomain(roleMapper.toEntity(role)).name shouldBe role.name

        val permission = Permission(id = 10, code = "USER_READ", description = "read", createdAt = now, updatedAt = now)
        permissionMapper.toDomain(permissionMapper.toEntity(permission)).code shouldBe permission.code
    }

    @Test
    fun `it should map composite models and relationships`() {
        val personMapper = PersonPersistenceMapper()
        val customerMapper = CustomerPersistenceMapper()
        val orderMapper = OrderPersistenceMapper()
        val leadMapper = LeadPersistenceMapper()
        val pipelineFlowMapper = PipelineFlowPersistenceMapper()

        val person = Person(
            id = 11,
            tenantId = 1,
            code = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
            physical = PersonPhysical("Maria", "12345678900", LocalDate.parse("1990-01-01")),
            legal = PersonLegal("Empresa X", "XPTO", "12345678000199"),
            contacts = listOf(Contact(id = 1, type = "EMAIL", contactValue = "maria@crm.com", isPrimary = true, createdAt = now, updatedAt = now)),
            createdAt = now,
            updatedAt = now
        )
        val personBack = personMapper.toDomain(personMapper.toEntity(person))
        personBack.physical?.fullName shouldBe "Maria"
        personBack.contacts.first().type shouldBe "EMAIL"

        val customer = Customer(id = 12, tenantId = 1, personId = 11, fullName = "Maria", email = "maria@crm.com", createdAt = now, updatedAt = now)
        customerMapper.toDomain(customerMapper.toEntity(customer)).fullName shouldBe customer.fullName

        val order = Order(
            id = 13,
            tenantId = 1,
            customerId = 12,
            userId = 2,
            subtotalCents = 1000,
            totalCents = 900,
            items = listOf(OrderItem(id = 1, itemId = 100, quantity = 2, unitPriceCents = 500, totalPriceCents = 1000, createdAt = now)),
            createdAt = now,
            updatedAt = now
        )
        val orderBack = orderMapper.toDomain(orderMapper.toEntity(order))
        orderBack.items.size shouldBe 1
        orderBack.items.first().itemId shouldBe 100

        val lead = Lead(id = 14, tenantId = 1, flowId = 20, customerId = 12, status = "NEW", source = "WHATSAPP", createdAt = now, updatedAt = now)
        leadMapper.toDomain(leadMapper.toEntity(lead)).source shouldBe "WHATSAPP"

        val leadMessage = LeadMessage(id = 2, leadId = 14, message = "Oi", channel = "CHAT", createdByUserId = 99, createdAt = now)
        leadMapper.toDomain(leadMapper.toEntity(leadMessage)).message shouldBe "Oi"

        val flow = PipelineFlow(
            id = 15,
            tenantId = 1,
            code = "FLOW-1",
            name = "Comercial",
            steps = listOf(PipelineFlowStep(id = 3, stepOrder = 1, code = "S1", name = "Inicio", stepType = "START", isTerminal = false, createdAt = now, updatedAt = now)),
            createdAt = now,
            updatedAt = now
        )
        val flowBack = pipelineFlowMapper.toDomain(pipelineFlowMapper.toEntity(flow))
        flowBack.code shouldBe "FLOW-1"
        flowBack.steps.first().code shouldBe "S1"
    }

    @Test
    fun `it should map read-only entities to domain`() {
        val countryEntity = CountryJpaEntity(id = 1, iso2 = "BR", iso3 = "BRA", country = "Brasil")
        countryEntity.createdAt = now
        countryEntity.updatedAt = now

        val stateEntity = StateJpaEntity(id = 2, countryId = 1, acronym = "SP", state = "São Paulo", ibgeCode = 35)
        stateEntity.createdAt = now
        stateEntity.updatedAt = now

        val cityEntity = CityJpaEntity(id = 3, stateId = 2, city = "Campinas", ibgeCode = 3509502)
        cityEntity.createdAt = now
        cityEntity.updatedAt = now

        val uomEntity = UnitOfMeasureJpaEntity(id = 4, code = "UN", name = "Unidade", symbol = "un", isActive = true)
        uomEntity.createdAt = now
        uomEntity.updatedAt = now

        CountryPersistenceMapper().toDomain(countryEntity).iso2 shouldBe "BR"
        StatePersistenceMapper().toDomain(stateEntity).acronym shouldBe "SP"
        CityPersistenceMapper().toDomain(cityEntity).city shouldBe "Campinas"
        UnitOfMeasurePersistenceMapper().toDomain(uomEntity).code shouldBe "UN"
    }
}

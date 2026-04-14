package com.example.crm.application.usecase

import com.example.crm.domain.exception.EntityNotFoundException
import com.example.crm.domain.model.*
import com.example.crm.domain.repository.*
import com.example.crm.support.shouldBe
import com.example.crm.support.shouldThrow
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.OffsetDateTime

class UseCasesTest {

    private val pageable = PageRequest.of(0, 10)
    private val now = OffsetDateTime.parse("2026-01-01T10:15:30+00:00")

    @Test
    fun `it should execute address use case flow`() {
        val repository = mockk<AddressRepository>()
        val useCase = AddressUseCaseImpl(repository)
        val model = Address(id = 1, street = "Rua A", neighborhood = "Centro", cityId = 10, postalCode = "13000-000", createdAt = now, updatedAt = now)

        every { repository.findAll(pageable) } returns PageImpl(listOf(model))
        every { repository.findById(1) } returns model
        every { repository.findById(99) } returns null
        every { repository.save(any()) } answers { firstArg() }

        useCase.list(pageable).content.size shouldBe 1
        useCase.getById(1).id shouldBe 1
        useCase.create(model).id shouldBe 1
        useCase.update(1, model.copy(street = "Rua B")).street shouldBe "Rua B"
        useCase.delete(1)

        shouldThrow<EntityNotFoundException> { useCase.getById(99) }
    }

    @Test
    fun `it should execute appointment use case flow`() {
        val repository = mockk<AppointmentRepository>()
        val useCase = AppointmentUseCaseImpl(repository)
        val model = Appointment(id = 1, status = "SCHEDULED", scheduledAt = now, createdAt = now, updatedAt = now)

        every { repository.findAll(pageable) } returns PageImpl(listOf(model))
        every { repository.findById(1) } returns model
        every { repository.findById(99) } returns null
        every { repository.save(any()) } answers { firstArg() }
        every { repository.deleteById(1) } just runs

        useCase.list(pageable).content.first().status shouldBe "SCHEDULED"
        useCase.getById(1).id shouldBe 1
        useCase.create(model).id shouldBe 1
        useCase.update(1, model.copy(status = "DONE")).status shouldBe "DONE"
        useCase.delete(1)

        shouldThrow<EntityNotFoundException> { useCase.getById(99) }
    }

    @Test
    fun `it should execute read only location use cases`() {
        val countryRepo = mockk<CountryRepository>()
        val stateRepo = mockk<StateRepository>()
        val cityRepo = mockk<CityRepository>()
        val uomRepo = mockk<UnitOfMeasureRepository>()

        every { countryRepo.findAll(pageable) } returns PageImpl(listOf(Country(id = 1, iso2 = "BR", iso3 = "BRA", country = "Brasil", createdAt = now, updatedAt = now)))
        every { countryRepo.findById(1) } returns Country(id = 1, iso2 = "BR", iso3 = "BRA", country = "Brasil", createdAt = now, updatedAt = now)

        every { stateRepo.findAll(pageable) } returns PageImpl(listOf(State(id = 2, countryId = 1, acronym = "SP", state = "São Paulo", createdAt = now, updatedAt = now)))
        every { stateRepo.findById(2) } returns State(id = 2, countryId = 1, acronym = "SP", state = "São Paulo", createdAt = now, updatedAt = now)
        every { stateRepo.findByCountryId(1) } returns listOf(State(id = 2, countryId = 1, acronym = "SP", state = "São Paulo", createdAt = now, updatedAt = now))

        every { cityRepo.findAll(pageable) } returns PageImpl(listOf(City(id = 3, stateId = 2, city = "Campinas", createdAt = now, updatedAt = now)))
        every { cityRepo.findById(3) } returns City(id = 3, stateId = 2, city = "Campinas", createdAt = now, updatedAt = now)
        every { cityRepo.findByStateId(2) } returns listOf(City(id = 3, stateId = 2, city = "Campinas", createdAt = now, updatedAt = now))

        every { uomRepo.findAll(pageable) } returns PageImpl(listOf(UnitOfMeasure(id = 4, code = "UN", name = "Unidade", createdAt = now, updatedAt = now)))
        every { uomRepo.findById(4) } returns UnitOfMeasure(id = 4, code = "UN", name = "Unidade", createdAt = now, updatedAt = now)

        CountryUseCaseImpl(countryRepo).list(pageable).content.first().iso2 shouldBe "BR"
        StateUseCaseImpl(stateRepo).findByCountryId(1).first().acronym shouldBe "SP"
        CityUseCaseImpl(cityRepo).findByStateId(2).first().city shouldBe "Campinas"
        UnitOfMeasureUseCaseImpl(uomRepo).getById(4).code shouldBe "UN"
    }

    @Test
    fun `it should execute tenant filtered use cases`() {
        val customerRepo = mockk<CustomerRepository>()
        val personAddressRepo = mockk<PersonAddressRepository>()
        val itemRepo = mockk<ItemRepository>()
        val personRepo = mockk<PersonRepository>()
        val scheduleRepo = mockk<ScheduleRepository>()
        val workerRepo = mockk<WorkerRepository>()
        val userRepo = mockk<UserRepository>()
        val leadRepo = mockk<LeadRepository>()
        val orderRepo = mockk<OrderRepository>()
        val flowRepo = mockk<PipelineFlowRepository>()

        val customer = Customer(id = 1, tenantId = 10, fullName = "Maria", createdAt = now, updatedAt = now)
        every { customerRepo.findByTenantId(10, pageable) } returns PageImpl(listOf(customer))
        every { customerRepo.findAll(pageable) } returns PageImpl(listOf(customer))
        every { customerRepo.findById(1) } returns customer
        every { customerRepo.save(any()) } answers { firstArg() }

        val item = Item(id = 2, tenantId = 10, type = "SERVICE", name = "Consultoria", createdAt = now, updatedAt = now)
        every { itemRepo.findByTenantId(10, pageable) } returns PageImpl(listOf(item))
        every { itemRepo.findAll(pageable) } returns PageImpl(listOf(item))
        every { itemRepo.findById(2) } returns item
        every { itemRepo.save(any()) } answers { firstArg() }

        val person = Person(id = 3, tenantId = 10, createdAt = now, updatedAt = now)
        every { personRepo.findByTenantId(10, pageable) } returns PageImpl(listOf(person))
        every { personAddressRepo.findPrimaryAddressByPersonId(any()) } returns null
        every { personAddressRepo.findPrimaryAddressesByPersonIds(any()) } returns emptyMap()
        every { personAddressRepo.upsertPrimaryAddress(any(), any()) } answers { secondArg() }
        every { personRepo.findAll(pageable) } returns PageImpl(listOf(person))
        every { personRepo.findById(3) } returns person
        every { personRepo.save(any()) } answers { firstArg() }

        val schedule = Schedule(id = 4, tenantId = 10, customerId = 1, appointmentId = 7, createdAt = now, updatedAt = now)
        every { scheduleRepo.findByTenantId(10, pageable) } returns PageImpl(listOf(schedule))
        every { scheduleRepo.findAll(pageable) } returns PageImpl(listOf(schedule))
        every { scheduleRepo.findById(4) } returns schedule
        every { scheduleRepo.save(any()) } answers { firstArg() }

        val worker = Worker(id = 5, tenantId = 10, personId = 3, createdAt = now, updatedAt = now)
        every { workerRepo.findByTenantId(10, pageable) } returns PageImpl(listOf(worker))
        every { workerRepo.findAll(pageable) } returns PageImpl(listOf(worker))
        every { workerRepo.findById(5) } returns worker
        every { workerRepo.save(any()) } answers { firstArg() }

        val user = User(id = 6, tenantId = 10, email = "u@crm.com", passwordHash = "hash", createdAt = now, updatedAt = now)
        every { userRepo.findByTenantId(10, pageable) } returns PageImpl(listOf(user))
        every { userRepo.findAll(pageable) } returns PageImpl(listOf(user))
        every { userRepo.findById(6) } returns user
        every { userRepo.findByEmail("u@crm.com") } returns user
        every { userRepo.save(any()) } answers { firstArg() }

        val lead = Lead(id = 7, tenantId = 10, flowId = 20, createdAt = now, updatedAt = now)
        every { leadRepo.findByTenantId(10, pageable) } returns PageImpl(listOf(lead))
        every { leadRepo.findAll(pageable) } returns PageImpl(listOf(lead))
        every { leadRepo.findById(7) } returns lead
        every { leadRepo.findMessagesByLeadId(7) } returns listOf(LeadMessage(id = 1, leadId = 7, message = "oi", createdAt = now))
        every { leadRepo.save(any()) } answers { firstArg() }
        every { leadRepo.saveMessage(any()) } answers { firstArg() }
        every { leadRepo.deleteById(7) } just runs

        val order = Order(id = 8, tenantId = 10, customerId = 1, userId = 6, createdAt = now, updatedAt = now)
        every { orderRepo.findByTenantId(10, pageable) } returns PageImpl(listOf(order))
        every { orderRepo.findAll(pageable) } returns PageImpl(listOf(order))
        every { orderRepo.findById(8) } returns order
        every { orderRepo.save(any()) } answers { firstArg() }
        every { orderRepo.deleteById(8) } just runs

        val flow = PipelineFlow(id = 9, tenantId = 10, code = "FLOW", name = "Flow", createdAt = now, updatedAt = now)
        every { flowRepo.findByTenantId(10, pageable) } returns PageImpl(listOf(flow))
        every { flowRepo.findAll(pageable) } returns PageImpl(listOf(flow))
        every { flowRepo.findById(9) } returns flow
        every { flowRepo.save(any()) } answers { firstArg() }

        CustomerUseCaseImpl(
            customerRepo,
            personRepo,
            personAddressRepo
        ).list(pageable, 10).content.first().id shouldBe 1
        ItemUseCaseImpl(itemRepo).list(pageable, 10).content.first().id shouldBe 2
        PersonUseCaseImpl(personRepo).list(pageable, 10).content.first().id shouldBe 3
        ScheduleUseCaseImpl(scheduleRepo).list(pageable, 10).content.first().id shouldBe 4
        WorkerUseCaseImpl(workerRepo, personRepo, personAddressRepo).list(pageable, 10).content.first().id shouldBe 5

        val userUseCase = UserUseCaseImpl(userRepo, personRepo)
        userUseCase.getByEmail("u@crm.com")?.id shouldBe 6
        userUseCase.update(6, user.copy(email = "n@crm.com")).email shouldBe "n@crm.com"

        val leadUseCase = LeadUseCaseImpl(leadRepo)
        leadUseCase.getMessages(7).first().message shouldBe "oi"
        leadUseCase.createMessage(7, LeadMessage(leadId = 0, message = "nova")).leadId shouldBe 7
        leadUseCase.delete(7)

        OrderUseCaseImpl(orderRepo).delete(8)
        PipelineFlowUseCaseImpl(flowRepo).update(9, flow.copy(name = "New")).name shouldBe "New"
    }

    @Test
    fun `it should execute remaining simple crud use cases`() {
        val tenantRepo = mockk<TenantRepository>()
        val personRepo = mockk<PersonRepository>()
        val personAddressRepo = mockk<PersonAddressRepository>()
        val roleRepo = mockk<RoleRepository>()
        val permissionRepo = mockk<PermissionRepository>()
        val itemCategoryRepo = mockk<ItemCategoryRepository>()

        val tenant = Tenant(id = 1, name = "Tenant", category = "BUSINESS", createdAt = now, updatedAt = now)
        every { tenantRepo.findAll(pageable) } returns PageImpl(listOf(tenant))
        every { tenantRepo.findById(1) } returns tenant
        every { tenantRepo.save(any()) } answers { firstArg() }
        every { personRepo.findByTenantId(eq(1), any()) } returns PageImpl(emptyList())
        every { personAddressRepo.findPrimaryAddressByPersonId(any()) } returns null
        every { personAddressRepo.upsertPrimaryAddress(any(), any()) } answers { secondArg() }

        val role = Role(id = 2, name = "ADMIN", createdAt = now, updatedAt = now)
        every { roleRepo.findAll(pageable) } returns PageImpl(listOf(role))
        every { roleRepo.findById(2) } returns role
        every { roleRepo.save(any()) } answers { firstArg() }

        val permission = Permission(id = 3, code = "READ", createdAt = now, updatedAt = now)
        every { permissionRepo.findAll(pageable) } returns PageImpl(listOf(permission))
        every { permissionRepo.findById(3) } returns permission
        every { permissionRepo.save(any()) } answers { firstArg() }

        val category = ItemCategory(id = 4, tenantId = 10, name = "Cat", createdAt = now, updatedAt = now)
        every { itemCategoryRepo.findByTenantId(10, pageable) } returns PageImpl(listOf(category))
        every { itemCategoryRepo.findAll(pageable) } returns PageImpl(listOf(category))
        every { itemCategoryRepo.findById(4) } returns category
        every { itemCategoryRepo.save(any()) } answers { firstArg() }
        every { itemCategoryRepo.deleteById(4) } just runs

        TenantUseCaseImpl(tenantRepo, personRepo, personAddressRepo).create(tenant).id shouldBe 1
        RoleUseCaseImpl(roleRepo).update(2, role.copy(name = "USER")).name shouldBe "USER"
        PermissionUseCaseImpl(permissionRepo).delete(3)
        ItemCategoryUseCaseImpl(itemCategoryRepo).delete(4)

        verify { permissionRepo.save(match { !it.isActive }) }
        verify { itemCategoryRepo.deleteById(4) }
    }
}

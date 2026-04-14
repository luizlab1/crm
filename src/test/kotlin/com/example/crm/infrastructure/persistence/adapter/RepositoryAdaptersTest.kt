package com.example.crm.infrastructure.persistence.adapter

import com.example.crm.domain.model.*
import com.example.crm.domain.repository.PersonAddressRepository
import com.example.crm.infrastructure.persistence.mapper.*
import com.example.crm.infrastructure.persistence.repository.*
import com.example.crm.support.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.OffsetDateTime
import java.util.Optional

class RepositoryAdaptersTest {

    private val pageable = PageRequest.of(0, 10)
    private val now = OffsetDateTime.parse("2026-01-01T10:15:30+00:00")

    @Test
    fun `it should execute person repository adapter with contacts`() {
        val personRepo = mockk<PersonJpaRepository>()
        val contactRepo = mockk<ContactJpaRepository>()
        val mapper = PersonPersistenceMapper()
        val adapter = PersonRepositoryAdapter(personRepo, contactRepo, mapper)

        val person = Person(id = 1, tenantId = 10, contacts = listOf(Contact(id = 1, type = "EMAIL", contactValue = "m@crm.com", createdAt = now, updatedAt = now)), createdAt = now, updatedAt = now)
        val entity = mapper.toEntity(person)

        every { personRepo.findAll(pageable) } returns PageImpl(listOf(entity))
        every { personRepo.findByTenantId(10, pageable) } returns PageImpl(listOf(entity))
        every { personRepo.findById(1) } returns Optional.of(entity)
        every { personRepo.save(any()) } answers { firstArg() }
        every { contactRepo.findByPersonIdIn(listOf(1L)) } returns entity.contacts

        adapter.findAll(pageable).content.first().id shouldBe 1
        adapter.findByTenantId(10, pageable).content.first().tenantId shouldBe 10
        adapter.findById(1)?.id shouldBe 1
        adapter.save(person.copy(id = 0)).tenantId shouldBe 10
        adapter.save(person).id shouldBe 1
    }

    @Test
    fun `it should execute order repository adapter branches`() {
        val repo = mockk<OrderJpaRepository>()
        val mapper = OrderPersistenceMapper()
        val adapter = OrderRepositoryAdapter(repo, mapper)

        val order = Order(id = 1, tenantId = 10, customerId = 20, userId = 30, items = listOf(OrderItem(itemId = 100, unitPriceCents = 100, totalPriceCents = 100, createdAt = now)), createdAt = now, updatedAt = now)
        val entity = mapper.toEntity(order)

        every { repo.findAll(pageable) } returns PageImpl(listOf(entity))
        every { repo.findByTenantId(10, pageable) } returns PageImpl(listOf(entity))
        every { repo.findById(1) } returns Optional.of(entity)
        every { repo.save(any()) } answers { firstArg() }
        every { repo.deleteById(1) } just runs

        adapter.findAll(pageable).content.first().tenantId shouldBe 10
        adapter.findByTenantId(10, pageable).content.first().customerId shouldBe 20
        adapter.findById(1)?.id shouldBe 1
        adapter.save(order.copy(id = 0)).tenantId shouldBe 10
        adapter.save(order).id shouldBe 1
        adapter.deleteById(1)

        verify { repo.deleteById(1) }
    }

    @Test
    fun `it should execute pipeline flow and lead repository adapters`() {
        val flowRepo = mockk<PipelineFlowJpaRepository>()
        val flowMapper = PipelineFlowPersistenceMapper()
        val flowAdapter = PipelineFlowRepositoryAdapter(flowRepo, flowMapper)

        val flow = PipelineFlow(id = 1, tenantId = 10, code = "FLOW", name = "Flow", steps = listOf(PipelineFlowStep(stepOrder = 1, code = "S1", name = "Start", stepType = "START", createdAt = now, updatedAt = now)), createdAt = now, updatedAt = now)
        val flowEntity = flowMapper.toEntity(flow)

        every { flowRepo.findAll(pageable) } returns PageImpl(listOf(flowEntity))
        every { flowRepo.findByTenantId(10, pageable) } returns PageImpl(listOf(flowEntity))
        every { flowRepo.findById(1) } returns Optional.of(flowEntity)
        every { flowRepo.save(any()) } answers { firstArg() }

        flowAdapter.findAll(pageable).content.first().code shouldBe "FLOW"
        flowAdapter.findByTenantId(10, pageable).content.first().tenantId shouldBe 10
        flowAdapter.findById(1)?.id shouldBe 1
        flowAdapter.save(flow.copy(id = 0)).name shouldBe "Flow"

        val leadRepo = mockk<LeadJpaRepository>()
        val messageRepo = mockk<LeadMessageJpaRepository>()
        val leadMapper = LeadPersistenceMapper()
        val leadAdapter = LeadRepositoryAdapter(leadRepo, messageRepo, leadMapper)
        val lead = Lead(id = 2, tenantId = 10, flowId = 1, createdAt = now, updatedAt = now)
        val leadEntity = leadMapper.toEntity(lead)

        every { leadRepo.findAll(pageable) } returns PageImpl(listOf(leadEntity))
        every { leadRepo.findByTenantId(10, pageable) } returns PageImpl(listOf(leadEntity))
        every { leadRepo.findById(2) } returns Optional.of(leadEntity)
        every { leadRepo.save(any()) } answers { firstArg() }
        every { leadRepo.deleteById(2) } just runs

        val leadMessage = LeadMessage(id = 1, leadId = 2, message = "oi", createdAt = now)
        val leadMessageEntity = leadMapper.toEntity(leadMessage)
        every { messageRepo.findByLeadId(2) } returns listOf(leadMessageEntity)
        every { messageRepo.save(any()) } answers { firstArg() }

        leadAdapter.findAll(pageable).content.first().id shouldBe 2
        leadAdapter.findByTenantId(10, pageable).content.first().tenantId shouldBe 10
        leadAdapter.findById(2)?.id shouldBe 2
        leadAdapter.save(lead).flowId shouldBe 1
        leadAdapter.findMessagesByLeadId(2).first().message shouldBe "oi"
        leadAdapter.saveMessage(leadMessage).leadId shouldBe 2
        leadAdapter.deleteById(2)
    }

    @Test
    fun `it should execute customer and simple repository adapters`() {
        val customerRepo = mockk<CustomerJpaRepository>()
        val personJpaRepo = mockk<PersonJpaRepository>()
        val contactJpaRepo = mockk<ContactJpaRepository>()
        val personAddressRepo = mockk<PersonAddressRepository>()
        val customerMapper = CustomerPersistenceMapper()
        val personMapper = PersonPersistenceMapper()
        val customerAdapter = CustomerRepositoryAdapter(
            customerRepo,
            personJpaRepo,
            contactJpaRepo,
            personAddressRepo,
            customerMapper,
            personMapper
        )
        val customer = Customer(id = 1, tenantId = 10, fullName = "Maria", createdAt = now, updatedAt = now)
        val customerEntity = customerMapper.toEntity(customer)

        every { customerRepo.findAll(pageable) } returns PageImpl(listOf(customerEntity))
        every { customerRepo.findByTenantId(10, pageable) } returns PageImpl(listOf(customerEntity))
        every { customerRepo.findById(1) } returns Optional.of(customerEntity)
        every { customerRepo.save(any()) } answers { firstArg() }
        // customer sem personId — enrich não busca person
        every { personJpaRepo.findById(any<Long>()) } returns Optional.empty()
        every { personAddressRepo.findPrimaryAddressByPersonId(any()) } returns null

        customerAdapter.findAll(pageable).content.first().fullName shouldBe "Maria"
        customerAdapter.findByTenantId(10, pageable).content.first().tenantId shouldBe 10
        customerAdapter.findById(1)?.id shouldBe 1
        customerAdapter.save(customer).id shouldBe 1

        val tenantJpa = mockk<TenantJpaRepository>()
        val tenantMapper = TenantPersistenceMapper()
        val tenantAdapter = TenantRepositoryAdapter(tenantJpa, tenantMapper)
        val tenant = Tenant(id = 1, name = "Tenant", category = "BUSINESS", createdAt = now, updatedAt = now)
        val tenantEntity = tenantMapper.toEntity(tenant)
        every { tenantJpa.findAll(pageable) } returns PageImpl(listOf(tenantEntity))
        every { tenantJpa.findById(1) } returns Optional.of(tenantEntity)
        every { tenantJpa.save(any()) } answers { firstArg() }

        tenantAdapter.findAll(pageable).content.first().name shouldBe "Tenant"
        tenantAdapter.findById(1)?.id shouldBe 1
        tenantAdapter.save(tenant).id shouldBe 1

        val stateJpa = mockk<StateJpaRepository>()
        val stateMapper = StatePersistenceMapper()
        val stateAdapter = StateRepositoryAdapter(stateJpa, stateMapper)
        val stateEntity = com.example.crm.infrastructure.persistence.entity.StateJpaEntity(id = 1, countryId = 1, acronym = "SP", state = "São Paulo", ibgeCode = 35)
        every { stateJpa.findAll(pageable) } returns PageImpl(listOf(stateEntity))
        every { stateJpa.findById(1) } returns Optional.of(stateEntity)
        every { stateJpa.findByCountryId(1) } returns listOf(stateEntity)

        stateAdapter.findAll(pageable).content.first().acronym shouldBe "SP"
        stateAdapter.findById(1)?.countryId shouldBe 1
        stateAdapter.findByCountryId(1).size shouldBe 1

        val cityJpa = mockk<CityJpaRepository>()
        val cityMapper = CityPersistenceMapper()
        val cityAdapter = CityRepositoryAdapter(cityJpa, cityMapper)
        val cityEntity = com.example.crm.infrastructure.persistence.entity.CityJpaEntity(id = 1, stateId = 1, city = "Campinas", ibgeCode = 3509502)
        every { cityJpa.findAll(pageable) } returns PageImpl(listOf(cityEntity))
        every { cityJpa.findById(1) } returns Optional.of(cityEntity)
        every { cityJpa.findByStateId(1) } returns listOf(cityEntity)

        cityAdapter.findAll(pageable).content.first().city shouldBe "Campinas"
        cityAdapter.findById(1)?.stateId shouldBe 1
        cityAdapter.findByStateId(1).size shouldBe 1
    }
}

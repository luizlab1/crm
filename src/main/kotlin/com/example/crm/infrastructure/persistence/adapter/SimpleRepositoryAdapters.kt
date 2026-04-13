package com.example.crm.infrastructure.persistence.adapter

import com.example.crm.domain.model.*
import com.example.crm.domain.repository.*
import com.example.crm.infrastructure.persistence.entity.UserJpaEntity
import com.example.crm.infrastructure.persistence.entity.WorkerJpaEntity
import com.example.crm.infrastructure.persistence.mapper.*
import com.example.crm.infrastructure.persistence.repository.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class TenantRepositoryAdapter(
    private val jpa: TenantJpaRepository, private val mapper: TenantPersistenceMapper
) : TenantRepository {
    override fun findAll(pageable: Pageable): Page<Tenant> = jpa.findAll(pageable).map { mapper.toDomain(it) }
    override fun findById(id: Long): Tenant? = jpa.findById(id).map { mapper.toDomain(it) }.orElse(null)
    override fun save(tenant: Tenant): Tenant = mapper.toDomain(jpa.save(mapper.toEntity(tenant)))
}

@Component
class UserRepositoryAdapter(
    private val jpa: UserJpaRepository,
    private val mapper: UserPersistenceMapper,
    private val personJpaRepository: PersonJpaRepository,
    private val contactJpaRepository: ContactJpaRepository,
    private val personMapper: PersonPersistenceMapper
) : UserRepository {
    override fun findAll(pageable: Pageable): Page<User> = jpa.findAll(pageable).map { enrich(it) }
    override fun findByTenantId(tenantId: Long, pageable: Pageable): Page<User> = jpa.findByTenantId(tenantId, pageable).map { enrich(it) }
    override fun findById(id: Long): User? = jpa.findById(id).map { enrich(it) }.orElse(null)
    override fun save(user: User): User = mapper.toDomain(jpa.save(mapper.toEntity(user)))
    override fun findByEmail(email: String): User? = jpa.findByEmail(email)?.let { enrich(it) }

    private fun enrich(entity: UserJpaEntity): User {
        val base = mapper.toDomain(entity)
        val person = entity.personId?.let { pid ->
            personJpaRepository.findById(pid).map { personEntity ->
                val contacts = contactJpaRepository.findByPersonIdIn(listOf(pid))
                personMapper.toDomain(personEntity).copy(contacts = contacts.map { personMapper.toDomain(it) })
            }.orElse(null)
        }
        return base.copy(person = person)
    }
}

@Component
class WorkerRepositoryAdapter(
    private val jpa: WorkerJpaRepository,
    private val mapper: WorkerPersistenceMapper,
    private val personJpaRepository: PersonJpaRepository,
    private val contactJpaRepository: ContactJpaRepository,
    private val personMapper: PersonPersistenceMapper
) : WorkerRepository {
    override fun findAll(pageable: Pageable): Page<Worker> = jpa.findAll(pageable).map { enrich(it) }
    override fun findByTenantId(tenantId: Long, pageable: Pageable): Page<Worker> = jpa.findByTenantId(tenantId, pageable).map { enrich(it) }
    override fun findById(id: Long): Worker? = jpa.findById(id).map { enrich(it) }.orElse(null)
    override fun save(worker: Worker): Worker = mapper.toDomain(jpa.save(mapper.toEntity(worker)))

    private fun enrich(entity: WorkerJpaEntity): Worker {
        val base = mapper.toDomain(entity)
        val person = personJpaRepository.findById(entity.personId).map { personEntity ->
            val contacts = contactJpaRepository.findByPersonIdIn(listOf(entity.personId))
            personMapper.toDomain(personEntity).copy(contacts = contacts.map { personMapper.toDomain(it) })
        }.orElse(null)
        return base.copy(person = person)
    }
}

@Component
class ItemRepositoryAdapter(
    private val jpa: ItemJpaRepository, private val mapper: ItemPersistenceMapper
) : ItemRepository {
    override fun findAll(pageable: Pageable): Page<Item> = jpa.findAll(pageable).map { mapper.toDomain(it) }
    override fun findByTenantId(tenantId: Long, pageable: Pageable): Page<Item> = jpa.findByTenantId(tenantId, pageable).map { mapper.toDomain(it) }
    override fun findById(id: Long): Item? = jpa.findById(id).map { mapper.toDomain(it) }.orElse(null)
    override fun save(item: Item): Item = mapper.toDomain(jpa.save(mapper.toEntity(item)))
}

@Component
class ItemCategoryRepositoryAdapter(
    private val jpa: ItemCategoryJpaRepository, private val mapper: ItemCategoryPersistenceMapper
) : ItemCategoryRepository {
    override fun findAll(pageable: Pageable): Page<ItemCategory> = jpa.findAll(pageable).map { mapper.toDomain(it) }
    override fun findByTenantId(tenantId: Long, pageable: Pageable): Page<ItemCategory> = jpa.findByTenantId(tenantId, pageable).map { mapper.toDomain(it) }
    override fun findById(id: Long): ItemCategory? = jpa.findById(id).map { mapper.toDomain(it) }.orElse(null)
    override fun save(itemCategory: ItemCategory): ItemCategory = mapper.toDomain(jpa.save(mapper.toEntity(itemCategory)))
    override fun deleteById(id: Long) = jpa.deleteById(id)
}

@Component
class AddressRepositoryAdapter(
    private val jpa: AddressJpaRepository, private val mapper: AddressPersistenceMapper
) : AddressRepository {
    override fun findAll(pageable: Pageable): Page<Address> = jpa.findAll(pageable).map { mapper.toDomain(it) }
    override fun findById(id: Long): Address? = jpa.findById(id).map { mapper.toDomain(it) }.orElse(null)
    override fun save(address: Address): Address = mapper.toDomain(jpa.save(mapper.toEntity(address)))
}

@Component
class AppointmentRepositoryAdapter(
    private val jpa: AppointmentJpaRepository, private val mapper: AppointmentPersistenceMapper
) : AppointmentRepository {
    override fun findAll(pageable: Pageable): Page<Appointment> = jpa.findAll(pageable).map { mapper.toDomain(it) }
    override fun findById(id: Long): Appointment? = jpa.findById(id).map { mapper.toDomain(it) }.orElse(null)
    override fun save(appointment: Appointment): Appointment = mapper.toDomain(jpa.save(mapper.toEntity(appointment)))
    override fun deleteById(id: Long) = jpa.deleteById(id)
}

@Component
class ScheduleRepositoryAdapter(
    private val jpa: ScheduleJpaRepository, private val mapper: SchedulePersistenceMapper
) : ScheduleRepository {
    override fun findAll(pageable: Pageable): Page<Schedule> = jpa.findAll(pageable).map { mapper.toDomain(it) }
    override fun findByTenantId(tenantId: Long, pageable: Pageable): Page<Schedule> = jpa.findByTenantId(tenantId, pageable).map { mapper.toDomain(it) }
    override fun findById(id: Long): Schedule? = jpa.findById(id).map { mapper.toDomain(it) }.orElse(null)
    override fun save(schedule: Schedule): Schedule = mapper.toDomain(jpa.save(mapper.toEntity(schedule)))
}

@Component
class RoleRepositoryAdapter(
    private val jpa: RoleJpaRepository, private val mapper: RolePersistenceMapper
) : RoleRepository {
    override fun findAll(pageable: Pageable): Page<Role> = jpa.findAll(pageable).map { mapper.toDomain(it) }
    override fun findById(id: Long): Role? = jpa.findById(id).map { mapper.toDomain(it) }.orElse(null)
    override fun save(role: Role): Role = mapper.toDomain(jpa.save(mapper.toEntity(role)))
}

@Component
class PermissionRepositoryAdapter(
    private val jpa: PermissionJpaRepository, private val mapper: PermissionPersistenceMapper
) : PermissionRepository {
    override fun findAll(pageable: Pageable): Page<Permission> = jpa.findAll(pageable).map { mapper.toDomain(it) }
    override fun findById(id: Long): Permission? = jpa.findById(id).map { mapper.toDomain(it) }.orElse(null)
    override fun save(permission: Permission): Permission = mapper.toDomain(jpa.save(mapper.toEntity(permission)))
}

@Component
class CountryRepositoryAdapter(
    private val jpa: CountryJpaRepository, private val mapper: CountryPersistenceMapper
) : CountryRepository {
    override fun findAll(pageable: Pageable): Page<Country> = jpa.findAll(pageable).map { mapper.toDomain(it) }
    override fun findById(id: Long): Country? = jpa.findById(id).map { mapper.toDomain(it) }.orElse(null)
}

@Component
class StateRepositoryAdapter(
    private val jpa: StateJpaRepository, private val mapper: StatePersistenceMapper
) : StateRepository {
    override fun findAll(pageable: Pageable): Page<State> = jpa.findAll(pageable).map { mapper.toDomain(it) }
    override fun findById(id: Long): State? = jpa.findById(id).map { mapper.toDomain(it) }.orElse(null)
    override fun findByCountryId(countryId: Long): List<State> = jpa.findByCountryId(countryId).map { mapper.toDomain(it) }
}

@Component
class CityRepositoryAdapter(
    private val jpa: CityJpaRepository, private val mapper: CityPersistenceMapper
) : CityRepository {
    override fun findAll(pageable: Pageable): Page<City> = jpa.findAll(pageable).map { mapper.toDomain(it) }
    override fun findById(id: Long): City? = jpa.findById(id).map { mapper.toDomain(it) }.orElse(null)
    override fun findByStateId(stateId: Long): List<City> = jpa.findByStateId(stateId).map { mapper.toDomain(it) }
}

@Component
class UnitOfMeasureRepositoryAdapter(
    private val jpa: UnitOfMeasureJpaRepository, private val mapper: UnitOfMeasurePersistenceMapper
) : UnitOfMeasureRepository {
    override fun findAll(pageable: Pageable): Page<UnitOfMeasure> = jpa.findAll(pageable).map { mapper.toDomain(it) }
    override fun findById(id: Long): UnitOfMeasure? = jpa.findById(id).map { mapper.toDomain(it) }.orElse(null)
}

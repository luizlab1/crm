package com.example.crm.infrastructure.persistence.mapper

import com.example.crm.domain.model.*
import com.example.crm.infrastructure.persistence.entity.*
import org.springframework.stereotype.Component

@Component
class TenantPersistenceMapper {
    fun toDomain(e: TenantJpaEntity) = Tenant(
        id = e.id, parentTenantId = e.parentTenantId, code = e.code,
        name = e.name, category = e.category, isActive = e.isActive,
        createdAt = e.createdAt, updatedAt = e.updatedAt
    )
    fun toEntity(d: Tenant): TenantJpaEntity {
        val e = TenantJpaEntity(id = d.id, parentTenantId = d.parentTenantId, code = d.code,
            name = d.name, category = d.category, isActive = d.isActive)
        e.createdAt = d.createdAt; e.updatedAt = d.updatedAt; return e
    }
}

@Component
class UserPersistenceMapper {
    fun toDomain(e: UserJpaEntity) = User(
        id = e.id, tenantId = e.tenantId, personId = e.personId, code = e.code,
        email = e.email, passwordHash = e.passwordHash, isActive = e.isActive,
        createdAt = e.createdAt, updatedAt = e.updatedAt
    )
    fun toEntity(d: User): UserJpaEntity {
        val e = UserJpaEntity(id = d.id, tenantId = d.tenantId, personId = d.personId, code = d.code,
            email = d.email, passwordHash = d.passwordHash, isActive = d.isActive)
        e.createdAt = d.createdAt; e.updatedAt = d.updatedAt; return e
    }
}

@Component
class WorkerPersistenceMapper {
    fun toDomain(e: WorkerJpaEntity) = Worker(
        id = e.id, code = e.code, tenantId = e.tenantId, personId = e.personId,
        userId = e.userId, isActive = e.isActive, createdAt = e.createdAt, updatedAt = e.updatedAt
    )
    fun toEntity(d: Worker): WorkerJpaEntity {
        val e = WorkerJpaEntity(id = d.id, code = d.code, tenantId = d.tenantId,
            personId = d.personId, userId = d.userId, isActive = d.isActive)
        e.createdAt = d.createdAt; e.updatedAt = d.updatedAt; return e
    }
}

@Component
class ItemPersistenceMapper {
    fun toDomain(e: ItemJpaEntity) = Item(
        id = e.id, code = e.code, tenantId = e.tenantId, categoryId = e.categoryId,
        type = e.type, name = e.name, sku = e.sku, isActive = e.isActive,
        createdAt = e.createdAt, updatedAt = e.updatedAt
    )
    fun toEntity(d: Item): ItemJpaEntity {
        val e = ItemJpaEntity(id = d.id, code = d.code, tenantId = d.tenantId, categoryId = d.categoryId,
            type = d.type, name = d.name, sku = d.sku, isActive = d.isActive)
        e.createdAt = d.createdAt; e.updatedAt = d.updatedAt; return e
    }
}

@Component
class ItemCategoryPersistenceMapper {
    fun toDomain(e: ItemCategoryJpaEntity) = ItemCategory(
        id = e.id, tenantId = e.tenantId, name = e.name, availableTypes = e.availableTypes,
        createdAt = e.createdAt, updatedAt = e.updatedAt
    )
    fun toEntity(d: ItemCategory): ItemCategoryJpaEntity {
        val e = ItemCategoryJpaEntity(
            id = d.id, tenantId = d.tenantId, name = d.name,
            availableTypes = d.availableTypes.toMutableSet()
        )
        e.createdAt = d.createdAt; e.updatedAt = d.updatedAt; return e
    }
}

@Component
class AddressPersistenceMapper {
    fun toDomain(e: AddressJpaEntity) = Address(
        id = e.id, street = e.street, number = e.number, complement = e.complement,
        neighborhood = e.neighborhood, cityId = e.cityId, postalCode = e.postalCode,
        latitude = e.latitude, longitude = e.longitude, isActive = e.isActive,
        createdAt = e.createdAt, updatedAt = e.updatedAt
    )
    fun toEntity(d: Address): AddressJpaEntity {
        val e = AddressJpaEntity(id = d.id, street = d.street, number = d.number, complement = d.complement,
            neighborhood = d.neighborhood, cityId = d.cityId, postalCode = d.postalCode,
            latitude = d.latitude, longitude = d.longitude, isActive = d.isActive)
        e.createdAt = d.createdAt; e.updatedAt = d.updatedAt; return e
    }
}

@Component
class AppointmentPersistenceMapper {
    fun toDomain(e: AppointmentJpaEntity) = Appointment(
        id = e.id, code = e.code, status = e.status, scheduledAt = e.scheduledAt,
        startedAt = e.startedAt, finishedAt = e.finishedAt, totalCents = e.totalCents,
        notes = e.notes, createdAt = e.createdAt, updatedAt = e.updatedAt
    )
    fun toEntity(d: Appointment): AppointmentJpaEntity {
        val e = AppointmentJpaEntity(id = d.id, code = d.code, status = d.status,
            scheduledAt = d.scheduledAt, startedAt = d.startedAt, finishedAt = d.finishedAt,
            totalCents = d.totalCents, notes = d.notes)
        e.createdAt = d.createdAt; e.updatedAt = d.updatedAt; return e
    }
}

@Component
class SchedulePersistenceMapper {
    fun toDomain(e: ScheduleJpaEntity) = Schedule(
        id = e.id, code = e.code, tenantId = e.tenantId, customerId = e.customerId,
        appointmentId = e.appointmentId, description = e.description, isActive = e.isActive,
        createdAt = e.createdAt, updatedAt = e.updatedAt
    )
    fun toEntity(d: Schedule): ScheduleJpaEntity {
        val e = ScheduleJpaEntity(id = d.id, code = d.code, tenantId = d.tenantId,
            customerId = d.customerId, appointmentId = d.appointmentId,
            description = d.description, isActive = d.isActive)
        e.createdAt = d.createdAt; e.updatedAt = d.updatedAt; return e
    }
}

@Component
class RolePersistenceMapper {
    fun toDomain(e: RoleJpaEntity) = Role(
        id = e.id, name = e.name, description = e.description,
        isActive = e.isActive, createdAt = e.createdAt, updatedAt = e.updatedAt
    )
    fun toEntity(d: Role): RoleJpaEntity {
        val e = RoleJpaEntity(id = d.id, name = d.name, description = d.description, isActive = d.isActive)
        e.createdAt = d.createdAt; e.updatedAt = d.updatedAt; return e
    }
}

@Component
class PermissionPersistenceMapper {
    fun toDomain(e: PermissionJpaEntity) = Permission(
        id = e.id, code = e.code, description = e.description,
        isActive = e.isActive, createdAt = e.createdAt, updatedAt = e.updatedAt
    )
    fun toEntity(d: Permission): PermissionJpaEntity {
        val e = PermissionJpaEntity(id = d.id, code = d.code, description = d.description, isActive = d.isActive)
        e.createdAt = d.createdAt; e.updatedAt = d.updatedAt; return e
    }
}

@Component
class CountryPersistenceMapper {
    fun toDomain(e: CountryJpaEntity) = Country(
        id = e.id, iso2 = e.iso2, iso3 = e.iso3, country = e.country,
        createdAt = e.createdAt, updatedAt = e.updatedAt
    )
}

@Component
class StatePersistenceMapper {
    fun toDomain(e: StateJpaEntity) = State(
        id = e.id, countryId = e.countryId, acronym = e.acronym,
        state = e.state, ibgeCode = e.ibgeCode,
        createdAt = e.createdAt, updatedAt = e.updatedAt
    )
}

@Component
class CityPersistenceMapper {
    fun toDomain(e: CityJpaEntity) = City(
        id = e.id, stateId = e.stateId, city = e.city,
        ibgeCode = e.ibgeCode, createdAt = e.createdAt, updatedAt = e.updatedAt
    )
}

@Component
class UnitOfMeasurePersistenceMapper {
    fun toDomain(e: UnitOfMeasureJpaEntity) = UnitOfMeasure(
        id = e.id, code = e.code, name = e.name, symbol = e.symbol,
        isActive = e.isActive, createdAt = e.createdAt, updatedAt = e.updatedAt
    )
}


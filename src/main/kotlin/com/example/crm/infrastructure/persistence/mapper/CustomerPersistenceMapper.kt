package com.example.crm.infrastructure.persistence.mapper

import com.example.crm.domain.model.Customer
import com.example.crm.infrastructure.persistence.entity.CustomerJpaEntity
import org.springframework.stereotype.Component

@Component
class CustomerPersistenceMapper {

    fun toDomain(entity: CustomerJpaEntity): Customer = Customer(
        id = entity.id, code = entity.code, tenantId = entity.tenantId,
        personId = entity.personId, fullName = entity.fullName,
        email = entity.email, phone = entity.phone, document = entity.document,
        isActive = entity.isActive, createdAt = entity.createdAt, updatedAt = entity.updatedAt
    )

    fun toEntity(domain: Customer): CustomerJpaEntity {
        val entity = CustomerJpaEntity(
            id = domain.id, code = domain.code, tenantId = domain.tenantId,
            personId = domain.personId, fullName = domain.fullName,
            email = domain.email, phone = domain.phone, document = domain.document,
            isActive = domain.isActive
        )
        entity.createdAt = domain.createdAt
        entity.updatedAt = domain.updatedAt
        return entity
    }
}


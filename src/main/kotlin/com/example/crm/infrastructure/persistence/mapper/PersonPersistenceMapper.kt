package com.example.crm.infrastructure.persistence.mapper

import com.example.crm.domain.model.Contact
import com.example.crm.domain.model.Person
import com.example.crm.domain.model.PersonLegal
import com.example.crm.domain.model.PersonPhysical
import com.example.crm.infrastructure.persistence.entity.ContactJpaEntity
import com.example.crm.infrastructure.persistence.entity.PersonJpaEntity
import com.example.crm.infrastructure.persistence.entity.PersonLegalJpaEntity
import com.example.crm.infrastructure.persistence.entity.PersonPhysicalJpaEntity
import org.springframework.stereotype.Component

@Component
class PersonPersistenceMapper {

    fun toDomain(entity: PersonJpaEntity): Person = Person(
        id = entity.id,
        tenantId = entity.tenantId,
        code = entity.code,
        isActive = entity.isActive,
        physical = entity.physical?.let {
            PersonPhysical(fullName = it.fullName, cpf = it.cpf, birthDate = it.birthDate)
        },
        legal = entity.legal?.let {
            PersonLegal(corporateName = it.corporateName, tradeName = it.tradeName, cnpj = it.cnpj)
        },
        contacts = entity.contacts.map { toDomain(it) },
        createdAt = entity.createdAt,
        updatedAt = entity.updatedAt
    )

    fun toDomain(entity: ContactJpaEntity): Contact = Contact(
        id = entity.id,
        type = entity.type,
        contactValue = entity.contactValue,
        isPrimary = entity.isPrimary,
        isActive = entity.isActive,
        createdAt = entity.createdAt,
        updatedAt = entity.updatedAt
    )

    fun toEntity(domain: Person): PersonJpaEntity {
        val entity = PersonJpaEntity(
            id = domain.id,
            tenantId = domain.tenantId,
            code = domain.code,
            isActive = domain.isActive
        )
        entity.createdAt = domain.createdAt
        entity.updatedAt = domain.updatedAt

        domain.physical?.let {
            val phys = PersonPhysicalJpaEntity(
                personId = domain.id,
                fullName = it.fullName,
                cpf = it.cpf,
                birthDate = it.birthDate
            )
            phys.person = entity
            entity.physical = phys
        }

        domain.legal?.let {
            val leg = PersonLegalJpaEntity(
                personId = domain.id,
                corporateName = it.corporateName,
                tradeName = it.tradeName,
                cnpj = it.cnpj
            )
            leg.person = entity
            entity.legal = leg
        }

        domain.contacts.forEach { c ->
            entity.contacts.add(
                ContactJpaEntity(
                    id = c.id,
                    personId = domain.id,
                    type = c.type,
                    contactValue = c.contactValue,
                    isPrimary = c.isPrimary,
                    isActive = c.isActive
                )
            )
        }

        return entity
    }
}


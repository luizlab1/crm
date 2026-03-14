package com.example.crm.application.service

import com.example.crm.infrastructure.persistence.repository.PersonJpaRepository
import com.example.crm.infrastructure.persistence.repository.ContactJpaRepository
import com.example.crm.infrastructure.web.dto.response.PersonResponse
import com.example.crm.infrastructure.web.dto.response.ContactResponse
import com.example.crm.infrastructure.web.dto.response.PersonPhysicalResponse
import com.example.crm.infrastructure.web.dto.response.PersonLegalResponse
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class PersonService(
    private val personRepository: PersonJpaRepository,
    private val contactRepository: ContactJpaRepository
) {
    fun list(pageable: Pageable, tenantId: Long?): PageImpl<PersonResponse> {
        val page = if (tenantId != null) personRepository.findByTenantId(tenantId, pageable)
                   else personRepository.findAll(pageable)

        val ids = page.content.map { it.id }
        val contacts = if (ids.isEmpty()) emptyList() else contactRepository.findByPersonIdIn(ids)
        val contactsByPerson = contacts.groupBy { it.personId }

        val dtoList = page.content.map { p ->
            val phys = p.physical
            val legal = p.legal
            val contactDtos = contactsByPerson[p.id]?.map {
                ContactResponse(
                    id = it.id, type = it.type, contactValue = it.contactValue,
                    isPrimary = it.isPrimary, isActive = it.isActive,
                    createdAt = it.createdAt, updatedAt = it.updatedAt
                )
            } ?: emptyList()

            PersonResponse(
                id = p.id, tenantId = p.tenantId, code = p.code, isActive = p.isActive,
                physical = phys?.let { PersonPhysicalResponse(it.fullName, it.cpf, it.birthDate) },
                legal = legal?.let { PersonLegalResponse(it.corporateName, it.tradeName, it.cnpj) },
                contacts = contactDtos,
                createdAt = p.createdAt, updatedAt = p.updatedAt
            )
        }

        return PageImpl(dtoList, pageable, page.totalElements)
    }
}




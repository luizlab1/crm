package com.example.crm.infrastructure.persistence.adapter

import com.example.crm.domain.model.PersonAddress
import com.example.crm.domain.repository.PersonAddressRepository
import com.example.crm.infrastructure.persistence.entity.PersonAddressJpaEntity
import com.example.crm.infrastructure.persistence.mapper.AddressPersistenceMapper
import com.example.crm.infrastructure.persistence.repository.AddressJpaRepository
import com.example.crm.infrastructure.persistence.repository.PersonAddressJpaRepository
import org.springframework.stereotype.Component

@Component
class PersonAddressRepositoryAdapter(
    private val personAddressJpaRepository: PersonAddressJpaRepository,
    private val addressJpaRepository: AddressJpaRepository,
    private val addressMapper: AddressPersistenceMapper
) : PersonAddressRepository {

    override fun findAddressesByPersonId(personId: Long): List<PersonAddress> {
        val links = personAddressJpaRepository.findByPersonIdOrderByIsPrimaryDescIdAsc(personId)
        return toAddressList(links)
    }

    override fun findAddressesByPersonIds(personIds: List<Long>): Map<Long, List<PersonAddress>> {
        if (personIds.isEmpty()) return emptyMap()

        val linksByPerson = personAddressJpaRepository.findByPersonIdIn(personIds)
            .groupBy { it.personId }
            .mapValues { (_, links) ->
                links.sortedWith(
                    compareByDescending<PersonAddressJpaEntity> { it.isPrimary }
                        .thenBy { it.id }
                )
            }

        return linksByPerson.mapValues { (_, links) -> toAddressList(links) }
    }

    override fun replaceAddresses(personId: Long, addresses: List<PersonAddress>): List<PersonAddress> {
        if (addresses.isEmpty()) {
            val existingLinks = personAddressJpaRepository.findByPersonIdOrderByIsPrimaryDescIdAsc(personId)
            if (existingLinks.isNotEmpty()) {
                personAddressJpaRepository.deleteAll(existingLinks)
            }
            return emptyList()
        }

        val existingLinks = personAddressJpaRepository.findByPersonIdOrderByIsPrimaryDescIdAsc(personId)
        val existingAddressIds = existingLinks.map { it.addressId }

        val savedAddresses = addresses.mapIndexed { index, personAddress ->
            val fallbackAddressId = existingAddressIds.getOrNull(index) ?: 0L
            val addressIdToUse = if (personAddress.address.id != 0L) personAddress.address.id else fallbackAddressId
            val entity = addressMapper.toEntity(personAddress.address.copy(id = addressIdToUse))
            val saved = addressJpaRepository.save(entity)
            personAddress.copy(address = addressMapper.toDomain(saved), type = normalizeType(personAddress.type))
        }

        if (existingLinks.isNotEmpty()) {
            personAddressJpaRepository.deleteAll(existingLinks)
        }

        val hasPrimary = savedAddresses.any { it.isPrimary }
        savedAddresses.forEachIndexed { index, savedAddress ->
            personAddressJpaRepository.save(
                PersonAddressJpaEntity(
                    personId = personId,
                    addressId = savedAddress.address.id,
                    type = normalizeType(savedAddress.type),
                    isPrimary = if (hasPrimary) savedAddress.isPrimary else index == 0
                )
            )
        }

        return findAddressesByPersonId(personId)
    }

    private fun toAddressList(links: List<PersonAddressJpaEntity>): List<PersonAddress> {
        if (links.isEmpty()) return emptyList()

        val addressesById = addressJpaRepository.findAllById(links.map { it.addressId }.distinct())
            .associateBy { it.id }

        return links.mapNotNull { link ->
            addressesById[link.addressId]?.let {
                PersonAddress(
                    address = addressMapper.toDomain(it),
                    type = normalizeType(link.type),
                    isPrimary = link.isPrimary,
                    createdAt = link.createdAt,
                    updatedAt = link.updatedAt
                )
            }
        }
    }

    private fun normalizeType(type: String): String {
        val normalized = type.trim().uppercase()
        return when (normalized) {
            "COMMERCIAL" -> "COMMERCIAL"
            else -> "RESIDENTIAL"
        }
    }
}

package com.example.crm.infrastructure.persistence.adapter

import com.example.crm.domain.model.Address
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

    override fun findPrimaryAddressByPersonId(personId: Long): Address? {
        val link = personAddressJpaRepository
            .findByPersonIdOrderByIsPrimaryDescIdAsc(personId)
            .firstOrNull() ?: return null

        return addressJpaRepository.findById(link.addressId)
            .map { addressMapper.toDomain(it) }
            .orElse(null)
    }

    override fun findPrimaryAddressesByPersonIds(personIds: List<Long>): Map<Long, Address> {
        if (personIds.isEmpty()) return emptyMap()

        val linksByPerson = personAddressJpaRepository.findByPersonIdIn(personIds)
            .groupBy { it.personId }
            .mapValues { (_, links) ->
                links.sortedWith(
                    compareByDescending<PersonAddressJpaEntity> { it.isPrimary }
                        .thenBy { it.id }
                ).first()
            }

        val addressesById = addressJpaRepository.findAllById(linksByPerson.values.map { it.addressId }.distinct())
            .associateBy { it.id }

        return linksByPerson.mapNotNull { (personId, link) ->
            addressesById[link.addressId]?.let { personId to addressMapper.toDomain(it) }
        }.toMap()
    }

    override fun upsertPrimaryAddress(personId: Long, address: Address): Address {
        val existingPrimaryLink = personAddressJpaRepository
            .findByPersonIdOrderByIsPrimaryDescIdAsc(personId)
            .firstOrNull()
        val addressIdToUse = if (address.id != 0L) address.id else existingPrimaryLink?.addressId ?: 0L

        val savedAddress = addressJpaRepository.save(addressMapper.toEntity(address.copy(id = addressIdToUse)))

        val existingLinks = personAddressJpaRepository.findByPersonIdOrderByIsPrimaryDescIdAsc(personId)
        if (existingLinks.isNotEmpty()) {
            personAddressJpaRepository.deleteAll(existingLinks)
        }

        personAddressJpaRepository.save(
            PersonAddressJpaEntity(
                personId = personId,
                addressId = savedAddress.id,
                type = "MAIN",
                isPrimary = true
            )
        )

        return addressMapper.toDomain(savedAddress)
    }
}

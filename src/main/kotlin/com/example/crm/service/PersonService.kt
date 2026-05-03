package com.example.crm.service

import com.example.crm.entity.AddressEntity
import com.example.crm.entity.ContactEntity
import com.example.crm.entity.PersonAddressEntity
import com.example.crm.entity.PersonEntity
import com.example.crm.exception.EntityNotFoundException
import com.example.crm.repository.AddressRepository
import com.example.crm.repository.ContactRepository
import com.example.crm.repository.PersonAddressRepository
import com.example.crm.repository.PersonRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PersonService(
    private val personRepository: PersonRepository,
    private val contactRepository: ContactRepository,
    private val personAddressRepository: PersonAddressRepository,
    private val addressRepository: AddressRepository
) {

    @Transactional(readOnly = true)
    fun list(pageable: Pageable, tenantId: Long?): Page<PersonEntity> =
        if (tenantId != null) personRepository.findByTenantId(tenantId, pageable)
        else personRepository.findAll(pageable)

    @Transactional(readOnly = true)
    fun getById(id: Long): PersonEntity =
        personRepository.findById(id).orElseThrow { EntityNotFoundException("Person", id) }

    @Transactional(readOnly = true)
    fun getContacts(personId: Long): List<ContactEntity> =
        contactRepository.findByPersonId(personId)

    @Transactional(readOnly = true)
    fun getAddresses(personId: Long): List<PersonAddressWithAddress> =
        loadPersonAddresses(personId)

    fun create(
        person: PersonEntity,
        contacts: List<ContactEntity>,
        addressRequests: List<PersonAddressRequest>
    ): PersonEntity {
        val saved = personRepository.save(person)
        contacts.forEach { c ->
            contactRepository.save(ContactEntity(
                personId = saved.id,
                type = c.type,
                contactValue = c.contactValue,
                isPrimary = c.isPrimary,
                isActive = c.isActive
            ))
        }
        if (addressRequests.isNotEmpty()) replaceAddresses(saved.id, addressRequests)
        return saved
    }

    fun update(
        id: Long,
        person: PersonEntity,
        contacts: List<ContactEntity>,
        addressRequests: List<PersonAddressRequest>
    ): PersonEntity {
        val existing = getById(id)
        person.physical?.let { ph ->
            if (existing.physical == null) {
                existing.physical = ph.also { it.person = existing }
            } else {
                existing.physical!!.fullName = ph.fullName
                existing.physical!!.cpf = ph.cpf
                existing.physical!!.birthDate = ph.birthDate
            }
        }
        person.legal?.let { lg ->
            if (existing.legal == null) {
                existing.legal = lg.also { it.person = existing }
            } else {
                existing.legal!!.corporateName = lg.corporateName
                existing.legal!!.tradeName = lg.tradeName
                existing.legal!!.cnpj = lg.cnpj
            }
        }
        existing.isActive = person.isActive

        val saved = personRepository.save(existing)

        val existingContacts = contactRepository.findByPersonId(saved.id)
        contactRepository.deleteAll(existingContacts)
        contacts.forEach { c ->
            contactRepository.save(ContactEntity(
                personId = saved.id,
                type = c.type,
                contactValue = c.contactValue,
                isPrimary = c.isPrimary,
                isActive = c.isActive
            ))
        }
        if (addressRequests.isNotEmpty()) replaceAddresses(saved.id, addressRequests)
        return saved
    }

    fun delete(id: Long) {
        val existing = getById(id)
        existing.isActive = false
        personRepository.save(existing)
    }

    fun upsertPerson(
        existingPersonId: Long?,
        personData: PersonEntity,
        tenantId: Long,
        contacts: List<ContactEntity>
    ): Long {
        val personToSave: PersonEntity = if (existingPersonId != null && existingPersonId != 0L) {
            personRepository.findById(existingPersonId).orElse(null)?.also { existing ->
                personData.physical?.let { ph ->
                    if (existing.physical == null) existing.physical = ph.also { it.person = existing }
                    else {
                        existing.physical!!.fullName = ph.fullName
                        existing.physical!!.cpf = ph.cpf
                        existing.physical!!.birthDate = ph.birthDate
                    }
                }
                personData.legal?.let { lg ->
                    if (existing.legal == null) existing.legal = lg.also { it.person = existing }
                    else {
                        existing.legal!!.corporateName = lg.corporateName
                        existing.legal!!.tradeName = lg.tradeName
                        existing.legal!!.cnpj = lg.cnpj
                    }
                }
                existing.isActive = personData.isActive
            } ?: personData.also { it.tenantId = tenantId }
        } else {
            personData.also { it.tenantId = tenantId }
        }

        val saved = personRepository.save(personToSave)
        val existingContacts = contactRepository.findByPersonId(saved.id)
        contactRepository.deleteAll(existingContacts)
        contacts.forEach { c ->
            contactRepository.save(ContactEntity(
                personId = saved.id,
                type = c.type,
                contactValue = c.contactValue,
                isPrimary = c.isPrimary,
                isActive = c.isActive
            ))
        }
        return saved.id
    }

    fun replaceAddresses(personId: Long, addressRequests: List<PersonAddressRequest>) {
        val existingLinks = personAddressRepository.findByPersonIdOrderByIsPrimaryDescIdAsc(personId)
        personAddressRepository.deleteAll(existingLinks)
        val hasPrimary = addressRequests.any { it.isPrimary }
        addressRequests.forEachIndexed { index, req ->
            val savedAddress = addressRepository.save(AddressEntity(
                street = req.street,
                number = req.number,
                complement = req.complement,
                neighborhood = req.neighborhood,
                cityId = req.cityId,
                postalCode = req.postalCode,
                latitude = req.latitude,
                longitude = req.longitude,
                isActive = req.isActive
            ))
            personAddressRepository.save(PersonAddressEntity(
                personId = personId,
                addressId = savedAddress.id,
                type = req.type.name,
                isPrimary = if (hasPrimary) req.isPrimary else index == 0
            ))
        }
    }

    fun loadPersonAddresses(personId: Long): List<PersonAddressWithAddress> {
        val links = personAddressRepository.findByPersonIdOrderByIsPrimaryDescIdAsc(personId)
        return links.mapNotNull { link ->
            addressRepository.findById(link.addressId).orElse(null)?.let { addr ->
                PersonAddressWithAddress(link, addr)
            }
        }
    }
}

package com.example.crm.infrastructure.persistence.repository

import com.example.crm.infrastructure.persistence.entity.AddressJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface AddressJpaRepository : JpaRepository<AddressJpaEntity, Long>


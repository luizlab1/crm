package com.example.crm.repository

import com.example.crm.entity.AddressEntity
import org.springframework.data.jpa.repository.JpaRepository

interface AddressRepository : JpaRepository<AddressEntity, Long>

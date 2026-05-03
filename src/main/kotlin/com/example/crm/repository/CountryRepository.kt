package com.example.crm.repository

import com.example.crm.entity.CountryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CountryRepository : JpaRepository<CountryEntity, Long>

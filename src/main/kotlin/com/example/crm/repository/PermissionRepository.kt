package com.example.crm.repository

import com.example.crm.entity.PermissionEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PermissionRepository : JpaRepository<PermissionEntity, Long>

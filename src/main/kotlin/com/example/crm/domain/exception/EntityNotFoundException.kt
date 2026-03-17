package com.example.crm.domain.exception

class EntityNotFoundException(
    val entity: String,
    val id: Any
) : RuntimeException("$entity not found: $id")


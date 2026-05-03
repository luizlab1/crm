package com.example.crm.exception

class EntityNotFoundException(
    val entity: String,
    val id: Any
) : RuntimeException("$entity not found: $id")

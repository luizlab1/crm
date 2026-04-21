package com.example.crm.domain.exception

class EntityHasDependenciesException(
    val entity: String,
    val id: Any,
    val dependencyType: String,
    val count: Long
) : IllegalArgumentException("Cannot delete $entity with id $id: has $count $dependencyType")

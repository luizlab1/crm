package com.example.crm.exception

data class ValidationError(
    val field: String,
    val message: String,
    val code: String
)

class RequestValidationException(
    val errors: List<ValidationError>
) : IllegalArgumentException("Erro de validacao")

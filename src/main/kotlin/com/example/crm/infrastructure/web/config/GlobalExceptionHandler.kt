package com.example.crm.infrastructure.web.config

import com.example.crm.exception.EntityHasDependenciesException
import com.example.crm.exception.EntityNotFoundException
import com.example.crm.exception.RequestValidationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    data class FieldErrorResponse(
        val field: String,
        val message: String,
        val code: String
    )

    data class ErrorResponse(
        val status: Int,
        val error: String,
        val message: String?,
        val errors: List<FieldErrorResponse>? = null
    )

    @ExceptionHandler(RequestValidationException::class)
    fun handleValidation(ex: RequestValidationException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponse(
                    status = 400,
                    error = "Bad Request",
                    message = ex.message,
                    errors = ex.errors.map {
                        FieldErrorResponse(
                            field = it.field,
                            message = it.message,
                            code = it.code
                        )
                    }
                )
            )

    @ExceptionHandler(EntityHasDependenciesException::class)
    fun handleEntityHasDependencies(ex: EntityHasDependenciesException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(400, "Bad Request", ex.message))

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(ex: NoSuchElementException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(404, "Not Found", ex.message))

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFound(ex: EntityNotFoundException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(404, "Not Found", ex.message))

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(ex: IllegalArgumentException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(400, "Bad Request", ex.message))

    @ExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse(500, "Internal Server Error", ex.message))
}


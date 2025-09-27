package com.hsryuuu.stock.application.exception

import com.hsryuuu.stock.application.response.StandardResponse
import com.hsryuuu.stock.application.type.OperationResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)
    
    @ExceptionHandler(GlobalException::class)
    fun handleBaseException(e: GlobalException): ResponseEntity<StandardResponse<Any>> {
        val standardResponse = StandardResponse(
            result = OperationResult.ERROR,
            statusCode = e.status.value(),
            message = e.cause?.message ?: e.message ?: "Unknown error",
            data = e.data
        )

        log.error("GlobalExceptionHandler-GlobalException => Error Message: {}", e.message, e)
        return ResponseEntity.status(e.status).body(standardResponse)
    }

    @ExceptionHandler(Exception::class)
    fun handleUnknownException(e: Exception): ResponseEntity<StandardResponse<Nothing>> {
        val standardResponse = StandardResponse(
            result = OperationResult.ERROR,
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            message = e.message ?: "Unexpected error",
            data = null
        )

        log.error("GlobalExceptionHandler-UnknownException Error: {}", e.message, e)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(standardResponse)
    }
}
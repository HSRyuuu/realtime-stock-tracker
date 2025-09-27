package com.hsryuuu.stock.domain.log.externalapi

import com.hsryuuu.stock.application.type.DomainType
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime

@Entity
@Table(name = "EXTERNAL_API_ERROR_LOG")
data class ExternalApiErrorLog(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "exception_type", nullable = false, length = 255)
    val exceptionType: DomainType,

    @Column(name = "class_name", nullable = false, length = 255)
    val className: String,

    @Column(name = "method_name", nullable = false, length = 255)
    val methodName: String,

    @Column(name = "file_name", length = 255)
    val fileName: String? = null,

    @Column(name = "line_number")
    val lineNumber: Int? = null,

    @Column(name = "parameters")
    val parameters: String? = null,

    @Lob
    @Column(name = "error_message")
    val errorMessage: String? = null,

    @CreatedDate
    @Column(name = "occurred_at", nullable = false, updatable = false, insertable = false)
    val occurredAt: LocalDateTime? = null
) {
    companion object {
        fun fromException(
            e: Exception,
            domainType: DomainType,
            parameters: Map<String, Any>? = null
        ): ExternalApiErrorLog {
            val element = e.stackTrace.firstOrNull { it.className.startsWith("com.hsryuuu") }
            return ExternalApiErrorLog(
                exceptionType = domainType,
                className = element?.className ?: "UnknownClass",
                methodName = element?.methodName ?: "UnknownMethod",
                fileName = element?.fileName,
                lineNumber = element?.lineNumber,
                parameters = parameters.toString(),
                errorMessage = e.message
            )
        }
    }
}
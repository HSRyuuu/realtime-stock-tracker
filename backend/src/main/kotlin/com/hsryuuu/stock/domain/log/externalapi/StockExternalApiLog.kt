package com.hsryuuu.stock.domain.log.externalapi

import com.hsryuuu.stock.application.utils.LogUtils
import com.hsryuuu.stock.infra.stockapi.type.StockApiResultType
import com.hsryuuu.stock.infra.stockapi.type.StockApiSource
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime

@Entity
@Table(name = "LOG_STOCK_EXTERNAL_API")
data class StockExternalApiLog(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "api_source", nullable = false, length = 255)
    val apiSource: StockApiSource,

    @Enumerated(EnumType.STRING)
    @Column(name = "result_type", nullable = false, length = 255)
    var resultType: StockApiResultType,

    @Column(name = "status")
    var status: Int? = null,

    @Column(name = "class_name", nullable = false, length = 255)
    val className: String? = null,

    @Column(name = "method_name", nullable = false, length = 255)
    val methodName: String? = null,

    @Column(name = "file_name", length = 255)
    val fileName: String? = null,

    @Column(name = "line_number")
    val lineNumber: Int? = null,

    @Column(name = "parameters")
    val parameters: String? = null,

    @Lob
    @Column(name = "message")
    var message: String? = "",

    @CreatedDate
    @Column(name = "occurred_at", nullable = false, updatable = false, insertable = false)
    val occurredAt: LocalDateTime? = null
) {
    companion object {

        fun defaultSuccess(
            apiSource: StockApiSource,
            parameters: Map<String, Any>? = null,
            location: LogUtils.LocationInfo
        ): StockExternalApiLog {
            return StockExternalApiLog(
                apiSource = apiSource,
                className = location.className,
                methodName = location.methodName,
                fileName = location.fileName,
                lineNumber = location.lineNumber,
                parameters = parameters.toString(),
                resultType = StockApiResultType.SUCCESS,
                status = 200,
                message = "success"
            )
        }

        fun defaultError(
            apiSource: StockApiSource,
            e: Exception,
            parameters: Map<String, Any>? = null
        ): StockExternalApiLog {
            val element = e.stackTrace.firstOrNull { it.className.startsWith("com.hsryuuu") }
            return StockExternalApiLog(
                apiSource = apiSource,
                resultType = StockApiResultType.UNKNOWN_ERROR,
                status = 500,
                className = element?.className ?: "UnknownClass",
                methodName = element?.methodName ?: "UnknownMethod",
                fileName = element?.fileName,
                lineNumber = element?.lineNumber,
                parameters = parameters.toString(),
                message = e.message
            )
        }
    }
}
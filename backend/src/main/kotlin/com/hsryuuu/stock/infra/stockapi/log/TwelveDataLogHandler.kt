package com.hsryuuu.stock.infra.stockapi.log

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.hsryuuu.stock.application.utils.LogUtils
import com.hsryuuu.stock.domain.log.externalapi.StockExternalApiLog
import com.hsryuuu.stock.domain.log.externalapi.StockExternalApiLogRepository
import com.hsryuuu.stock.infra.stockapi.response.TwelveData
import com.hsryuuu.stock.infra.stockapi.type.StockApiResultType
import com.hsryuuu.stock.infra.stockapi.type.StockApiSource
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class TwelveDataLogHandler(
    private val stockApiLogRepository: StockExternalApiLogRepository,
    private val objectMapper: ObjectMapper

) {

    @Transactional
    fun logSuccess(paramMap: Map<String, String>, clazz: Class<*>) {
        stockApiLogRepository.save(
            StockExternalApiLog.defaultSuccess(
                StockApiSource.TWELVE_DATA,
                paramMap,
                LogUtils.currentLocation(clazz)
            )
        )
    }

    @Transactional
    fun handleJsonParsingError(
        e: JsonProcessingException,
        rawJson: String? = null,
        paramMap: Map<String, String>
    ) {
        val errorResponse = try {
            objectMapper.readValue(rawJson, TwelveData.ErrorResponse::class.java)
        } catch (_: Exception) {
            TwelveData.ErrorResponse(code = 0, message = "Invalid JSON structure", status = "error")
        }
        stockApiLogRepository.save(StockExternalApiLog.defaultError(StockApiSource.TWELVE_DATA, e, paramMap).apply {
            resultType = StockApiResultType.ERROR
            message = errorResponse.message
            status = errorResponse.code
        })
    }

    @Transactional
    fun handleGeneralError(e: Exception, paramMap: Map<String, String>) {
        stockApiLogRepository.save(StockExternalApiLog.defaultError(StockApiSource.TWELVE_DATA, e, paramMap))
    }

}
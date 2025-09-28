package com.hsryuuu.stock.infra.stock.provider

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.hsryuuu.stock.application.type.ProcessResult
import com.hsryuuu.stock.application.utils.LogUtils
import com.hsryuuu.stock.domain.log.externalapi.StockExternalApiLog
import com.hsryuuu.stock.domain.log.externalapi.StockExternalApiLogRepository
import com.hsryuuu.stock.domain.stock.model.dto.CandleResponse
import com.hsryuuu.stock.domain.stock.model.type.Timeframe
import com.hsryuuu.stock.infra.stock.api.TwelveDataFeignClient
import com.hsryuuu.stock.infra.stock.param.ParameterConverter
import com.hsryuuu.stock.infra.stock.response.TwelveDataErrorResponse
import com.hsryuuu.stock.infra.stock.response.TwelveDataTimeSeriesResponse
import com.hsryuuu.stock.infra.stock.type.StockApiResultType
import com.hsryuuu.stock.infra.stock.type.StockApiSource
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class TwelveDataStockDataProvider(
    private val client: TwelveDataFeignClient,
    private val converter: ParameterConverter,
    private val objectMapper: ObjectMapper,
    private val stockApiLogRepository: StockExternalApiLogRepository,
    @Value("\${twelve-data.api.key}") private val apiKey: String
) : StockDataProvider {

    /**
     * 시간대별 주가 정보 조회
     */
    override fun getTimeSeries(symbol: String, timeframe: Timeframe): ProcessResult<CandleResponse> {
        val intervalString = converter.interval(timeframe)
        var rawJson: String? = null // response JSON String
        val paramMap = mapOf("symbol" to symbol, "timeframe" to timeframe.name) // params of this method
        try {
            rawJson = client.getTimeSeries(symbol, intervalString, apiKey) // TwelveData API 호출
            val response = objectMapper.readValue(rawJson, TwelveDataTimeSeriesResponse::class.java)
            val stockCandles = response.values
                .map { TwelveDataTimeSeriesResponse.toCandleDto(response.meta, it, timeframe) }
                .toList()

            // API 호출 로그 저장
            stockApiLogRepository.save(
                StockExternalApiLog.defaultSuccess(
                    StockApiSource.TWELVE_DATA,
                    paramMap,
                    LogUtils.currentLocation(TwelveDataStockDataProvider::class.java)
                )
            )

            return ProcessResult.success(
                CandleResponse(
                    meta = CandleResponse.Meta(
                        symbol = response.meta.symbol,
                        timeframe = timeframe,
                        currency = response.meta.currency,
                        exchangeTimezone = response.meta.exchangeTimezone,
                        exchange = response.meta.exchange,
                        micCode = response.meta.micCode,
                        type = response.meta.type
                    ),
                    candles = stockCandles
                )
            )
        } catch (e: JsonProcessingException) {
            // 에러 응답 파싱
            val errorResponse = try {
                objectMapper.readValue(rawJson, TwelveDataErrorResponse::class.java)
            } catch (_: Exception) {
                TwelveDataErrorResponse(code = 0, message = "Invalid JSON structure", status = "error")
            }
            stockApiLogRepository.save(StockExternalApiLog.defaultError(StockApiSource.TWELVE_DATA, e, paramMap).apply {
                resultType = StockApiResultType.TIME_SERIES_ERROR
                message = errorResponse.message
                status = errorResponse.code
            })
            return ProcessResult.error(errorResponse.message)
        } catch (e: Exception) {
            val savedLog =
                stockApiLogRepository.save(StockExternalApiLog.defaultError(StockApiSource.TWELVE_DATA, e, paramMap))
            return ProcessResult.error(savedLog.message ?: "Unknown Error")
        }
    }
}
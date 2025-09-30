package com.hsryuuu.stock.infra.stockapi.provider

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.hsryuuu.stock.application.exception.GlobalException
import com.hsryuuu.stock.application.type.ProcessResult
import com.hsryuuu.stock.application.utils.LogUtils
import com.hsryuuu.stock.domain.log.externalapi.StockExternalApiLog
import com.hsryuuu.stock.domain.log.externalapi.StockExternalApiLogRepository
import com.hsryuuu.stock.domain.stock.model.dto.CandleResponse
import com.hsryuuu.stock.domain.stock.model.type.Timeframe
import com.hsryuuu.stock.infra.redis.limit.TwelveDataApiRateLimiter
import com.hsryuuu.stock.infra.stockapi.api.TwelveDataFeignClient
import com.hsryuuu.stock.infra.stockapi.param.ParameterConverter
import com.hsryuuu.stock.infra.stockapi.response.TwelveData
import com.hsryuuu.stock.infra.stockapi.type.StockApiResultType
import com.hsryuuu.stock.infra.stockapi.type.StockApiSource
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Component
class TwelveDataStockDataProvider(
    private val client: TwelveDataFeignClient,
    private val converter: ParameterConverter,
    private val objectMapper: ObjectMapper,
    private val stockApiLogRepository: StockExternalApiLogRepository,
    private val twelveDataApiRateLimiter: TwelveDataApiRateLimiter,
    @Value("\${twelve-data.api.key}") private val apiKey: String
) : StockDataProvider {

    private val log = LoggerFactory.getLogger(TwelveDataStockDataProvider::class.java)


    /**
     * 시간대별 주가 정보 조회
     */
    @Transactional
    override fun getTimeSeries(symbol: String, timeframe: Timeframe, startDate: LocalDate): CandleResponse? {
        if (!twelveDataApiRateLimiter.checkAndIncrement(StockApiSource.TWELVE_DATA.name)) {
            throw GlobalException(HttpStatus.TOO_MANY_REQUESTS, "TwelveData API 호출 제한 초과")
        }
        val intervalString = converter.interval(timeframe)
        var rawJson: String? = null // response JSON String
        val paramMap = mapOf("symbol" to symbol, "timeframe" to timeframe.name) // params of this method
        try {
            rawJson = client.getTimeSeries(symbol, intervalString, apiKey, startDate.toString()) // TwelveData API 호출
            val response = objectMapper.readValue(rawJson, TwelveData.TimeSeriesResponse::class.java)
            val stockCandles = response.values
                .map { TwelveData.TimeSeriesResponse.toCandleDto(response.meta, it, timeframe) }
                .toList()

            // API 호출 로그 저장
            stockApiLogRepository.save(
                StockExternalApiLog.defaultSuccess(
                    StockApiSource.TWELVE_DATA,
                    paramMap,
                    LogUtils.currentLocation(TwelveDataStockDataProvider::class.java)
                )
            )
            log.info("TwelveData TimeSeries 데이터 수집 성공: symbol: {}", symbol)


            return CandleResponse(
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
        } catch (e: JsonProcessingException) {

            log.info("TwelveData TimeSeries 데이터 수집 실패: symbol: {} \n response={}", symbol, rawJson)

            // 에러 응답 파싱
            val errorResponse = try {
                objectMapper.readValue(rawJson, TwelveData.ErrorResponse::class.java)
            } catch (_: Exception) {
                TwelveData.ErrorResponse(code = 0, message = "Invalid JSON structure", status = "error")
            }
            stockApiLogRepository.save(StockExternalApiLog.defaultError(StockApiSource.TWELVE_DATA, e, paramMap).apply {
                resultType = StockApiResultType.TIME_SERIES_ERROR
                message = errorResponse.message
                status = errorResponse.code
            })

        } catch (e: Exception) {
            val savedLog =
                stockApiLogRepository.save(StockExternalApiLog.defaultError(StockApiSource.TWELVE_DATA, e, paramMap))
        }
        return null
    }

    fun getAllStocks(): ProcessResult<TwelveData.StockSymbolResult> {
        try {
            val response = client.getStocks("demo")
            return ProcessResult.success(response);
        } catch (e: Exception) {
            return ProcessResult.error("Unknown Error")
        }
    }

    fun getAllEtfs(): ProcessResult<TwelveData.StockSymbolResult> {
        try {
            val response = client.getETFs("demo")
            return ProcessResult.success(response);
        } catch (e: Exception) {
            return ProcessResult.error("Unknown Error")
        }
    }
}
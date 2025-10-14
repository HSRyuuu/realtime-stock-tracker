package com.hsryuuu.stock.infra.stockapi.provider.stock

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.hsryuuu.stock.application.dto.ProcessResult
import com.hsryuuu.stock.domain.stock.model.dto.CandleDto
import com.hsryuuu.stock.domain.stock.model.dto.CandleResponse
import com.hsryuuu.stock.domain.stock.model.type.Timeframe
import com.hsryuuu.stock.infra.redis.stockapi.TwelveDataApiRateLimiter
import com.hsryuuu.stock.infra.stockapi.api.TwelveDataFeignClient
import com.hsryuuu.stock.infra.stockapi.log.TwelveDataLogHandler
import com.hsryuuu.stock.infra.stockapi.param.ParameterConverter
import com.hsryuuu.stock.infra.stockapi.response.TwelveData
import com.hsryuuu.stock.infra.stockapi.type.StockApiSource
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Component
class TwelveDataStockDataProvider(
    private val client: TwelveDataFeignClient,
    private val converter: ParameterConverter,
    private val objectMapper: ObjectMapper,
    private val twelveDataApiRateLimiter: TwelveDataApiRateLimiter,
    private val twelveDataLogHandler: TwelveDataLogHandler,
    @Value("\${twelve-data.api.key}") private val apiKey: String
) : StockDataProvider {

    private val log = LoggerFactory.getLogger(TwelveDataStockDataProvider::class.java)


    /**
     * 시간대별 주가 정보 조회
     */
    @Transactional
    override fun getTimeSeries(
        symbol: String,
        timeframe: Timeframe,
        startDate: LocalDate
    ): ProcessResult<CandleResponse> {
        if (!checkRateLimit().success) {
            return ProcessResult.fail();
        }
        val paramMap =
            mapOf("symbol" to symbol, "timeframe" to timeframe.name, "startDate" to startDate.toString()) // 로그용
        var rawJson: String? = null // response JSON String

        return try {
            rawJson = fetchTimeSeries(symbol, timeframe, startDate) // TwelveData API 호출
            val response = parseResponseJson(rawJson)
            val stockCandles = response.values
                .map { TwelveData.TimeSeriesResponse.toCandleDto(response.meta, it, timeframe) }
                .toList()

            twelveDataLogHandler.logSuccess(paramMap, TwelveDataStockDataProvider::class.java) //성공 로그 저장
            log.info("TwelveData TimeSeries 데이터 수집 성공: symbol: {}", symbol)

            ProcessResult.success(buildCandleResponse(response, timeframe, stockCandles))
        } catch (e: JsonProcessingException) {
            log.info("TwelveData TimeSeries 데이터 수집 실패: symbol: {} \n response={}", symbol, rawJson)
            twelveDataLogHandler.handleJsonParsingError(e, rawJson, paramMap)
            ProcessResult.fail()
        } catch (e: Exception) {
            log.info("알수없는 에러 발생 - TwelveData TimeSeries 데이터 수집 실패: symbol: {} \n response={}", symbol, rawJson)
            twelveDataLogHandler.handleGeneralError(e, paramMap)
            ProcessResult.fail()
        }
    }

    private fun checkRateLimit(): ProcessResult<Unit> {
        if (!twelveDataApiRateLimiter.checkAndIncrement(StockApiSource.TWELVE_DATA.name)) {
            log.warn("TwelveData API 호출 제한 초과")
            return ProcessResult.fail()
        }
        return ProcessResult.success(Unit)
    }

    private fun fetchTimeSeries(symbol: String, timeframe: Timeframe, startDate: LocalDate): String =
        client.getTimeSeries(symbol, converter.interval(timeframe), apiKey, startDate.toString())

    private fun parseResponseJson(rawJson: String): TwelveData.TimeSeriesResponse =
        objectMapper.readValue(rawJson, TwelveData.TimeSeriesResponse::class.java)

    private fun buildCandleResponse(
        response: TwelveData.TimeSeriesResponse,
        timeframe: Timeframe,
        candles: List<CandleDto>
    ) = CandleResponse(
        meta = CandleResponse.Meta(
            symbol = response.meta.symbol,
            timeframe = timeframe,
            currency = response.meta.currency,
            exchangeTimezone = response.meta.exchangeTimezone,
            exchange = response.meta.exchange,
            micCode = response.meta.micCode,
            type = response.meta.type
        ),
        candles = candles
    )

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
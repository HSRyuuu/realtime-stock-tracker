package com.hsryuuu.stock.infra.stockapi.provider.market

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.hsryuuu.stock.application.dto.ProcessResult
import com.hsryuuu.stock.infra.stockapi.api.TwelveDataFeignClient
import com.hsryuuu.stock.infra.stockapi.log.TwelveDataLogHandler
import com.hsryuuu.stock.infra.stockapi.response.TwelveData.ExchangeRateResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class TwelveDataMarketDataProvider(
    private val twelveDataFeignClient: TwelveDataFeignClient,
    private val objectMapper: ObjectMapper,
    private val twelveDataLogHandler: TwelveDataLogHandler,
    @Value("\${twelve-data.api.key}") private val apiKey: String
) : MarketDataProvider {

    private val log = LoggerFactory.getLogger(TwelveDataMarketDataProvider::class.java)

    override fun getExchangeRate(base: String, quote: String): ProcessResult<ExchangeRateResponse> {
        val paramMap = mapOf("base" to base, "quote" to quote)
        var rawJson: String? = null;

        return try {
            rawJson = twelveDataFeignClient.getExchangeRate("$base/$quote", apiKey)
            val response = objectMapper.readValue(rawJson, ExchangeRateResponse::class.java)
            twelveDataLogHandler.logSuccess(paramMap, TwelveDataMarketDataProvider::class.java)
            log.info("TwelveData ExchangeRate 데이터 수집 성공: param={}", paramMap)
            return ProcessResult.success(response)
        } catch (e: JsonProcessingException) {
            twelveDataLogHandler.handleJsonParsingError(e, rawJson, paramMap)
            ProcessResult.fail()
        } catch (e: Exception) {
            log.info("알수없는 에러 발생 - TwelveData ExchangeRate 데이터 수집 실패: param={} \n response={}", paramMap, rawJson)
            twelveDataLogHandler.handleGeneralError(e, paramMap);
            ProcessResult.fail()
        }
    }

}
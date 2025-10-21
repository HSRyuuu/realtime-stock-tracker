package com.hsryuuu.stock.domain.market.common.service

import com.hsryuuu.stock.application.utils.TimeUtils
import com.hsryuuu.stock.domain.market.common.model.ExchangeRateDto
import com.hsryuuu.stock.infra.redis.common.RedisKeys
import com.hsryuuu.stock.infra.redis.common.StringRedisUtils
import com.hsryuuu.stock.infra.stockapi.provider.market.TwelveDataMarketDataProvider
import org.springframework.stereotype.Service

@Service
class ExchangeService(
    private val redisUtils: StringRedisUtils,
    private val twelveDataMarketDataProvider: TwelveDataMarketDataProvider
) {

    fun getCurrentExchangeRate(base: String, quote: String): ExchangeRateDto {
        val key = RedisKeys.buildKey(RedisKeys.FX_RATE, base, quote)

        redisUtils.getObject(key, RedisKeys.FX_RATE.valueType)?.let {
            return it as ExchangeRateDto
        }

        val fetchResult = twelveDataMarketDataProvider.getExchangeRate(base, quote)
        val exchangeRateResponse = fetchResult.data ?: throw Exception("Fetch failed")

        val localDateTime =
            TimeUtils.toLocalDateTimeAt(exchangeRateResponse.timestamp, TimeUtils.TIME_ZONE_ASIA_SEOUL)

        return ExchangeRateDto(
            baseCurrency = base,
            quoteCurrency = quote,
            rate = exchangeRateResponse.rate,
            lastUpdated = localDateTime
        )
    }
}
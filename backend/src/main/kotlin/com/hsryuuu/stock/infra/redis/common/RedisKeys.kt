package com.hsryuuu.stock.infra.redis.common

import com.hsryuuu.stock.domain.market.common.model.ExchangeRateDto
import com.hsryuuu.stock.domain.stock.model.dto.SymbolStatus


object RedisKeys {

    data class RedisKeySpec(
        val keyPattern: String,
        val valueType: Class<*>,
        val ttlSeconds: Long
    )

    fun buildKey(spec: RedisKeySpec, vararg args: Any): String {
        val expectedArgs = Regex("%s").findAll(spec.keyPattern).count()
        require(expectedArgs == args.size) {
            "Invalid argument count for Redis key pattern: expected $expectedArgs but got ${args.size}"
        }
        return spec.keyPattern.format(*args)
    }

    // 환율 데이터
    val FX_RATE = RedisKeySpec(
        keyPattern = "fx:rate:%s:%s",
        valueType = ExchangeRateDto::class.java,
        ttlSeconds = 3600               // 1시간 캐시
    )

    // 캔들 수집 상태
    val CANDLE_COLLECT_STATUS = RedisKeySpec(
        keyPattern = "candle:status:%s:%s", // symbol / timeframe
        valueType = SymbolStatus::class.java,
        ttlSeconds = 600
    )

    // 주식 현재가
    val CURRENT_STOCK_PRICE = RedisKeySpec(
        keyPattern = "stock:price:%s:date:%s",
        valueType = Double::class.java,
        ttlSeconds = 60 * 60 * 24
    )

}
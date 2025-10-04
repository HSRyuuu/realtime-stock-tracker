package com.hsryuuu.stock.infra.redis.common

import com.hsryuuu.stock.domain.market.model.ExchangeRateDto


object RedisKeys {

    // 환율 데이터
    val FX_RATE = RedisKeySpec(
        keyPattern = "fx:rate:%s:%s",
        valueType = ExchangeRateDto::class.java,
        ttlSeconds = 3600               // 1시간 캐시
    )

    fun exchangeRateKey(base: String, quote: String) = "fx:rate:$base:$quote"

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
}
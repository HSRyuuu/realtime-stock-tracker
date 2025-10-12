package com.hsryuuu.stock.domain.stock.service

import com.hsryuuu.stock.domain.stock.model.dto.SymbolStatus
import com.hsryuuu.stock.domain.stock.model.type.CandleCollectState
import com.hsryuuu.stock.domain.stock.model.type.Timeframe
import com.hsryuuu.stock.infra.redis.common.RedisKeys
import com.hsryuuu.stock.infra.redis.common.StringRedisUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class CandleStatusService(
    private val redisUtils: StringRedisUtils
) {
    private val log = LoggerFactory.getLogger(CandleStatusService::class.java)

    private val ttl = Duration.ofSeconds(RedisKeys.CANDLE_COLLECT_STATUS.ttlSeconds)

    fun get(symbol: String, timeframe: Timeframe): SymbolStatus? {
        log.info("get: symbol=$symbol, timeframe=$timeframe")
        return redisUtils.getObject(
            RedisKeys.buildKey(RedisKeys.CANDLE_COLLECT_STATUS, symbol, timeframe.name),
            SymbolStatus::class.java
        )
    }

    fun setPending(symbol: String, timeframe: Timeframe) {
        val status = SymbolStatus(symbol, false, CandleCollectState.PENDING, "pending")
        redisUtils.setObject(RedisKeys.buildKey(RedisKeys.CANDLE_COLLECT_STATUS, symbol, timeframe.name), status, ttl)
    }

    fun setRunning(symbol: String, timeframe: Timeframe) {
        val status = SymbolStatus(symbol, false, CandleCollectState.RUNNING, "collect-start")
        redisUtils.setObject(RedisKeys.buildKey(RedisKeys.CANDLE_COLLECT_STATUS, symbol, timeframe.name), status, ttl)
    }

    fun setSuccess(symbol: String, timeframe: Timeframe) {
        val status = SymbolStatus(symbol, true, CandleCollectState.SUCCESS, "collect-success")
        redisUtils.setObject(RedisKeys.buildKey(RedisKeys.CANDLE_COLLECT_STATUS, symbol, timeframe.name), status, ttl)
    }

    fun setFailed(symbol: String, timeframe: Timeframe, message: String) {
        val status = SymbolStatus(symbol, false, CandleCollectState.FAIL, message)
        redisUtils.setObject(RedisKeys.buildKey(RedisKeys.CANDLE_COLLECT_STATUS, symbol, timeframe.name), status, ttl)
    }
}
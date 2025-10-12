package com.hsryuuu.stock.domain.stock.model.dto

import com.hsryuuu.stock.application.type.CurrencyType
import com.hsryuuu.stock.application.utils.TimeUtils
import com.hsryuuu.stock.domain.stock.model.entity.StockCandle
import com.hsryuuu.stock.domain.stock.model.type.Timeframe
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class CandleDto(
    val datetime: LocalDateTime,
    val time: Long,   // Unix timestamp (seconds)
    val date: LocalDate,
    val open: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal,
    val close: BigDecimal,
    val volume: Long? = 0,
    val currency: CurrencyType? = null,
    val updatedAt: LocalDateTime? = null,
) {
    companion object {
        fun fromEntity(stockCandle: StockCandle): CandleDto = CandleDto(
            datetime = TimeUtils.toLocalDateTimeAt(
                stockCandle.bucketStartUtc,
                TimeUtils.TIME_ZONE_ASIA_SEOUL
            ),
            date = stockCandle.date,
            time = stockCandle.bucketStartUtc,
            open = stockCandle.open,
            high = stockCandle.high,
            low = stockCandle.low,
            close = stockCandle.close,
            volume = stockCandle.volume,
            currency = stockCandle.currency,
            updatedAt = stockCandle.updatedAt,
        )

        fun toEntity(symbol: String, timeframe: Timeframe, candleDto: CandleDto): StockCandle = StockCandle(
            symbol = symbol,
            timeframe = timeframe,
            bucketStartUtc = candleDto.time,
            date = TimeUtils.toLocalDateAt(candleDto.time, TimeUtils.TIME_ZONE_UTC),
            open = candleDto.open,
            high = candleDto.high,
            low = candleDto.low,
            close = candleDto.close,
            volume = candleDto.volume,
            currency = candleDto.currency,
        )
    }
}
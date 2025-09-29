package com.hsryuuu.stock.domain.stock.model.dto

import com.hsryuuu.stock.application.type.CurrencyType
import com.hsryuuu.stock.application.utils.BucketTimeUtils
import com.hsryuuu.stock.application.utils.TimeUtils
import com.hsryuuu.stock.domain.stock.model.entity.StockCandle
import com.hsryuuu.stock.domain.stock.model.type.Timeframe
import java.math.BigDecimal
import java.time.LocalDateTime

data class CandleDto(
    val datetime: LocalDateTime,
    val time: Long,   // Unix timestamp (seconds)
    val open: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal,
    val close: BigDecimal,
    val volume: Long? = 0,
    val currency: CurrencyType? = null,
) {
    companion object {
        fun fromEntity(stockCandle: StockCandle): CandleDto = CandleDto(
            datetime = BucketTimeUtils.toDateTimeByZone(
                stockCandle.bucketStartUtc,
                TimeUtils.TIME_ZONE_ASIA_SEOUL
            ),
            time = stockCandle.bucketStartUtc,
            open = stockCandle.open,
            high = stockCandle.high,
            low = stockCandle.low,
            close = stockCandle.close,
            volume = stockCandle.volume,
            currency = stockCandle.currency,
        )

        fun toEntity(symbol: String, timeframe: Timeframe, candleDto: CandleDto): StockCandle = StockCandle(
            symbol = symbol,
            timeframe = timeframe,
            bucketStartUtc = candleDto.time,
            date = BucketTimeUtils.toUtcDate(candleDto.time),
            open = candleDto.open,
            high = candleDto.high,
            low = candleDto.low,
            close = candleDto.close,
            volume = candleDto.volume,
            currency = candleDto.currency,
        )
    }
}
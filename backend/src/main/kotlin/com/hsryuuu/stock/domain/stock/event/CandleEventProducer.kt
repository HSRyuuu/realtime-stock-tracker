package com.hsryuuu.stock.domain.stock.event

import com.hsryuuu.stock.domain.stock.model.type.Timeframe
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class CandleEventProducer(
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {

    private val log = LoggerFactory.getLogger(CandleEventProducer::class.java)

    /**
     * 캔들 수집 이벤트 발행
     */
    fun sendCandleCollectEvent(symbol: String, timeframe: Timeframe) {
        val event = CandleCollectEvent(symbol, timeframe)
        kafkaTemplate.send("candle-collect-topic", event)
        log.info("📤[Kafka] 캔들 수집 이벤트 발행: $event")
    }

    /**
     * 볼린저밴드 계산 이벤트 발행
     */
    fun sendBollingerBandCalculateEvent(symbol: String, timeframe: Timeframe, period: Int = 20, kValue: Double = 2.0) {
        val event = BollingerBandCalculateEvent(symbol, timeframe, period, kValue)
        kafkaTemplate.send("bollinger-calc-topic", event)
        log.info(
            "📤 [Kafka] BollingerBandCalculateEvent 발행: symbol={}, timeframe={}, period={}, k={}",
            symbol, timeframe, period, kValue
        )
    }

    /**
     * RSI 계산 이벤트 발행
     */
    fun sendRSICalculateEvent(symbol: String, timeframe: Timeframe, period: Int = 14) {
        val event = RSICalculateEvent(symbol, timeframe, period)
        kafkaTemplate.send("rsi-calc-topic", event)
        log.info(
            "📤 [Kafka] BRSICalculateEvent 발행: symbol={}, timeframe={}, period={}",
            symbol, timeframe, period
        )
    }
}

data class CandleCollectEvent(
    val symbol: String,
    val timeframe: Timeframe,
    val requestedAt: LocalDateTime = LocalDateTime.now()
)

data class BollingerBandCalculateEvent(
    val symbol: String,
    val timeframe: Timeframe,
    val period: Int = 20,
    val kValue: Double = 2.0,
    val requestedAt: LocalDateTime = LocalDateTime.now()
)

data class RSICalculateEvent(
    val symbol: String,
    val timeframe: Timeframe,
    val period: Int = 14,
    val requestedAt: LocalDateTime = LocalDateTime.now()
)
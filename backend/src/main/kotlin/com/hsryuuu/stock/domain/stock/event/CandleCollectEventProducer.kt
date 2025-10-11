package com.hsryuuu.stock.domain.stock.event

import com.hsryuuu.stock.domain.stock.model.type.Timeframe
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class CandleCollectEventProducer(
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {

    private val log = LoggerFactory.getLogger(CandleCollectEventProducer::class.java)

    fun sendCandleCollectEvent(symbol: String, timeframe: Timeframe) {
        val event = CandleCollectEvent(symbol, timeframe)
        kafkaTemplate.send("candle-collect-topic", event)
        log.info("📤[Kafka] 캔들 수집 이벤트 발행: $event")

    }
}

data class CandleCollectEvent(
    val symbol: String,
    val timeframe: Timeframe,
    val requestedAt: LocalDateTime = LocalDateTime.now()
)
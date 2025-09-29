package com.hsryuuu.stock.infra.stockapi.config

import com.hsryuuu.stock.infra.stockapi.param.ParameterConverter
import com.hsryuuu.stock.infra.stockapi.param.TwelveDataParameterConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class StockConfig {

    @Bean
    fun twelveDataParameterConverter(): ParameterConverter {
        return TwelveDataParameterConverter()
    }
}
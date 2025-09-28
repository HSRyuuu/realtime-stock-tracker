package com.hsryuuu.stock.infra.stock.config

import com.hsryuuu.stock.infra.stock.param.ParameterConverter
import com.hsryuuu.stock.infra.stock.param.TwelveDataParameterConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class StockConfig {

    @Bean
    fun twelveDataParameterConverter(): ParameterConverter {
        return TwelveDataParameterConverter()
    }
}
package com.hsryuuu.stock.application.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun realtimeStockTrackerOpenApi(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Realtime Stock Tracker")
                    .version("v1.0.0")
                    .description("Realtime Stock Tracker API")
            )
    }
}
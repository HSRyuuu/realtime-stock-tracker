package com.hsryuuu.stock.application.config

import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Configuration

@Configuration
@EnableFeignClients(basePackages = ["com.hsryuuu.stock.infra.stockapi.api"])
class FeignConfig {
}
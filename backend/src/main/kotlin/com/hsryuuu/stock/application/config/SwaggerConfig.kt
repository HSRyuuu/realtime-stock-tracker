package com.hsryuuu.stock.application.config

import com.hsryuuu.stock.application.security.JwtTokenProvider
import com.hsryuuu.stock.application.security.UserInfo
import com.hsryuuu.stock.application.security.UserRole
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun realtimeStockTrackerOpenApi(): OpenAPI {
        val securitySchemeName = "bearerAuth"

        return OpenAPI()
            .info(
                Info()
                    .title("Realtime Stock Tracker")
                    .description("Realtime Stock Tracker API")
                    .version("v1.0.0")
            )
            // 보안 스키마 정의
            .components(
                Components()
                    .addSecuritySchemes(
                        securitySchemeName,
                        SecurityScheme()
                            .name("Authorization")
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                            .description("token => " + test())
                    )
            )
            // 모든 API 요청에 인증 필요
            .addSecurityItem(
                SecurityRequirement().addList(securitySchemeName)
            )

    }

    fun test(): String {
        val userInfo = UserInfo(
            id = 1L,
            username = "admin",
            nickname = "admin",
            email = "<EMAIL>",
            role = UserRole.ROLE_ADMIN
        )
        return JwtTokenProvider().createToken(userInfo)
    }
}
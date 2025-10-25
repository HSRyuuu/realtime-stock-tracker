package com.hsryuuu.stock.application.config

import com.hsryuuu.stock.application.security.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {
    companion object {
        val PERMIT_ALL_URLS = listOf("/api/auth/**", "/api/public/**")
        val SWAGGER_URLS = listOf("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")

    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .cors { it.configurationSource(corsConfigurationSource()) }

            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers(*PERMIT_ALL_URLS.toTypedArray()).permitAll() // 공개 API
                    .requestMatchers(*SWAGGER_URLS.toTypedArray())
                    .permitAll() // Swagger UI 접근 허용
                    .anyRequest().authenticated() // 나머지는 인증 필요
            }
            .formLogin { it.disable() } // REST API에서는 비활성화
            .httpBasic { it.disable() } // REST API에서는 비활성화
            // JWT 인증 필터 추가
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)


        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins =
            listOf("http://localhost:4000", "https://finsight-sandy-pi.vercel.app") // 모든 origin 허용 또는 특정 도메인 지정
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        configuration.allowedHeaders = listOf("Authorization", "Content-Type")
        configuration.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }


}
package com.hsryuuu.stock.application.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userDetailsService: CustomUserDetailsService
) : OncePerRequestFilter() {
    companion object {
        const val BEARER_PREFIX = "Bearer "
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // Authorization 헤더에서 JWT 토큰 추출
        val authHeader = request.getHeader("Authorization")

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response)
            return
        }

        // "Bearer " 접두사 이후의 토큰 부분 추출
        val jwt = authHeader.substring(BEARER_PREFIX.length)

        try {
            // 토큰에서 사용자 이름 추출
            //val username = jwtTokenProvider.getUsernameFromToken(jwt)
            val userInfo = jwtTokenProvider.getUserFromToken(jwt)

            // 인증 컨텍스트가 비어있는 경우에만 처리
            if (userInfo.username.isNotEmpty() && SecurityContextHolder.getContext().authentication == null) {
                // 사용자 상세 정보 로드
                val userDetails = userDetailsService.loadUserByUsername(userInfo.username)

                // 토큰 유효성 검증
                if (jwtTokenProvider.validateToken(jwt, userInfo)) {
                    // 인증 객체 생성
                    val authentication = UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.authorities
                    )

                    // 인증 객체에 요청 상세 정보 추가
                    authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

                    // 보안 컨텍스트에 인증 객체 설정
                    SecurityContextHolder.getContext().authentication = authentication
                }
            }
        } catch (ex: Exception) {
            // 토큰 검증 실패 시 로깅 (실제 환경에서는 로그 레벨 조정 필요)
            logger.error("인증 설정 중 오류 발생: ${ex.message}")
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response)
    }
}
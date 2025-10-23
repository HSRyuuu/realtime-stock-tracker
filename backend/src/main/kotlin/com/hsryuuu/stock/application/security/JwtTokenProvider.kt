package com.hsryuuu.stock.application.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtTokenProvider {

    @Value("\${jwt.secret:defaultSecretKey12345678901234567890}")
    private var secretKey: String = "defaultSecretKey12345678901234567890"

    @Value("\${jwt.expiration:86400000}") // 기본값: 1일
    private val validityInMilliseconds: Long = 86400000

    fun createToken(userInfo: UserInfo): String {
        val claims: Claims = Jwts.claims().setSubject(userInfo.username)
        val now = Date()
        val validity = Date(now.time + validityInMilliseconds)

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validity)
            .addClaims(
                mapOf(
                    "id" to userInfo.id,
                    "username" to userInfo.username,
                    "nickname" to userInfo.nickname,
                    "email" to userInfo.email,
                    "role" to userInfo.role.name
                )
            )
            .signWith(Keys.hmacShaKeyFor(secretKey.toByteArray()), SignatureAlgorithm.HS256)
            .compact()
    }

    fun validateToken(token: String, userInfo: UserInfo): Boolean {
        val user = getUserFromToken(token)
        return (user.username == userInfo.username && !isTokenExpired(token))
    }

    fun getUserFromToken(token: String): UserInfo {
        val claims = Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(secretKey.toByteArray()))
            .build()
            .parseClaimsJws(token)
            .body

        return UserInfo(
            id = (claims["id"] as Number).toLong(),
            username = claims["username"] as String,
            nickname = claims["nickname"] as String,
            email = claims["email"] as String,
            role = UserRole.valueOf(claims["role"] as String)
        )
    }

    private fun isTokenExpired(token: String): Boolean {
        val expiration = Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(secretKey.toByteArray()))
            .build()
            .parseClaimsJws(token)
            .body
            .expiration
        return expiration.before(Date())
    }

}

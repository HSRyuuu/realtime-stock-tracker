package com.hsryuuu.stock.domain.user.auth.model

import com.hsryuuu.stock.application.security.UserInfo

data class LoginRequest(
    val username: String,
    val password: String
)


data class LoginResponse(
    val accessToken: String,
    val userInfo: UserInfo
)

data class LogoutResponse(
    val success: Boolean
)
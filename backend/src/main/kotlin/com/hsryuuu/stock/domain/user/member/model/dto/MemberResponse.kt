package com.hsryuuu.stock.domain.user.member.model.dto

data class DuplicateCheckResponse(
    val exists: Boolean
)

data class SignupResponse(
    val id: Long,
    val username: String,
    val nickname: String
)

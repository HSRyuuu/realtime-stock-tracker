package com.hsryuuu.stock.application.security

import com.hsryuuu.stock.domain.user.member.model.entity.Member

data class UserInfo(
    val id: Long,
    val username: String,
    val nickname: String,
    val email: String,
    val role: UserRole
) {
    companion object {
        fun from(member: Member): UserInfo {
            return UserInfo(
                id = member.id ?: 0L,
                username = member.username,
                nickname = member.nickname,
                email = member.email,
                role = UserRole.ROLE_USER
            )
        }
    }
}

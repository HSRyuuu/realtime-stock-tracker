package com.hsryuuu.stock.domain.user.member

import com.hsryuuu.stock.domain.user.member.model.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberRepository : JpaRepository<Member, Long> {
    fun existsByUsername(username: String): Boolean
    fun existsByNickname(nickname: String): Boolean
    fun existsByEmail(email: String): Boolean
}
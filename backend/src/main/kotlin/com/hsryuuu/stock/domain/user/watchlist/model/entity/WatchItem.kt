package com.hsryuuu.stock.domain.user.watchlist.model.entity

import com.hsryuuu.stock.domain.user.member.model.entity.Member
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@EntityListeners(AuditingEntityListener::class)
@Entity
@Table(name = "WATCH_ITEM")
data class WatchItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    val group: WatchGroup,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,

    @Column(nullable = false, length = 20)
    val symbol: String,

    @Column(length = 100)
    val alias: String? = null,

    val sortOrder: Int? = null,

    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
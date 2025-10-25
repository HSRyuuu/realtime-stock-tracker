package com.hsryuuu.stock.domain.user.watchlist.repository

import com.hsryuuu.stock.domain.user.watchlist.model.entity.WatchGroup
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WatchGroupRepository : JpaRepository<WatchGroup, Long> {

    fun findByMemberIdOrderBySortOrder(memberId: Long): List<WatchGroup>
}
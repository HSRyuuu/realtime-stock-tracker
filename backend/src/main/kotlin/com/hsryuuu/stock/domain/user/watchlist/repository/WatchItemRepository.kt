package com.hsryuuu.stock.domain.user.watchlist.repository

import com.hsryuuu.stock.domain.user.watchlist.model.entity.WatchItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WatchItemRepository : JpaRepository<WatchItem, Long> {
}
package com.hsryuuu.stock.domain.user.watchlist.model.dto

import com.hsryuuu.stock.domain.user.watchlist.model.entity.WatchItem

data class WatchItemDto(
    val id: Long?,
    val symbol: String,
    val alias: String?,
    val sortOrder: Int?
) {
    companion object {
        fun fromEntity(entity: WatchItem): WatchItemDto {
            return WatchItemDto(
                id = entity.id,
                symbol = entity.symbol,
                alias = entity.alias,
                sortOrder = entity.sortOrder
            )
        }
    }
}
package com.hsryuuu.stock.domain.user.watchlist.controller

import com.hsryuuu.stock.domain.stock.model.dto.SymbolWithIndicatorSignals
import com.hsryuuu.stock.domain.user.watchlist.model.dto.WatchGroupDto
import com.hsryuuu.stock.domain.user.watchlist.service.WatchListService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "관심종목 API")
@RequestMapping("/api/watchlist")
@RestController
class WatchListController(
    private val watchListService: WatchListService
) {


    @Operation(summary = "인기 관심종목 with indicator 조회")
    @GetMapping("/items/popular")
    fun getPopularWatchItems(): List<SymbolWithIndicatorSignals> {
        return watchListService.getPopularWatchItems();
    }

    @Operation(summary = "관심종목 목록 조회")
    @GetMapping("/items")
    fun getWatchItems(): List<SymbolWithIndicatorSignals> {
        return watchListService.getWatchItems();
    }

    @Operation(deprecated = true)
    @GetMapping("/groups")
    fun getWatchGroups(): List<WatchGroupDto> {
        return watchListService.getWatchGroups();
    }

}
package com.hsryuuu.stock.domain.user.watchlist.controller

import com.hsryuuu.stock.domain.stock.model.dto.SymbolWithIndicatorSignals
import com.hsryuuu.stock.domain.user.watchlist.model.dto.WatchGroupDto
import com.hsryuuu.stock.domain.user.watchlist.service.WatchListService
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

    @GetMapping("/groups")
    fun getWatchGroups(): List<WatchGroupDto> {
        return watchListService.getWatchGroups();
    }


    @GetMapping("/items/popular")
    fun getWatchItems(): List<SymbolWithIndicatorSignals> {
        return watchListService.getPopularWatchItems();
    }


}
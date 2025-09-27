package com.hsryuuu.stock

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class StockTrackerApplication

fun main(args: Array<String>) {
    runApplication<StockTrackerApplication>(*args)
}

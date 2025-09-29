package com.hsryuuu.stock.domain.stock.service

import com.hsryuuu.stock.domain.stock.model.type.StockType
import com.hsryuuu.stock.infra.stockapi.provider.TwelveDataStockDataProvider
import com.hsryuuu.stock.infra.stockapi.response.TwelveData
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StockMasterService(
    private val em: EntityManager,
    private val twelveDataStockDataProvider: TwelveDataStockDataProvider
) {

    @Transactional
    fun upsertStockSymbols() {
        val allStocks = twelveDataStockDataProvider.getAllStocks()
        val data = allStocks.data ?: return;
        val symbolList = data.data;
        upsertSymbols(symbolList, StockType.STOCK);
    }

    @Transactional
    fun upsertETFSymbols() {
        val allStocks = twelveDataStockDataProvider.getAllEtfs()
        val data = allStocks.data ?: return;
        val symbolList = data.data;
        upsertSymbols(symbolList, StockType.ETF);
    }

    fun upsertSymbols(symbolList: List<TwelveData.StockSymbolResult.StockInfo>, stockType: StockType) {
        val batchSize = 1000
        val onDuplicated =
            """
                ON DUPLICATE KEY UPDATE
            name       = VALUES(name),
            stock_type = VALUES(stock_type),
            currency   = VALUES(currency),
            mic_code   = VALUES(mic_code),
            country    = VALUES(country),
            cfi_code   = VALUES(cfi_code),
            isin       = VALUES(isin),
            cusip      = VALUES(cusip),
            meta_data  = VALUES(meta_data),
            updated_at = CURRENT_TIMESTAMP
            """;
        val sql = """
        INSERT INTO STOCK_SYMBOL
        (
            symbol, name, stock_type, currency, exchange, mic_code, country,
            figi_code, cfi_code, isin, cusip, meta_data
        )
        VALUES (:symbol, :name, :stockType, :currency, :exchange, :micCode, :country,
                :figiCode, :cfiCode, :isin, :cusip, :metaData)
        $onDuplicated
    """.trimIndent()

        val total = symbolList.size
        var i = 0

        while (i < total) {
            val end = minOf(i + batchSize, total)
            val batch = symbolList.subList(i, end)

            for (stock in batch) {
                em.createNativeQuery(sql)
                    .setParameter("symbol", stock.symbol)
                    .setParameter("name", stock.name)
                    .setParameter("stockType", stockType)
                    .setParameter("currency", stock.currency)
                    .setParameter("exchange", stock.exchange)
                    .setParameter("micCode", stock.mic_code)
                    .setParameter("country", stock.country)
                    .setParameter("figiCode", stock.figi_code)
                    .setParameter("cfiCode", stock.cfi_code)
                    .setParameter("isin", stock.isin)
                    .setParameter("cusip", stock.cusip)
                    .setParameter("metaData", stock.access?.toString())
                    .executeUpdate()
            }

            em.flush()
            em.clear()

            i = end
            println("\rUpserted $i / $total stocks")
        }
    }
}
package com.stockmarket.alphastock.model

/**
 * Data class to transfer response data to the controllers.
 */
data class StockDataDTO(
    val date: String,
    val dailyStockVolume: Long?,
    val errorMessage: String?
)
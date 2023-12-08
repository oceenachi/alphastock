package com.stockmarket.alphastock.model

data class StockDataDTO(
    val date: String,
    val dailyStockVolume: Long?,
    val errorMessage: String?
)
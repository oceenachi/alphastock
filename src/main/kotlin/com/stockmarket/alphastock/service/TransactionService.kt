package com.stockmarket.alphastock.service

import com.stockmarket.alphastock.model.StockDataDTO
import java.util.Date

/**
 * Fetch the stock volume for the date passed in or the most recent date.
 */
interface TransactionService {
    //Fetch the stock volume of a given date.
    fun getStockVolume(date: Date): StockDataDTO
}
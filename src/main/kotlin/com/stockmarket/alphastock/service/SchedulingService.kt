package com.stockmarket.alphastock.service

/**
 * Handle retrieval and processing of stock volumes using a collection ticker symbols.
 */
interface SchedulingService {

    fun fetchAndProcessDailyStockVolume()
}
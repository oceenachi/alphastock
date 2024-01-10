package com.stockmarket.alphastock.service

import com.stockmarket.alphastock.entity.StockVolumeEntity

/**
 * Handle retrieval and processing of stock volumes using a collection ticker symbols.
 */
interface SchedulingService {

    fun fetchAndProcessDailyStockVolume(dateString: String): StockVolumeEntity?
}
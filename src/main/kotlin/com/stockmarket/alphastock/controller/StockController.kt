package com.stockmarket.alphastock.controller

import com.stockmarket.alphastock.model.StockDataDTO
import com.stockmarket.alphastock.service.TransactionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.Date

/**
 * Controller to handle routing of client's request.
 */
@RestController
@RequestMapping("/api/v1")
class StockController @Autowired constructor(
    private val transactionService: TransactionService
) {
    @GetMapping("/stocks")
    fun index(
        @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") date: Date?
    ): StockDataDTO {
        return transactionService.getStockVolume(date)
    }
}
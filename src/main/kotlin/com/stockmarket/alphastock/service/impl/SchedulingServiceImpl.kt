//package com.stockmarket.alphastock.service.impl
//
//import com.stockmarket.alphastock.service.SchedulingService
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.scheduling.annotation.Scheduled
//
///**
// * Executes daily to fetch the daily transaction volume of stocks.
// */
//class SchedulingServiceImpl @Autowired constructor(
//    private val schedulingService: SchedulingService
//) {
//    @Scheduled(cron = "0 0 0 * * *")
//    fun execute() {
////        schedulingService.fetchAndProcessDailyStockVolume()
//    }
//}
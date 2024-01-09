package com.stockmarket.alphastock.service.impl

import com.stockmarket.alphastock.entity.StockVolumeEntity
import com.stockmarket.alphastock.exception.AlphaStockException
import com.stockmarket.alphastock.model.StockDataDTO
import com.stockmarket.alphastock.repository.StockVolumeRepository
import com.stockmarket.alphastock.service.SchedulingService
import com.stockmarket.alphastock.service.TransactionService
import jakarta.annotation.PostConstruct
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date

/**
 * Custom implementation of the transaction service.
 */
@Service
class TransactionServiceImpl @Autowired constructor(
    private val restTemplate: RestTemplate,
    private val stockVolumeRepository: StockVolumeRepository,
    @Value("\${configuration.baseUrl}")
    private val url: String,
    @Value("\${configuration.apiKey}")
    private val apiKey: String
) : TransactionService, SchedulingService {

    private val formatter = SimpleDateFormat(FORMAT_PATTERN)
    val tickerSymbols = listOf("IBM", "TSLA", "WMT", "AMZN", "XOM", "AAPL", "UNH", "CVS", "GOOG")

    override fun getStockVolume(date: Date?): StockDataDTO {
        val dateString = formatter.format(date ?: Date.from(Instant.now()))
        return stockVolumeRepository.findByDate(dateString)?.let { StockDataDTO(dateString, it.volume, null) }
            ?: StockDataDTO(
                dateString,
                null,
                "The daily stock volume for the requested date, $dateString is not yet available"
            )
    }

    override fun fetchAndProcessDailyStockVolume() {
        logger.info("Starting daily stock volume processing")
        var date: String? = null
        val totalVolume = tickerSymbols.fold(0L) { acc, tickerSymbol ->
            try {
                val response =
                    restTemplate.getForObject("$url&symbol=$tickerSymbol&apikey=$apiKey", String::class.java)
                        ?: throw AlphaStockException("Error fetching data for ticker symbol $tickerSymbol")
                val timeSeriesMetadata =
                    Json.parseToJsonElement(response).jsonObject[TIME_SERIES_IDENTIFIER]?.jsonObject?.entries?.firstOrNull()
                timeSeriesMetadata?.let {
                    date = timeSeriesMetadata.key
                    val currentVolume = it.value.jsonObject["5. volume"]?.jsonPrimitive?.content?.toLongOrNull() ?: 0L
                    acc + currentVolume
                } ?: acc
            } catch (ex: Exception) {
                logger.error(ex.message)
                acc
            }
        }
        stockVolumeRepository.save(
            StockVolumeEntity(
                id = -1,
                date = date!!,
                volume = totalVolume,
                createdAt = Instant.now()
            )
        )
        logger.info("New stock volume for $date saved successfully")
    }

    @PostConstruct
    fun init() {
        fetchAndProcessDailyStockVolume()
    }

    companion object {
        val logger = KotlinLogging.logger {}
        const val TIME_SERIES_IDENTIFIER = "Time Series (Daily)"
        const val FORMAT_PATTERN = "yyyy-MM-dd"
    }

}
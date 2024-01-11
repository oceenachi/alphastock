package com.stockmarket.alphastock.service.impl

import com.stockmarket.alphastock.entity.StockVolumeEntity
import com.stockmarket.alphastock.model.StockDataDTO
import com.stockmarket.alphastock.repository.StockVolumeRepository
import com.stockmarket.alphastock.service.SchedulingService
import com.stockmarket.alphastock.service.TransactionService
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
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
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

    override fun getStockVolume(date: Date): StockDataDTO {
        val dateString = formatter.format(date)
        if (date.after(Date.from(Instant.now()))) {
            return StockDataDTO(
                dateString,
                null,
                "Date must be on or before current date of US/Eastern timezone"
            )
        }
        val stockVolume = stockVolumeRepository.findByDate(dateString)
        val stockVolumeEntity = stockVolume ?: fetchAndProcessDailyStockVolume(dateString)

        return stockVolumeEntity?.let { StockDataDTO(dateString, stockVolumeEntity.volume, null) } ?: StockDataDTO(
            dateString,
            null,
            "The daily stock volume for the requested date, $dateString is not yet available"
        )
    }

    override fun fetchAndProcessDailyStockVolume(dateString: String): StockVolumeEntity? {
        val outputSize = if (isWithinLast100Days(dateString)) "outputsize=compact" else "outputsize=full"
        logger.info("Starting daily stock volume processing")
        val totalVolume = tickerSymbols.fold(0L) { acc, tickerSymbol ->
            try {
                val response =
                    restTemplate.getForObject(
                        "$url&symbol=$tickerSymbol&apikey=$apiKey&$outputSize",
                        String::class.java
                    )
                        ?: return null
                val timeSeriesMetadata =
                    Json.parseToJsonElement(response).jsonObject[TIME_SERIES_IDENTIFIER]?.jsonObject?.get(dateString)
                timeSeriesMetadata?.let {
                    val currentVolume = it.jsonObject["5. volume"]?.jsonPrimitive?.content?.toLongOrNull() ?: 0L
                    acc + currentVolume
                } ?: acc
            } catch (ex: Exception) {
                logger.error(ex.message)
                acc
            }
        }
        val stockVolumeEntity = stockVolumeRepository.save(
            StockVolumeEntity(
                id = -1,
                date = dateString,
                volume = totalVolume,
                createdAt = Instant.now()
            )
        )
        logger.info("New stock volume for $dateString saved successfully")

        return stockVolumeEntity
    }

    fun isWithinLast100Days(dateToCheck: String): Boolean {
        val timeZone = ZoneId.of("America/New_York")
        val currentDate = LocalDate.now(timeZone)
        val dateToCheckParsed = LocalDate.parse(dateToCheck, DateTimeFormatter.ISO_DATE).atStartOfDay(timeZone).toLocalDate()
        return dateToCheckParsed.isAfter(currentDate.minus(100, ChronoUnit.DAYS))
    }

    companion object {
        val logger = KotlinLogging.logger {}
        const val TIME_SERIES_IDENTIFIER = "Time Series (Daily)"
        const val FORMAT_PATTERN = "yyyy-MM-dd"
    }

}
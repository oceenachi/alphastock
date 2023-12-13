package com.stockmarket.alphastock.repository

import com.stockmarket.alphastock.entity.StockVolumeEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * Handles database access for stockVolumeEntity.
 */
@Repository
interface StockVolumeRepository : CrudRepository<StockVolumeEntity, Long> {
    fun findByDate(date: String): StockVolumeEntity?
}
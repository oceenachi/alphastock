package com.stockmarket.alphastock.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.Instant

/**
 * Database representation of daily stock transaction volume.
 */
@Entity
data class StockVolumeEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val id: Long,

    val volume: Long,

    @Column(unique=true)
    val date: String,

    val createdAt: Instant
)
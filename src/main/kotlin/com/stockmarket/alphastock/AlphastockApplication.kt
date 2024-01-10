package com.stockmarket.alphastock

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
class AlphastockApplication

fun main(args: Array<String>) {
    runApplication<AlphastockApplication>(*args)
}
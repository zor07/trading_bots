package com.zor07.tradingbot.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app")
data class AppProperties(
    val baseUrl: String = "http://localhost:8080"
)

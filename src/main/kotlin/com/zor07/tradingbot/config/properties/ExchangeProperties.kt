package com.zor07.tradingbot.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "exchange")
data class ExchangeProperties(
    val binance: ExchangeConnectionProperties,
    val bybit: ExchangeConnectionProperties
)

data class ExchangeConnectionProperties(
    val baseUrl: String,
    val enabled: Boolean = true
)

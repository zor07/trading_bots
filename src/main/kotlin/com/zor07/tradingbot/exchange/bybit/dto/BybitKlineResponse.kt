package com.zor07.tradingbot.exchange.bybit.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class BybitKlineResponse(
    val retCode: Int,
    val result: BybitKlineResult
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class BybitKlineResult(
    val symbol: String? = null,
    // Each entry: [startTime, open, high, low, close, volume, turnover]
    val list: List<List<String>> = emptyList()
)

package com.zor07.tradingbot.exchange.binance.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class BinanceLsrDto(
    @JsonProperty("longAccount") val longAccount: String
)

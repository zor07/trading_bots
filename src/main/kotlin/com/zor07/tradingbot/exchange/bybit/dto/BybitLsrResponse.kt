package com.zor07.tradingbot.exchange.bybit.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class BybitLsrResponse(
    @JsonProperty("retCode") val retCode: Int,
    @JsonProperty("result") val result: BybitLsrResult
)

data class BybitLsrResult(
    @JsonProperty("list") val list: List<BybitLsrEntry>?
)

data class BybitLsrEntry(
    @JsonProperty("buyRatio") val buyRatio: String
)

package com.zor07.tradingbot.exchange.bybit

import com.fasterxml.jackson.databind.ObjectMapper
import com.zor07.tradingbot.exchange.ExchangeClient
import com.zor07.tradingbot.exchange.bybit.dto.BybitKlineResponse
import com.zor07.tradingbot.exchange.bybit.dto.BybitLsrResponse
import com.zor07.tradingbot.exchange.lsr.LongShortRatioClient
import com.zor07.tradingbot.exchange.model.Kline
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException
import java.math.BigDecimal
import java.time.Instant

@Component
class BybitClient(
    @Qualifier("bybitRestClient") private val restClient: RestClient,
    private val objectMapper: ObjectMapper
) : ExchangeClient, LongShortRatioClient {

    private val log = LoggerFactory.getLogger(javaClass)

    override val exchangeName: String = "BYBIT"

    override fun getLsrByAccounts(symbol: String): Double? {
        val rawJson = try {
            restClient.get()
                .uri { builder ->
                    builder.path("/v5/market/account-ratio")
                        .queryParam("category", "linear")
                        .queryParam("symbol", symbol)
                        .queryParam("period", "5min")
                        .queryParam("limit", 1)
                        .build()
                }
                .retrieve()
                .body(String::class.java)
                ?: return null
        } catch (e: RestClientException) {
            log.warn("Bybit LSR request failed for {}: {}", symbol, e.message)
            return null
        }

        return try {
            val response = objectMapper.readValue(rawJson, BybitLsrResponse::class.java)
            if (response.retCode != 0) return null
            response.result.list?.firstOrNull()?.buyRatio?.toDoubleOrNull()?.times(100)
        } catch (e: Exception) {
            log.warn("Bybit LSR deserialization failed for {}, raw JSON: {}", symbol, rawJson)
            null
        }
    }

    // Bybit v5 does not expose a separate top-trader positions ratio endpoint
    override fun getLsrByPositions(symbol: String): Double? = null

    override fun getKlines(symbol: String, candleInterval: String, limit: Int): List<Kline> {
        val response = restClient.get()
            .uri { builder ->
                builder.path("/v5/market/kline")
                    .queryParam("category", "linear")
                    .queryParam("symbol", symbol)
                    .queryParam("interval", mapInterval(candleInterval))
                    .queryParam("limit", limit)
                    .build()
            }
            .retrieve()
            .body(BybitKlineResponse::class.java)
            ?: return emptyList()

        if (response.retCode != 0) {
            log.warn("Bybit returned error retCode={} for symbol={}", response.retCode, symbol)
            return emptyList()
        }

        val intervalMillis = intervalToMillis(candleInterval)
        return response.result.list.map { row ->
            // row: [startTime, open, high, low, close, volume, turnover]
            val openTime = Instant.ofEpochMilli(row[0].toLong())
            Kline(
                symbol = symbol,
                exchange = exchangeName,
                openTime = openTime,
                open = BigDecimal(row[1]),
                high = BigDecimal(row[2]),
                low = BigDecimal(row[3]),
                close = BigDecimal(row[4]),
                volume = BigDecimal(row[5]),
                closeTime = openTime.plusMillis(intervalMillis)
            )
        }
    }

    private fun mapInterval(candleInterval: String): String = when (candleInterval) {
        "1m"  -> "1"
        "3m"  -> "3"
        "5m"  -> "5"
        "15m" -> "15"
        "30m" -> "30"
        "1h"  -> "60"
        "2h"  -> "120"
        "4h"  -> "240"
        "6h"  -> "360"
        "12h" -> "720"
        "1d"  -> "D"
        else  -> candleInterval
    }

    private fun intervalToMillis(candleInterval: String): Long = when (candleInterval) {
        "1m"  -> 60_000L
        "3m"  -> 180_000L
        "5m"  -> 300_000L
        "15m" -> 900_000L
        "30m" -> 1_800_000L
        "1h"  -> 3_600_000L
        "2h"  -> 7_200_000L
        "4h"  -> 14_400_000L
        "6h"  -> 21_600_000L
        "12h" -> 43_200_000L
        "1d"  -> 86_400_000L
        else  -> 60_000L
    }
}

package com.zor07.tradingbot.exchange.bybit

import com.zor07.tradingbot.exchange.ExchangeClient
import com.zor07.tradingbot.exchange.bybit.dto.BybitKlineResponse
import com.zor07.tradingbot.exchange.model.Kline
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.math.BigDecimal
import java.time.Instant

@Component
class BybitClient(
    @Qualifier("bybitRestClient") private val restClient: RestClient
) : ExchangeClient {

    override val exchangeName: String = "BYBIT"

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

        return response.result.list.map { row ->
            // row: [startTime, open, high, low, close, volume, turnover]
            Kline(
                symbol = symbol,
                exchange = exchangeName,
                openTime = Instant.ofEpochMilli(row[0].toLong()),
                open = BigDecimal(row[1]),
                high = BigDecimal(row[2]),
                low = BigDecimal(row[3]),
                close = BigDecimal(row[4]),
                volume = BigDecimal(row[5]),
                closeTime = Instant.ofEpochMilli(row[0].toLong())
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
}

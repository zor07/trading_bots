package com.zor07.tradingbot.exchange.binance

import com.fasterxml.jackson.databind.JsonNode
import com.zor07.tradingbot.exchange.ExchangeClient
import com.zor07.tradingbot.exchange.SymbolProvider
import com.zor07.tradingbot.exchange.binance.dto.BinanceLsrDto
import com.zor07.tradingbot.exchange.binance.dto.BinanceTickerDto
import com.zor07.tradingbot.exchange.binance.dto.toKline
import com.zor07.tradingbot.exchange.lsr.LongShortRatioClient
import com.zor07.tradingbot.exchange.model.Kline
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class BinanceClient(
    @Qualifier("binanceRestClient") private val restClient: RestClient
) : ExchangeClient, SymbolProvider, LongShortRatioClient {

    override val exchangeName: String = "BINANCE"

    override fun getTopSymbolsByVolume(limit: Int): List<String> {
        val tickers = restClient.get()
            .uri("/fapi/v1/ticker/24hr")
            .retrieve()
            .body(object : ParameterizedTypeReference<List<BinanceTickerDto>>() {})
            ?: emptyList()

        return tickers
            .filter { it.symbol.endsWith("USDT") }
            .sortedByDescending { it.quoteVolume.toBigDecimal() }
            .take(limit)
            .map { it.symbol }
    }

    override fun getLsrByAccounts(symbol: String): Double? =
        fetchLsr("/futures/data/topLongShortAccountRatio", symbol)

    override fun getLsrByPositions(symbol: String): Double? =
        fetchLsr("/futures/data/topLongShortPositionRatio", symbol)

    private fun fetchLsr(path: String, symbol: String): Double? {
        val entries = restClient.get()
            .uri { builder ->
                builder.path(path)
                    .queryParam("symbol", symbol)
                    .queryParam("period", "5m")
                    .queryParam("limit", 1)
                    .build()
            }
            .retrieve()
            .body(object : ParameterizedTypeReference<List<BinanceLsrDto>>() {})
            ?: return null
        return entries.firstOrNull()?.longAccount?.toDoubleOrNull()?.times(100)
    }

    override fun getKlines(symbol: String, candleInterval: String, limit: Int): List<Kline> {
        val rows = restClient.get()
            .uri { builder ->
                builder.path("/fapi/v1/klines")
                    .queryParam("symbol", symbol)
                    .queryParam("interval", candleInterval)
                    .queryParam("limit", limit)
                    .build()
            }
            .retrieve()
            .body(object : ParameterizedTypeReference<List<JsonNode>>() {})
            ?: emptyList()

        return rows.map { it.toKline(symbol, exchangeName) }
    }
}

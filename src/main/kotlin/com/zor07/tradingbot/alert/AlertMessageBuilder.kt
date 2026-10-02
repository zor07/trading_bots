package com.zor07.tradingbot.alert

import com.zor07.tradingbot.alert.settings.OiAlertSettings
import com.zor07.tradingbot.alert.settings.PriceAlertSettings
import kotlin.math.abs

object AlertMessageBuilder {

    fun buildPriceMessage(symbol: String, changePercent: Double, count: Int, settings: PriceAlertSettings): String {
        val emoji = if (changePercent > 0) "🟢" else "🔴"
        val sign = if (changePercent > 0) "+" else ""
        val intervalDisplay = settings.candleInterval.replace("m", "м").replace("h", "ч")
        return """
            |$emoji $symbol $sign${String.format("%.2f", changePercent)}% · $intervalDisplay · ${settings.candleLimit} св.
            |
            |# $count
            |
            |${links(symbol)}
        """.trimMargin()
    }

    fun buildOiMessage(symbol: String, changePercent: Double, prevUsd: Double, currUsd: Double, count: Int, settings: OiAlertSettings): String {
        val usdDelta = currUsd - prevUsd
        val periodDisplay = settings.period.replace("m", "м").replace("h", "ч")
        return """
            |📈 $symbol - $periodDisplay
            |
            |Рост на *${String.format("%.2f", changePercent)}% (${formatUsd(usdDelta)})*
            |
            |# $count
            |
            |${links(symbol)}
        """.trimMargin()
    }

    private fun links(symbol: String): String =
        "Bybit: https://www.bybit.com/ru-RU/trade/usdt/$symbol\nCoinglass: https://www.coinglass.com/tv/ru/Bybit\\_$symbol"

    private fun formatUsd(value: Double): String {
        val a = abs(value)
        return when {
            a >= 1_000_000_000 -> "${String.format("%.2f", value / 1_000_000_000)} млрд. $"
            a >= 1_000_000     -> "${String.format("%.2f", value / 1_000_000)} млн. $"
            else               -> "${String.format("%.0f", value)} $"
        }
    }
}

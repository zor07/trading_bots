package com.zor07.tradingbot.alert.oi

import com.zor07.tradingbot.alert.AlertCounterService
import com.zor07.tradingbot.alert.AlertHistory
import com.zor07.tradingbot.alert.AlertHistoryRepository
import com.zor07.tradingbot.alert.AlertNotifier
import com.zor07.tradingbot.alert.model.AlertType
import com.zor07.tradingbot.bot.BotType
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Instant
import kotlin.math.abs

@Service
class OiAlertService(
    private val repository: AlertHistoryRepository,
    private val notifier: AlertNotifier,
    private val counterService: AlertCounterService
) {

    fun handle(symbol: String, changePercent: Double, prevUsd: Double, currUsd: Double, period: String) {
        val count = counterService.increment("OI:$symbol")
        val periodDisplay = period.replace("m", "м").replace("h", "ч")
        val delta = currUsd - prevUsd

        val message = """
            |📈 $symbol - $periodDisplay
            |
            |Рост на *${String.format("%.2f", changePercent)}% (${formatUsd(delta)})*
            |
            |# $count
            |
            |Bybit: https://www.bybit.com/ru-RU/trade/usdt/$symbol
            |Coinglass: https://www.coinglass.com/tv/ru/Bybit\_$symbol
        """.trimMargin()

        notifier.send(message, BotType.OI)
        repository.save(
            AlertHistory(
                alertType = AlertType.OPEN_INTEREST_SPIKE.name,
                symbol = symbol,
                value = BigDecimal.valueOf(changePercent),
                message = message,
                createdAt = Instant.now()
            )
        )
    }

    private fun formatUsd(value: Double): String {
        val a = abs(value)
        return when {
            a >= 1_000_000_000 -> "${String.format("%.2f", value / 1_000_000_000)} млрд. $"
            a >= 1_000_000     -> "${String.format("%.2f", value / 1_000_000)} млн. $"
            else               -> "${String.format("%.0f", value)} $"
        }
    }
}

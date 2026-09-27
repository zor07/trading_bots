package com.zor07.tradingbot.alert.oi

import com.zor07.tradingbot.alert.AlertHistory
import com.zor07.tradingbot.alert.AlertHistoryRepository
import com.zor07.tradingbot.alert.AlertNotifier
import com.zor07.tradingbot.alert.model.AlertType
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Instant

@Service
class OiAlertService(
    private val repository: AlertHistoryRepository,
    private val notifier: AlertNotifier
) {

    fun handle(symbol: String, changePercent: Double, prevUsd: Double, currUsd: Double) {
        val emoji = if (changePercent > 0) "📈" else "📉"
        val sign = if (changePercent > 0) "+" else ""
        val message = """
            |📊 Open Interest: $symbol $emoji $sign${String.format("%.2f", changePercent)}%
            |${formatUsd(prevUsd)} → ${formatUsd(currUsd)}
            |
            |Bybit: https://www.bybit.com/ru-RU/trade/usdt/$symbol
            |Coinglass: https://www.coinglass.com/tv/ru/Bybit_$symbol
        """.trimMargin()

        notifier.send(message)
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
        return when {
            value >= 1_000_000_000 -> "$${String.format("%.2f", value / 1_000_000_000)}B"
            value >= 1_000_000 -> "$${String.format("%.2f", value / 1_000_000)}M"
            else -> "$${String.format("%.0f", value)}"
        }
    }
}

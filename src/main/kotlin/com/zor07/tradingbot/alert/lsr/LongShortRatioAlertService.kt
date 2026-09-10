package com.zor07.tradingbot.alert.lsr

import com.zor07.tradingbot.alert.AlertHistory
import com.zor07.tradingbot.alert.AlertHistoryRepository
import com.zor07.tradingbot.alert.AlertNotifier
import com.zor07.tradingbot.alert.model.AlertType
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Instant

@Service
class LongShortRatioAlertService(
    private val repository: AlertHistoryRepository,
    private val notifier: AlertNotifier
) {

    fun handle(symbol: String, accountDelta: Double, positionDelta: Double) {
        val accountEmoji = if (accountDelta > 0) "🟢" else "🔴"
        val positionEmoji = if (positionDelta > 0) "🟢" else "🔴"
        val accountDirection = if (accountDelta > 0) "лонги выросли" else "шорты выросли"
        val positionDirection = if (positionDelta > 0) "лонги выросли" else "шорты выросли"

        val message = """
            |⚖️ Long/Short Ratio: $symbol
            |$accountEmoji Аккаунты: ${formatDelta(accountDelta)}% → $accountDirection
            |$positionEmoji Позиции: ${formatDelta(positionDelta)}% → $positionDirection
            |
            |Bybit: https://www.bybit.com/ru-RU/trade/usdt/$symbol
            |Coinglass: https://www.coinglass.com/tv/ru/Bybit_$symbol
        """.trimMargin()

        notifier.send(message)
        repository.save(
            AlertHistory(
                alertType = AlertType.LONG_SHORT_RATIO.name,
                symbol = symbol,
                value = BigDecimal.valueOf(accountDelta),
                message = message,
                createdAt = Instant.now()
            )
        )
    }

    private fun formatDelta(delta: Double): String {
        val sign = if (delta > 0) "+" else ""
        return "$sign${String.format("%.2f", delta)}"
    }
}

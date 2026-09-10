package com.zor07.tradingbot.alert.price

import com.zor07.tradingbot.alert.AlertHistory
import com.zor07.tradingbot.alert.AlertHistoryRepository
import com.zor07.tradingbot.alert.AlertNotifier
import com.zor07.tradingbot.alert.model.AlertType
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Instant

@Service
class PriceAlertService(
    private val repository: AlertHistoryRepository,
    private val notifier: AlertNotifier
) {

    fun handle(symbol: String, changePercent: Double) {
        val emoji = if (changePercent > 0) "🟢" else "🔴"
        val message = """
            |$emoji Price alert: $symbol ${String.format("%.2f", changePercent)}%
            |
            |Bybit: https://www.bybit.com/ru-RU/trade/usdt/$symbol
            |Coinglass: https://www.coinglass.com/tv/ru/Bybit_$symbol
        """.trimMargin()

        notifier.send(message)
        repository.save(
            AlertHistory(
                alertType = AlertType.PRICE_MOVE.name,
                symbol = symbol,
                value = BigDecimal.valueOf(changePercent),
                message = message,
                createdAt = Instant.now()
            )
        )
    }
}

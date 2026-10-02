package com.zor07.tradingbot.alert.price

import com.zor07.tradingbot.alert.AlertCounterService
import com.zor07.tradingbot.alert.AlertHistory
import com.zor07.tradingbot.alert.AlertHistoryRepository
import com.zor07.tradingbot.alert.AlertNotifier
import com.zor07.tradingbot.alert.model.AlertType
import com.zor07.tradingbot.bot.BotType
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Instant

@Service
class PriceAlertService(
    private val repository: AlertHistoryRepository,
    private val notifier: AlertNotifier,
    private val counterService: AlertCounterService
) {

    fun handle(symbol: String, changePercent: Double, candleInterval: String, candleLimit: Int) {
        val count = counterService.increment("PRICE:$symbol")
        val emoji = if (changePercent > 0) "🟢" else "🔴"
        val sign = if (changePercent > 0) "+" else ""
        val intervalDisplay = candleInterval.replace("m", "м").replace("h", "ч")
        val message = """
            |$emoji $symbol $sign${String.format("%.2f", changePercent)}% · $intervalDisplay · $candleLimit св.
            |
            |# $count
            |
            |Bybit: https://www.bybit.com/ru-RU/trade/usdt/$symbol
            |Coinglass: https://www.coinglass.com/tv/ru/Bybit\_$symbol
        """.trimMargin()

        notifier.send(message, BotType.PRICE)
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

package com.zor07.tradingbot.alert.price

import com.zor07.tradingbot.alert.AlertHistory
import com.zor07.tradingbot.alert.AlertHistoryRepository
import com.zor07.tradingbot.alert.AlertNotifier
import com.zor07.tradingbot.alert.model.AlertType
import com.zor07.tradingbot.config.properties.PriceAlertProperties
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant

@Service
class PriceAlertService(
    private val repository: AlertHistoryRepository,
    private val notifier: AlertNotifier,
    private val properties: PriceAlertProperties
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun handle(symbol: String, changePercent: Double, cooldownMinutes: Int) {
        if (!properties.debugMode && isCoolingDown(symbol, cooldownMinutes)) {
            log.debug("Cooldown active for {}, skipping", symbol)
            return
        }

        val emoji = if (changePercent > 0) "🟢" else "🔴"
        val message = "$emoji Price alert: $symbol ${String.format("%.2f", changePercent)}%"

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

    private fun isCoolingDown(symbol: String, cooldownMinutes: Int): Boolean {
        val last = repository.findTopByAlertTypeAndSymbolOrderByCreatedAtDesc(
            AlertType.PRICE_MOVE.name, symbol
        ) ?: return false
        return last.createdAt.isAfter(Instant.now().minus(Duration.ofMinutes(cooldownMinutes.toLong())))
    }
}

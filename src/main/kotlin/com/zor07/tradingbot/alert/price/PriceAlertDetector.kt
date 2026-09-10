package com.zor07.tradingbot.alert.price

import com.zor07.tradingbot.exchange.model.Kline
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode

@Component
class PriceAlertDetector {

    fun computeChange(klines: List<Kline>): Double? {
        if (klines.isEmpty()) return null
        val open = klines.first().open
        val close = klines.last().close
        if (open.compareTo(BigDecimal.ZERO) == 0) return null
        return (close - open)
            .divide(open, 6, RoundingMode.HALF_UP)
            .multiply(BigDecimal("100"))
            .toDouble()
    }
}

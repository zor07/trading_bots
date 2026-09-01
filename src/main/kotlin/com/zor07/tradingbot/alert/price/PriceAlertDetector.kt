package com.zor07.tradingbot.alert.price

import com.zor07.tradingbot.exchange.model.Kline
import org.springframework.stereotype.Component

@Component
class PriceAlertDetector {

    fun detect(klines: List<Kline>, threshold: Double): PriceAlertResult? {
        if (klines.size < 2) return null

        val open = klines.first().open
        val close = klines.last().close
        val changePercent = (close - open).divide(open, 6, java.math.RoundingMode.HALF_UP)
            .multiply(java.math.BigDecimal("100"))
            .toDouble()

        if (Math.abs(changePercent) < threshold) return null

        val direction = if (changePercent > 0) PriceAlertResult.Direction.UP
                        else PriceAlertResult.Direction.DOWN

        return PriceAlertResult(changePercent, direction)
    }
}

package com.zor07.tradingbot.alert.lsr

import com.zor07.tradingbot.exchange.lsr.LongShortRatioSnapshot
import org.springframework.stereotype.Component

data class LongShortRatioChange(val accountDelta: Double, val positionDelta: Double)

@Component
class LongShortRatioDetector {

    fun detect(prev: LongShortRatioSnapshot, curr: LongShortRatioSnapshot): LongShortRatioChange {
        return LongShortRatioChange(
            accountDelta = curr.accountLongRatio - prev.accountLongRatio,
            positionDelta = curr.positionLongRatio - prev.positionLongRatio
        )
    }
}

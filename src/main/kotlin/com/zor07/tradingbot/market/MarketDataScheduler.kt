package com.zor07.tradingbot.market

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class MarketDataScheduler(private val service: MarketDataService) {

    @Scheduled(fixedDelayString = "\${alerts.price.interval}")
    fun run() = service.refresh()
}

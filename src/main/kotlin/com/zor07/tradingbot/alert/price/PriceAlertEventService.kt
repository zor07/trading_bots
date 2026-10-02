package com.zor07.tradingbot.alert.price

import com.zor07.tradingbot.alert.AlertCounterService
import com.zor07.tradingbot.alert.AlertMessageBuilder
import com.zor07.tradingbot.alert.AlertNotifier
import com.zor07.tradingbot.alert.settings.AlertSettingsService
import com.zor07.tradingbot.bot.BotType
import com.zor07.tradingbot.market.SymbolDeltaEvent
import kotlin.math.abs
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class PriceAlertEventService(
    private val settingsService: AlertSettingsService,
    private val notifier: AlertNotifier,
    private val counterService: AlertCounterService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener
    fun onDelta(event: SymbolDeltaEvent) {
        val delta = event.delta
        val prev = delta.prev.price ?: return
        val curr = delta.curr.price ?: return

        val prevClose = prev.close.toDouble()
        val currClose = curr.close.toDouble()
        if (prevClose == 0.0) return

        val settings = settingsService.getPriceSettings()
        if (!settings.enabled) return

        val changePercent = (currClose - prevClose) / prevClose * 100
        log.info("{} price change={}%", delta.symbol, String.format("%.2f", changePercent))

        if (abs(changePercent) < settings.threshold) return

        val count = counterService.increment("PRICE:${delta.symbol}")
        val message = AlertMessageBuilder.buildPriceMessage(delta.symbol, changePercent, count, settings)

        log.info("PRICE ALERT: {} change={}% threshold={}%", delta.symbol, String.format("%.2f", changePercent), settings.threshold)
        notifier.send(message, BotType.PRICE)
    }

}

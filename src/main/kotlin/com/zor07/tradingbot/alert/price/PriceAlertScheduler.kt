package com.zor07.tradingbot.alert.price

import com.zor07.tradingbot.alert.settings.AlertSettingsService
import com.zor07.tradingbot.exchange.ExchangeClient
import com.zor07.tradingbot.user.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import kotlin.math.abs

@Component
class PriceAlertScheduler(
    private val exchangeClients: List<ExchangeClient>,
    private val detector: PriceAlertDetector,
    private val alertService: PriceAlertService,
    private val userService: UserService,
    private val settingsService: AlertSettingsService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(fixedDelayString = "\${alerts.price.interval}")
    fun run() {
        val settings = settingsService.getPriceSettings()
        val symbols = settingsService.getWatchlist()
        val subscriberCount = userService.getChatIds().size

        log.info("Price alert tick: {} symbols, {} exchanges, {} subscribers", symbols.size, exchangeClients.size, subscriberCount)

        if (subscriberCount == 0) {
            log.warn("No subscribers — alerts will not be sent")
            return
        }

        if (!settings.enabled) {
            log.info("Alerts disabled in settings — skipping tick")
            return
        }

        runBlocking {
            symbols.map { symbol ->
                async(Dispatchers.IO) {
                    val changes = exchangeClients.mapNotNull { client ->
                        runCatching {
                            val klines = client.getKlines(symbol, settings.candleInterval, settings.candleLimit)
                            detector.computeChange(klines)
                        }.onFailure {
                            log.warn("Failed to get klines for {} from {}: {}", symbol, client.exchangeName, it.message)
                        }.getOrNull()
                    }

                    if (changes.isEmpty()) return@async

                    val avgChange = changes.average()
                    log.info("{} avgChange={}%", symbol, String.format("%.2f", avgChange))

                    if (abs(avgChange) >= settings.threshold) {
                        log.info("ALERT triggered: {} change={}% threshold={}%", symbol, String.format("%.2f", avgChange), settings.threshold)
                        alertService.handle(symbol, avgChange)
                    }
                }
            }.awaitAll()
        }
    }
}

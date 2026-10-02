package com.zor07.tradingbot

import com.zor07.tradingbot.config.properties.AppProperties
import com.zor07.tradingbot.config.properties.ExchangeProperties
import com.zor07.tradingbot.config.properties.LiquidationAlertProperties
import com.zor07.tradingbot.config.properties.OiAlertProperties
import com.zor07.tradingbot.config.properties.PriceAlertProperties
import com.zor07.tradingbot.config.properties.SymbolsProperties
import com.zor07.tradingbot.config.properties.TelegramProperties
import io.github.cdimascio.dotenv.dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(
    AppProperties::class,
    TelegramProperties::class,
    ExchangeProperties::class,
    SymbolsProperties::class,
    PriceAlertProperties::class,
    OiAlertProperties::class,
    LiquidationAlertProperties::class
)
class TradingBotApplication

fun main(args: Array<String>) {
    dotenv { ignoreIfMissing = true }.entries().forEach {
        System.setProperty(it.key, it.value)
    }
    runApplication<TradingBotApplication>(*args)
}

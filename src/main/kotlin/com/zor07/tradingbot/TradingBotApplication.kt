package com.zor07.tradingbot

import io.github.cdimascio.dotenv.dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@ConfigurationPropertiesScan
class TradingBotApplication

fun main(args: Array<String>) {
    dotenv { ignoreIfMissing = true }.entries().forEach {
        System.setProperty(it.key, it.value)
    }
    runApplication<TradingBotApplication>(*args)
}

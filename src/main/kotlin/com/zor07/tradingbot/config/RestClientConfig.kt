package com.zor07.tradingbot.config

import com.zor07.tradingbot.config.properties.ExchangeProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration
class RestClientConfig(private val exchangeProperties: ExchangeProperties) {

    @Bean("binanceRestClient")
    fun binanceRestClient(builder: RestClient.Builder): RestClient =
        builder.baseUrl(exchangeProperties.binance.baseUrl).build()

    @Bean("bybitRestClient")
    fun bybitRestClient(builder: RestClient.Builder): RestClient =
        builder.baseUrl(exchangeProperties.bybit.baseUrl).build()
}

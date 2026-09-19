package com.zor07.tradingbot.config

import com.zor07.tradingbot.config.properties.ExchangeProperties
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.impl.routing.SystemDefaultRoutePlanner
import org.apache.hc.core5.http.HttpHost
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestClient
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.ProxySelector

@Configuration
class RestClientConfig(private val exchangeProperties: ExchangeProperties) {

    @Bean("binanceRestClient")
    fun binanceRestClient(builder: RestClient.Builder): RestClient =
        builder.baseUrl(exchangeProperties.binance.baseUrl)
            .requestFactory(buildRequestFactory())
            .build()

    @Bean("bybitRestClient")
    fun bybitRestClient(builder: RestClient.Builder): RestClient =
        builder.baseUrl(exchangeProperties.bybit.baseUrl)
            .requestFactory(buildRequestFactory())
            .build()

    private fun buildRequestFactory(): HttpComponentsClientHttpRequestFactory {
        val httpClient = if (exchangeProperties.proxyHost.isNotBlank() && exchangeProperties.proxyPort > 0) {
            val proxy = Proxy(Proxy.Type.SOCKS, InetSocketAddress(exchangeProperties.proxyHost, exchangeProperties.proxyPort))
            val proxySelector = object : ProxySelector() {
                override fun select(uri: java.net.URI) = listOf(proxy)
                override fun connectFailed(uri: java.net.URI, sa: java.net.SocketAddress, ioe: java.io.IOException) {}
            }
            HttpClients.custom()
                .setRoutePlanner(SystemDefaultRoutePlanner(proxySelector))
                .build()
        } else {
            HttpClients.createDefault()
        }
        return HttpComponentsClientHttpRequestFactory(httpClient)
    }
}

package com.zor07.tradingbot.exchange.binance

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.zor07.tradingbot.exchange.binance.dto.BinanceLiquidationDto
import com.zor07.tradingbot.market.LiquidationData
import com.zor07.tradingbot.market.LiquidationEvent
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.time.Instant

@Component
class BinanceLiquidationWebSocketClient(
    private val objectMapper: ObjectMapper,
    private val publisher: ApplicationEventPublisher
) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val url = "wss://fstream.binance.com/ws/!forceOrder@arr"

    @EventListener(ApplicationReadyEvent::class)
    fun connect() {
        log.info("Connecting to Binance liquidation stream: {}", url)
        runCatching {
            StandardWebSocketClient().execute(handler(), url).get()
            log.info("Connected to Binance liquidation stream")
        }.onFailure {
            log.error("Failed to connect to Binance liquidation stream: {}", it.message)
        }
    }

    private fun handler() = object : TextWebSocketHandler() {

        override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
            runCatching {
                val dto = objectMapper.readValue<BinanceLiquidationDto>(message.payload)
                val order = dto.order
                val price = order.price.toDouble()
                val quantity = order.quantity.toDouble()
                val usdValue = price * quantity

                log.info("Liquidation: {} {} price={} qty={} usd={}", order.symbol, order.side, price, quantity, usdValue)

                publisher.publishEvent(
                    LiquidationEvent(
                        symbol = order.symbol,
                        data = LiquidationData(
                            side = order.side,
                            price = price,
                            quantity = quantity,
                            usdValue = usdValue,
                            timestamp = Instant.ofEpochMilli(order.timestamp)
                        )
                    )
                )
            }.onFailure {
                log.error("Failed to handle liquidation message: {}", it.message)
            }
        }

        override fun afterConnectionEstablished(session: WebSocketSession) {
            log.info("Liquidation WebSocket connected")
        }

        override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
            log.error("Liquidation WebSocket error: {}", exception.message)
        }
    }
}

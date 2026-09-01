package com.zor07.tradingbot.alert

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "alert_history")
data class AlertHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "alert_type", nullable = false)
    val alertType: String,

    @Column(name = "symbol", nullable = false)
    val symbol: String,

    @Column(name = "exchange")
    val exchange: String? = null,

    @Column(name = "value")
    val value: BigDecimal? = null,

    @Column(name = "message")
    val message: String? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now()
)

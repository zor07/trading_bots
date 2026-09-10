package com.zor07.tradingbot.user

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "chat_id", nullable = false, unique = true)
    val chatId: Long,

    @Column(name = "username")
    val username: String? = null,

    @Column(name = "subscribed_at", nullable = false)
    val subscribedAt: Instant = Instant.now(),

    @Column(name = "auth_token", unique = true)
    val authToken: String? = null
)

package com.zor07.tradingbot.releasenote

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "release_notes")
data class ReleaseNote(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val message: String,

    @Column(nullable = false)
    var sent: Boolean = false,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now()
)

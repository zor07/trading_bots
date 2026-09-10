package com.zor07.tradingbot.user.alert

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.Instant

@Entity
@Table(
    name = "user_alert_settings",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "alert_type"])]
)
data class UserAlertSettings(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "alert_type", nullable = false)
    val alertType: String,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "settings", nullable = false, columnDefinition = "jsonb")
    val settings: String,

    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant = Instant.now()
)

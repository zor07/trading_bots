package com.zor07.tradingbot.user

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun existsByChatIdAndBotType(chatId: Long, botType: String): Boolean
    fun findFirstByChatId(chatId: Long): User?
    fun findByChatIdAndBotType(chatId: Long, botType: String): User?
    fun findByAuthToken(authToken: String): User?
    fun findAllByBotType(botType: String): List<User>
}

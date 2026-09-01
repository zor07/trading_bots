package com.zor07.tradingbot.user

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun existsByChatId(chatId: Long): Boolean
    fun findByChatId(chatId: Long): User?
}

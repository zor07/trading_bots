package com.zor07.tradingbot.user

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.CopyOnWriteArrayList
import jakarta.annotation.PostConstruct

@Service
class UserService(private val repository: UserRepository) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val cache = CopyOnWriteArrayList<Long>()

    @PostConstruct
    fun init() {
        val chatIds = repository.findAll().map { it.chatId }
        cache.addAllAbsent(chatIds)
        log.info("Loaded {} subscribed users into cache", cache.size)
    }

    fun subscribe(chatId: Long, username: String?): Boolean {
        if (repository.existsByChatId(chatId)) return false
        repository.save(User(chatId = chatId, username = username))
        cache.addIfAbsent(chatId)
        log.info("New subscriber: chatId={}, username={}", chatId, username)
        return true
    }

    fun getChatIds(): List<Long> = cache.toList()
}

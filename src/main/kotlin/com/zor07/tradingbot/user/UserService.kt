package com.zor07.tradingbot.user

import com.zor07.tradingbot.bot.BotType
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

@Service
class UserService(private val repository: UserRepository) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val cache = ConcurrentHashMap<BotType, CopyOnWriteArrayList<Long>>()

    @PostConstruct
    fun init() {
        BotType.entries.forEach { botType ->
            val chatIds = repository.findAllByBotType(botType.name).map { it.chatId }
            cache[botType] = CopyOnWriteArrayList(chatIds)
        }
        log.info("Loaded {} price subscribers, {} OI subscribers into cache",
            cache[BotType.PRICE]?.size ?: 0,
            cache[BotType.OI]?.size ?: 0)
    }

    fun subscribe(chatId: Long, username: String?, botType: BotType): Boolean {
        if (repository.existsByChatIdAndBotType(chatId, botType.name)) return false
        repository.save(User(chatId = chatId, username = username, botType = botType.name))
        cache.getOrPut(botType) { CopyOnWriteArrayList() }.addIfAbsent(chatId)
        log.info("New subscriber: chatId={}, username={}, bot={}", chatId, username, botType)
        return true
    }

    fun generateToken(chatId: Long): String? {
        val user = repository.findFirstByChatId(chatId) ?: return null
        val token = UUID.randomUUID().toString()
        repository.save(user.copy(authToken = token))
        return token
    }

    fun findByToken(token: String): User? = repository.findByAuthToken(token)

    fun getChatIds(botType: BotType): List<Long> = cache[botType]?.toList() ?: emptyList()

    fun getAllChatIds(): List<Long> = BotType.entries.flatMap { getChatIds(it) }.distinct()
}

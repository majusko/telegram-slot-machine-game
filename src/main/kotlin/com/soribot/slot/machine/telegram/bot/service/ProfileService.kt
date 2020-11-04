package com.soribot.slot.machine.telegram.bot.service

import com.soribot.slot.machine.telegram.bot.bot.BotSender
import com.soribot.slot.machine.telegram.bot.repository.Profile
import com.soribot.slot.machine.telegram.bot.repository.ProfileRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Message

@Service
@ExperimentalCoroutinesApi
class ProfileService(
    private val profileRepository: ProfileRepository,
    private val botSender: BotSender,
    private val redisTemplate: RedisTemplate<String, Long>
) {
    companion object {
        const val successfullySaved = "Bardzo dobrze przechowywane."
        const val profileInformation = "Twój piękny profil: \n Imię i Nazwisko: %s \n kurtki: %s"
        const val slotPushCount = "SlotPushCount"
        const val dicePushCount = "DicePushCount"
        const val spentPointsCount = "SpentPointsCount"
    }

    fun getSlotPushCount(id: Int) = redisTemplate.boundValueOps(slotPushCount + "_" + id).get() ?: 0

    fun getDicePushCount(id: Int) = redisTemplate.boundValueOps(dicePushCount + "_" + id).get() ?: 0

    fun getSpentPointsCount(id: Int) = redisTemplate.boundValueOps(spentPointsCount + "_" + id).get() ?: 0

    fun incrementSpentPointsCount(message: Message, amount: Long) = redisTemplate
        .boundValueOps(spentPointsCount + "_" + message.from.id)
        .run {
            set((get() ?: 0) + amount)
        }

    fun incrementSlotPushCount(message: Message) = redisTemplate
        .boundValueOps(slotPushCount + "_" + message.from.id)
        .run {
            set((get() ?: 0) + 1)
        }

    fun incrementDicePushCount(message: Message) = redisTemplate
        .boundValueOps(dicePushCount + "_" + message.from.id)
        .run {
            set((get() ?: 0) + 1)
        }

    fun findById(id: Int) = profileRepository.findByIdOrNull(id)

    fun findByIdOrRegister(message: Message) = findById(message.from.id) ?: Profile(
        firstName = message.from.firstName ?: "",
        lastName = message.from.lastName ?: "",
        username = message.from.userName ?: "",
        id = message.from.id
    ).let { saveAndNotify(it, message.chatId) }

    fun save(profile: Profile) = profileRepository.save(profile)

    fun findAll() = profileRepository.findAll().toList()

    fun saveAndNotify(profile: Profile, chatId: Long) = save(profile)
        .also {
            botSender.textAsync(chatId, successfullySaved)
            botSender.textAsync(chatId, profile.toInfoMessage())
        }

    private fun Profile.toInfoMessage() = profileInformation.format("$firstName $lastName", numberOfJackpots)
}
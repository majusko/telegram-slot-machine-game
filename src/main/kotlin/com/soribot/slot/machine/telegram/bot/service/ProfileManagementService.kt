package com.soribot.slot.machine.telegram.bot.service

import com.soribot.slot.machine.telegram.bot.bot.BotSender
import com.soribot.slot.machine.telegram.bot.repository.Profile
import com.soribot.slot.machine.telegram.bot.repository.points
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update

@Service
@ExperimentalCoroutinesApi
class ProfileManagementService(
    private val profileService: ProfileService,
    private val botSender: BotSender,
    private val leaderboardService: LeaderboardService
) {
    companion object {
        const val successfullySaved = "Bardzo dobrze przechowywane."
        const val editTrigger = "nastav"
        const val myCountsTrigger = "kolko"
        val recordTrigger = listOf("rekord", "kurtk")
        val lemonTrigger = listOf("citron", "cytryn")
        val barTrigger = listOf("bar")
        val cherryTrigger = listOf("ceresn", "wisnia")
        val spentTrigger = listOf("minul")
        val diceTrigger = listOf("kostki", "kocky")
    }

    fun start(update: Update) = updateProfile(update.message)

    private fun updateProfile(message: Message) {
        if (message.hasText() && message.isReply) {
            message.text.split("\\s".toRegex()).chunked(3).filter { it.size == 3 }.map {
                editProfileByCommand(message, it[0], it[1], it[2])
            }.also {
                if (it.isNotEmpty()) {
                    botSender.textAsync(message.chatId, successfullySaved)
                    leaderboardService.sendLightLeaderboards(message)
                }
            }
        }
        if (message.hasText() && message.text == myCountsTrigger) {
            val userId = if (message.isReply) {
                message.replyToMessage.from.id
            } else {
                message.from.id
            }

            val text = "Ovocie: " + profileService.getSlotPushCount(userId) + " \n" +
                "Kostki: " + profileService.getDicePushCount(userId) + " \n" +
                "Puntki: " + profileService.findById(userId)?.points()
            botSender.textAsync(message.chatId, text)
        }
    }

    private fun String.handleNumber(count: String) = if (contains(editTrigger)) {
        count.toInt()
    } else {
        null
    }

    private fun editProfileByCommand(message: Message, command: String, amount: String, field: String) =
        command.handleNumber(amount)?.also { convertedAmount ->
            when {
                recordTrigger.any { field.contains(it) } -> profileService.findByIdOrRegister(message.replyToMessage)
                    .apply { numberOfJackpots = convertedAmount }
                    .also { afterEdit(it, message) }
                lemonTrigger.any { field.contains(it) } -> profileService.findByIdOrRegister(message.replyToMessage)
                    .apply { threeLemons = convertedAmount }
                    .also { afterEdit(it, message) }
                barTrigger.any { field.contains(it) } -> profileService.findByIdOrRegister(message.replyToMessage)
                    .apply { threeBars = convertedAmount }
                    .also { afterEdit(it, message) }
                cherryTrigger.any { field.contains(it) } -> profileService.findByIdOrRegister(message.replyToMessage)
                    .apply { threeCherries = convertedAmount }
                    .also { afterEdit(it, message) }
                spentTrigger.any { field.contains(it) } -> profileService.findByIdOrRegister(message.replyToMessage)
                    .apply { spentPoints = convertedAmount.toLong() }
                    .also { afterEdit(it, message) }
                diceTrigger.any { field.contains(it) } -> profileService.findByIdOrRegister(message.replyToMessage)
                    .apply { diceWins = convertedAmount }
                    .also { afterEdit(it, message) }
            }
        }

    fun afterEdit(profile: Profile, message: Message) {
        profileService.save(profile)
    }
}
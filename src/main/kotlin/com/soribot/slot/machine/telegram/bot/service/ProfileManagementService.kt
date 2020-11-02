package com.soribot.slot.machine.telegram.bot.service

import com.soribot.slot.machine.telegram.bot.bot.BotSender
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
        val recordTrigger = listOf("rekord", "kurtk")
    }

    fun start(update: Update) = updateProfile(update.message)

    private fun updateProfile(message: Message) {
        if (message.hasText() && message.isReply) {
            val commands = message.text.split("\\s".toRegex())

            if (commands.size == 3) {
                editProfileByCommand(message, commands[0], commands[1], commands[2])
            }
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
                recordTrigger.any { field.contains(it) } -> {
                    profileService.findByIdOrRegister(message.replyToMessage)
                        .apply { numberOfJackpots = convertedAmount }
                        .also {
                            profileService.save(it)
                            botSender.textAsync(message.chatId, successfullySaved)
                            leaderboardService.sendLeaderboards(message)
                        }
                }
            }
        }
}
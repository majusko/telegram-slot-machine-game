package com.soribot.slot.machine.telegram.bot.bot

import com.soribot.slot.machine.telegram.bot.service.SlotMachineService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendDice
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message

@Component
@ExperimentalCoroutinesApi
class BotSender(val slotMachineSender: BroadcastChannel<BotApiMethod<Message>>) {

    suspend fun text(chatId: Long, text: String): SendMessage = SendMessage()
        .setChatId(chatId)
        .setText(text)
        .also { slotMachineSender.send(it) }

    suspend fun dice(chatId: Long, replyTo: Int): SendDice = SendDice()
        .setChatId(chatId)
        .setReplyToMessageId(replyTo)
        .setEmoji(SlotMachineService.diceEmoji)
        .also { slotMachineSender.send(it) }

    fun textAsync(chatId: Long, text: String) = runBlocking {
        launch { text(chatId, text) }
    }

    fun diceAsync(chatId: Long, replyTo: Int) = runBlocking {
        launch { dice(chatId, replyTo) }
    }
}
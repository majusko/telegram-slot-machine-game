package com.soribot.slot.machine.telegram.bot.bot

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

@Component
@ExperimentalCoroutinesApi
class BotSender(val slotMachineSender: BroadcastChannel<SendMessage>) {

    suspend fun text(chatId: Long, text: String): SendMessage = SendMessage()
        .setChatId(chatId)
        .setText(text)
        .also { slotMachineSender.send(it) }

    fun textAsync(chatId: Long, text: String) = runBlocking {
        launch { text(chatId, text) }
    }
}
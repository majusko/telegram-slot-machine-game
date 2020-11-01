package com.soribot.slot.machine.telegram.bot.configuration

import com.soribot.slot.machine.telegram.bot.bot.SlotsMachineBot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.generics.BotSession
import javax.annotation.PreDestroy

@ExperimentalCoroutinesApi
@Configuration
class SlotMachineBotConfiguration(slotsMachineBot: SlotsMachineBot) {

    private final val sessions = mutableListOf<BotSession>()

    companion object {
        init {
            ApiContextInitializer.init()
        }
    }

    init {
        val telegramBotsApi = TelegramBotsApi()
        sessions.add(telegramBotsApi.registerBot(slotsMachineBot))
    }

    @PreDestroy
    fun destroy() = sessions.forEach { it.stop() }
}
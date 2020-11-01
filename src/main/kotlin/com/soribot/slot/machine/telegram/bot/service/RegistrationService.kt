package com.soribot.slot.machine.telegram.bot.service

import com.soribot.slot.machine.telegram.bot.bot.BotSender
import com.soribot.slot.machine.telegram.bot.repository.Profile
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update

@Service
@ExperimentalCoroutinesApi
class RegistrationService(
    private val profileService: ProfileService,
    private val botSender: BotSender
) {
    companion object {
        const val firsNameText = "Jak masz na imię?"
        const val lastNameText = "Jakie jest Twoje nazwisko?"
        val nameRegex = "([A-Z][a-zA-Z]*)".toRegex()
    }

    fun start(update: Update) = profileService.findById(update.message.from.id)
        ?.also { profileValidation(it, update.message) }
        ?: firstUserRegistration(update.message)

    fun firstUserRegistration(message: Message) = Profile(
        firstName = message.from.firstName ?: "",
        lastName = message.from.lastName ?: "",
        username = message.from.userName ?: "",
        id = message.from.id
    ).let { profileService.saveAndNotify(it, message.chatId) }

    private fun profileValidation(profile: Profile, message: Message) = profile.also {
        when {
            it.firstName.isBlank() -> firstNameStep(it, message)
            it.lastName.isBlank() -> lastNameStep(it, message)
            else -> {
            }
        }
    }

    private fun firstNameStep(profile: Profile, message: Message) = simpleUpdateFlow(
        profile.apply { firstName = message.text }, message, nameRegex, firsNameText)

    private fun lastNameStep(profile: Profile, message: Message) = simpleUpdateFlow(
        profile.apply { lastName = message.text }, message, nameRegex, lastNameText)

    private fun simpleUpdateFlow(profile: Profile, message: Message, regex: Regex, text: String) {
        if (message.text.matches(regex)) {
            profile.also { profileService.saveAndNotify(it, message.chatId) }
        } else {
            botSender.textAsync(message.chatId, text)
        }
    }
}
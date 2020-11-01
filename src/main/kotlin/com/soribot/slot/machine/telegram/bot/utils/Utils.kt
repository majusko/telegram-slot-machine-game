package com.soribot.slot.machine.telegram.bot.utils

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread

@ExperimentalCoroutinesApi
fun <T> BroadcastChannel<T>.subscribe(consumer: suspend (nextElement: T) -> Unit) {
    thread {
        runBlocking {
            consumeEach { consumer(it) }
        }
    }
}
package com.soribot.slot.machine.telegram.bot.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories

@Configuration
@EnableRedisRepositories
class RedisConfiguration {

    @Value("\${app.redis.configuration.host}")
    var redisHost: String = ""

    @Value("\${app.redis.configuration.port}")
    var redisPort: Int = 0

    @Value("\${app.redis.configuration.password:}")
    var password: String = ""

    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory {
        val config = RedisStandaloneConfiguration(redisHost, redisPort)

        if (password.isNotEmpty()) {
            config.setPassword(password)
        }

        return LettuceConnectionFactory(config)
    }
}
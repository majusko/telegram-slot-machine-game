package com.soribot.slot.machine.telegram.bot.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.GenericToStringSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer


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

    @Bean
    fun redisTemplate(redisConnectionFactory: LettuceConnectionFactory): RedisTemplate<String, Long> {
        val template = RedisTemplate<String, Long>()
        template.setConnectionFactory(redisConnectionFactory)
        template.keySerializer = StringRedisSerializer()
        template.hashValueSerializer = GenericToStringSerializer(Long::class.java)
        template.valueSerializer = GenericToStringSerializer(Long::class.java)
        return template
    }

    @Bean
    fun redisListTemplate(redisConnectionFactory: LettuceConnectionFactory): RedisTemplate<String, List<Int>> {
        val template = RedisTemplate<String, List<Int>>()
        template.setConnectionFactory(redisConnectionFactory)
        template.keySerializer = StringRedisSerializer()
        template.hashValueSerializer = GenericToStringSerializer(List::class.java)
        template.valueSerializer = GenericToStringSerializer(List::class.java)
        return template
    }
}
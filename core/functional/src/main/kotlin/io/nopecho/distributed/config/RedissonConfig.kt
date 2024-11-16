package io.nopecho.distributed.config

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@ConfigurationPropertiesScan
@Configuration
class RedissonConfig(
    private val properties: LockProperties,
) {

    @Bean
    @ConditionalOnMissingBean
    fun redissonClient(): RedissonClient {
        val config = Config().apply {
            useSingleServer().setAddress(properties.getSingleRedissonAddress())
        }
        return Redisson.create(config)
    }
}
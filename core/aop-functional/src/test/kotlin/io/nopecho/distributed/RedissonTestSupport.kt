package io.nopecho.distributed

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.utility.DockerImageName.parse


@SpringJUnitConfig(RedissonTestSupport.Companion.TestRedissonConfig::class)
abstract class RedissonTestSupport {

    companion object {
        private const val REDIS_IMAGE = "redis:7.0-alpine"

        @Container
        val redisContainer = GenericContainer<Nothing>(parse(REDIS_IMAGE)).apply {
            exposedPorts = listOf(6379)
            start()
        }

        @Configuration
        class TestRedissonConfig {

            @Bean
            @Primary
            fun testRedissonClient(): RedissonClient {
                val addr = "redis://${redisContainer.host}:${redisContainer.firstMappedPort}"
                val config = Config().apply {
                    useSingleServer().setAddress(addr)
                }

                return Redisson.create(config).apply {
                    println(this.config.toYAML())
                }
            }
        }
    }
}
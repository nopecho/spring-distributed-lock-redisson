package io.nopecho.distributed.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "distributed.lock")
data class LockProperties(
    val redisson: RedissonClientProperties,
) {
    fun getSingleRedissonAddress() = redisson.getSingleServerAddress()
}

@ConfigurationProperties(prefix = "distributed.lock.redisson")
data class RedissonClientProperties(
    val host: String,
    val port: Int = 6379,
    val username: String = "",
    val password: String = "",
) {
    fun getSingleServerAddress(): String {
        return when {
            hasCredentials() -> "redis://$username:$password@$host:$port"
            else -> "redis://$host:$port"
        }
    }

    private fun hasCredentials() = username.isNotBlank() || password.isNotBlank()
}
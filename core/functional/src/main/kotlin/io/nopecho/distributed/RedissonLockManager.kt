package io.nopecho.distributed

import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.stereotype.Component
import java.util.concurrent.locks.Lock

@Component
@ConditionalOnMissingBean(DistributedLockManager::class)
class RedissonLockManager(private val client: RedissonClient) : DistributedLockManager {

    override fun getLock(key: String): Lock {
        return client.getLock(key)
    }

    override fun tryLock(lock: Lock, option: LockOption): Boolean {
        lock as RLock
        return lock.tryLock(option.waitTime, option.leaseTime, option.timeUnit)
    }

    override fun unlock(lock: Lock) {
        lock as RLock
        lock.unlock()
    }
}
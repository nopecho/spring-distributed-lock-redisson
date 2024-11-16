package io.nopecho.distributed

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

class RedissonLockManagerTest : RedissonTestSupport() {

    @Autowired
    private lateinit var client: RedissonClient

    private lateinit var sut: RedissonLockManager

    @BeforeEach
    fun setUp() {
        sut = RedissonLockManager(client)
    }

    @Test
    fun `Lock 을 획득할 수 있다`() {
        val lock = sut.getLock(UUID.randomUUID().toString()) as RLock
        sut.tryLock(lock)
        lock.isLocked shouldBe true
    }

    @Test
    fun `Lock 획득 후 해제할 수 있다`() {
        val lock = sut.getLock(UUID.randomUUID().toString()) as RLock
        sut.tryLock(lock)
        lock.isLocked shouldBe true

        sut.unlock(lock)
        lock.isLocked shouldBe false
    }

    @Test
    fun `분산 락 테스트 - 동시요청 200건`() {
        val numOfThread = 200
        val numOfRepeat = 1000
        var target = numOfThread * numOfRepeat

        ConcurrentUtils.run(threadCount = numOfThread) {
            sut.lock("number lock") {
                repeat(numOfRepeat) {
                    target--
                }
            }
        }

        target shouldBe 0
    }
}
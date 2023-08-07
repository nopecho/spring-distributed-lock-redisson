package io.nopecho.distributed.services;

import io.nopecho.distributed.SetupRedisTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import static org.assertj.core.api.Assertions.*;

class RedissonLockServiceTest extends SetupRedisTest {

    Config config = new Config();
    RedissonClient redissonClient;

    DistributedLockService sut;

    @BeforeEach
    void setUp() {
        config.useSingleServer().setAddress("redis://localhost:6379");
        redissonClient = Redisson.create(config);
        sut = new RedissonLockService(redissonClient);
    }

    @DisplayName("Lock 객체를 생성 할 수 있다.")
    @Test
    void getLock() {
        Lock lock = sut.getLock("lock1");
        RLock rLock = (RLock) lock;

        String actual = rLock.getName();

        assertThat(actual).isEqualTo("lock1");
    }

    @DisplayName("Lock 획득 성공 시 Redis 에 Lock 정보가 저장된다.")
    @Test
    void tryLock() throws InterruptedException {
        Lock lock = sut.getLock("any");

        boolean isLocked = sut.tryLock(lock, 1L, 1L, TimeUnit.SECONDS);
        long lockCount = redissonClient.getKeys().getKeysStream().count();

        assertThat(isLocked).isTrue();
        assertThat(lockCount).isEqualTo(1);
    }
}
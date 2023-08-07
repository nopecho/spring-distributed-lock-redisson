package io.nopecho.distributed;

import io.nopecho.distributed.services.*;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.*;


class DistributedLockAspectTest extends SetupTestRedis {

    SpelExpressionParser parser = new SpelExpressionParser();
    KeyParseService keyParseService = new SpelParseService(parser);
    AopTransaction aopTransaction = new AopTransaction();
    Config config = new Config();
    RedissonClient redissonClient;
    DistributedLockService lockService;

    AspectJProxyFactory proxyFactory;
    DistributedLockAspect sut;

    @BeforeEach
    void setUp() {
        config.useSingleServer().setAddress("redis://localhost:6666");
        redissonClient = Redisson.create(config);
        lockService = new RedissonLockService(redissonClient);

        sut = new DistributedLockAspect(lockService, keyParseService, aopTransaction);
    }

    @AfterEach
    void tearDown() {
        proxyFactory = null;
    }

    @DisplayName("@DistributedLock 미적용 시 동시성이 보장되지 않는다.")
    @Test
    void noneAopTest() throws InterruptedException {
        int maxThreadsCount = 200;
        ExecutorService threadPool = Executors.newFixedThreadPool(maxThreadsCount);
        CountDownLatch latch = new CountDownLatch(maxThreadsCount);
        AopTargetClass target = AopTargetClass.initCount(maxThreadsCount);
        for (int i = 0; i < maxThreadsCount; i++) {
            threadPool.execute(() -> {
                target.simpleDecrease();
                latch.countDown();
            });
        }
        latch.await();

        int actual = target.initCount;

        assertThat(actual).isGreaterThan(0);
    }

    @DisplayName("동시성 테스트 : 200건 동시 요청")
    @Test
    void aopTest() throws InterruptedException {
        int maxThreadsCount = 200;
        ExecutorService threadPool = Executors.newFixedThreadPool(maxThreadsCount);
        CountDownLatch latch = new CountDownLatch(maxThreadsCount);
        addProxy(AopTargetClass.initCount(maxThreadsCount));
        AopTargetClass proxy = proxyFactory.getProxy();
        for (int i = 0; i < maxThreadsCount; i++) {
            threadPool.execute(() -> {
                try {
                    proxy.lockDecrease("lock");
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        int actual = proxy.initCount;

        assertThat(actual).isEqualTo(0);
    }

    void addProxy(Object o) {
        proxyFactory = new AspectJProxyFactory(o);
        proxyFactory.addAspect(sut);
    }



    @AllArgsConstructor
    static class AopTargetClass {

        public int initCount;

        public static AopTargetClass initCount(int count) {
            return new AopTargetClass(count);
        }

        @DistributedLock(key = "#key", waitTime = 10L)
        public void lockDecrease(String key) {
            try {
                Thread.sleep(300L);
                this.initCount -= 1;
            } catch (Exception ignored) {
            }
        }

        public void simpleDecrease() {
            try {
                Thread.sleep(300L);
                this.initCount -= 1;
            } catch (Exception ignored) {
            }
        }
    }
}
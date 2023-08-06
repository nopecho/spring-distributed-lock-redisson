package io.nopecho.distributed.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedissonLockService implements DistributedLockService {

    private final RedissonClient redissonClient;

    @Override
    public boolean tryLock(Lock lock, Long waitTime, Long leaseTime, TimeUnit timeUnit) throws InterruptedException {
        RLock redissonLock = (RLock) lock;
        return redissonLock.tryLock(waitTime, leaseTime, timeUnit);
    }

    @Override
    public Lock getLock(String key) {
        return redissonClient.getLock(key);
    }

    @Override
    public void unLock(Lock lock) {
        RLock redissonLock = (RLock) lock;
        try {
            redissonLock.unlock();
        } catch (IllegalMonitorStateException e) {
            log.info("Redisson Lock is Already UnLock. lock: {}", redissonLock.getName());
        }
    }
}

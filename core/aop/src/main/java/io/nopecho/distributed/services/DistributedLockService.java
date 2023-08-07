package io.nopecho.distributed.services;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public interface DistributedLockService {

    Lock getLock(String key);

    boolean tryLock(Lock lock, Long waitTime, Long leaseTime, TimeUnit timeUnit) throws InterruptedException;

    void unLock(Lock lock);

}

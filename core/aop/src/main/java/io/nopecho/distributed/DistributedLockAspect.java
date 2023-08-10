package io.nopecho.distributed;

import io.nopecho.distributed.exceptions.LockAcquisitionFailureException;
import io.nopecho.distributed.services.AopTransaction;
import io.nopecho.distributed.services.DistributedLockService;
import io.nopecho.distributed.services.KeyParseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.locks.Lock;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DistributedLockAspect {

    private static final String LOCK_KEY_PREFIX = "lock:";
    private final DistributedLockService lockService;
    private final KeyParseService keyParseService;
    private final AopTransaction aopTransaction;

    @Around("@annotation(DistributedLock)")
    public Object distributedLock(final ProceedingJoinPoint joinPoint) throws Throwable {
        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        DistributedLock annotation = getDistributedLockAnnotation(signature);

        final String key = getLockKey(signature, joinPoint, annotation);
        final Lock lock = lockService.getLock(key);

        try {
            boolean isLocked = lockService.tryLock(lock, annotation.waitTime(), annotation.leaseTime(), annotation.timeUnit());
            if (!isLocked) {
                throw new LockAcquisitionFailureException("Redisson Lock Acquire Failure. Already Using Lock. key = " + key);
            }
            return aopTransaction.proceed(joinPoint);
        } finally {
            lockService.unLock(lock);
        }
    }

    private DistributedLock getDistributedLockAnnotation(MethodSignature signature) {
        Method method = signature.getMethod();
        return method.getAnnotation(DistributedLock.class);
    }

    private String getLockKey(MethodSignature signature, ProceedingJoinPoint joinPoint, DistributedLock annotation) {
        return LOCK_KEY_PREFIX + keyParseService.parseDynamicKey(signature.getParameterNames(), joinPoint.getArgs(), annotation.key());
    }
}

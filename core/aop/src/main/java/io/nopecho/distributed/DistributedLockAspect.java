package io.nopecho.distributed;

import io.nopecho.distributed.parser.KeyParseService;
import io.nopecho.distributed.service.DistributedLockService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.locks.Lock;

@Aspect
@Component
@RequiredArgsConstructor
public class DistributedLockAspect {

    private static final String LOCK_PREFIX = "LOCK:";

    private final DistributedLockService lockService;
    private final KeyParseService parseService;

    @Around("@annotation(io.nopecho.distributed.DistributedLock)")
    public Object distributedLock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        DistributedLock annotation = getDistributedLockAnnotation(signature);

        String key = getLockKey(signature, joinPoint, annotation);
        Lock lock = lockService.getLock(key);

        try {
            boolean isLock = lockService.tryLock(lock, annotation.waitTime(), annotation.leaseTime(), annotation.timeUnit());
            if(!isLock) {
                return false;
            }

            return joinPoint.proceed();
        } catch (InterruptedException e) {
            throw e;
        } finally {
            lockService.unLock(lock);
        }
    }

    private String getLockKey(MethodSignature signature, ProceedingJoinPoint joinPoint, DistributedLock annotation) {
        return LOCK_PREFIX + parseService.parseDynamicKey(signature.getParameterNames(), joinPoint.getArgs(), annotation.key());
    }

    private DistributedLock getDistributedLockAnnotation(MethodSignature signature) {
        Method method = signature.getMethod();
        return method.getAnnotation(DistributedLock.class);
    }
}

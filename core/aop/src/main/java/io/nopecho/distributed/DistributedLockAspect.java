package io.nopecho.distributed;

import io.nopecho.distributed.exceptions.LockAcquisitionFailureException;
import io.nopecho.distributed.services.AopTransaction;
import io.nopecho.distributed.services.DistributedLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DistributedLockAspect {

    private static final String LOCK_KEY_PREFIX = "lock:";
    private final DistributedLockService lockService;
    private final AopTransaction aopTransaction;
    private final ExpressionParser parser = new SpelExpressionParser();

    @Around("@annotation(distributedLock)")
    public Object distributedLock(final ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        final String key = getLockKey(signature, joinPoint, distributedLock);

        Lock lock = lockService.getLock(key);
        try {
            boolean isLocked = lockService.tryLock(lock, distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());
            if (!isLocked) {
                throw new LockAcquisitionFailureException("Redisson Lock Acquire Failure. Already Using Lock. key = " + key);
            }
            return aopTransaction.proceed(joinPoint);
        } finally {
            lockService.unLock(lock);
        }
    }

    private String getLockKey(MethodSignature signature, ProceedingJoinPoint joinPoint, DistributedLock annotation) {
        return LOCK_KEY_PREFIX + parseDynamicKey(signature.getParameterNames(), joinPoint.getArgs(), annotation.key());
    }

    public String parseDynamicKey(String[] paramNames, Object[] args, String key) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }
        return parser.parseExpression(key).getValue(context, String.class);
    }
}

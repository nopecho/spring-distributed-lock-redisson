package io.nopecho.distributed;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * This Is An Annotation For The Implementation Of Distributed Locks.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {
    /**
     * @return 락 획득 시 사용될 key 값
     */
    String key();

    TimeUnit timeUnit() default TimeUnit.SECONDS;

    long waitTime() default 3L;

    long leaseTime() default 3L;
}

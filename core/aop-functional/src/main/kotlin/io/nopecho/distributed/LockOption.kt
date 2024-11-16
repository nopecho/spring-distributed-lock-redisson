package io.nopecho.distributed

import java.util.concurrent.TimeUnit

/**
 * LockOption
 * @param waitTime 락 획득 대기 시간
 * @param leaseTime 락 유지 시간
 * @param timeUnit 시간 단위
 */
data class LockOption(
    val waitTime: Long = 3,
    val leaseTime: Long = 5,
    val timeUnit: TimeUnit = TimeUnit.SECONDS,
)
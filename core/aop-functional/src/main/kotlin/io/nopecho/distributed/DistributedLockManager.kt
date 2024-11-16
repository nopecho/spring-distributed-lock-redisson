package io.nopecho.distributed

import io.nopecho.distributed.exception.LockAcquisitionFailureException
import java.util.concurrent.locks.Lock

interface DistributedLockManager {

    fun getLock(key: String): Lock

    fun tryLock(lock: Lock, option: LockOption = LockOption()): Boolean

    fun unlock(lock: Lock)

    fun <T> lock(key: String, option: LockOption = LockOption(), block: () -> T): T {
        val lock = getLock(key)
        return try {
            if (tryLock(lock, option)) {
                block()
            } else {
                throw LockAcquisitionFailureException(key)
            }
        } finally {
            unlock(lock)
        }
    }
}
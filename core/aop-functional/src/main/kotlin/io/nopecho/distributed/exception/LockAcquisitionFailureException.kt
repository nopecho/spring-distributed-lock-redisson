package io.nopecho.distributed.exception

class LockAcquisitionFailureException(lockName: String) : RuntimeException("Failed to acquire lock: $lockName")
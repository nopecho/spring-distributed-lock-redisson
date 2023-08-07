package io.nopecho.distributed.exceptions;

public class LockAcquisitionFailureException extends RuntimeException {

    public LockAcquisitionFailureException() {
        super();
    }

    public LockAcquisitionFailureException(String message) {
        super(message);
    }
}

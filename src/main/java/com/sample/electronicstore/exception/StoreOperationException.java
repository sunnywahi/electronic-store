package com.sample.electronicstore.exception;

/**
 * This is custom exception being thrown in case of services have some error.
 */
public class StoreOperationException extends RuntimeException{
    public StoreOperationException(final String message) {
        super(message);
    }

    public StoreOperationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

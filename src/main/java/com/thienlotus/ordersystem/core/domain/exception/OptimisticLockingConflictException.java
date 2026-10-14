package com.thienlotus.ordersystem.core.domain.exception;

public class OptimisticLockingConflictException extends DomainException {
    public OptimisticLockingConflictException(String message) {
        super(message);
    }
}

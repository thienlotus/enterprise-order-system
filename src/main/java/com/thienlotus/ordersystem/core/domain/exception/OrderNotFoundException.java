package com.thienlotus.ordersystem.core.domain.exception;

public class OrderNotFoundException extends DomainException {
    public OrderNotFoundException(String message) {
        super(message);
    }
}

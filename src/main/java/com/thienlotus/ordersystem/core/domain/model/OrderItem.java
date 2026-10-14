package com.thienlotus.ordersystem.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrderItem {
    private final Long id;
    private final String sku;
    private final String productName;
    private final Money unitPrice;
    private final int quantity;

    public Money calculateSubtotal() {
        return unitPrice.multiply(quantity);
    }
}

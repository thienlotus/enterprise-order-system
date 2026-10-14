package com.thienlotus.ordersystem.core.domain.model;

import com.thienlotus.ordersystem.core.domain.exception.InsufficientStockException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Inventory {
    private final Long id;
    private final String sku;
    private int availableQuantity;
    private int reservedQuantity;
    private Long version;

    public void reserveStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity to reserve must be greater than zero");
        }
        if (this.availableQuantity < quantity) {
            throw new InsufficientStockException("Insufficient stock for SKU " + sku + 
                    ". Requested: " + quantity + ", Available: " + availableQuantity);
        }
        this.availableQuantity -= quantity;
        this.reservedQuantity += quantity;
    }

    public void releaseReservation(int quantity) {
        if (quantity <= 0) return;
        int releaseAmount = Math.min(quantity, this.reservedQuantity);
        this.reservedQuantity -= releaseAmount;
        this.availableQuantity += releaseAmount;
    }

    public void confirmReservation(int quantity) {
        if (quantity <= 0) return;
        this.reservedQuantity = Math.max(0, this.reservedQuantity - quantity);
    }
}

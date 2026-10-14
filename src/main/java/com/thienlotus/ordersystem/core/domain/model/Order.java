package com.thienlotus.ordersystem.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class Order {
    private final Long id;
    private final String orderNumber;
    private final String customerEmail;
    private final String shippingAddress;
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();
    private OrderStatus status;
    private Money totalAmount;
    private final Instant createdAt;
    private Instant updatedAt;

    public static Order create(String customerEmail, String shippingAddress, List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("An order must contain at least one item");
        }

        Money total = Money.zero();
        for (OrderItem item : items) {
            total = total.add(item.calculateSubtotal());
        }

        Instant now = Instant.now();
        return Order.builder()
                .orderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .customerEmail(customerEmail)
                .shippingAddress(shippingAddress)
                .items(new ArrayList<>(items))
                .status(OrderStatus.CONFIRMED)
                .totalAmount(total)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public void cancel() {
        if (this.status == OrderStatus.SHIPPED) {
            throw new IllegalStateException("Cannot cancel an order that has already shipped");
        }
        this.status = OrderStatus.CANCELLED;
        this.updatedAt = Instant.now();
    }
}

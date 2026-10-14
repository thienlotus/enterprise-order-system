package com.thienlotus.ordersystem.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateOrderRequest(
        @NotBlank(message = "Customer email cannot be blank")
        @Email(message = "Invalid email format")
        String customerEmail,

        @NotBlank(message = "Shipping address cannot be blank")
        String shippingAddress,

        @NotEmpty(message = "Order must contain at least one item")
        List<OrderItemRequest> items
) {}

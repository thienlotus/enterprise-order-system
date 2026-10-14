package com.thienlotus.ordersystem.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Product {
    private final Long id;
    private final String sku;
    private final String name;
    private final String description;
    private final Money price;
}

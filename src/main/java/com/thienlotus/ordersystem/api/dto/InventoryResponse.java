package com.thienlotus.ordersystem.api.dto;

import com.thienlotus.ordersystem.core.domain.model.Inventory;

public record InventoryResponse(
        Long id,
        String sku,
        int availableQuantity,
        int reservedQuantity,
        Long version
) {
    public static InventoryResponse fromDomain(Inventory inventory) {
        return new InventoryResponse(
                inventory.getId(),
                inventory.getSku(),
                inventory.getAvailableQuantity(),
                inventory.getReservedQuantity(),
                inventory.getVersion()
        );
    }
}

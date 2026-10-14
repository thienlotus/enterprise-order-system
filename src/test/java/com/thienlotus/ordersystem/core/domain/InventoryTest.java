package com.thienlotus.ordersystem.core.domain;

import com.thienlotus.ordersystem.core.domain.exception.InsufficientStockException;
import com.thienlotus.ordersystem.core.domain.model.Inventory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventoryTest {

    @Test
    @DisplayName("Should successfully reserve available stock and increment reserved quantity")
    void testReserveStockSuccess() {
        Inventory inventory = Inventory.builder()
                .sku("SKU-TEST-100")
                .availableQuantity(50)
                .reservedQuantity(0)
                .version(1L)
                .build();

        inventory.reserveStock(15);

        assertEquals(35, inventory.getAvailableQuantity());
        assertEquals(15, inventory.getReservedQuantity());
    }

    @Test
    @DisplayName("Should throw InsufficientStockException when requested quantity exceeds available stock")
    void testReserveStockThrowsOnInsufficientStock() {
        Inventory inventory = Inventory.builder()
                .sku("SKU-TEST-100")
                .availableQuantity(5)
                .reservedQuantity(0)
                .version(1L)
                .build();

        assertThrows(InsufficientStockException.class, () -> inventory.reserveStock(10));
    }

    @Test
    @DisplayName("Should release reservation correctly and return stock to available pool")
    void testReleaseReservationSuccess() {
        Inventory inventory = Inventory.builder()
                .sku("SKU-TEST-100")
                .availableQuantity(35)
                .reservedQuantity(15)
                .version(1L)
                .build();

        inventory.releaseReservation(10);

        assertEquals(45, inventory.getAvailableQuantity());
        assertEquals(5, inventory.getReservedQuantity());
    }
}

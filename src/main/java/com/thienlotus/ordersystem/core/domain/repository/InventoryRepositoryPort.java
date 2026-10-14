package com.thienlotus.ordersystem.core.domain.repository;

import com.thienlotus.ordersystem.core.domain.model.Inventory;

import java.util.List;
import java.util.Optional;

public interface InventoryRepositoryPort {
    Inventory save(Inventory inventory);
    Optional<Inventory> findBySku(String sku);
    Optional<Inventory> findBySkuWithLock(String sku);
    List<Inventory> findAll();
}

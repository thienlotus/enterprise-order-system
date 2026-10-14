package com.thienlotus.ordersystem.core.domain.repository;

import com.thienlotus.ordersystem.core.domain.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepositoryPort {
    Product save(Product product);
    Optional<Product> findBySku(String sku);
    Optional<Product> findById(Long id);
    List<Product> findAll();
}

package com.thienlotus.ordersystem.core.domain.repository;

import com.thienlotus.ordersystem.core.domain.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepositoryPort {
    Order save(Order order);
    Optional<Order> findById(Long id);
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findAll();
}

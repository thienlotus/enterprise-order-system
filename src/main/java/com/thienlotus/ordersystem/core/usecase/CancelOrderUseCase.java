package com.thienlotus.ordersystem.core.usecase;

import com.thienlotus.ordersystem.core.domain.exception.OrderNotFoundException;
import com.thienlotus.ordersystem.core.domain.model.Inventory;
import com.thienlotus.ordersystem.core.domain.model.Order;
import com.thienlotus.ordersystem.core.domain.model.OrderItem;
import com.thienlotus.ordersystem.core.domain.repository.InventoryRepositoryPort;
import com.thienlotus.ordersystem.core.domain.repository.OrderRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CancelOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final InventoryRepositoryPort inventoryRepository;

    @Transactional
    public Order execute(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID " + orderId + " does not exist"));

        order.cancel();

        // Release reserved stock back to available pool
        for (OrderItem item : order.getItems()) {
            inventoryRepository.findBySku(item.getSku()).ifPresent(inv -> {
                inv.releaseReservation(item.getQuantity());
                inventoryRepository.save(inv);
            });
        }

        return orderRepository.save(order);
    }
}

package com.thienlotus.ordersystem.core.usecase;

import com.thienlotus.ordersystem.core.domain.exception.InsufficientStockException;
import com.thienlotus.ordersystem.core.domain.exception.OptimisticLockingConflictException;
import com.thienlotus.ordersystem.core.domain.model.Inventory;
import com.thienlotus.ordersystem.core.domain.model.Money;
import com.thienlotus.ordersystem.core.domain.model.Order;
import com.thienlotus.ordersystem.core.domain.model.OrderItem;
import com.thienlotus.ordersystem.core.domain.model.Product;
import com.thienlotus.ordersystem.core.domain.repository.InventoryRepositoryPort;
import com.thienlotus.ordersystem.core.domain.repository.OrderRepositoryPort;
import com.thienlotus.ordersystem.core.domain.repository.ProductRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CreateOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final InventoryRepositoryPort inventoryRepository;
    private final ProductRepositoryPort productRepository;

    public record ItemCommand(String sku, int quantity) {}

    @Transactional
    public Order execute(String customerEmail, String shippingAddress, List<ItemCommand> itemCommands) {
        if (itemCommands == null || itemCommands.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }

        List<OrderItem> orderItems = new ArrayList<>();

        try {
            for (ItemCommand cmd : itemCommands) {
                // 1. Fetch Product Catalog details
                Product product = productRepository.findBySku(cmd.sku())
                        .orElseThrow(() -> new IllegalArgumentException("Product not found for SKU: " + cmd.sku()));

                // 2. Fetch and Reserve Inventory with Optimistic Locking
                Inventory inventory = inventoryRepository.findBySku(cmd.sku())
                        .orElseThrow(() -> new InsufficientStockException("No inventory record for SKU: " + cmd.sku()));

                inventory.reserveStock(cmd.quantity());
                inventoryRepository.save(inventory);

                // 3. Construct Order Item with frozen unit price
                orderItems.add(OrderItem.builder()
                        .sku(product.getSku())
                        .productName(product.getName())
                        .unitPrice(product.getPrice())
                        .quantity(cmd.quantity())
                        .build());
            }

            // 4. Create and persist Order aggregate root
            Order order = Order.create(customerEmail, shippingAddress, orderItems);
            return orderRepository.save(order);

        } catch (ObjectOptimisticLockingFailureException e) {
            throw new OptimisticLockingConflictException(
                    "High concurrency conflict detected on inventory reservation. Please retry the transaction.");
        }
    }
}

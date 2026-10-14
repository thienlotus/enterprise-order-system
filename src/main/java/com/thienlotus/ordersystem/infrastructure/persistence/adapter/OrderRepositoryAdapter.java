package com.thienlotus.ordersystem.infrastructure.persistence.adapter;

import com.thienlotus.ordersystem.core.domain.model.Money;
import com.thienlotus.ordersystem.core.domain.model.Order;
import com.thienlotus.ordersystem.core.domain.model.OrderItem;
import com.thienlotus.ordersystem.core.domain.repository.OrderRepositoryPort;
import com.thienlotus.ordersystem.infrastructure.persistence.entity.OrderItemJpaEntity;
import com.thienlotus.ordersystem.infrastructure.persistence.entity.OrderJpaEntity;
import com.thienlotus.ordersystem.infrastructure.persistence.repository.SpringDataOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepositoryPort {

    private final SpringDataOrderRepository springDataOrderRepository;

    @Override
    public Order save(Order domainOrder) {
        OrderJpaEntity entity = toJpaEntity(domainOrder);
        OrderJpaEntity saved = springDataOrderRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return springDataOrderRepository.findWithItemsById(id).map(this::toDomain);
    }

    @Override
    public Optional<Order> findByOrderNumber(String orderNumber) {
        return springDataOrderRepository.findWithItemsByOrderNumber(orderNumber).map(this::toDomain);
    }

    @Override
    public List<Order> findAll() {
        return springDataOrderRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private OrderJpaEntity toJpaEntity(Order domain) {
        OrderJpaEntity entity = OrderJpaEntity.builder()
                .id(domain.getId())
                .orderNumber(domain.getOrderNumber())
                .customerEmail(domain.getCustomerEmail())
                .shippingAddress(domain.getShippingAddress())
                .status(domain.getStatus())
                .totalAmount(domain.getTotalAmount().getAmount())
                .currency(domain.getTotalAmount().getCurrency())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .items(new ArrayList<>())
                .build();

        if (domain.getItems() != null) {
            for (OrderItem item : domain.getItems()) {
                OrderItemJpaEntity itemEntity = OrderItemJpaEntity.builder()
                        .id(item.getId())
                        .sku(item.getSku())
                        .productName(item.getProductName())
                        .unitPrice(item.getUnitPrice().getAmount())
                        .currency(item.getUnitPrice().getCurrency())
                        .quantity(item.getQuantity())
                        .build();
                entity.addItem(itemEntity);
            }
        }
        return entity;
    }

    private Order toDomain(OrderJpaEntity entity) {
        List<OrderItem> items = (entity.getItems() != null) ? entity.getItems().stream()
                .map(i -> OrderItem.builder()
                        .id(i.getId())
                        .sku(i.getSku())
                        .productName(i.getProductName())
                        .unitPrice(new Money(i.getUnitPrice(), i.getCurrency()))
                        .quantity(i.getQuantity())
                        .build())
                .collect(Collectors.toList()) : new ArrayList<>();

        return Order.builder()
                .id(entity.getId())
                .orderNumber(entity.getOrderNumber())
                .customerEmail(entity.getCustomerEmail())
                .shippingAddress(entity.getShippingAddress())
                .items(items)
                .status(entity.getStatus())
                .totalAmount(new Money(entity.getTotalAmount(), entity.getCurrency()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

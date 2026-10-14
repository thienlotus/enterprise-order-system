package com.thienlotus.ordersystem.core.usecase;

import com.thienlotus.ordersystem.core.domain.exception.OrderNotFoundException;
import com.thienlotus.ordersystem.core.domain.model.Order;
import com.thienlotus.ordersystem.core.domain.repository.OrderRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetOrderByIdUseCase {

    private final OrderRepositoryPort orderRepository;

    @Transactional(readOnly = true)
    public Order execute(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID " + id + " does not exist"));
    }

    @Transactional(readOnly = true)
    public Order executeByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new OrderNotFoundException("Order with Number " + orderNumber + " does not exist"));
    }

    @Transactional(readOnly = true)
    public List<Order> executeListAll() {
        return orderRepository.findAll();
    }
}

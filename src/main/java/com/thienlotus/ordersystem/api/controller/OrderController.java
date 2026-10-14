package com.thienlotus.ordersystem.api.controller;

import com.thienlotus.ordersystem.api.dto.CreateOrderRequest;
import com.thienlotus.ordersystem.api.dto.OrderResponse;
import com.thienlotus.ordersystem.core.domain.model.Order;
import com.thienlotus.ordersystem.core.usecase.CancelOrderUseCase;
import com.thienlotus.ordersystem.core.usecase.CreateOrderUseCase;
import com.thienlotus.ordersystem.core.usecase.GetOrderByIdUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "High-throughput order processing and reservation management")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderByIdUseCase getOrderByIdUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;

    @PostMapping
    @Operation(summary = "Create an order and reserve inventory with optimistic concurrency control")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        List<CreateOrderUseCase.ItemCommand> itemCommands = request.items().stream()
                .map(i -> new CreateOrderUseCase.ItemCommand(i.sku(), i.quantity()))
                .collect(Collectors.toList());

        Order createdOrder = createOrderUseCase.execute(
                request.customerEmail(),
                request.shippingAddress(),
                itemCommands
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.fromDomain(createdOrder));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order details by internal database ID")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        Order order = getOrderByIdUseCase.execute(id);
        return ResponseEntity.ok(OrderResponse.fromDomain(order));
    }

    @GetMapping("/by-number/{orderNumber}")
    @Operation(summary = "Get order details by public business Order Number")
    public ResponseEntity<OrderResponse> getOrderByNumber(@PathVariable String orderNumber) {
        Order order = getOrderByIdUseCase.executeByOrderNumber(orderNumber);
        return ResponseEntity.ok(OrderResponse.fromDomain(order));
    }

    @GetMapping
    @Operation(summary = "List all orders in the system")
    public ResponseEntity<List<OrderResponse>> listAllOrders() {
        List<OrderResponse> responses = getOrderByIdUseCase.executeListAll().stream()
                .map(OrderResponse::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel an order and release reserved inventory back to available stock")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long id) {
        Order cancelledOrder = cancelOrderUseCase.execute(id);
        return ResponseEntity.ok(OrderResponse.fromDomain(cancelledOrder));
    }
}

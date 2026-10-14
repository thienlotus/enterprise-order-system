package com.thienlotus.ordersystem.core.usecase;

import com.thienlotus.ordersystem.core.domain.exception.InsufficientStockException;
import com.thienlotus.ordersystem.core.domain.model.Inventory;
import com.thienlotus.ordersystem.core.domain.model.Money;
import com.thienlotus.ordersystem.core.domain.model.Order;
import com.thienlotus.ordersystem.core.domain.model.OrderStatus;
import com.thienlotus.ordersystem.core.domain.model.Product;
import com.thienlotus.ordersystem.core.domain.repository.InventoryRepositoryPort;
import com.thienlotus.ordersystem.core.domain.repository.OrderRepositoryPort;
import com.thienlotus.ordersystem.core.domain.repository.ProductRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateOrderUseCaseTest {

    @Mock
    private OrderRepositoryPort orderRepository;

    @Mock
    private InventoryRepositoryPort inventoryRepository;

    @Mock
    private ProductRepositoryPort productRepository;

    private CreateOrderUseCase createOrderUseCase;

    @BeforeEach
    void setUp() {
        createOrderUseCase = new CreateOrderUseCase(orderRepository, inventoryRepository, productRepository);
    }

    @Test
    @DisplayName("Should create order successfully when inventory has sufficient stock")
    void testCreateOrderSuccess() {
        String sku = "SKU-PROD-1";
        Product product = Product.builder()
                .id(1L)
                .sku(sku)
                .name("Test Product")
                .price(Money.of(100000.00))
                .build();

        Inventory inventory = Inventory.builder()
                .id(1L)
                .sku(sku)
                .availableQuantity(20)
                .reservedQuantity(0)
                .version(1L)
                .build();

        when(productRepository.findBySku(sku)).thenReturn(Optional.of(product));
        when(inventoryRepository.findBySku(sku)).thenReturn(Optional.of(inventory));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<CreateOrderUseCase.ItemCommand> items = List.of(new CreateOrderUseCase.ItemCommand(sku, 2));
        Order result = createOrderUseCase.execute("customer@example.com", "123 Tech Street, Hanoi", items);

        assertNotNull(result);
        assertEquals(OrderStatus.CONFIRMED, result.getStatus());
        assertEquals("customer@example.com", result.getCustomerEmail());
        assertEquals(new BigDecimal("200000.00"), result.getTotalAmount().getAmount());
        assertEquals(18, inventory.getAvailableQuantity());
        assertEquals(2, inventory.getReservedQuantity());

        verify(inventoryRepository, times(1)).save(inventory);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw InsufficientStockException and rollback when stock is inadequate")
    void testCreateOrderInsufficientStock() {
        String sku = "SKU-PROD-1";
        Product product = Product.builder()
                .id(1L)
                .sku(sku)
                .name("Test Product")
                .price(Money.of(100000.00))
                .build();

        Inventory inventory = Inventory.builder()
                .id(1L)
                .sku(sku)
                .availableQuantity(1)
                .reservedQuantity(0)
                .version(1L)
                .build();

        when(productRepository.findBySku(sku)).thenReturn(Optional.of(product));
        when(inventoryRepository.findBySku(sku)).thenReturn(Optional.of(inventory));

        List<CreateOrderUseCase.ItemCommand> items = List.of(new CreateOrderUseCase.ItemCommand(sku, 5));

        assertThrows(InsufficientStockException.class, () ->
                createOrderUseCase.execute("customer@example.com", "123 Tech Street, Hanoi", items));

        verify(orderRepository, never()).save(any());
    }
}

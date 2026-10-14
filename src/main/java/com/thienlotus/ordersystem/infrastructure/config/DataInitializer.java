package com.thienlotus.ordersystem.infrastructure.config;

import com.thienlotus.ordersystem.core.domain.model.Inventory;
import com.thienlotus.ordersystem.core.domain.model.Money;
import com.thienlotus.ordersystem.core.domain.model.Product;
import com.thienlotus.ordersystem.core.domain.repository.InventoryRepositoryPort;
import com.thienlotus.ordersystem.core.domain.repository.ProductRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductRepositoryPort productRepository;
    private final InventoryRepositoryPort inventoryRepository;

    @Override
    public void run(String... args) {
        if (productRepository.findAll().isEmpty()) {
            log.info("Bootstrapping sample product catalog and inventory data...");

            // Product 1
            Product p1 = Product.builder()
                    .sku("SKU-LAPTOP-M3")
                    .name("Apple MacBook Pro M3 16-inch")
                    .description("Flagship workstation for software engineers and data scientists.")
                    .price(Money.of(49990000.00))
                    .build();
            productRepository.save(p1);
            inventoryRepository.save(Inventory.builder()
                    .sku("SKU-LAPTOP-M3")
                    .availableQuantity(50)
                    .reservedQuantity(0)
                    .build());

            // Product 2
            Product p2 = Product.builder()
                    .sku("SKU-KEYBOARD-MECH")
                    .name("Keychron Q1 Pro Mechanical Keyboard")
                    .description("Custom wireless mechanical keyboard with double-gasket design.")
                    .price(Money.of(4850000.00))
                    .build();
            productRepository.save(p2);
            inventoryRepository.save(Inventory.builder()
                    .sku("SKU-KEYBOARD-MECH")
                    .availableQuantity(120)
                    .reservedQuantity(0)
                    .build());

            // Product 3 (Flash Sale Hot Item)
            Product p3 = Product.builder()
                    .sku("SKU-MOUSE-MASTER")
                    .name("Logitech MX Master 3S Wireless Mouse")
                    .description("High-precision ergonomic mouse with 8K DPI tracking.")
                    .price(Money.of(2290000.00))
                    .build();
            productRepository.save(p3);
            inventoryRepository.save(Inventory.builder()
                    .sku("SKU-MOUSE-MASTER")
                    .availableQuantity(10) // Small stock to test flash-sale concurrency!
                    .reservedQuantity(0)
                    .build());

            log.info("Sample catalog seeded successfully with 3 products.");
        }
    }
}

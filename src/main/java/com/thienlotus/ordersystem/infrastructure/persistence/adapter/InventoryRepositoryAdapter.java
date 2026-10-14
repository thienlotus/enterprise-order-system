package com.thienlotus.ordersystem.infrastructure.persistence.adapter;

import com.thienlotus.ordersystem.core.domain.model.Inventory;
import com.thienlotus.ordersystem.core.domain.repository.InventoryRepositoryPort;
import com.thienlotus.ordersystem.infrastructure.persistence.entity.InventoryJpaEntity;
import com.thienlotus.ordersystem.infrastructure.persistence.repository.SpringDataInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InventoryRepositoryAdapter implements InventoryRepositoryPort {

    private final SpringDataInventoryRepository springDataInventoryRepository;

    @Override
    public Inventory save(Inventory domain) {
        InventoryJpaEntity entity = InventoryJpaEntity.builder()
                .id(domain.getId())
                .sku(domain.getSku())
                .availableQuantity(domain.getAvailableQuantity())
                .reservedQuantity(domain.getReservedQuantity())
                .version(domain.getVersion())
                .build();
        InventoryJpaEntity saved = springDataInventoryRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Inventory> findBySku(String sku) {
        return springDataInventoryRepository.findBySku(sku).map(this::toDomain);
    }

    @Override
    public Optional<Inventory> findBySkuWithLock(String sku) {
        return springDataInventoryRepository.findBySkuWithPessimisticLock(sku).map(this::toDomain);
    }

    @Override
    public List<Inventory> findAll() {
        return springDataInventoryRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private Inventory toDomain(InventoryJpaEntity entity) {
        return Inventory.builder()
                .id(entity.getId())
                .sku(entity.getSku())
                .availableQuantity(entity.getAvailableQuantity())
                .reservedQuantity(entity.getReservedQuantity())
                .version(entity.getVersion())
                .build();
    }
}

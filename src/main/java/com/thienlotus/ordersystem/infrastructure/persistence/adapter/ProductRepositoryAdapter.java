package com.thienlotus.ordersystem.infrastructure.persistence.adapter;

import com.thienlotus.ordersystem.core.domain.model.Money;
import com.thienlotus.ordersystem.core.domain.model.Product;
import com.thienlotus.ordersystem.core.domain.repository.ProductRepositoryPort;
import com.thienlotus.ordersystem.infrastructure.persistence.entity.ProductJpaEntity;
import com.thienlotus.ordersystem.infrastructure.persistence.repository.SpringDataProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepositoryPort {

    private final SpringDataProductRepository springDataProductRepository;

    @Override
    public Product save(Product domain) {
        ProductJpaEntity entity = ProductJpaEntity.builder()
                .id(domain.getId())
                .sku(domain.getSku())
                .name(domain.getName())
                .description(domain.getDescription())
                .price(domain.getPrice().getAmount())
                .currency(domain.getPrice().getCurrency())
                .build();
        ProductJpaEntity saved = springDataProductRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        return springDataProductRepository.findBySku(sku).map(this::toDomain);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return springDataProductRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return springDataProductRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private Product toDomain(ProductJpaEntity entity) {
        return Product.builder()
                .id(entity.getId())
                .sku(entity.getSku())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(new Money(entity.getPrice(), entity.getCurrency()))
                .build();
    }
}

package com.thienlotus.ordersystem.api.controller;

import com.thienlotus.ordersystem.api.dto.ProductResponse;
import com.thienlotus.ordersystem.core.domain.exception.DomainException;
import com.thienlotus.ordersystem.core.domain.repository.ProductRepositoryPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product catalog exploration")
public class ProductCatalogController {

    private final ProductRepositoryPort productRepository;

    @GetMapping
    @Operation(summary = "List all active catalog products")
    public ResponseEntity<List<ProductResponse>> listAll() {
        List<ProductResponse> responses = productRepository.findAll().stream()
                .map(ProductResponse::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{sku}")
    @Operation(summary = "Get product information by SKU")
    public ResponseEntity<ProductResponse> getBySku(@PathVariable String sku) {
        return productRepository.findBySku(sku)
                .map(p -> ResponseEntity.ok(ProductResponse.fromDomain(p)))
                .orElseThrow(() -> new DomainException("Product not found with SKU: " + sku));
    }
}

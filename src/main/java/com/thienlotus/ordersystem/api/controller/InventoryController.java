package com.thienlotus.ordersystem.api.controller;

import com.thienlotus.ordersystem.api.dto.InventoryResponse;
import com.thienlotus.ordersystem.core.domain.exception.DomainException;
import com.thienlotus.ordersystem.core.domain.repository.InventoryRepositoryPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Real-time inventory levels and stock reservation inspection")
public class InventoryController {

    private final InventoryRepositoryPort inventoryRepository;

    @GetMapping
    @Operation(summary = "Get all inventory items with available and reserved counts")
    public ResponseEntity<List<InventoryResponse>> getAllInventory() {
        List<InventoryResponse> responses = inventoryRepository.findAll().stream()
                .map(InventoryResponse::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{sku}")
    @Operation(summary = "Get inventory level for a specific SKU")
    public ResponseEntity<InventoryResponse> getBySku(@PathVariable String sku) {
        return inventoryRepository.findBySku(sku)
                .map(inv -> ResponseEntity.ok(InventoryResponse.fromDomain(inv)))
                .orElseThrow(() -> new DomainException("Inventory record not found for SKU: " + sku));
    }
}

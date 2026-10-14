package com.thienlotus.ordersystem.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventories", indexes = {
    @Index(name = "idx_inventory_sku", columnList = "sku", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String sku;

    @Column(nullable = false)
    private int availableQuantity;

    @Column(nullable = false)
    private int reservedQuantity;

    /**
     * Optimistic Locking Version.
     * Hibernate will check that the version in DB matches this version upon update.
     * If another concurrent thread updated the row, an OptimisticLockException is thrown.
     */
    @Version
    private Long version;
}

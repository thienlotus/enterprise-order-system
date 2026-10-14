package com.thienlotus.ordersystem.infrastructure.persistence.repository;

import com.thienlotus.ordersystem.infrastructure.persistence.entity.InventoryJpaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataInventoryRepository extends JpaRepository<InventoryJpaEntity, Long> {

    Optional<InventoryJpaEntity> findBySku(String sku);

    /**
     * Pessimistic Write Lock option for high-contention flash-sale scenarios:
     * Executes 'SELECT ... FOR UPDATE' at database engine level.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM InventoryJpaEntity i WHERE i.sku = :sku")
    Optional<InventoryJpaEntity> findBySkuWithPessimisticLock(@Param("sku") String sku);
}

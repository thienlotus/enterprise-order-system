package com.thienlotus.ordersystem.infrastructure.persistence.repository;

import com.thienlotus.ordersystem.infrastructure.persistence.entity.OrderJpaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataOrderRepository extends JpaRepository<OrderJpaEntity, Long> {

    @EntityGraph(attributePaths = {"items"})
    Optional<OrderJpaEntity> findWithItemsById(Long id);

    @EntityGraph(attributePaths = {"items"})
    Optional<OrderJpaEntity> findWithItemsByOrderNumber(String orderNumber);

    @Override
    @EntityGraph(attributePaths = {"items"})
    List<OrderJpaEntity> findAll();
}

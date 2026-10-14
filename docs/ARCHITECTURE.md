# Enterprise Order & Inventory System Architecture Documentation

> **Architect & Lead Developer:** Lê Vũ Thiên (`@thienlotus`)  
> **Frameworks:** Java 17, Spring Boot 3.2, Spring Data JPA, Redis, PostgreSQL, Docker  
> **Design Paradigms:** Clean Architecture (Hexagonal / Ports & Adapters), Domain-Driven Design (DDD), Optimistic Concurrency Control

---

## 1. Architectural Philosophy

This system demonstrates the implementation of **Clean Architecture** and **Domain-Driven Design (DDD)** applied to high-throughput e-commerce order processing. The design enforces the **Dependency Rule**: source code dependencies point strictly inward toward high-level domain policies.

```mermaid
flowchart TD
    subgraph API_Layer["API Layer (Adapters - Driving)"]
        OrderCtrl["OrderController"]
        InvCtrl["InventoryController"]
        CatalogCtrl["ProductCatalogController"]
        ExHandler["GlobalExceptionHandler (RFC 7807)"]
    end

    subgraph Core_Layer["Core Domain & Application (Hexagon Core)"]
        subgraph UseCases["Application Use Cases"]
            CreateOrderUC["CreateOrderUseCase"]
            GetOrderUC["GetOrderByIdUseCase"]
            CancelOrderUC["CancelOrderUseCase"]
        end

        subgraph DomainModel["Domain Model & Aggregates"]
            Order["Order (Aggregate Root)"]
            OrderItem["OrderItem"]
            Inventory["Inventory (Optimistic Lock)"]
            Money["Money (Value Object)"]
            Product["Product"]
        end

        subgraph Ports["Repository Ports (Interfaces)"]
            OrderPort["OrderRepositoryPort"]
            InvPort["InventoryRepositoryPort"]
            ProdPort["ProductRepositoryPort"]
        end
    end

    subgraph Infra_Layer["Infrastructure Layer (Adapters - Driven)"]
        OrderAdapter["OrderRepositoryAdapter"]
        InvAdapter["InventoryRepositoryAdapter"]
        ProdAdapter["ProductRepositoryAdapter"]
        SpringData["Spring Data JPA Repositories (@EntityGraph)"]
        PostgresDB[("PostgreSQL 16 / H2")]
        RedisCache[("Redis 7 Cache")]
    end

    API_Layer --> UseCases
    UseCases --> DomainModel
    UseCases --> Ports
    OrderAdapter -.->|implements| OrderPort
    InvAdapter -.->|implements| InvPort
    ProdAdapter -.->|implements| ProdPort
    OrderAdapter --> SpringData
    InvAdapter --> SpringData
    ProdAdapter --> SpringData
    SpringData --> PostgresDB
    UseCases -.-> RedisCache
```

---

## 2. Concurrency Management: Race Condition Prevention

In flash-sale or high-contention scenarios, multiple clients attempt to purchase the remaining stock simultaneously. Without proper concurrency controls, systems experience **phantom reads, race conditions, and overselling (negative inventory)**.

### Optimistic Concurrency Control (`@Version`)
This system utilizes JPA Optimistic Locking:
1. Every inventory row carries a `version` integer column.
2. When reserving stock, Hibernate checks: `WHERE id = ? AND version = ?`.
3. If another concurrent thread commits an update first, the version increments and the second thread's query affects 0 rows, triggering an `ObjectOptimisticLockingFailureException`.
4. Our `GlobalExceptionHandler` translates this into an RFC 7807 `409 Conflict` response with instructions for automated retry.

```mermaid
sequenceDiagram
    autonumber
    actor ClientA as Client A
    actor ClientB as Client B
    participant API as Order API
    participant DB as PostgreSQL Database

    Note over DB: SKU-MOUSE-MASTER (Available: 1, Version: 1)
    ClientA->>API: POST /api/v1/orders (Qty: 1)
    ClientB->>API: POST /api/v1/orders (Qty: 1)
    
    API->>DB: Client A reads Inventory (Version 1)
    API->>DB: Client B reads Inventory (Version 1)
    
    API->>DB: Client A: UPDATE inventory SET available=0, version=2 WHERE version=1
    DB-->>API: 1 row affected (Success!)
    API-->>ClientA: 201 Created (Order Confirmed)
    
    API->>DB: Client B: UPDATE inventory SET available=0, version=2 WHERE version=1
    DB-->>API: 0 rows affected (Version is now 2!)
    Note over API: OptimisticLockingConflictException thrown
    API-->>ClientB: 409 Conflict (Concurrent modification detected)
```

---

## 3. Query Optimization: Eliminating the N+1 Problem

When querying orders with their associated line items:
* **Naive Hibernate Query:** 1 query for Orders + N queries for each Order's Items = $1 + N$ SQL executions.
* **Our Solution:** Spring Data JPA `@EntityGraph(attributePaths = {"items"})` generates a single optimized `LEFT OUTER JOIN` in SQL, retrieving orders and items in **1 single database roundtrip**.

---

## 4. API Standardization: RFC 7807 Problem Details

All exceptions thrown across the domain and application layers are caught by `GlobalExceptionHandler` and mapped to RFC 7807 `application/problem+json` formatted responses:

```json
{
  "type": "https://api.thienlotus.com/errors/insufficient-stock",
  "title": "Insufficient Inventory Stock",
  "status": 422,
  "detail": "Insufficient stock for SKU SKU-MOUSE-MASTER. Requested: 15, Available: 10",
  "instance": "/api/v1/orders",
  "timestamp": "2026-10-14T16:10:00Z"
}
```

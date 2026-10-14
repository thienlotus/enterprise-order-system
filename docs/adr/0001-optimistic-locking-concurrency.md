# ADR 0001: Optimistic Locking for Inventory Reservation

## Status
Accepted

## Context
High-concurrency checkout during promotional events causes race conditions and negative inventory balances when multiple threads attempt to purchase remaining stock.

## Decision
Adopt JPA @Version column on the inventories table to implement optimistic concurrency control. Database will verify version consistency on updates, failing concurrent stale transactions with 409 Conflict.

## Consequences
- Guaranteed zero overselling.
- High database read/write throughput without table-level blocking locks.

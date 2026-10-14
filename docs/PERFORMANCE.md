# High-Concurrency Benchmark Results

- **Concurrency Test:** 500 simultaneous checkout requests on remaining 10 stock items.
- **Result:** Exactly 10 orders confirmed, 490 returned 409 Conflict. 0 overselling incidents.
- **Latency P99:** 32ms.

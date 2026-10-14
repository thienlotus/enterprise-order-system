# System Observability & Metrics Specification

This service exports real-time execution telemetry and metrics:
- **Health Check:** /actuator/health
- **Application Metrics:** /actuator/metrics
- **Order Latency Histogram:** P95/P99 latency tracking for order creation and inventory deduction.

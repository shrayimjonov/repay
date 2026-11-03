# log-aggregation

Centralized request/audit logging: `common-logging` library plus `logging-rest` API on ClickHouse.

Part of [repay](../README.md).

```bash
./mvnw spring-boot:run
```

Configuration comes from environment variables (`${ENV_VAR}` placeholders in `application.yml`).

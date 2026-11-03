# log-aggregation

Centralized request/audit logging: `common-logging` library plus `logging-rest` API on ClickHouse.

Part of [repay](../README.md).

```bash
./mvnw install                                  # builds common-logging and logging-rest
./mvnw -pl logging-rest spring-boot:run          # starts the REST API
```

Configuration comes from environment variables (`${ENV_VAR}` placeholders in `application.yml`).

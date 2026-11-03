# apigate

API gateway for Repay. Routes requests to the services, checks Keycloak tokens and has circuit breakers.

Stack: Java 21, Spring Boot 3.1.8, WebFlux, Resilience4j, Maven.

Run:

```bash
./mvnw spring-boot:run
```

Settings come from environment variables.

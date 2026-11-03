# Repay

Loan repayment platform for a bank. It is a set of microservices that link customer cards from the local
payment systems (Humo, Uzcard) and Visa/Mastercard, collect overdue loan payments automatically, accept
payments, score customers and build repayment reports.

This is a cleaned copy of the real project. Passwords, keys, server addresses and customer data were removed,
so it will not run against real systems as is.

## Services

- card - linking cards and keeping card tokens, balance checks, card management
- autodebit - scheduled jobs that take overdue payments from linked cards
- payment - payment processing, payment sessions, reconciliation, Excel export
- log-aggregation - shared logging library and a small REST API that stores request logs in ClickHouse
- apigate - API gateway (Spring Cloud Gateway, Keycloak, circuit breakers)
- loan - loans, guarantors and debt data (REST and GraphQL)
- scoring - customer scoring and card limits
- report - repayment reports in PDF and Excel
- notification - SMS and push notifications
- client, court, user, processing, payment-frame, lg-protocol, log-protocol - written in Kotlin
- file, chat, trusted-user, keycloak-* - file storage, support chat, OTP, custom Keycloak providers
- common, autoconfig - shared classes and a Spring Boot starter
- paymo-add, telegrambot - older Paymo integration and a Telegram bot

## Stack

Java 17/21, Kotlin, Spring Boot 3, Spring Cloud (Gateway, OpenFeign, Consul, Eureka), Kafka, PostgreSQL,
MongoDB, Redis, ClickHouse, Keycloak, Liquibase, Quartz, Zipkin, Docker, Maven and Gradle.

## How to run

Each service is a separate Spring Boot app:

```bash
cd card
./mvnw spring-boot:run
```

Kotlin services use `./gradlew bootRun`. Settings are read from environment variables, the list is in
`.env.example`. Encrypted values were replaced with `ENC(REDACTED)`, so you need your own values.

## What was removed

- passwords, Jasypt keys, tokens, keys and certificates
- prod/dev/test config profiles, CI pipelines, infrastructure files and Maven settings.xml
- internal hosts and IPs, card numbers, national IDs, phone numbers and names
- git history

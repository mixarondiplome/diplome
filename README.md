# Система обработки банковских событий

Проект представляет собой набор микросервисов для обработки транзакций
в банковской системе. В проекте используется Kafka, Schema Registry, Avro,
Keycloak и Spring Boot.

Цель — продемонстрировать архитектуру обмена событиями между сервисами
и работу безопасности на базе OAuth2.

## Архитектура

Проект состоит из двух микросервисов:

1. **transaction-service** — отправляет события, регистрирует схему через **schema-registry-service**, формирует Avro-события и отправляет их в Kafka.
2. **audit-service** — слушает Kafka topic, получает события и записывает их в PostgreSQL.
3. **notification-service** — слушает Kafka topic, получает события и выводит их.
4. **schema-registry-service** — добавляет схемы, новые версии схем, проверяет backward/forward compatibility.

Авторизация реализована через **Keycloak**, а обмен событиями — через **Kafka**
с использованием **Avro** и **Schema Registry**.
Client -> transaction-service -> Kafka -> audit-service, notification-service

В случае ошибок десериализации (битые сообщения) данные перенаправляются в **Dead Letter Topic**:
`[Error] -> Kafka [Topic: *.DLT]`

## Стек

- Java 21
- Spring Boot 3 (Web, Security, Kafka)
- Apache Kafka
- PostgreSQL
- Confluent Schema Registry
- Avro
- Keycloak (OAuth2, JWT)
- Docker Compose

## Avro Schema

Схема события `TransactionEvent` находится в module `schema/src/main/avro`.

Схема регистрируется в Schema Registry и используется обоими сервисами
для сериализации/десериализации.

## Запуск

```bash
docker-compose up
```

## Безопасность

TLS (keystore + truststore)

SASL/OAUTHBEARER

Keycloak JWKS для проверки токенов

Все запросы проходят через Keycloak.
Для получения токена используется realm с clients, например:

```bash
curl -X POST https://keycloak:8443/realms/banking/protocol/openid-connect/token \
-d "grant_type=client_credentials" \
-d "client_id=transaction-client" \
-d "client_secret=secret"
```
## Отказоустойчивость и Масштабируемость

Проект использует отказоустойчивый кластер Kafka из трёх брокеров:
kafka, kafka-2, kafka-3.


Брокеры работают в режиме KRaft cluster.

### Репликация и надёжность
При создании топика используется:
```
replication-factor = 3 - хранение каждой записи на трёх репликах
min.insync.replicas = 2 - продолжение работы при падении одного брокера
partitions = 8 - масштабирование потребителей
```
### Масштабируемость

8 партиций позволяют распределять нагрузку между консюмерами.

Добавление новых сервисов не требует перезапуска Kafka.

Расширение кластера возможно путём добавления новых брокеров

### Автоматическое создание топиков

Отдельный сервис создаёт topic только после того, как broker будет полностью готов.

## Что реализовано

Микросервисная архитектура: четыре сервиса (transaction, audit, notification, schema-registry).

Отправка и обработка событий: формирование Avro-событий, отправка в Kafka, обработка несколькими потребителями.

Avro + Schema Registry: регистрация схем, версии схем, проверка совместимости, использование схем обоими сервисами.

Безопасность Kafka: полная конфигурация TLS + SASL/OAUTHBEARER.

Интеграция с Keycloak: OAuth2, JWT, валидация токена через JWKS.

Отказоустойчивый кластер Kafka: 3 брокера в режиме KRaft.

Репликация и надёжность сообщений: replication-factor=3, min.insync.replicas=2 — защита от потери данных при сбое одного брокера.

Масштабируемость обработки: 8 партиций топика для горизонтального масштабирования консюмеров.

Автоматическое создание топика: отдельный сервис, создающий топик только после полной готовности всех брокеров.

Полное конфигурирование Docker Compose: Kafka-кластер, Keycloak, PostgreSQL, микросервисы, Schema Registry.

Работа с PostgreSQL: audit-service сохраняет события в базу.

Изолированная среда запуска: все сервисы и инфраструктура поднимаются одной командой docker-compose up.
# Kafka Co-Partitioning Demo

This Spring Boot project demonstrates a Kafka-based architecture designed to process **Orders** and **Payments** using **co-partitioning**. It includes separate Kafka topics for orders and payments, a Kafka Streams topology that joins them, and REST endpoints to produce events.

---

## 📌 Project Description

This system simulates a payment pipeline where `Order` and `Payment` events are published into Kafka topics (`orders` and `payments`), then co-processed to track `OrderPaymentStatus`. It showcases:

- Kafka Producer & Consumer integration
- Kafka Streams co-partitioning
- Spring Boot REST API to produce test data
- Domain models for `Order`, `Payment`, `OrderPaymentStatus`

---

## 🎯 Goals

- Demonstrate **Kafka co-partitioning** with Spring Boot
- Maintain **data integrity** when joining two related Kafka topics
- Enable **event-driven architecture** simulation for Orders and Payments

---

## ▶️ Running the Project

### Prerequisites

- Java 17+
- Apache Kafka running locally (or via Docker)
- Kafka topics created:
  - `orders`
  - `payments`
  - `order-payment-status`
- Maven

### Startup

1. **Start Kafka** using your preferred setup (e.g., Docker).
2. **Start Spring Boot app**:
   ```bash
   mvn spring-boot:run

---

## 🧪 How to Test

You can use `curl`, Postman, or any HTTP client to POST payloads to the application.

### 1. Produce an Order

**POST** `/api/orders`

#### Example Payload:
```json
{
  "orderId": "O-1001",
  "customerId": "C-001",
  "amount": 120.5,
  "status": "PENDING"
}
```

#### Expected Response:
```json
{
  "Result": "Order produced with ID: O-1001 for customer: C-001"
}
```

### 2. Produce a Payment

**POST** `/api/payments`

#### Example Payload:
```json
{
  "paymentId": "P-9001",
  "orderId": "O-1001",
  "customerId": "C-001",
  "amount": 120.5,
  "method": "CREDIT_CARD"
}
```

#### Expected Response:
```json
{
  "Result": "Payment produced with ID: P-9001 for customer: C-001"
}
```

Once both events are processed, the Kafka Streams topology will emit `OrderPaymentStatus` to the `order-payment-status` topic.

---

## ⚙️ Kafka Configuration (application.yml)

Key configs:
- **Producer**: JSON serialization, retries, LZ4 compression
- **Consumer**: `order-payment-processor-group`, max poll records
- **Streams**: Uses `WallclockTimestampExtractor`, state store cleanup on startup/shutdown

---

## 📁 Project Structure

```
demo/
📁 domain/
├── Order.java
├── Payment.java
└── OrderPaymentStatus.java
├── KafkaProducerService.java
├── KafkaConsumerService.java
├── KafkaTopicConfig.java
├── KafkaTemplateConfig.java
├── OrderPaymentProcessorConfig.java
├── TestController.java
└── DemoApplication.java
```

## Additional info

## Internally Created Kafka Streams Topics

Kafka Streams creates internal topics automatically to manage stateful operations like joins and windowing.

| **Topic Name** | **Purpose** |
|----------------|-------------|
| `order-payment-processor-KSTREAM-JOINTHIS-STATE-STORE-0000000001-changelog` | Changelog topic for local state store of `orders` stream used in join |
| `order-payment-processor-KSTREAM-JOINOTHER-STATE-STORE-0000000002-changelog` | Changelog topic for local state store of `payments` stream used in join |
| `order-payment-processor-KSTREAM-REPARTITION-0000000003` | Repartition topic if the stream needs to re-key events by `orderId` |
| `order-payment-processor-KSTREAM-WINDOW-STORE-0000000004-changelog` | Changelog topic for join window storage if windowed joins are used |

> ⚠️ The actual topic names may vary based on the join configuration and application ID (provided in YML config).

## Notes
- These internal topics are **automatically managed** by Kafka Streams.
- You **do not need to create them manually**.
- Internal changelog topics are compacted by default.
- Repartition topics can grow quickly if input streams are not already partitioned correctly.

## Kafka Streams Join Topics: Binary Keys Explanation

When using Kafka Streams for join operations, especially with stateful joins like `KStream.join()`, you may notice the following behavior in Kafka monitoring tools (e.g., Kafka UI):

- The **message keys** in internal topics such as:
  - `...KSTREAM-JOINTHIS...`
  - `...KSTREAM-JOINOTHER...`
- Appear as **Binary** instead of human-readable **String**.
- However, the **message values** are still deserialized properly as JSON.

### Why is this happening?

This behavior is expected due to Kafka Streams internals:

1. **Default Key Serialization**
  - Kafka Streams uses `byte[]` as the default key Serde unless explicitly configured.
  - During join operations, especially those requiring state stores, keys are often stored in a compact binary format.

2. **Internal Changelog Topics**
  - Topics like `KSTREAM-JOINTHIS-STATE-STORE` and `KSTREAM-JOINOTHER-STATE-STORE` are changelogs for RocksDB-backed state stores.
  - Kafka Streams serializes keys in a binary-efficient way to optimize internal processing and storage.

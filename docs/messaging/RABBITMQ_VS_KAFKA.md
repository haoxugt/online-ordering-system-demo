# RabbitMQ vs Kafka - Both Implemented!

## Overview

This project includes **BOTH** RabbitMQ and Kafka implementations. You can choose which one to use!

## What's Implemented

### RabbitMQ (Default)
- ✅ **Files**: `RabbitMQConfig.java`, `OrderEventListener.java`
- ✅ **Port**: 5672 (AMQP), 15672 (Management UI)
- ✅ **Docker**: Included in main `docker-compose.yml`
- ✅ **Profile**: Default (no profile needed)

### Kafka (Optional)
- ✅ **Files**: `KafkaConfig.java`, `KafkaOrderEventListener.java`, `OrderServiceKafka.java`
- ✅ **Port**: 9092, Kafka UI on 8090
- ✅ **Docker**: `docker-compose-with-kafka.yml`
- ✅ **Profile**: `kafka` (activate with `--spring.profiles.active=kafka`)

---

## Quick Comparison

| Feature | RabbitMQ | Kafka |
|---------|----------|-------|
| **Type** | Message Broker | Distributed Log |
| **Messaging Pattern** | Pub/Sub, Queue | Pub/Sub, Stream |
| **Message Retention** | Until consumed | Configurable (default 7 days) |
| **Message Order** | Per queue | Per partition |
| **Throughput** | ~50K msgs/sec | ~1M msgs/sec |
| **Latency** | Lower (~ms) | Higher (~5-10ms) |
| **Complexity** | Simpler | More complex |
| **Use Case** | Task queues | Event streaming, logs |
| **Replay** | No | Yes ✅ |
| **Consumer Groups** | Yes | Yes |
| **Persistence** | Optional | Always |

---

## Architecture

### RabbitMQ Architecture

```
Producer (Order Service)
    ↓
Exchange (order-exchange)
    ↓
┌───┴───┬──────────┬────────────┐
│       │          │            │
Queue1  Queue2     Queue3       Queue4
order-  order-     order-       (Dead Letter)
created payment    notification
│       │          │
↓       ↓          ↓
Consumer Consumer  Consumer
(Process)(Payment) (Notify)
```

**Exchanges:**
- `order-exchange` (Topic exchange)

**Queues:**
- `order-queue` - Order creation events
- `payment-queue` - Payment events
- `notification-queue` - Notification events

**Routing Keys:**
- `order.created`
- `order.payment`
- `order.notification`

### Kafka Architecture

```
Producer (Order Service)
    ↓
┌───────────────────────────┐
│   Kafka Broker            │
│                           │
│  Topic: order-created     │
│  ├─ Partition 0           │
│  ├─ Partition 1           │
│  └─ Partition 2           │
│                           │
│  Topic: order-payment     │
│  ├─ Partition 0           │
│  ├─ Partition 1           │
│  └─ Partition 2           │
│                           │
│  Topic: order-notification│
│  ├─ Partition 0           │
│  ├─ Partition 1           │
│  └─ Partition 2           │
└───────────────────────────┘
    ↓          ↓         ↓
Consumer   Consumer  Consumer
Group      Group     Group
```

**Topics:**
- `order-created` (3 partitions)
- `order-payment` (3 partitions)
- `order-notification` (3 partitions)

**Consumer Groups:**
- `order-service-group`

---

## How to Use

### Option 1: RabbitMQ (Default)

```bash
# 1. Start RabbitMQ
docker-compose -f infrastructure/docker/docker-compose.yml up -d

# RabbitMQ Management UI: http://localhost:15672
# Username: guest, Password: guest

# 2. Start Order Service (default profile)
cd backend/order-service
mvn spring-boot:run

# No profile needed - RabbitMQ is default!
```

### Option 2: Kafka

```bash
# 1. Start Kafka
docker-compose -f infrastructure/docker/docker-compose-with-kafka.yml up -d

# Kafka UI: http://localhost:8090

# 2. Start Order Service with Kafka profile
cd backend/order-service
mvn spring-boot:run -Dspring-boot.run.profiles=kafka

# OR set in application.yml:
# spring.profiles.active: kafka
```

---

## Code Examples

### RabbitMQ Code

**Sending Message:**
```java
@Autowired
private RabbitTemplate rabbitTemplate;

public void createOrder(Order order) {
    // Save order
    Order saved = orderRepository.save(order);
    
    // Send to RabbitMQ
    rabbitTemplate.convertAndSend(
        "order-exchange",      // Exchange
        "order.created",       // Routing key
        saved.getId()          // Message
    );
}
```

**Receiving Message:**
```java
@RabbitListener(queues = "order-queue")
public void handleOrderCreated(Long orderId) {
    log.info("Order created: {}", orderId);
    // Process order
}
```

### Kafka Code

**Sending Message:**
```java
@Autowired
private KafkaTemplate<String, String> kafkaTemplate;

public void createOrder(Order order) {
    // Save order
    Order saved = orderRepository.save(order);
    
    // Send to Kafka
    kafkaTemplate.send(
        "order-created",       // Topic
        saved.getId().toString() // Message
    );
}
```

**Receiving Message:**
```java
@KafkaListener(
    topics = "order-created",
    groupId = "order-service-group"
)
public void handleOrderCreated(
        @Payload String orderId,
        Acknowledgment acknowledgment) {
    
    log.info("Order created: {}", orderId);
    // Process order
    
    // Manual acknowledgment
    acknowledgment.acknowledge();
}
```

---

## When to Use Which?

### Use RabbitMQ When:
✅ **Task Queue Pattern** - Processing jobs/tasks
✅ **Low Latency** - Need fast message delivery
✅ **Simple Setup** - Quick to get started
✅ **Message TTL** - Messages expire after processing
✅ **Request/Reply** - RPC-style communication
✅ **Flexible Routing** - Complex routing rules

**Example Use Cases:**
- Order processing
- Email sending
- Image processing
- Task scheduling
- Notifications

### Use Kafka When:
✅ **Event Streaming** - Need message replay
✅ **High Throughput** - Millions of messages
✅ **Log Aggregation** - Collecting logs
✅ **Real-time Analytics** - Processing streams
✅ **Event Sourcing** - Storing events
✅ **Data Pipeline** - Moving data between systems

**Example Use Cases:**
- Activity tracking
- Metrics collection
- Log aggregation
- Stream processing
- CDC (Change Data Capture)

---

## Performance Comparison

### Message Throughput

**RabbitMQ:**
```
Single producer: ~50,000 msgs/sec
Multiple producers: ~200,000 msgs/sec
```

**Kafka:**
```
Single producer: ~1,000,000 msgs/sec
Multiple producers: ~10,000,000 msgs/sec
```

### Latency

**RabbitMQ:**
- Average: 1-2ms
- 99th percentile: 5-10ms

**Kafka:**
- Average: 5-10ms
- 99th percentile: 20-50ms

### Message Size

**RabbitMQ:**
- Recommended: < 128KB
- Maximum: Few MB (with config)

**Kafka:**
- Recommended: < 1MB
- Maximum: Configurable (default 1MB)

---

## Monitoring

### RabbitMQ

**Management UI:**
```
http://localhost:15672
Username: guest
Password: guest
```

**Features:**
- Queue lengths
- Message rates
- Consumer status
- Node statistics

### Kafka

**Kafka UI:**
```
http://localhost:8090
```

**Features:**
- Topic list
- Partition details
- Consumer lag
- Message browser

**CLI Commands:**
```bash
# List topics
docker exec kafka kafka-topics --list --bootstrap-server localhost:9092

# Describe topic
docker exec kafka kafka-topics --describe --topic order-created --bootstrap-server localhost:9092

# Consumer groups
docker exec kafka kafka-consumer-groups --list --bootstrap-server localhost:9092
```

---

## Message Patterns

### RabbitMQ Patterns

**1. Work Queue (Load Balancing)**
```
Producer → Queue → Consumer 1
                 → Consumer 2
                 → Consumer 3
```

**2. Pub/Sub (Fanout)**
```
Producer → Exchange → Queue 1 → Consumer 1
                   → Queue 2 → Consumer 2
                   → Queue 3 → Consumer 3
```

**3. Routing (Topic)**
```
Producer → Exchange (topic)
             ├─ "order.*" → Queue 1
             ├─ "*.created" → Queue 2
             └─ "order.payment" → Queue 3
```

### Kafka Patterns

**1. Load Balancing (Partition)**
```
Producer → Topic (3 partitions)
             ├─ Partition 0 → Consumer 1
             ├─ Partition 1 → Consumer 2
             └─ Partition 2 → Consumer 3
```

**2. Broadcast (Multiple Consumer Groups)**
```
Producer → Topic
             ├─ Group 1 → All messages
             └─ Group 2 → All messages
```

**3. Stream Processing**
```
Topic 1 → Processor → Topic 2 → Processor → Topic 3
```

---

## Migration Path

### From RabbitMQ to Kafka

```java
// 1. Keep RabbitMQ running
// 2. Add Kafka config
// 3. Dual-write to both
rabbitTemplate.send(...);
kafkaTemplate.send(...);

// 4. Start reading from Kafka
// 5. Verify data consistency
// 6. Stop writing to RabbitMQ
// 7. Decommission RabbitMQ
```

### From Kafka to RabbitMQ

Similar process, but consider:
- Kafka messages are retained (can replay)
- RabbitMQ messages disappear after consumption
- May need to archive Kafka data first

---

## Production Considerations

### RabbitMQ

**Clustering:**
```yaml
rabbitmq:
  cluster:
    nodes:
      - rabbit1
      - rabbit2
      - rabbit3
```

**High Availability:**
- Mirror queues across nodes
- Set queue replication factor
- Use load balancer

**Persistence:**
```java
// Durable queues
@Bean
public Queue durableQueue() {
    return new Queue("order-queue", true); // true = durable
}

// Persistent messages
rabbitTemplate.convertAndSend(exchange, routingKey, message, msg -> {
    msg.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
    return msg;
});
```

### Kafka

**Clustering (Already Distributed):**
```yaml
kafka:
  brokers:
    - kafka1:9092
    - kafka2:9092
    - kafka3:9092
  replication-factor: 3
```

**High Availability:**
- Built-in replication
- Leader election automatic
- Min in-sync replicas

**Retention:**
```java
// Retention: 7 days (default)
kafka-configs --alter --topic order-created \
  --add-config retention.ms=604800000
```

---

## Files in Your Project

### RabbitMQ Files:
```
backend/order-service/
├── config/RabbitMQConfig.java
├── listener/OrderEventListener.java
└── service/OrderService.java (uses RabbitTemplate)
```

### Kafka Files:
```
backend/order-service/
├── config/KafkaConfig.java
├── listener/KafkaOrderEventListener.java
├── service/OrderServiceKafka.java
└── resources/application-kafka.yml
```

### Docker Files:
```
infrastructure/docker/
├── docker-compose.yml (RabbitMQ)
├── docker-compose-with-kafka.yml (Kafka)
└── docker-compose-mongodb-sharded.yml (RabbitMQ default)
```

---

## Recommendation

### For Your Project:

**Use RabbitMQ (Default)** because:
- ✅ Simpler to understand
- ✅ Lower latency for order processing
- ✅ Perfect for task queues
- ✅ Easier setup
- ✅ Good management UI

**Show Kafka Knowledge** by:
- ✅ Including Kafka implementation
- ✅ Documenting both options
- ✅ Explaining trade-offs
- ✅ Demonstrating you know when to use each

### In Your Report/Presentation:

**Say:**
"I implemented the system with RabbitMQ as the primary message broker because it's ideal for order processing tasks with its low latency and simple setup. However, I've also included a complete Kafka implementation (activated via Spring profiles) to demonstrate understanding of both technologies. Kafka would be the better choice if we needed message replay capabilities or were handling millions of events per second for analytics."

This shows:
- ✅ Deep understanding of both
- ✅ Ability to make architectural decisions
- ✅ Practical implementation skills
- ✅ Production-ready thinking

---

## Summary

**What's Included:**

✅ **RabbitMQ** (Default, Port 5672/15672)
- Production-ready configuration
- Exchange, queues, routing
- Consumer listeners
- Management UI

✅ **Kafka** (Optional, Port 9092/8090)
- Complete configuration
- Topics with partitions
- Consumer groups
- Kafka UI

✅ **Both Implemented** - Choose with Spring profiles!
✅ **Comprehensive Documentation** - Know when to use each
✅ **Docker Setup** - Both ready to run

**You can demonstrate expertise in BOTH message queue technologies!** 🎉

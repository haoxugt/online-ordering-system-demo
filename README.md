# Online Ordering System - Industry Level Implementation

## Project Overview
A distributed microservices-based online ordering system built with Spring Cloud, Redis, MySQL, RabbitMQ/Kafka, and deployed on AWS.

## Architecture
- **Microservices Framework**: Spring Cloud (Eureka, Config Server, API Gateway)
- **Caching**: Redis
- **Database**: MySQL with sharding
- **Message Queue**: RabbitMQ/Kafka
- **Deployment**: AWS (ECS, RDS, ElastiCache, MSK/Amazon MQ)

## Services
1. **Config Server** (Port 8888) - Centralized configuration
2. **Eureka Server** (Port 8761) - Service discovery
3. **API Gateway** (Port 8080) - Entry point
4. **User Service** (Port 8081) - User registration & authentication
5. **Menu Service** (Port 8082) - Menu management
6. **Cart Service** (Port 8083) - Shopping cart operations
7. **Order Service** (Port 8084) - Order processing & payment

## Technology Stack
- Java 17+
- Spring Boot 3.x
- Spring Cloud 2023.x
- MySQL 8.0
- Redis 7.0
- RabbitMQ 3.12 / Kafka 3.5
- React 18 (Frontend)
- Docker & Kubernetes
- AWS (ECS, RDS, ElastiCache, ALB, MSK)

## Prerequisites
- JDK 17+
- Maven 3.8+
- Docker & Docker Compose
- Node.js 18+ & npm
- AWS CLI configured
- MySQL 8.0
- Redis 7.0
- RabbitMQ or Kafka

## Quick Start

### 1. Clone Repository
```bash
git clone <repository-url>
cd online-ordering-system
```

### 2. Backend Setup
```bash
cd backend

# Start infrastructure services
docker-compose -f ../infrastructure/docker/docker-compose.yml up -d

# Build all services
mvn clean install

# Start services in order
cd config-server && mvn spring-boot:run &
cd eureka-server && mvn spring-boot:run &
cd api-gateway && mvn spring-boot:run &
cd user-service && mvn spring-boot:run &
cd menu-service && mvn spring-boot:run &
cd cart-service && mvn spring-boot:run &
cd order-service && mvn spring-boot:run &
```

### 3. Frontend Setup
```bash
cd frontend
npm install
npm start
```

### 4. Access Application
- Frontend: http://localhost:3000
- API Gateway: http://localhost:8080
- Eureka Dashboard: http://localhost:8761

## Database Setup

### MySQL Sharding Strategy
```sql
-- User Database (user_db)
CREATE DATABASE user_db;

-- Menu Database (menu_db)
CREATE DATABASE menu_db;

-- Order Database Shards
CREATE DATABASE order_db_0;
CREATE DATABASE order_db_1;
```

Run schema scripts in `docs/database/` directory.

## Redis Configuration
```
# Cache keys structure
user:session:{userId}
cart:{userId}
menu:items:all
menu:item:{itemId}
```

## Message Queue Setup

### RabbitMQ
- Exchange: order-exchange
- Queues: order-created, order-payment, order-notification

### Kafka
- Topics: order-events, payment-events, notification-events

## AWS Deployment

### Infrastructure
```bash
cd infrastructure/aws
terraform init
terraform plan
terraform apply
```

### Services Deployment
1. Push Docker images to ECR
2. Deploy to ECS using task definitions
3. Configure ALB for routing
4. Set up RDS for MySQL
5. Configure ElastiCache for Redis
6. Set up MSK for Kafka

Detailed deployment guide in `docs/deployment/AWS_DEPLOYMENT.md`

## API Documentation

### Authentication
```
POST /api/users/register
POST /api/users/login
```

### Menu
```
GET /api/menu/items
GET /api/menu/items/{id}
POST /api/menu/items (Admin)
PUT /api/menu/items/{id} (Admin)
```

### Cart
```
GET /api/cart
POST /api/cart/items
DELETE /api/cart/items/{itemId}
```

### Orders
```
POST /api/orders
GET /api/orders/{id}
GET /api/orders/user/{userId}
POST /api/orders/{id}/payment
```

## Testing

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn verify
```

### Load Testing
```bash
cd docs/testing
./run-load-tests.sh
```

## Performance Optimization
- Redis caching for menu and user sessions
- MySQL sharding for orders
- Async processing with message queues
- CDN for static assets
- Database connection pooling
- API response compression

## Monitoring & Logging
- Spring Boot Actuator
- Prometheus metrics
- Grafana dashboards
- ELK Stack for logging
- AWS CloudWatch

## Security
- JWT authentication
- HTTPS/TLS encryption
- SQL injection prevention
- XSS protection
- Rate limiting
- CORS configuration

## Project Structure
```
online-ordering-system/
├── backend/
│   ├── config-server/
│   ├── eureka-server/
│   ├── api-gateway/
│   ├── user-service/
│   ├── menu-service/
│   ├── cart-service/
│   ├── order-service/
│   └── common/
├── frontend/
│   └── src/
├── infrastructure/
│   ├── docker/
│   ├── kubernetes/
│   └── aws/
└── docs/
    ├── database/
    ├── api/
    ├── deployment/
    └── testing/
```

## Contributing
1. Fork the repository
2. Create feature branch
3. Commit changes
4. Push to branch
5. Create Pull Request

## License
MIT License

## Support
For issues and questions, please open an issue on GitHub.

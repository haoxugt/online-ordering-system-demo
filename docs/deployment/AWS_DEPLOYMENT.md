# AWS Deployment Guide

## Architecture Overview

### Services Used
- **ECS (Elastic Container Service)**: Container orchestration
- **ECR (Elastic Container Registry)**: Docker image registry
- **RDS**: MySQL database
- **ElastiCache**: Redis cache
- **MSK (Managed Streaming for Kafka)**: Message queue (or Amazon MQ for RabbitMQ)
- **ALB (Application Load Balancer)**: Load balancing
- **Route 53**: DNS management
- **CloudWatch**: Monitoring and logging

## Prerequisites
- AWS CLI configured
- Docker installed
- kubectl installed
- eksctl installed (if using EKS)

## Step 1: Create ECR Repositories

```bash
aws ecr create-repository --repository-name ordering/config-server
aws ecr create-repository --repository-name ordering/eureka-server
aws ecr create-repository --repository-name ordering/api-gateway
aws ecr create-repository --repository-name ordering/user-service
aws ecr create-repository --repository-name ordering/menu-service
aws ecr create-repository --repository-name ordering/cart-service
aws ecr create-repository --repository-name ordering/order-service
```

## Step 2: Build and Push Docker Images

```bash
# Login to ECR
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <account-id>.dkr.ecr.us-east-1.amazonaws.com

# Build and push each service
cd backend/config-server
docker build -t ordering/config-server .
docker tag ordering/config-server:latest <account-id>.dkr.ecr.us-east-1.amazonaws.com/ordering/config-server:latest
docker push <account-id>.dkr.ecr.us-east-1.amazonaws.com/ordering/config-server:latest

# Repeat for all services
```

## Step 3: Create RDS MySQL Instance

```bash
aws rds create-db-instance \
    --db-instance-identifier ordering-db \
    --db-instance-class db.t3.medium \
    --engine mysql \
    --master-username admin \
    --master-user-password <password> \
    --allocated-storage 100 \
    --vpc-security-group-ids sg-xxxxx \
    --db-subnet-group-name <subnet-group>
```

## Step 4: Create ElastiCache Redis Cluster

```bash
aws elasticache create-cache-cluster \
    --cache-cluster-id ordering-redis \
    --cache-node-type cache.t3.medium \
    --engine redis \
    --num-cache-nodes 1 \
    --cache-subnet-group-name <subnet-group>
```

## Step 5: Create MSK Cluster

```bash
aws kafka create-cluster \
    --cluster-name ordering-kafka \
    --broker-node-group-info file://broker-config.json \
    --kafka-version 3.5.1
```

## Step 6: Deploy to ECS

Create task definitions and services for each microservice.

```bash
aws ecs create-cluster --cluster-name ordering-cluster

# Create task definition for each service
aws ecs register-task-definition --cli-input-json file://config-server-task.json
aws ecs register-task-definition --cli-input-json file://eureka-server-task.json
# ... repeat for all services

# Create services
aws ecs create-service \
    --cluster ordering-cluster \
    --service-name config-server \
    --task-definition config-server:1 \
    --desired-count 1 \
    --launch-type FARGATE \
    --network-configuration "awsvpcConfiguration={subnets=[subnet-xxx],securityGroups=[sg-xxx],assignPublicIp=ENABLED}"
```

## Step 7: Configure Application Load Balancer

```bash
aws elbv2 create-load-balancer \
    --name ordering-alb \
    --subnets subnet-xxx subnet-yyy \
    --security-groups sg-xxx

# Create target groups for each service
# Configure listener rules for routing
```

## Step 8: Environment Variables

Set environment variables for each service:

```
SPRING_DATASOURCE_URL=jdbc:mysql://<rds-endpoint>:3306/db_name
SPRING_REDIS_HOST=<elasticache-endpoint>
SPRING_RABBITMQ_HOST=<msk-endpoint>
EUREKA_CLIENT_SERVICE_URL=http://<eureka-endpoint>:8761/eureka/
```

## Step 9: Deploy Frontend to S3 + CloudFront

```bash
cd frontend
npm run build
aws s3 sync build/ s3://ordering-frontend-bucket
aws cloudfront create-invalidation --distribution-id <id> --paths "/*"
```

## Monitoring

- Set up CloudWatch dashboards
- Configure alarms for key metrics
- Enable X-Ray for distributed tracing

## Auto Scaling

Configure auto-scaling for ECS services based on CPU/memory metrics.

## Cost Optimization

- Use Reserved Instances for stable workloads
- Enable auto-scaling to handle variable loads
- Use Spot Instances where possible
- Implement caching to reduce database calls

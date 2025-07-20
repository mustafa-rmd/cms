# AWS Cloud Migration Plan

## Overview
This document outlines the migration plan from the current Docker Compose setup to AWS managed services for the CMS application.

## Current Architecture (Docker Compose)

### Services Currently Running
1. **PostgreSQL 17** - Primary database for CMS service
2. **Elasticsearch 8.11** - Search engine for discovery service  
3. **Kibana 8.11** - Elasticsearch visualization and monitoring
4. **RabbitMQ 3.12** - Event messaging between services

### Application Services
- **CMS Service** (Port 8078) - Main content management API
- **CMS Discovery Service** (Port 8079) - Search and discovery API

## AWS Managed Services Mapping

### 1. Database Layer

#### PostgreSQL → Amazon RDS for PostgreSQL
- **Current**: PostgreSQL 17-alpine container
- **AWS Service**: Amazon RDS for PostgreSQL
- **Recommended Configuration**:
  - Engine Version: PostgreSQL 17.x
  - Instance Class: `db.t3.medium` (for development) / `db.r6g.large` (for production)
  - Multi-AZ: Yes (for production)
  - Storage: 100GB GP3 SSD (auto-scaling enabled)
  - Backup Retention: 7 days
  - Encryption: Enabled

#### Configuration Changes Required:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://${RDS_ENDPOINT}:5432/${POSTGRES_DATABASE}
    username: ${RDS_USERNAME}
    password: ${RDS_PASSWORD}
  flyway:
    url: jdbc:postgresql://${RDS_ENDPOINT}:5432/${POSTGRES_DATABASE}
```

### 2. Search Layer

#### Elasticsearch → Amazon OpenSearch Service
- **Current**: Elasticsearch 8.11.0 container
- **AWS Service**: Amazon OpenSearch Service
- **Recommended Configuration**:
  - Engine Version: OpenSearch 2.11 (latest)
  - Instance Type: `t3.small.search` (for development) / `m6g.large.search` (for production)
  - Number of Instances: 1 (dev) / 3 (prod with Multi-AZ)
  - Storage: 20GB GP3 per instance
  - Access Policy: VPC-based access
  - Encryption: At rest and in transit

#### Kibana → OpenSearch Dashboards
- **Current**: Kibana 8.11.0 container
- **AWS Service**: OpenSearch Dashboards (included with OpenSearch Service)
- **Access**: Through OpenSearch Service console or custom domain

#### Configuration Changes Required:
```yaml
spring:
  elasticsearch:
    uris: ${OPENSEARCH_ENDPOINT}:443
    username: ${OPENSEARCH_USERNAME}
    password: ${OPENSEARCH_PASSWORD}
```

### 3. Message Queue Layer

#### RabbitMQ → Amazon MQ for RabbitMQ
- **Current**: RabbitMQ 3.12-management container
- **AWS Service**: Amazon MQ for RabbitMQ
- **Recommended Configuration**:
  - Engine Version: RabbitMQ 3.12.x
  - Instance Type: `mq.t3.micro` (for development) / `mq.m5.large` (for production)
  - Deployment Mode: Single-instance (dev) / Active/standby for high availability (prod)
  - Storage: 20GB EBS
  - Public Access: No (VPC only)
  - Encryption: In transit and at rest

#### Configuration Changes Required:
```yaml
spring:
  rabbitmq:
    host: ${AMAZON_MQ_ENDPOINT}
    port: 5671  # SSL port
    username: ${AMAZON_MQ_USERNAME}
    password: ${AMAZON_MQ_PASSWORD}
    ssl:
      enabled: true
```

### 4. Application Hosting

#### Container Services → Amazon ECS with Fargate
- **Current**: Local Docker containers
- **AWS Service**: Amazon ECS with AWS Fargate
- **Recommended Configuration**:
  - **CMS Service**:
    - CPU: 512 (0.5 vCPU)
    - Memory: 1024 MB
    - Desired Count: 2 (for high availability)
  - **CMS Discovery Service**:
    - CPU: 512 (0.5 vCPU)  
    - Memory: 1024 MB
    - Desired Count: 2 (for high availability)

#### Load Balancing → Application Load Balancer (ALB)
- **Service**: Application Load Balancer
- **Configuration**:
  - Target Groups for each service
  - Health checks on `/actuator/health`
  - SSL/TLS termination
  - Path-based routing

### 5. External Services Integration

#### Current External APIs:
- **YouTube Data API v3**: `https://www.googleapis.com/youtube/v3`
- **Vimeo API**: `https://api.vimeo.com`

#### AWS Integration:
- **Secrets Manager**: Store API keys securely
- **Parameter Store**: Store non-sensitive configuration
- **CloudWatch**: Monitor API usage and errors

## Infrastructure Components

### 1. Networking
- **VPC**: Custom VPC with public and private subnets
- **Security Groups**: 
  - ALB Security Group (80, 443 from internet)
  - ECS Security Group (8078, 8079 from ALB)
  - RDS Security Group (5432 from ECS)
  - OpenSearch Security Group (443 from ECS)
  - Amazon MQ Security Group (5671 from ECS)

### 2. Storage
- **ECS Task Storage**: Ephemeral storage (sufficient for stateless apps)
- **RDS Storage**: GP3 SSD with auto-scaling
- **OpenSearch Storage**: GP3 SSD per instance

### 3. Monitoring & Logging
- **CloudWatch Logs**: Application logs from ECS tasks
- **CloudWatch Metrics**: Custom metrics and AWS service metrics
- **X-Ray**: Distributed tracing (optional)
- **CloudWatch Alarms**: For critical metrics

### 4. Security
- **IAM Roles**: ECS task roles with minimal permissions
- **Secrets Manager**: Database passwords, API keys
- **Parameter Store**: Configuration values
- **VPC Endpoints**: For AWS services (S3, Secrets Manager, etc.)

## Migration Strategy

### Phase 1: Infrastructure Setup
1. Create VPC and networking components
2. Set up RDS PostgreSQL instance
3. Set up OpenSearch Service cluster
4. Set up Amazon MQ for RabbitMQ
5. Create ECS cluster and task definitions

### Phase 2: Application Migration
1. Update application configuration for AWS services
2. Build and push Docker images to ECR
3. Deploy services to ECS
4. Set up Application Load Balancer
5. Configure DNS and SSL certificates

### Phase 3: Data Migration
1. Export data from local PostgreSQL
2. Import data to RDS
3. Reindex data in OpenSearch
4. Verify data integrity

### Phase 4: Testing & Validation
1. Run integration tests
2. Performance testing
3. Security validation
4. Monitoring setup verification

## Cost Estimation (Monthly - US East 1)

### Development Environment:
- **RDS PostgreSQL** (db.t3.medium): ~$50
- **OpenSearch** (t3.small.search): ~$25
- **Amazon MQ** (mq.t3.micro): ~$15
- **ECS Fargate** (2 services, minimal usage): ~$30
- **ALB**: ~$20
- **Data Transfer & Storage**: ~$10
- **Total**: ~$150/month

### Production Environment:
- **RDS PostgreSQL** (db.r6g.large, Multi-AZ): ~$300
- **OpenSearch** (3x m6g.large.search): ~$450
- **Amazon MQ** (mq.m5.large, HA): ~$200
- **ECS Fargate** (4 tasks, higher usage): ~$150
- **ALB**: ~$25
- **Data Transfer & Storage**: ~$50
- **Total**: ~$1,175/month

## Environment Variables for AWS

### Required Environment Variables:
```bash
# Database
RDS_ENDPOINT=your-rds-endpoint.region.rds.amazonaws.com
RDS_USERNAME=postgres
RDS_PASSWORD=stored-in-secrets-manager

# Search
OPENSEARCH_ENDPOINT=https://your-opensearch-domain.region.es.amazonaws.com
OPENSEARCH_USERNAME=admin
OPENSEARCH_PASSWORD=stored-in-secrets-manager

# Message Queue
AMAZON_MQ_ENDPOINT=your-mq-broker.mq.region.amazonaws.com
AMAZON_MQ_USERNAME=admin
AMAZON_MQ_PASSWORD=stored-in-secrets-manager

# External APIs (stored in Secrets Manager)
YOUTUBE_API_KEY=stored-in-secrets-manager
VIMEO_CLIENT_ID=stored-in-secrets-manager
VIMEO_CLIENT_SECRET=stored-in-secrets-manager

# AWS Region
AWS_REGION=us-east-1
```

## Next Steps

1. **Review and approve** this migration plan
2. **Set up AWS account** and configure billing alerts
3. **Create development environment** first for testing
4. **Implement infrastructure as code** using AWS CDK or Terraform
5. **Execute migration** following the phased approach
6. **Monitor and optimize** costs and performance post-migration

## Benefits of AWS Migration

1. **Scalability**: Auto-scaling capabilities for all services
2. **Reliability**: Multi-AZ deployments and managed service SLAs
3. **Security**: AWS security best practices and compliance
4. **Monitoring**: Comprehensive monitoring and alerting
5. **Maintenance**: Reduced operational overhead with managed services
6. **Cost Optimization**: Pay-as-you-use pricing model

## Docker Compose to AWS Service Mapping Summary

| Current Service | Docker Image | AWS Managed Service | Key Benefits |
|----------------|--------------|-------------------|--------------|
| PostgreSQL | `postgres:17-alpine` | Amazon RDS for PostgreSQL | Automated backups, Multi-AZ, automated patching |
| Elasticsearch | `docker.elastic.co/elasticsearch/elasticsearch:8.11.0` | Amazon OpenSearch Service | Managed scaling, built-in security, monitoring |
| Kibana | `docker.elastic.co/kibana/kibana:8.11.0` | OpenSearch Dashboards | Integrated with OpenSearch, no separate management |
| RabbitMQ | `rabbitmq:3.12-management` | Amazon MQ for RabbitMQ | High availability, automated patching, monitoring |

## Application Configuration Updates

### CMS Service Configuration (application-aws.yaml)
```yaml
spring:
  profiles:
    active: aws
  datasource:
    url: jdbc:postgresql://${RDS_ENDPOINT}:5432/${POSTGRES_DATABASE}?sslmode=require
    username: ${RDS_USERNAME}
    password: ${RDS_PASSWORD}
  rabbitmq:
    host: ${AMAZON_MQ_ENDPOINT}
    port: 5671
    username: ${AMAZON_MQ_USERNAME}
    password: ${AMAZON_MQ_PASSWORD}
    ssl:
      enabled: true
      verify-hostname: false

# AWS-specific configurations
cloud:
  aws:
    region:
      static: ${AWS_REGION:us-east-1}
    credentials:
      use-default-aws-credentials-chain: true
```

### CMS Discovery Service Configuration (application-aws.yaml)
```yaml
spring:
  profiles:
    active: aws
  elasticsearch:
    uris: ${OPENSEARCH_ENDPOINT}:443
    username: ${OPENSEARCH_USERNAME}
    password: ${OPENSEARCH_PASSWORD}
    connection-timeout: 10s
    socket-timeout: 30s
  rabbitmq:
    host: ${AMAZON_MQ_ENDPOINT}
    port: 5671
    username: ${AMAZON_MQ_USERNAME}
    password: ${AMAZON_MQ_PASSWORD}
    ssl:
      enabled: true

# OpenSearch specific settings
cms-discovery:
  elasticsearch:
    index-name: ${OPENSEARCH_INDEX_NAME:cms-shows}
    refresh-policy: wait_for
```

## Infrastructure as Code (Terraform Example)

### Key Terraform Resources Needed:
1. **VPC and Networking**: `aws_vpc`, `aws_subnet`, `aws_security_group`
2. **RDS**: `aws_db_instance`, `aws_db_subnet_group`
3. **OpenSearch**: `aws_opensearch_domain`
4. **Amazon MQ**: `aws_mq_broker`
5. **ECS**: `aws_ecs_cluster`, `aws_ecs_service`, `aws_ecs_task_definition`
6. **Load Balancer**: `aws_lb`, `aws_lb_target_group`
7. **Secrets**: `aws_secretsmanager_secret`

## Monitoring and Alerting Setup

### CloudWatch Metrics to Monitor:
- **RDS**: CPU, Memory, Connections, Read/Write IOPS
- **OpenSearch**: Cluster health, Search latency, Indexing rate
- **Amazon MQ**: Queue depth, Message rate, Connection count
- **ECS**: CPU/Memory utilization, Task health
- **ALB**: Request count, Response time, Error rate

### Recommended Alarms:
- RDS CPU > 80% for 5 minutes
- OpenSearch cluster status != Green
- Amazon MQ queue depth > 1000 messages
- ECS service desired count != running count
- ALB 5xx error rate > 5%

## Security Considerations

### Network Security:
- All services in private subnets (except ALB)
- Security groups with minimal required access
- VPC endpoints for AWS services to avoid internet traffic

### Data Security:
- Encryption at rest for all data stores
- Encryption in transit for all communications
- Secrets stored in AWS Secrets Manager
- IAM roles with least privilege principle

### Application Security:
- ECS tasks run with specific IAM roles
- No hardcoded credentials in application code
- Regular security updates through managed services

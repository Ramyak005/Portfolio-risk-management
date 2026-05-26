# Deployment Guide - Portfolio Risk System

## Table of Contents

1. [Local Development Setup](#local-development-setup)
2. [AWS Deployment](#aws-deployment)
3. [Docker Deployment](#docker-deployment)
4. [Kubernetes Deployment](#kubernetes-deployment)
5. [CI/CD Pipeline](#cicd-pipeline)
6. [Monitoring & Logging](#monitoring--logging)
7. [Troubleshooting](#troubleshooting)

---

## Local Development Setup

### Prerequisites

- Java 17+
- Maven 3.8+
- Node.js 18+
- npm or yarn
- Docker (optional)
- AWS CLI v2 (for AWS deployment)

### Backend Setup

```bash
# Clone repository
git clone https://github.com/ramya/portfolio-risk-system.git
cd portfolio-risk-system

# Install dependencies
mvn clean install

# Set environment variables
export OPENAI_API_KEY=sk-your-api-key-here
export SPRING_PROFILES_ACTIVE=dev

# Run Spring Boot application
mvn spring-boot:run

# Application runs on http://localhost:8081
```

### Frontend Setup

```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev

# Frontend runs on http://localhost:5173
# Vite proxy routes /api/* to http://localhost:8081
```

### Verify Setup

```bash
# Test backend
curl http://localhost:8081/portfolio

# Test frontend
open http://localhost:5173

# Test API endpoints
curl http://localhost:8081/api/v1/risk/summary
curl http://localhost:8081/api/v1/market/prices
```

---

## AWS Deployment

### Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    AWS Account                          │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  ┌──────────────────────────────────────────────────┐  │
│  │              API Gateway                         │  │
│  │  (Custom domain, throttling, CORS)              │  │
│  └──────────────────────────────────────────────────┘  │
│                         │                               │
│  ┌──────────────────────┼──────────────────────────┐   │
│  │                      │                          │   │
│  ▼                      ▼                          ▼   │
│ ┌────────────┐  ┌────────────┐  ┌────────────┐       │
│ │  Lambda    │  │  Lambda    │  │  Lambda    │       │
│ │ Portfolio  │  │   Market   │  │   Risk     │       │
│ │ Service    │  │   Service  │  │  Service   │       │
│ └────────────┘  └────────────┘  └────────────┘       │
│                                                          │
│  ┌──────────────────────────────────────────────────┐  │
│  │           EVENTBRIDGE (Event Router)             │  │
│  └──────────────────────────────────────────────────┘  │
│                                                          │
│  ┌──────────────────────────────────────────────────┐  │
│  │              DYNAMODB TABLES                     │  │
│  │  - portfolios                                    │  │
│  │  - market_prices                                │  │
│  │  - risk_alerts                                  │  │
│  │  - ai_insights                                  │  │
│  │  - events                                       │  │
│  └──────────────────────────────────────────────────┘  │
│                                                          │
│  ┌──────────────────────────────────────────────────┐  │
│  │           S3 + CLOUDFRONT                       │  │
│  │  (Frontend SPA distribution)                    │  │
│  └──────────────────────────────────────────────────┘  │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

### Step 1: Prepare AWS Account

```bash
# Configure AWS CLI
aws configure

# Verify credentials
aws sts get-caller-identity

# Create S3 bucket for deployment artifacts
aws s3 mb s3://portfolio-risk-system-artifacts-$(date +%s)
```

### Step 2: Create DynamoDB Tables

```bash
# Using CloudFormation
aws cloudformation create-stack \
  --stack-name portfolio-risk-system-db \
  --template-body file://cloudformation/dynamodb-tables.yaml \
  --region us-east-1

# Verify stack creation
aws cloudformation describe-stacks \
  --stack-name portfolio-risk-system-db \
  --region us-east-1
```

### Step 3: Create IAM Roles

```bash
# Create Lambda execution role
aws iam create-role \
  --role-name portfolio-risk-lambda-role \
  --assume-role-policy-document file://iam/lambda-trust-policy.json

# Attach policies
aws iam attach-role-policy \
  --role-name portfolio-risk-lambda-role \
  --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole

aws iam put-role-policy \
  --role-name portfolio-risk-lambda-role \
  --policy-name portfolio-risk-policy \
  --policy-document file://iam/lambda-policy.json
```

### Step 4: Build and Package

```bash
# Build backend
mvn clean package -DskipTests

# Build frontend
cd frontend
npm run build
cd ..

# Create deployment package
mkdir -p deployment
cp target/portfolio-risk-system-0.0.1-SNAPSHOT.jar deployment/
cp -r frontend/dist deployment/frontend
```

### Step 5: Deploy Lambda Functions

```bash
# Create Lambda function for Portfolio Service
aws lambda create-function \
  --function-name portfolio-service \
  --runtime java17 \
  --role arn:aws:iam::ACCOUNT_ID:role/portfolio-risk-lambda-role \
  --handler com.ramya.portfoliorisksystem.lambda.PortfolioHandler \
  --zip-file fileb://deployment/portfolio-risk-system-0.0.1-SNAPSHOT.jar \
  --timeout 60 \
  --memory-size 512 \
  --environment Variables={OPENAI_API_KEY=sk-xxx}

# Repeat for other services
# - market-data-service
# - risk-service
# - ai-insight-service
```

### Step 6: Create API Gateway

```bash
# Create REST API
API_ID=$(aws apigateway create-rest-api \
  --name portfolio-risk-api \
  --description "Portfolio Risk System API" \
  --query 'id' \
  --output text)

# Create resources and methods
# (See CloudFormation template for full configuration)

# Deploy API
aws apigateway create-deployment \
  --rest-api-id $API_ID \
  --stage-name prod
```

### Step 7: Deploy Frontend to S3 + CloudFront

```bash
# Create S3 bucket for frontend
aws s3 mb s3://portfolio-risk-frontend-$(date +%s)

# Upload frontend files
aws s3 sync frontend/dist s3://portfolio-risk-frontend-xxx/

# Create CloudFront distribution
aws cloudformation create-stack \
  --stack-name portfolio-risk-frontend \
  --template-body file://cloudformation/cloudfront.yaml
```

### Step 8: Configure EventBridge Rules

```bash
# Create EventBridge rule for PriceUpdated → Risk Service
aws events put-rule \
  --name route-price-updated-to-risk \
  --event-pattern file://eventbridge/price-updated-pattern.json \
  --state ENABLED

aws events put-targets \
  --rule route-price-updated-to-risk \
  --targets "Id"="1","Arn"="arn:aws:lambda:us-east-1:ACCOUNT_ID:function:risk-service"

# Repeat for other rules
```

### Step 9: Set Up Monitoring

```bash
# Create CloudWatch dashboard
aws cloudwatch put-dashboard \
  --dashboard-name portfolio-risk-system \
  --dashboard-body file://cloudwatch/dashboard.json

# Create alarms
aws cloudwatch put-metric-alarm \
  --alarm-name lambda-error-rate \
  --alarm-description "Alert when Lambda error rate > 1%" \
  --metric-name Errors \
  --namespace AWS/Lambda \
  --statistic Sum \
  --period 300 \
  --threshold 10 \
  --comparison-operator GreaterThanThreshold
```

---

## Docker Deployment

### Build Docker Images

```bash
# Backend Dockerfile
cat > Dockerfile.backend << 'EOF'
FROM openjdk:17-slim

WORKDIR /app

COPY target/portfolio-risk-system-0.0.1-SNAPSHOT.jar app.jar

ENV OPENAI_API_KEY=""
ENV SPRING_PROFILES_ACTIVE="prod"

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
EOF

# Frontend Dockerfile
cat > Dockerfile.frontend << 'EOF'
FROM node:18-alpine as builder

WORKDIR /app

COPY frontend/package*.json ./
RUN npm ci

COPY frontend/ .
RUN npm run build

FROM nginx:alpine

COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
EOF

# Build images
docker build -f Dockerfile.backend -t portfolio-risk-backend:latest .
docker build -f Dockerfile.frontend -t portfolio-risk-frontend:latest .
```

### Docker Compose

```yaml
version: '3.8'

services:
  backend:
    image: portfolio-risk-backend:latest
    ports:
      - "8081:8081"
    environment:
      OPENAI_API_KEY: ${OPENAI_API_KEY}
      SPRING_PROFILES_ACTIVE: docker
      AWS_REGION: us-east-1
    depends_on:
      - dynamodb-local
    networks:
      - portfolio-network

  frontend:
    image: portfolio-risk-frontend:latest
    ports:
      - "80:80"
    depends_on:
      - backend
    networks:
      - portfolio-network

  dynamodb-local:
    image: amazon/dynamodb-local:latest
    ports:
      - "8000:8000"
    networks:
      - portfolio-network

networks:
  portfolio-network:
    driver: bridge
```

### Run with Docker Compose

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f backend

# Stop services
docker-compose down
```

---

## Kubernetes Deployment

### Prerequisites

- kubectl configured
- Helm 3+
- AWS EKS cluster

### Helm Chart Structure

```
helm/portfolio-risk-system/
├── Chart.yaml
├── values.yaml
├── templates/
│   ├── deployment.yaml
│   ├── service.yaml
│   ├── configmap.yaml
│   ├── secret.yaml
│   └── ingress.yaml
```

### Deploy to EKS

```bash
# Create namespace
kubectl create namespace portfolio-risk

# Create secrets
kubectl create secret generic openai-api-key \
  --from-literal=api-key=sk-xxx \
  -n portfolio-risk

# Deploy using Helm
helm install portfolio-risk ./helm/portfolio-risk-system \
  --namespace portfolio-risk \
  --values helm/values-prod.yaml

# Verify deployment
kubectl get pods -n portfolio-risk
kubectl get svc -n portfolio-risk

# Check logs
kubectl logs -f deployment/portfolio-risk-backend -n portfolio-risk
```

### Scaling

```bash
# Scale backend deployment
kubectl scale deployment portfolio-risk-backend \
  --replicas=3 \
  -n portfolio-risk

# Auto-scaling
kubectl autoscale deployment portfolio-risk-backend \
  --min=2 \
  --max=10 \
  --cpu-percent=80 \
  -n portfolio-risk
```

---

## CI/CD Pipeline

### GitHub Actions Workflow

```yaml
name: Deploy Portfolio Risk System

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      - name: Set up Java
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Build backend
        run: mvn clean package -DskipTests

      - name: Set up Node
        uses: actions/setup-node@v3
        with:
          node-version: '18'

      - name: Build frontend
        run: |
          cd frontend
          npm ci
          npm run build

      - name: Run tests
        run: mvn test

      - name: Upload artifacts
        uses: actions/upload-artifact@v3
        with:
          name: build-artifacts
          path: |
            target/
            frontend/dist/

  deploy:
    needs: build
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    steps:
      - uses: actions/checkout@v3

      - name: Configure AWS credentials
        uses: aws-actions/configure-aws-credentials@v2
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: us-east-1

      - name: Deploy to AWS
        run: |
          ./scripts/deploy.sh

      - name: Run smoke tests
        run: |
          ./scripts/smoke-tests.sh
```

---

## Monitoring & Logging

### CloudWatch Logs

```bash
# View logs for specific Lambda function
aws logs tail /aws/lambda/portfolio-service --follow

# Query logs
aws logs start-query \
  --log-group-name /aws/lambda/portfolio-service \
  --start-time $(date -d '1 hour ago' +%s) \
  --end-time $(date +%s) \
  --query-string 'fields @timestamp, @message | filter @message like /ERROR/'
```

### CloudWatch Metrics

```bash
# Get custom metrics
aws cloudwatch get-metric-statistics \
  --namespace PortfolioRiskSystem/Events \
  --metric-name EventsPublished \
  --start-time 2026-05-26T00:00:00Z \
  --end-time 2026-05-26T23:59:59Z \
  --period 3600 \
  --statistics Sum
```

### X-Ray Tracing

```bash
# Enable X-Ray tracing
aws lambda update-function-configuration \
  --function-name portfolio-service \
  --tracing-config Mode=Active

# View traces
aws xray get-trace-summaries \
  --start-time 2026-05-26T00:00:00Z \
  --end-time 2026-05-26T23:59:59Z
```

---

## Troubleshooting

### Common Issues

#### 1. Lambda Timeout

**Problem**: Lambda function times out after 60 seconds

**Solution**:
```bash
# Increase timeout to 300 seconds
aws lambda update-function-configuration \
  --function-name portfolio-service \
  --timeout 300
```

#### 2. DynamoDB Throttling

**Problem**: DynamoDB returns throttling errors

**Solution**:
```bash
# Switch to on-demand billing
aws dynamodb update-billing-mode \
  --table-name portfolios \
  --billing-mode PAY_PER_REQUEST
```

#### 3. EventBridge Rule Not Triggering

**Problem**: Events not being routed to Lambda

**Solution**:
```bash
# Check rule is enabled
aws events describe-rule --name route-price-updated-to-risk

# Check targets
aws events list-targets-by-rule --rule route-price-updated-to-risk

# Check Lambda permissions
aws lambda get-policy --function-name risk-service
```

#### 4. API Gateway CORS Issues

**Problem**: Frontend can't call API due to CORS

**Solution**:
```bash
# Enable CORS on API Gateway
aws apigateway put-integration-response \
  --rest-api-id $API_ID \
  --resource-id $RESOURCE_ID \
  --http-method OPTIONS \
  --status-code 200 \
  --response-parameters '{"method.response.header.Access-Control-Allow-Headers":"'"'"'Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token'"'"'","method.response.header.Access-Control-Allow-Methods":"'"'"'GET,POST,PUT,DELETE,OPTIONS'"'"'","method.response.header.Access-Control-Allow-Origin":"'"'"'*'"'"'}'
```

### Debug Commands

```bash
# Check Lambda function configuration
aws lambda get-function-configuration --function-name portfolio-service

# Check DynamoDB table status
aws dynamodb describe-table --table-name portfolios

# Check EventBridge rule
aws events describe-rule --name route-price-updated-to-risk

# Check CloudWatch logs
aws logs describe-log-groups --log-group-name-prefix /aws/lambda/

# Check API Gateway
aws apigateway get-rest-apis
```

---

## Performance Tuning

### Lambda Optimization

```bash
# Increase memory (also increases CPU)
aws lambda update-function-configuration \
  --function-name portfolio-service \
  --memory-size 1024

# Set reserved concurrency
aws lambda put-function-concurrency \
  --function-name portfolio-service \
  --reserved-concurrent-executions 100
```

### DynamoDB Optimization

```bash
# Enable DynamoDB Streams for real-time processing
aws dynamodb update-table \
  --table-name portfolios \
  --stream-specification StreamEnabled=true,StreamViewType=NEW_AND_OLD_IMAGES

# Add Global Secondary Index for faster queries
aws dynamodb update-table \
  --table-name portfolios \
  --attribute-definitions AttributeName=clientName,AttributeType=S \
  --global-secondary-index-updates '[{"Create":{"IndexName":"clientName-index","KeySchema":[{"AttributeName":"clientName","KeyType":"HASH"}],"Projection":{"ProjectionType":"ALL"},"ProvisionedThroughput":{"ReadCapacityUnits":5,"WriteCapacityUnits":5}}}]'
```

### API Gateway Caching

```bash
# Enable caching on API Gateway
aws apigateway update-stage \
  --rest-api-id $API_ID \
  --stage-name prod \
  --cache-cluster-enabled \
  --cache-cluster-size '0.5'
```

---

## Rollback Procedures

### Lambda Rollback

```bash
# Get previous version
aws lambda list-versions-by-function --function-name portfolio-service

# Rollback to previous version
aws lambda update-alias \
  --function-name portfolio-service \
  --name prod \
  --function-version 5
```

### CloudFormation Rollback

```bash
# Rollback stack
aws cloudformation cancel-update-stack \
  --stack-name portfolio-risk-system

# Or delete and recreate
aws cloudformation delete-stack --stack-name portfolio-risk-system
aws cloudformation create-stack --stack-name portfolio-risk-system --template-body file://template.yaml
```

---

## Cost Optimization

### Estimated Monthly Costs

| Service | Usage | Cost |
|---------|-------|------|
| API Gateway | 10M requests | $35 |
| Lambda | 100M invocations | $20 |
| DynamoDB | On-demand | $25 |
| EventBridge | 50M events | $20 |
| CloudWatch | Logs + metrics | $15 |
| S3 + CloudFront | Frontend | $10 |
| **Total** | | **$125** |

### Cost Reduction Tips

1. Use Lambda reserved concurrency
2. Enable DynamoDB auto-scaling
3. Use CloudFront caching
4. Archive old events to S3 Glacier
5. Use spot instances for non-critical workloads

---

## Maintenance

### Regular Tasks

- [ ] Review CloudWatch logs weekly
- [ ] Check DynamoDB capacity monthly
- [ ] Update dependencies quarterly
- [ ] Security audit semi-annually
- [ ] Disaster recovery drill annually

### Backup Strategy

```bash
# Backup DynamoDB table
aws dynamodb create-backup \
  --table-name portfolios \
  --backup-name portfolios-backup-$(date +%Y%m%d)

# Export to S3
aws dynamodb export-table-to-point-in-time \
  --table-arn arn:aws:dynamodb:us-east-1:ACCOUNT_ID:table/portfolios \
  --s3-bucket portfolio-risk-backups \
  --s3-prefix backups/
```

---

## Support & Documentation

- **AWS Documentation**: https://docs.aws.amazon.com
- **Spring Boot Documentation**: https://spring.io/projects/spring-boot
- **React Documentation**: https://react.dev
- **Support Email**: support@beautyinvest.com

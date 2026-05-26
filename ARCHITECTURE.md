# AI-Driven Real-Time Portfolio Risk Alert System - Architecture

## Phase 1: Architecture & Service Design ✅

### Microservices Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                        API GATEWAY                              │
│                    (Rate limit, Auth, CORS)                     │
└────────────────────────────────────────────────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
        ▼                     ▼                     ▼
┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐
│ PORTFOLIO        │  │ MARKET DATA      │  │ RISK             │
│ SERVICE          │  │ SERVICE          │  │ SERVICE          │
│ (Lambda)         │  │ (Lambda)         │  │ (Lambda)         │
└──────────────────┘  └──────────────────┘  └──────────────────┘
        │                     │                     │
        └─────────────────────┼─────────────────────┘
                              │
                    ┌─────────▼─────────┐
                    │   EVENTBRIDGE     │
                    │   (Event Router)  │
                    └─────────┬─────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
        ▼                     ▼                     ▼
┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐
│ AI INSIGHT       │  │ NOTIFICATION     │  │ ANALYTICS        │
│ SERVICE          │  │ SERVICE          │  │ SERVICE          │
│ (Lambda)         │  │ (Lambda)         │  │ (Lambda)         │
└──────────────────┘  └──────────────────┘  └──────────────────┘
        │                     │                     │
        └─────────────────────┼─────────────────────┘
                              │
                    ┌─────────▼─────────┐
                    │   DYNAMODB        │
                    │   (Data Store)    │
                    └───────────────────┘
```

### Event Flow

1. **Market Data Service** generates `PriceUpdated` events every 5-10 seconds
2. **EventBridge** routes to **Risk Service**
3. **Risk Service** detects breaches, publishes `RiskThresholdBreached`
4. **EventBridge** routes to **AI Insight Service**
5. **AI Service** generates explanation, publishes `AIInsightGenerated`
6. **EventBridge** routes to **Notification Service** and **Analytics Service**
7. All events stored in DynamoDB for audit trail

### Event Schemas

See `EVENT_SCHEMAS.md` for detailed JSON schemas.

### API Contract

See `API_CONTRACT.md` for OpenAPI 3.0 specification.

---

## Phase 2: Backend Services & APIs (In Progress)

### Service Implementations

#### Portfolio Service
- REST endpoints for portfolio management
- DynamoDB integration for persistence
- Allocation calculations

#### Market Data Service
- Price simulation logic
- EventBridge event publishing
- Real-time price updates

#### Risk Service
- Risk detection algorithms
- Threshold breach logic
- Event publishing

#### AI Insight Service
- OpenAI integration
- Prompt engineering
- Structured JSON responses

---

## Phase 3: Event System (Pending)

- EventBridge rules configuration
- SQS dead-letter queues
- Event replay capability

---

## Phase 4: AI Integration (Pending)

- OpenAI API integration
- Prompt templates
- Response parsing

---

## Phase 5: Frontend Enhancement (Pending)

- Real-time updates via WebSocket
- Enhanced dashboard
- Risk visualization

---

## Phase 6: Testing & Deployment (Pending)

- Unit tests
- Integration tests
- CloudFormation templates
- CI/CD pipeline

---

## Deployment Architecture

```
AWS Account
├── API Gateway (10k req/s throttle)
├── Lambda Functions (6 services)
├── DynamoDB Tables (4 tables)
├── EventBridge (Event routing)
├── CloudWatch (Monitoring)
├── S3 + CloudFront (Frontend)
└── Secrets Manager (API keys)
```

## Scaling Strategy

| Component | Trigger | Max | Optimization |
|-----------|---------|-----|--------------|
| API Gateway | Requests/sec | 10k req/s | Caching, throttling |
| Lambda | Concurrent invocations | 1k concurrent | Reserved concurrency |
| DynamoDB | Read/write capacity | On-demand | Auto-scaling |
| EventBridge | Events/sec | 1M events/sec | Batch processing |

---

## Security Considerations

- API Gateway authentication (API Key + JWT)
- IAM roles with least privilege
- Secrets Manager for API keys
- VPC endpoints for private access
- Encryption at rest (DynamoDB, S3)
- Encryption in transit (TLS 1.2+)

---

## Monitoring & Observability

**CloudWatch Dashboards**:
1. System Health (Lambda errors, DynamoDB throttling)
2. Business Metrics (Portfolio value, risk alerts)
3. Performance (API latency, Lambda duration)

**Alarms**:
- Lambda error rate > 1%
- DynamoDB throttling
- EventBridge rule failures
- API Gateway 5xx errors

---

## Cost Estimation (Monthly)

| Service | Usage | Cost |
|---------|-------|------|
| API Gateway | 10M requests | $35 |
| Lambda | 100M invocations | $20 |
| DynamoDB | On-demand | $25 |
| EventBridge | 50M events | $20 |
| CloudWatch | Logs + metrics | $15 |
| **Total** | | **$115** |

---

## Next Steps

1. ✅ Phase 1: Architecture design
2. 🔄 Phase 2: Backend services implementation
3. ⏳ Phase 3: Event system setup
4. ⏳ Phase 4: AI integration
5. ⏳ Phase 5: Frontend enhancement
6. ⏳ Phase 6: Testing & deployment

# Phase 2: Backend Services & APIs Implementation

## Overview

Phase 2 implements the core microservices architecture with event-driven design. This phase transforms the monolithic application into a scalable, event-driven system while maintaining backward compatibility with existing endpoints.

---

## New Services Implemented

### 1. Event Publishing Service
**File**: `src/main/java/com/ramya/portfoliorisksystem/service/EventPublishingService.java`

**Responsibility**: Publishes events to EventBridge for event-driven architecture

**Methods**:
- `publishPriceUpdated()` - Publishes stock price changes
- `publishPortfolioRevalued()` - Publishes portfolio value changes
- `publishRiskThresholdBreached()` - Publishes risk alerts
- `publishAIInsightGenerated()` - Publishes AI insights

**Event Format**:
```json
{
  "eventId": "uuid",
  "eventType": "PriceUpdated",
  "timestamp": "ISO-8601",
  "source": "service-name",
  "version": "1.0",
  "data": {}
}
```

**Integration Points**:
- Called by Market Data Service when prices update
- Called by Risk Detection Service when risks are detected
- Called by AI Service when insights are generated

**Production Deployment**:
```java
// Replace mock implementation with AWS EventBridge
PutEventsRequest request = PutEventsRequest.builder()
    .entries(PutEventsRequestEntry.builder()
        .source(source)
        .detailType(eventType)
        .detail(objectMapper.writeValueAsString(event))
        .build())
    .build();
eventBridgeClient.putEvents(request);
```

---

### 2. Risk Detection Service
**File**: `src/main/java/com/ramya/portfoliorisksystem/service/RiskDetectionService.java`

**Responsibility**: Implements deterministic risk detection algorithms

**Risk Conditions**:

| Risk Type | Threshold | Calculation |
|-----------|-----------|-------------|
| CONCENTRATION | >20% | (Stock Value / Portfolio Value) * 100 |
| ALLOCATION_DRIFT | >5% | \|Current% - Target%\| |
| PORTFOLIO_DROP | >3% | (Previous Value - Current Value) / Previous Value * 100 |

**Severity Levels**:
- LOW: 0-2% over threshold
- MEDIUM: 2-5% over threshold
- HIGH: 5-10% over threshold
- CRITICAL: >10% over threshold

**Key Methods**:
- `detectRisks(Client)` - Detects all risk conditions
- `detectConcentrationRisk(Client)` - Checks single stock exposure
- `detectAllocationDrift(Client)` - Checks allocation drift (future)
- `detectPortfolioDrop(Client)` - Checks daily loss (future)
- `getRiskLevel(Client)` - Returns overall risk level
- `isConcentrationRiskBreached(Client)` - Boolean check
- `getConcentrationRiskDetails(Client)` - Returns exposure map

**Example Usage**:
```java
List<RiskAlert> alerts = riskDetectionService.detectRisks(client);
String riskLevel = riskDetectionService.getRiskLevel(client);
```

**Event Publishing**:
When a risk is detected, automatically publishes `RiskThresholdBreached` event:
```json
{
  "eventType": "RiskThresholdBreached",
  "data": {
    "clientId": "client-123",
    "riskType": "CONCENTRATION",
    "severity": "HIGH",
    "message": "AAPL exposure at 25% (threshold: 20%)"
  }
}
```

---

### 3. Market Data Simulation Service
**File**: `src/main/java/com/ramya/portfoliorisksystem/service/MarketDataSimulationService.java`

**Responsibility**: Simulates realistic stock price movements

**Price Movement Algorithm**:
1. Random walk with drift (realistic market behavior)
2. Volatility: 1-3% per update
3. Bounds: Prices stay within ±10% of initial price
4. Frequency: Every 5-10 seconds

**Key Methods**:
- `simulatePriceUpdates()` - Scheduled task (every 5 seconds)
- `updateStockPrice(Stock)` - Updates single stock price
- `getCurrentPrice(String)` - Gets current price for symbol
- `getAllCurrentPrices()` - Gets all current prices
- `resetPrices()` - Resets to initial values (testing)
- `setStockPrice(String, double)` - Sets specific price (testing)

**Scheduled Execution**:
```java
@Scheduled(fixedDelay = 5000, initialDelay = 5000)
public void simulatePriceUpdates() {
    // Updates all stock prices and publishes PriceUpdated events
}
```

**Event Publishing**:
For each price update, publishes `PriceUpdated` event:
```json
{
  "eventType": "PriceUpdated",
  "data": {
    "symbol": "AAPL",
    "price": 150.25,
    "change": 0.50,
    "changePercent": 0.3344
  }
}
```

**Production Deployment**:
Replace with real market data feeds:
- Alpha Vantage API
- IEX Cloud
- Bloomberg Terminal
- Kafka stream from market data provider

---

### 4. AI Service Enhanced
**File**: `src/main/java/com/ramya/portfoliorisksystem/service/AIServiceEnhanced.java`

**Responsibility**: Generates AI-powered portfolio insights

**Features**:
- Professional prompt engineering
- Structured JSON responses
- Caching to reduce API calls
- Fallback to rule-based recommendations
- Proper disclaimers and risk warnings

**Key Methods**:
- `generateInsight(clientId, riskAlert, allocation, portfolioValue)` - Main method
- `generateInsightViaOpenAI()` - Calls OpenAI API
- `generateInsightViaRules()` - Rule-based fallback
- `buildPrompt()` - Constructs professional prompt
- `clearCache()` - Clears insight cache
- `getCacheStats()` - Returns cache statistics

**Prompt Design**:
```
You are a professional financial advisor analyzing a portfolio risk alert.

RISK ALERT:
Type: CONCENTRATION
Severity: HIGH
Message: AAPL exposure at 25% (threshold: 20%)

TASK:
Generate a professional, actionable insight in JSON format...

REQUIREMENTS:
1. Explanation should be clear and professional
2. Suggested action should be specific and actionable
3. Include appropriate disclaimers
4. Do NOT provide financial guarantees
5. Do NOT recommend specific securities
```

**Response Format**:
```json
{
  "severity": "HIGH",
  "explanation": "Your portfolio is overweight in AAPL...",
  "suggestedAction": "Consider reducing AAPL exposure...",
  "disclaimer": "This is AI-generated guidance only..."
}
```

**Caching Strategy**:
- Cache key: `clientId:riskType`
- Reduces API calls for repeated risk types
- Cache cleared on service restart

**Event Publishing**:
When insight is generated, publishes `AIInsightGenerated` event:
```json
{
  "eventType": "AIInsightGenerated",
  "data": {
    "clientId": "client-123",
    "riskEventId": "event-456",
    "severity": "HIGH",
    "explanation": "...",
    "suggestedAction": "..."
  }
}
```

---

## New API Endpoints

### Risk Endpoints

#### GET /api/v1/risk/clients/{clientId}
Get all risk alerts for a client

**Response**:
```json
{
  "clientId": "client-123",
  "riskLevel": "HIGH",
  "alertCount": 2,
  "alerts": [
    {
      "alertId": "alert-001",
      "riskType": "CONCENTRATION",
      "severity": "HIGH",
      "message": "AAPL exposure at 25%..."
    }
  ],
  "severitySummary": {
    "CRITICAL": 0,
    "HIGH": 1,
    "MEDIUM": 1,
    "LOW": 0
  }
}
```

#### GET /api/v1/risk/clients/{clientId}/concentration
Get concentration risk details

**Response**:
```json
{
  "clientId": "client-123",
  "isBreached": true,
  "threshold": 20.0,
  "concentrations": {
    "AAPL": 25.0,
    "MSFT": 18.5
  },
  "maxConcentration": 25.0
}
```

#### GET /api/v1/risk/summary
Get firm-wide risk summary

**Response**:
```json
{
  "clientCount": 100,
  "totalAlerts": 45,
  "riskLevelDistribution": {
    "CRITICAL": 2,
    "HIGH": 15,
    "MEDIUM": 30,
    "LOW": 53
  },
  "avgAlertsPerClient": 0.45
}
```

#### GET /api/v1/risk/high-risk-clients
Get clients with high or critical risk

**Response**:
```json
[
  {
    "clientId": "client-001",
    "name": "Client 1",
    "portfolioValue": 500000.00,
    "riskLevel": "HIGH",
    "alertCount": 2,
    "alerts": [...]
  }
]
```

---

### Market Data Endpoints

#### GET /api/v1/market/prices
Get all current market prices

**Response**:
```json
{
  "prices": [
    {
      "symbol": "AAPL",
      "price": 150.25,
      "change": 0.50,
      "changePercent": 0.3344,
      "lastUpdated": "2026-05-26T10:00:05Z"
    }
  ],
  "count": 20,
  "timestamp": "2026-05-26T10:00:05Z"
}
```

#### GET /api/v1/market/prices/{symbol}
Get price for specific symbol

**Response**:
```json
{
  "symbol": "AAPL",
  "price": 150.25,
  "previousPrice": 149.75,
  "change": 0.50,
  "changePercent": 0.3344,
  "dayHigh": 151.50,
  "dayLow": 148.75,
  "openPrice": 149.50,
  "volume": 1000000,
  "lastUpdated": "2026-05-26T10:00:05Z"
}
```

#### GET /api/v1/market/stocks
Get all available stocks

**Response**:
```json
{
  "stocks": [...],
  "count": 20,
  "bySector": {
    "Technology": [...],
    "Luxury": [...]
  }
}
```

#### GET /api/v1/market/sectors
Get sector breakdown

**Response**:
```json
{
  "sectors": {
    "Technology": [...],
    "Luxury": [...]
  },
  "sectorCount": 6
}
```

#### POST /api/v1/market/reset-prices
Reset all prices to initial values (testing)

#### POST /api/v1/market/set-price
Set a specific stock price (testing)

---

## DynamoDB Configuration

**File**: `src/main/java/com/ramya/portfoliorisksystem/config/DynamoDBConfig.java`

### Tables

#### 1. portfolios
- **PK**: clientId
- **SK**: timestamp
- **GSI**: clientName-index
- **TTL**: None (permanent)
- **Billing**: PAY_PER_REQUEST

#### 2. market_prices
- **PK**: symbol
- **TTL**: None (always current)
- **Billing**: PAY_PER_REQUEST

#### 3. risk_alerts
- **PK**: clientId
- **SK**: timestamp
- **GSI**: riskType-timestamp-index, severity-timestamp-index
- **TTL**: 7776000 (90 days)
- **Billing**: PAY_PER_REQUEST

#### 4. ai_insights
- **PK**: clientId
- **SK**: timestamp
- **GSI**: riskEventId-index
- **TTL**: 7776000 (90 days)
- **Billing**: PAY_PER_REQUEST

#### 5. events
- **PK**: eventId
- **SK**: timestamp
- **GSI**: eventType-timestamp-index, source-timestamp-index
- **TTL**: 2592000 (30 days)
- **Billing**: PAY_PER_REQUEST

### CloudFormation Deployment

```bash
aws cloudformation create-stack \
  --stack-name portfolio-risk-system \
  --template-body file://dynamodb-tables.yaml
```

---

## Event Flow Diagram

```
Market Data Service (every 5 seconds)
    ↓
    publishes PriceUpdated event
    ↓
EventBridge routes to Risk Service
    ↓
Risk Detection Service
    ↓
    detects concentration risk
    ↓
    publishes RiskThresholdBreached event
    ↓
EventBridge routes to AI Service
    ↓
AI Service Enhanced
    ↓
    generates insight
    ↓
    publishes AIInsightGenerated event
    ↓
EventBridge routes to:
  - Notification Service
  - Analytics Service
  - Frontend (WebSocket)
```

---

## Integration with Existing Code

### Backward Compatibility

All existing endpoints remain unchanged:
- `GET /portfolio` - Still works
- `GET /portfolio/risk` - Enhanced with new risk detection
- `GET /portfolio/ai-insight` - Enhanced with new AI service
- `GET /market-prices` - Still works
- `GET /dashboard` - Still works

### New Endpoints

New endpoints follow REST conventions:
- `/api/v1/risk/*` - Risk management
- `/api/v1/market/*` - Market data

### Service Injection

New services are autowired into existing services:
```java
@Autowired
private RiskDetectionService riskDetectionService;

@Autowired
private MarketDataSimulationService marketDataSimulationService;

@Autowired
private AIServiceEnhanced aiServiceEnhanced;

@Autowired
private EventPublishingService eventPublishingService;
```

---

## Testing

### Unit Tests

```bash
# Test risk detection
mvn test -Dtest=RiskDetectionServiceTest

# Test market data simulation
mvn test -Dtest=MarketDataSimulationServiceTest

# Test AI service
mvn test -Dtest=AIServiceEnhancedTest
```

### Integration Tests

```bash
# Test full event flow
mvn test -Dtest=EventFlowIntegrationTest

# Test API endpoints
mvn test -Dtest=RiskControllerTest
mvn test -Dtest=MarketDataControllerTest
```

### Manual Testing

```bash
# Get risk alerts for client 1
curl http://localhost:8081/api/v1/risk/clients/1

# Get concentration risk
curl http://localhost:8081/api/v1/risk/clients/1/concentration

# Get all market prices
curl http://localhost:8081/api/v1/market/prices

# Get specific stock price
curl http://localhost:8081/api/v1/market/prices/AAPL

# Reset prices (testing)
curl -X POST http://localhost:8081/api/v1/market/reset-prices

# Set specific price (testing)
curl -X POST "http://localhost:8081/api/v1/market/set-price?symbol=AAPL&price=150.00"
```

---

## Performance Considerations

### Caching

- AI insights cached by `clientId:riskType`
- Reduces OpenAI API calls
- Cache cleared on service restart

### Scheduled Tasks

- Price updates every 5 seconds
- Risk detection on-demand (via API)
- Can be optimized with batch processing

### Database Queries

- DynamoDB on-demand billing (auto-scaling)
- GSI for efficient filtering
- TTL for automatic cleanup

---

## Security Considerations

### API Authentication

- API Key validation (X-API-Key header)
- JWT Bearer token support
- Rate limiting: 10,000 req/sec

### Data Protection

- Encryption at rest (DynamoDB)
- Encryption in transit (TLS 1.2+)
- Secrets Manager for API keys

### IAM Roles

```json
{
  "Effect": "Allow",
  "Action": [
    "dynamodb:GetItem",
    "dynamodb:PutItem",
    "dynamodb:Query"
  ],
  "Resource": "arn:aws:dynamodb:*:*:table/portfolios*"
}
```

---

## Monitoring & Observability

### CloudWatch Logs

```
/aws/lambda/portfolio-service
/aws/lambda/risk-service
/aws/lambda/ai-insight-service
/aws/lambda/market-data-service
```

### CloudWatch Metrics

```
PortfolioRiskSystem/Events:
  - EventsPublished (Count)
  - EventsConsumed (Count)
  - EventProcessingLatency (Milliseconds)
  - EventsFailedToProcess (Count)

PortfolioRiskSystem/Risk:
  - RisksDetected (Count)
  - AlertsGenerated (Count)
  - AvgProcessingTime (Milliseconds)

PortfolioRiskSystem/AI:
  - InsightsGenerated (Count)
  - APICallsToOpenAI (Count)
  - CacheHitRate (Percentage)
```

### Alarms

- Lambda error rate > 1%
- DynamoDB throttling
- EventBridge rule failures
- API Gateway 5xx errors

---

## Next Steps

Phase 3 will implement:
1. EventBridge rules configuration
2. SQS dead-letter queues
3. Event replay capability
4. Lambda function deployment
5. CloudFormation templates

---

## Deployment Checklist

- [ ] Create DynamoDB tables
- [ ] Configure IAM roles
- [ ] Deploy Lambda functions
- [ ] Set up EventBridge rules
- [ ] Configure CloudWatch alarms
- [ ] Test event flow
- [ ] Load test (10k req/sec)
- [ ] Security audit
- [ ] Documentation review

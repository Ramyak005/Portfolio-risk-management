# Event Schemas - Portfolio Risk System

All events follow a standard envelope format with typed data payloads.

---

## Standard Event Envelope

```json
{
  "eventId": "uuid-v4",
  "eventType": "string",
  "timestamp": "ISO-8601",
  "source": "service-name",
  "version": "1.0",
  "data": {}
}
```

---

## Event 1: PriceUpdated

**Source**: Market Data Service  
**Consumers**: Risk Service, Analytics Service  
**Frequency**: Every 5-10 seconds per symbol  
**Retention**: 24 hours

### Schema

```json
{
  "eventId": "550e8400-e29b-41d4-a716-446655440000",
  "eventType": "PriceUpdated",
  "timestamp": "2026-05-26T10:00:05.123Z",
  "source": "market-data-service",
  "version": "1.0",
  "data": {
    "symbol": "AAPL",
    "price": 150.25,
    "previousPrice": 149.75,
    "change": 0.50,
    "changePercent": 0.3344,
    "volume": 1000000,
    "dayHigh": 151.50,
    "dayLow": 148.75,
    "openPrice": 149.50
  }
}
```

### DynamoDB Storage

**Table**: `events`  
**PK**: `eventId`  
**SK**: `timestamp`  
**TTL**: 86400 (24 hours)

```json
{
  "eventId": "550e8400-e29b-41d4-a716-446655440000",
  "timestamp": "2026-05-26T10:00:05.123Z",
  "eventType": "PriceUpdated",
  "source": "market-data-service",
  "data": { ... },
  "ttl": 1748000405
}
```

### EventBridge Rule

```json
{
  "Name": "route-price-updated",
  "EventPattern": {
    "source": ["market-data-service"],
    "detail-type": ["PriceUpdated"]
  },
  "Targets": [
    {
      "Arn": "arn:aws:lambda:us-east-1:123456789:function:risk-service",
      "RoleArn": "arn:aws:iam::123456789:role/eventbridge-invoke-lambda"
    },
    {
      "Arn": "arn:aws:lambda:us-east-1:123456789:function:analytics-service",
      "RoleArn": "arn:aws:iam::123456789:role/eventbridge-invoke-lambda"
    }
  ]
}
```

---

## Event 2: PortfolioRevalued

**Source**: Portfolio Service  
**Consumers**: Risk Service, Analytics Service  
**Frequency**: On-demand (when holdings change)  
**Retention**: 30 days

### Schema

```json
{
  "eventId": "660e8400-e29b-41d4-a716-446655440001",
  "eventType": "PortfolioRevalued",
  "timestamp": "2026-05-26T10:00:10.456Z",
  "source": "portfolio-service",
  "version": "1.0",
  "data": {
    "clientId": "client-123",
    "clientName": "Ramya Sharma",
    "portfolioValue": 500000.00,
    "previousValue": 498000.00,
    "change": 2000.00,
    "changePercent": 0.4016,
    "holdings": {
      "AAPL": 100,
      "MSFT": 50,
      "LVMH": 25
    },
    "allocation": {
      "AAPL": 0.30,
      "MSFT": 0.20,
      "LVMH": 0.50
    },
    "diversificationScore": 65.5,
    "topRiskExposure": "LVMH"
  }
}
```

### DynamoDB Storage

**Table**: `events`  
**PK**: `eventId`  
**SK**: `timestamp`  
**GSI**: `clientId-timestamp` (for client-specific queries)

---

## Event 3: RiskThresholdBreached

**Source**: Risk Service  
**Consumers**: AI Insight Service, Notification Service, Analytics Service  
**Frequency**: On-demand (when threshold breached)  
**Retention**: 90 days

### Schema

```json
{
  "eventId": "770e8400-e29b-41d4-a716-446655440002",
  "eventType": "RiskThresholdBreached",
  "timestamp": "2026-05-26T10:00:15.789Z",
  "source": "risk-service",
  "version": "1.0",
  "data": {
    "clientId": "client-123",
    "clientName": "Ramya Sharma",
    "riskType": "CONCENTRATION",
    "severity": "HIGH",
    "threshold": 20.0,
    "currentValue": 25.0,
    "message": "AAPL exposure at 25% (threshold: 20%)",
    "affectedAssets": ["AAPL"],
    "portfolioValue": 500000.00,
    "exposureValue": 125000.00,
    "detectionMethod": "dollar-weighted-concentration",
    "previousAlertId": null,
    "isRecurring": false
  }
}
```

### Risk Types

| Type | Threshold | Calculation |
|------|-----------|-------------|
| CONCENTRATION | >20% | (Stock Value / Portfolio Value) * 100 |
| ALLOCATION_DRIFT | >5% | \|Current% - Target%\| |
| PORTFOLIO_DROP | >3% | (Previous Value - Current Value) / Previous Value * 100 |

### Severity Levels

| Level | Criteria |
|-------|----------|
| LOW | Threshold exceeded by 0-2% |
| MEDIUM | Threshold exceeded by 2-5% |
| HIGH | Threshold exceeded by 5-10% |
| CRITICAL | Threshold exceeded by >10% |

### DynamoDB Storage

**Table**: `risk_alerts`  
**PK**: `clientId`  
**SK**: `timestamp`  
**GSI**: `riskType-timestamp` (for risk type queries)  
**GSI**: `severity-timestamp` (for severity queries)

---

## Event 4: AIInsightGenerated

**Source**: AI Insight Service  
**Consumers**: Notification Service, Analytics Service, Frontend  
**Frequency**: On-demand (triggered by RiskThresholdBreached)  
**Retention**: 90 days

### Schema

```json
{
  "eventId": "880e8400-e29b-41d4-a716-446655440003",
  "eventType": "AIInsightGenerated",
  "timestamp": "2026-05-26T10:00:20.012Z",
  "source": "ai-insight-service",
  "version": "1.0",
  "data": {
    "clientId": "client-123",
    "riskEventId": "770e8400-e29b-41d4-a716-446655440002",
    "severity": "HIGH",
    "explanation": "Your portfolio is overweight in luxury retail sector (LVMH, Hermès). This concentration increases volatility and reduces diversification benefits. Luxury retail is cyclical and sensitive to economic downturns.",
    "suggestedAction": "Consider reducing luxury retail exposure from 50% to 30% and diversifying into healthcare (10%), utilities (10%), and technology (10%). This would improve risk-adjusted returns.",
    "disclaimer": "This is AI-generated guidance only and should not be considered financial advice. Consult a qualified financial advisor before making investment decisions.",
    "model": "gpt-4o-mini",
    "processingTimeMs": 1250,
    "tokenUsage": {
      "prompt": 450,
      "completion": 280,
      "total": 730
    },
    "confidence": 0.92
  }
}
```

### DynamoDB Storage

**Table**: `ai_insights`  
**PK**: `clientId`  
**SK**: `timestamp`  
**GSI**: `riskEventId` (for linking to risk events)

---

## Event 5: NotificationSent (Optional)

**Source**: Notification Service  
**Consumers**: Analytics Service  
**Frequency**: On-demand  
**Retention**: 30 days

### Schema

```json
{
  "eventId": "990e8400-e29b-41d4-a716-446655440004",
  "eventType": "NotificationSent",
  "timestamp": "2026-05-26T10:00:25.345Z",
  "source": "notification-service",
  "version": "1.0",
  "data": {
    "clientId": "client-123",
    "riskEventId": "770e8400-e29b-41d4-a716-446655440002",
    "aiInsightId": "880e8400-e29b-41d4-a716-446655440003",
    "channels": ["dashboard", "email"],
    "status": "sent",
    "deliveryStatus": {
      "dashboard": "delivered",
      "email": "queued"
    },
    "timestamp": "2026-05-26T10:00:25.345Z"
  }
}
```

---

## Event Publishing Patterns

### Synchronous Publishing (REST API)

```java
// Portfolio Service publishes PortfolioRevalued
EventBridgeClient client = EventBridgeClient.builder().build();
PutEventsRequest request = PutEventsRequest.builder()
    .entries(PutEventsRequestEntry.builder()
        .source("portfolio-service")
        .detailType("PortfolioRevalued")
        .detail(gson.toJson(portfolioRevaluedData))
        .build())
    .build();
client.putEvents(request);
```

### Asynchronous Publishing (Lambda)

```java
// Market Data Service publishes PriceUpdated via Lambda
@Handler
public void handlePriceUpdate(ScheduledEvent event, Context context) {
    for (Stock stock : stocks) {
        stock.updatePrice();
        publishPriceUpdatedEvent(stock);
    }
}
```

---

## Event Routing Rules

### Rule 1: PriceUpdated → Risk Service

```json
{
  "Name": "route-price-updated-to-risk",
  "EventPattern": {
    "source": ["market-data-service"],
    "detail-type": ["PriceUpdated"]
  },
  "Targets": [
    {
      "Arn": "arn:aws:lambda:us-east-1:123456789:function:risk-service",
      "RoleArn": "arn:aws:iam::123456789:role/eventbridge-invoke-lambda"
    }
  ]
}
```

### Rule 2: RiskThresholdBreached → AI Service

```json
{
  "Name": "route-risk-to-ai",
  "EventPattern": {
    "source": ["risk-service"],
    "detail-type": ["RiskThresholdBreached"]
  },
  "Targets": [
    {
      "Arn": "arn:aws:lambda:us-east-1:123456789:function:ai-insight-service",
      "RoleArn": "arn:aws:iam::123456789:role/eventbridge-invoke-lambda"
    }
  ]
}
```

### Rule 3: AIInsightGenerated → Notification Service

```json
{
  "Name": "route-ai-to-notification",
  "EventPattern": {
    "source": ["ai-insight-service"],
    "detail-type": ["AIInsightGenerated"]
  },
  "Targets": [
    {
      "Arn": "arn:aws:lambda:us-east-1:123456789:function:notification-service",
      "RoleArn": "arn:aws:iam::123456789:role/eventbridge-invoke-lambda"
    }
  ]
}
```

---

## Event Replay & Debugging

### Query Events by Type

```sql
-- DynamoDB Query
SELECT * FROM events 
WHERE eventType = 'RiskThresholdBreached' 
AND timestamp BETWEEN '2026-05-26T00:00:00Z' AND '2026-05-26T23:59:59Z'
ORDER BY timestamp DESC
```

### Replay Events

```bash
# AWS CLI - Replay events from last 24 hours
aws events put-events \
  --entries file://replay-events.json
```

---

## Event Versioning

All events include a `version` field for backward compatibility.

**Current Version**: 1.0

**Migration Path**:
1. New events published with `version: 2.0`
2. Consumers support both 1.0 and 2.0
3. Old consumers deprecated after 30 days
4. Old events archived after 90 days

---

## Monitoring Events

### CloudWatch Metrics

```
Namespace: PortfolioRiskSystem/Events

Metrics:
- EventsPublished (Count)
- EventsConsumed (Count)
- EventProcessingLatency (Milliseconds)
- EventsFailedToProcess (Count)
- EventQueueDepth (Count)
```

### CloudWatch Logs

```
Log Groups:
- /aws/events/portfolio-risk-system
- /aws/lambda/portfolio-service
- /aws/lambda/risk-service
- /aws/lambda/ai-insight-service
```

---

## Dead-Letter Queue (DLQ) Handling

Failed events are sent to SQS DLQ for manual review:

```json
{
  "originalEvent": { ... },
  "error": "Lambda function timeout",
  "timestamp": "2026-05-26T10:00:30Z",
  "retryCount": 3
}
```

**DLQ Processing**:
1. Alert on-call engineer
2. Log to CloudWatch
3. Archive to S3 for analysis
4. Manual retry after fix

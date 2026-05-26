# API Contract - Portfolio Risk System

**Version**: 1.0  
**Base URL**: `https://api.beautyinvest.com/api/v1`  
**Authentication**: API Key + JWT Bearer Token  
**Rate Limit**: 10,000 requests/second per API key  
**Response Format**: JSON  

---

## Authentication

### API Key Authentication

```bash
curl -H "X-API-Key: your-api-key" \
  https://api.beautyinvest.com/api/v1/portfolios/client-123
```

### JWT Bearer Token

```bash
curl -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIs..." \
  https://api.beautyinvest.com/api/v1/portfolios/client-123
```

---

## Error Responses

All error responses follow this format:

```json
{
  "error": {
    "code": "RESOURCE_NOT_FOUND",
    "message": "Client with ID 'client-999' not found",
    "timestamp": "2026-05-26T10:00:00Z",
    "requestId": "req-123456789",
    "details": {
      "clientId": "client-999"
    }
  }
}
```

### Error Codes

| Code | HTTP | Description |
|------|------|-------------|
| INVALID_REQUEST | 400 | Missing or invalid parameters |
| UNAUTHORIZED | 401 | Missing or invalid authentication |
| FORBIDDEN | 403 | Insufficient permissions |
| RESOURCE_NOT_FOUND | 404 | Resource does not exist |
| CONFLICT | 409 | Resource already exists |
| RATE_LIMIT_EXCEEDED | 429 | Too many requests |
| INTERNAL_ERROR | 500 | Server error |
| SERVICE_UNAVAILABLE | 503 | Service temporarily unavailable |

---

## Portfolio Endpoints

### GET /portfolios/{clientId}

Get complete portfolio for a client.

**Parameters**:
- `clientId` (path, required): Client identifier

**Response** (200 OK):

```json
{
  "clientId": "client-123",
  "clientName": "Ramya Sharma",
  "portfolioValue": 500000.00,
  "previousValue": 498000.00,
  "change": 2000.00,
  "changePercent": 0.4016,
  "holdings": {
    "AAPL": 100,
    "MSFT": 50,
    "LVMH": 25,
    "HERMES": 10
  },
  "allocation": {
    "AAPL": 0.30,
    "MSFT": 0.20,
    "LVMH": 0.40,
    "HERMES": 0.10
  },
  "targetAllocation": {
    "AAPL": 0.35,
    "MSFT": 0.25,
    "LVMH": 0.25,
    "HERMES": 0.15
  },
  "diversificationScore": 65.5,
  "topRiskExposure": "LVMH",
  "lastUpdated": "2026-05-26T10:00:00Z"
}
```

**Errors**:
- 404: Client not found
- 401: Unauthorized

---

### GET /portfolios/{clientId}/allocation

Get detailed allocation breakdown.

**Parameters**:
- `clientId` (path, required): Client identifier
- `includeMetrics` (query, optional): Include risk metrics (default: false)

**Response** (200 OK):

```json
{
  "clientId": "client-123",
  "portfolioValue": 500000.00,
  "allocation": [
    {
      "symbol": "AAPL",
      "shares": 100,
      "price": 150.25,
      "value": 15025.00,
      "percentOfPortfolio": 30.05,
      "sector": "Technology",
      "targetPercent": 35.00,
      "drift": -4.95,
      "riskLevel": "MEDIUM"
    },
    {
      "symbol": "MSFT",
      "shares": 50,
      "price": 420.50,
      "value": 21025.00,
      "percentOfPortfolio": 42.05,
      "sector": "Technology",
      "targetPercent": 25.00,
      "drift": 17.05,
      "riskLevel": "HIGH"
    }
  ],
  "summary": {
    "totalValue": 500000.00,
    "sectorBreakdown": {
      "Technology": 0.72,
      "Luxury": 0.28
    },
    "concentrationRisk": "HIGH",
    "diversificationScore": 65.5
  },
  "lastUpdated": "2026-05-26T10:00:00Z"
}
```

---

### POST /portfolios/{clientId}/rebalance

Calculate rebalancing recommendation.

**Parameters**:
- `clientId` (path, required): Client identifier

**Request Body**:

```json
{
  "targetAllocation": {
    "AAPL": 0.35,
    "MSFT": 0.25,
    "LVMH": 0.25,
    "HERMES": 0.15
  },
  "constraints": {
    "maxTransactionCost": 500.00,
    "minHoldingValue": 1000.00,
    "preferredHoldingPeriod": "LONG_TERM"
  }
}
```

**Response** (200 OK):

```json
{
  "clientId": "client-123",
  "currentAllocation": {
    "AAPL": 0.30,
    "MSFT": 0.42,
    "LVMH": 0.20,
    "HERMES": 0.08
  },
  "targetAllocation": {
    "AAPL": 0.35,
    "MSFT": 0.25,
    "LVMH": 0.25,
    "HERMES": 0.15
  },
  "recommendedActions": [
    {
      "action": "BUY",
      "symbol": "AAPL",
      "shares": 25,
      "estimatedCost": 3756.25,
      "rationale": "Increase underweight position"
    },
    {
      "action": "SELL",
      "symbol": "MSFT",
      "shares": 50,
      "estimatedProceeds": 21025.00,
      "rationale": "Reduce overweight position"
    }
  ],
  "estimatedTransactionCost": 250.00,
  "expectedImpactOnDiversification": 0.08,
  "timestamp": "2026-05-26T10:00:00Z"
}
```

---

## Client Endpoints

### GET /clients

List all clients with pagination.

**Parameters**:
- `limit` (query, optional): Results per page (default: 100, max: 1000)
- `offset` (query, optional): Pagination offset (default: 0)
- `search` (query, optional): Search by name
- `sortBy` (query, optional): Sort field (portfolioValue, name, riskLevel)
- `sortOrder` (query, optional): asc or desc (default: desc)

**Response** (200 OK):

```json
{
  "data": [
    {
      "clientId": "client-123",
      "name": "Ramya Sharma",
      "portfolioValue": 500000.00,
      "diversificationScore": 65.5,
      "topRiskExposure": "LVMH",
      "riskLevel": "HIGH",
      "lastUpdated": "2026-05-26T10:00:00Z"
    },
    {
      "clientId": "client-124",
      "name": "Priya Patel",
      "portfolioValue": 750000.00,
      "diversificationScore": 72.3,
      "topRiskExposure": "AAPL",
      "riskLevel": "MEDIUM",
      "lastUpdated": "2026-05-26T10:00:00Z"
    }
  ],
  "pagination": {
    "limit": 100,
    "offset": 0,
    "total": 100,
    "hasMore": false
  }
}
```

---

### GET /clients/{clientId}

Get detailed client information.

**Parameters**:
- `clientId` (path, required): Client identifier

**Response** (200 OK):

```json
{
  "clientId": "client-123",
  "name": "Ramya Sharma",
  "email": "ramya@example.com",
  "phone": "+1-555-0123",
  "portfolioValue": 500000.00,
  "previousValue": 498000.00,
  "change": 2000.00,
  "changePercent": 0.4016,
  "diversificationScore": 65.5,
  "topRiskExposure": "LVMH",
  "riskLevel": "HIGH",
  "holdingCount": 4,
  "sectorCount": 2,
  "createdAt": "2025-01-15T00:00:00Z",
  "lastUpdated": "2026-05-26T10:00:00Z"
}
```

---

### GET /clients/{clientId}/risk

Get risk alerts for a client.

**Parameters**:
- `clientId` (path, required): Client identifier
- `severity` (query, optional): Filter by severity (LOW, MEDIUM, HIGH, CRITICAL)
- `riskType` (query, optional): Filter by type (CONCENTRATION, DRIFT, PORTFOLIO_DROP)
- `limit` (query, optional): Results per page (default: 50)

**Response** (200 OK):

```json
{
  "clientId": "client-123",
  "alerts": [
    {
      "alertId": "alert-001",
      "riskType": "CONCENTRATION",
      "severity": "HIGH",
      "message": "AAPL exposure at 25% (threshold: 20%)",
      "affectedAssets": ["AAPL"],
      "threshold": 20.0,
      "currentValue": 25.0,
      "portfolioValue": 500000.00,
      "exposureValue": 125000.00,
      "timestamp": "2026-05-26T10:00:15Z",
      "isActive": true
    },
    {
      "alertId": "alert-002",
      "riskType": "ALLOCATION_DRIFT",
      "severity": "MEDIUM",
      "message": "MSFT allocation drifted 17% from target",
      "affectedAssets": ["MSFT"],
      "threshold": 5.0,
      "currentValue": 17.0,
      "targetValue": 0.0,
      "timestamp": "2026-05-26T09:55:00Z",
      "isActive": true
    }
  ],
  "summary": {
    "totalAlerts": 2,
    "activeAlerts": 2,
    "criticalCount": 0,
    "highCount": 1,
    "mediumCount": 1,
    "lowCount": 0
  }
}
```

---

### GET /clients/{clientId}/ai-insight

Get latest AI insight for a client.

**Parameters**:
- `clientId` (path, required): Client identifier
- `includeHistory` (query, optional): Include last 5 insights (default: false)

**Response** (200 OK):

```json
{
  "clientId": "client-123",
  "insightId": "insight-001",
  "riskEventId": "event-002",
  "severity": "HIGH",
  "explanation": "Your portfolio is overweight in luxury retail sector (LVMH, Hermès). This concentration increases volatility and reduces diversification benefits. Luxury retail is cyclical and sensitive to economic downturns.",
  "suggestedAction": "Consider reducing luxury retail exposure from 50% to 30% and diversifying into healthcare (10%), utilities (10%), and technology (10%). This would improve risk-adjusted returns.",
  "disclaimer": "This is AI-generated guidance only and should not be considered financial advice. Consult a qualified financial advisor before making investment decisions.",
  "model": "gpt-4o-mini",
  "confidence": 0.92,
  "processingTimeMs": 1250,
  "generatedAt": "2026-05-26T10:00:20Z",
  "history": [
    {
      "insightId": "insight-002",
      "severity": "MEDIUM",
      "generatedAt": "2026-05-26T09:30:00Z"
    }
  ]
}
```

---

### GET /clients/{clientId}/analysis

Get comprehensive client analysis.

**Parameters**:
- `clientId` (path, required): Client identifier

**Response** (200 OK):

```json
{
  "clientId": "client-123",
  "portfolio": {
    "value": 500000.00,
    "change": 2000.00,
    "changePercent": 0.4016
  },
  "allocation": [
    {
      "symbol": "AAPL",
      "percentOfPortfolio": 30.05,
      "value": 15025.00
    }
  ],
  "risks": [
    {
      "alertId": "alert-001",
      "riskType": "CONCENTRATION",
      "severity": "HIGH"
    }
  ],
  "aiInsight": {
    "insightId": "insight-001",
    "severity": "HIGH",
    "explanation": "..."
  },
  "metrics": {
    "diversificationScore": 65.5,
    "concentrationRisk": "HIGH",
    "volatilityRating": "MEDIUM"
  },
  "lastUpdated": "2026-05-26T10:00:00Z"
}
```

---

## Market Endpoints

### GET /market/prices

Get all current market prices.

**Parameters**:
- `symbols` (query, optional): Comma-separated symbols (e.g., "AAPL,MSFT")
- `limit` (query, optional): Results per page (default: 100)

**Response** (200 OK):

```json
{
  "prices": [
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
    },
    {
      "symbol": "MSFT",
      "price": 420.50,
      "previousPrice": 420.00,
      "change": 0.50,
      "changePercent": 0.1190,
      "dayHigh": 421.00,
      "dayLow": 418.50,
      "openPrice": 419.75,
      "volume": 500000,
      "lastUpdated": "2026-05-26T10:00:05Z"
    }
  ],
  "timestamp": "2026-05-26T10:00:05Z"
}
```

---

### GET /market/prices/{symbol}

Get price for specific symbol.

**Parameters**:
- `symbol` (path, required): Stock symbol

**Response** (200 OK):

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

---

## Dashboard Endpoints

### GET /dashboard

Get aggregated dashboard data.

**Response** (200 OK):

```json
{
  "summary": {
    "clientCount": 100,
    "stockCount": 20,
    "totalAUM": 50000000.00,
    "avgPortfolioValue": 500000.00,
    "avgDiversificationScore": 68.5
  },
  "topPortfolios": [
    {
      "clientId": "client-001",
      "name": "Client 1",
      "portfolioValue": 1000000.00
    }
  ],
  "firmWideExposures": {
    "AAPL": 0.15,
    "MSFT": 0.12,
    "LVMH": 0.10
  },
  "riskSummary": {
    "criticalAlerts": 5,
    "highAlerts": 15,
    "mediumAlerts": 30,
    "lowAlerts": 50
  },
  "lastUpdated": "2026-05-26T10:00:00Z"
}
```

---

## Leaderboard Endpoints

### GET /leaderboard

Get ranked clients by portfolio value.

**Parameters**:
- `metric` (query, optional): Ranking metric (portfolioValue, diversificationScore, riskLevel)
- `limit` (query, optional): Top N results (default: 10, max: 100)

**Response** (200 OK):

```json
{
  "metric": "portfolioValue",
  "period": "current",
  "leaderboard": [
    {
      "rank": 1,
      "clientId": "client-001",
      "name": "Client 1",
      "portfolioValue": 1000000.00,
      "diversificationScore": 75.5,
      "riskLevel": "MEDIUM"
    },
    {
      "rank": 2,
      "clientId": "client-002",
      "name": "Client 2",
      "portfolioValue": 900000.00,
      "diversificationScore": 72.3,
      "riskLevel": "MEDIUM"
    }
  ]
}
```

---

## Rate Limiting

All endpoints are rate-limited to 10,000 requests/second per API key.

**Response Headers**:

```
X-RateLimit-Limit: 10000
X-RateLimit-Remaining: 9999
X-RateLimit-Reset: 1748000460
```

**When Limit Exceeded** (429 Too Many Requests):

```json
{
  "error": {
    "code": "RATE_LIMIT_EXCEEDED",
    "message": "Rate limit exceeded. Max 10000 requests/second",
    "retryAfter": 60
  }
}
```

---

## Pagination

Endpoints that return lists support pagination:

```json
{
  "data": [...],
  "pagination": {
    "limit": 100,
    "offset": 0,
    "total": 500,
    "hasMore": true
  }
}
```

---

## Versioning

API uses URL versioning: `/api/v1/`, `/api/v2/`, etc.

**Current Version**: v1  
**Deprecation Policy**: 12 months notice before removing old versions

---

## Webhooks (Future)

Clients can subscribe to webhooks for real-time events:

```bash
POST /webhooks/subscribe
{
  "url": "https://your-app.com/webhook",
  "events": ["RiskThresholdBreached", "AIInsightGenerated"],
  "secret": "webhook-secret-key"
}
```

---

## SDK Support

Official SDKs available for:
- Python: `pip install portfolio-risk-sdk`
- JavaScript: `npm install portfolio-risk-sdk`
- Java: `maven: com.beautyinvest:portfolio-risk-sdk`

---

## Support

- **Documentation**: https://docs.beautyinvest.com
- **API Status**: https://status.beautyinvest.com
- **Support Email**: api-support@beautyinvest.com
- **Slack Community**: https://slack.beautyinvest.com

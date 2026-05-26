# Developer Quick Reference Guide

**Project**: AI-Driven Real-Time Portfolio Risk Alert System  
**Date**: May 26, 2026  
**Version**: 1.0.0

---

## 🚀 Quick Start (5 Minutes)

### 1. Clone & Setup
```bash
# Clone repository
git clone <repo-url>
cd portfolio-risk-system

# Backend setup
mvn clean install
mvn spring-boot:run

# Frontend setup (in new terminal)
cd frontend
npm install
npm run dev
```

### 2. Verify Installation
```bash
# Backend running on http://localhost:8081
curl http://localhost:8081/api/v1/market/prices

# Frontend running on http://localhost:5173
# Open browser to http://localhost:5173
```

### 3. Test APIs
```bash
# Get all market prices
curl http://localhost:8081/api/v1/market/prices

# Get risk summary
curl http://localhost:8081/api/v1/risk/summary

# Get client risk alerts
curl http://localhost:8081/api/v1/risk/clients/1
```

---

## 📁 Project Structure

### Backend
```
src/main/java/com/ramya/portfoliorisksystem/
├── config/
│   ├── DynamoDBConfig.java
│   ├── OpenAIConfig.java
│   └── OpenAIProperties.java
├── controller/
│   ├── MarketDataController.java
│   ├── PortfolioController.java
│   └── RiskController.java
├── model/
│   ├── Portfolio.java
│   ├── RiskAlert.java
│   ├── Stock.java
│   └── ...
├── service/
│   ├── AIServiceEnhanced.java
│   ├── EventPublishingService.java
│   ├── MarketDataSimulationService.java
│   ├── RiskDetectionService.java
│   └── ...
├── seed/
│   └── DataSeeder.java
├── util/
│   └── ...
└── PortfolioRiskSystemApplication.java
```

### Frontend
```
frontend/src/
├── pages/
│   ├── HomePage.jsx
│   ├── MarketPage.jsx
│   ├── ClientPage.jsx
│   ├── AIPage.jsx
│   └── PortfolioBuilderPage.jsx
├── components/
│   ├── cards/
│   │   ├── AIInsightCard.jsx
│   │   ├── DashboardSummaryCard.jsx
│   │   ├── LeaderboardCard.jsx
│   │   ├── PortfolioValueCard.jsx
│   │   ├── RiskAlertsCard.jsx
│   │   └── TopRiskExposuresCard.jsx
│   ├── charts/
│   │   ├── HoldingsChart.jsx
│   │   └── MarketPricesChart.jsx
│   ├── forms/
│   │   ├── ClientSelector.jsx
│   │   └── PortfolioCalculatorForm.jsx
│   ├── layout/
│   │   └── Header.jsx
│   ├── ui/
│   │   ├── Card.jsx
│   │   ├── ErrorAlert.jsx
│   │   ├── LoadingSpinner.jsx
│   │   └── TickerHoverCard.jsx
│   ├── Navbar.jsx
│   └── Ticker.jsx
├── api/
│   ├── client.js
│   └── portfolioApi.js
├── hooks/
│   └── useAsync.js
├── utils/
│   └── formatters.js
├── assets/
│   ├── logos/
│   │   └── brandLogos.js
│   ├── hero.png
│   ├── react.svg
│   └── vite.svg
├── App.jsx
├── main.jsx
└── index.css
```

---

## 🔧 Common Commands

### Backend
```bash
# Build
mvn clean install

# Run
mvn spring-boot:run

# Run tests
mvn test

# Run specific test
mvn test -Dtest=RiskDetectionServiceTest

# Build JAR
mvn clean package

# Run JAR
java -jar target/portfolio-risk-system-1.0.0.jar
```

### Frontend
```bash
# Install dependencies
npm install

# Development server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview

# Lint code
npm run lint
```

---

## 📚 Key Files to Know

### Backend Services
| File | Purpose | Key Methods |
|------|---------|-------------|
| EventPublishingService.java | Publish events | publishPriceUpdated(), publishRiskThresholdBreached() |
| RiskDetectionService.java | Detect risks | detectRisks(), detectConcentrationRisk() |
| MarketDataSimulationService.java | Simulate prices | simulatePriceUpdates(), updateStockPrice() |
| AIServiceEnhanced.java | Generate insights | generateInsight(), generateInsightViaOpenAI() |

### Frontend Components
| File | Purpose | Props |
|------|---------|-------|
| HomePage.jsx | Dashboard | - |
| MarketPage.jsx | Market data | - |
| ClientPage.jsx | Client management | - |
| AIPage.jsx | AI insights | - |
| PortfolioBuilderPage.jsx | Portfolio builder | - |

### API Endpoints
| Endpoint | Method | Purpose |
|----------|--------|---------|
| /api/v1/risk/summary | GET | Get firm-wide risk summary |
| /api/v1/risk/clients/{id} | GET | Get client risk alerts |
| /api/v1/market/prices | GET | Get all market prices |
| /api/v1/market/stocks | GET | Get all stocks |

---

## 🎯 Development Workflow

### Adding a New Feature

#### Backend
1. Create model class in `model/`
2. Create service in `service/`
3. Create controller in `controller/`
4. Add tests in `src/test/`
5. Update documentation

#### Frontend
1. Create component in `components/`
2. Create page in `pages/` (if needed)
3. Add API calls in `api/`
4. Add styling in component or `index.css`
5. Update routing in `App.jsx`

### Example: Adding a New Risk Alert Type

**Backend**:
```java
// 1. Add to RiskDetectionService
public void detectNewRisk() {
    // Detection logic
}

// 2. Add to EventPublishingService
public void publishNewRiskEvent() {
    // Publishing logic
}

// 3. Add endpoint to RiskController
@GetMapping("/new-risk")
public ResponseEntity<?> getNewRisk() {
    // Return data
}
```

**Frontend**:
```jsx
// 1. Create component
function NewRiskCard() {
  return <div>New Risk Alert</div>;
}

// 2. Add to page
function HomePage() {
  return <NewRiskCard />;
}

// 3. Add API call
export async function fetchNewRisk() {
  return client.get('/api/v1/risk/new-risk');
}
```

---

## 🐛 Debugging Tips

### Backend Debugging
```bash
# Enable debug logging
export DEBUG=true
mvn spring-boot:run

# Check logs
tail -f logs/application.log

# Test endpoint with curl
curl -X GET http://localhost:8081/api/v1/risk/summary

# Test with verbose output
curl -v http://localhost:8081/api/v1/risk/summary
```

### Frontend Debugging
```bash
# Open browser DevTools (F12)
# Check Console tab for errors
# Check Network tab for API calls
# Use React DevTools extension

# Check component state
console.log(state);

# Check API response
fetch('/api/v1/risk/summary')
  .then(r => r.json())
  .then(d => console.log(d));
```

### Common Issues

| Issue | Solution |
|-------|----------|
| Port 8081 already in use | `lsof -i :8081` then `kill -9 <PID>` |
| Port 5173 already in use | `lsof -i :5173` then `kill -9 <PID>` |
| CORS errors | Check API client configuration |
| API not responding | Check backend is running on 8081 |
| Frontend not updating | Check API endpoints in browser DevTools |

---

## 📊 API Reference

### Risk Endpoints

#### Get Risk Summary
```bash
GET /api/v1/risk/summary

Response:
{
  "totalClients": 100,
  "highRiskClients": 5,
  "mediumRiskClients": 15,
  "lowRiskClients": 80,
  "totalAUM": 50000000
}
```

#### Get Client Risk Alerts
```bash
GET /api/v1/risk/clients/{clientId}

Response:
{
  "clientId": "1",
  "alerts": [
    {
      "type": "CONCENTRATION_RISK",
      "severity": "HIGH",
      "message": "Portfolio overweight in AAPL"
    }
  ]
}
```

#### Get Concentration Risk
```bash
GET /api/v1/risk/clients/{clientId}/concentration

Response:
{
  "clientId": "1",
  "concentrationRisk": {
    "AAPL": 25.5,
    "MSFT": 18.2,
    "GOOGL": 15.3
  }
}
```

### Market Endpoints

#### Get All Prices
```bash
GET /api/v1/market/prices

Response:
{
  "prices": {
    "AAPL": 150.25,
    "MSFT": 320.50,
    "GOOGL": 140.75
  }
}
```

#### Get Specific Price
```bash
GET /api/v1/market/prices/{symbol}

Response:
{
  "symbol": "AAPL",
  "price": 150.25,
  "change": 2.5,
  "changePercent": 1.69
}
```

#### Get All Stocks
```bash
GET /api/v1/market/stocks

Response:
{
  "stocks": [
    {
      "symbol": "AAPL",
      "name": "Apple Inc.",
      "sector": "Technology"
    }
  ]
}
```

---

## 🔐 Security Checklist

- [ ] Never commit secrets to git
- [ ] Use environment variables for sensitive data
- [ ] Validate all user inputs
- [ ] Use HTTPS in production
- [ ] Implement rate limiting
- [ ] Use strong authentication
- [ ] Encrypt sensitive data
- [ ] Log security events
- [ ] Regular security audits

---

## 📈 Performance Tips

### Backend
- Use caching for frequently accessed data
- Implement pagination for large datasets
- Use connection pooling
- Monitor database performance
- Use async processing for long operations

### Frontend
- Lazy load components
- Optimize images
- Minimize bundle size
- Use React.memo for expensive components
- Implement virtual scrolling for large lists

---

## 🧪 Testing

### Backend Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=RiskDetectionServiceTest

# Run specific test method
mvn test -Dtest=RiskDetectionServiceTest#testConcentrationRisk

# Run with coverage
mvn test jacoco:report
```

### Frontend Tests
```bash
# Run tests (when configured)
npm test

# Run tests in watch mode
npm test -- --watch

# Run tests with coverage
npm test -- --coverage
```

---

## 📚 Documentation Links

### Quick References
- [QUICK_START.md](QUICK_START.md) - 5-minute setup
- [API_CONTRACT.md](API_CONTRACT.md) - API specification
- [EVENT_SCHEMAS.md](EVENT_SCHEMAS.md) - Event definitions

### Detailed Guides
- [ARCHITECTURE.md](ARCHITECTURE.md) - System design
- [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md) - Deployment
- [PHASE2_IMPLEMENTATION.md](PHASE2_IMPLEMENTATION.md) - Implementation details

### Status & Planning
- [CURRENT_STATUS_REPORT.md](CURRENT_STATUS_REPORT.md) - Current state
- [PHASE3_RESUMPTION_PLAN.md](PHASE3_RESUMPTION_PLAN.md) - Phase 3 plan
- [PHASE3_START_HERE.md](PHASE3_START_HERE.md) - Phase 3 getting started

---

## 🎯 Development Checklist

### Before Starting
- [ ] Read QUICK_START.md
- [ ] Set up development environment
- [ ] Review ARCHITECTURE.md
- [ ] Review API_CONTRACT.md

### During Development
- [ ] Write tests
- [ ] Follow code style
- [ ] Add logging
- [ ] Update documentation
- [ ] Test locally

### Before Committing
- [ ] Run tests
- [ ] Check code style
- [ ] Update documentation
- [ ] Review changes
- [ ] Commit with clear message

### Before Deploying
- [ ] All tests passing
- [ ] Code review approved
- [ ] Documentation updated
- [ ] Performance tested
- [ ] Security audit passed

---

## 🚀 Deployment Checklist

### Pre-Deployment
- [ ] All tests passing
- [ ] Code review approved
- [ ] Documentation updated
- [ ] Performance tested
- [ ] Security audit passed

### Deployment
- [ ] Build production JAR
- [ ] Deploy to staging
- [ ] Run smoke tests
- [ ] Deploy to production
- [ ] Monitor logs

### Post-Deployment
- [ ] Verify all services running
- [ ] Check API endpoints
- [ ] Monitor performance
- [ ] Check error logs
- [ ] Notify team

---

## 📞 Getting Help

### Documentation
- Check [QUICK_START.md](QUICK_START.md) for setup issues
- Check [API_CONTRACT.md](API_CONTRACT.md) for API questions
- Check [ARCHITECTURE.md](ARCHITECTURE.md) for design questions

### Code Examples
- Review existing services for patterns
- Check test files for examples
- Review documentation for best practices

### External Resources
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [React Documentation](https://react.dev)
- [AWS Documentation](https://docs.aws.amazon.com)
- [OpenAI API Documentation](https://platform.openai.com/docs)

---

## 🎉 You're Ready!

You now have everything you need to:
1. ✅ Set up the development environment
2. ✅ Understand the project structure
3. ✅ Run the application
4. ✅ Test the APIs
5. ✅ Start developing

**Next Step**: Choose your task and start coding!

---

## Sign-Off

**Project**: AI-Driven Real-Time Portfolio Risk Alert System  
**Date**: May 26, 2026  
**Version**: 1.0.0

**Happy coding! 🚀**

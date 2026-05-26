# Quick Start Guide - Portfolio Risk System

## 5-Minute Setup

### Prerequisites
- Java 17+
- Maven 3.8+
- Node.js 18+
- npm

### Step 1: Clone & Build Backend

```bash
# Clone repository
git clone https://github.com/ramya/portfolio-risk-system.git
cd portfolio-risk-system

# Build backend
mvn clean install

# Run backend
mvn spring-boot:run
```

Backend runs on `http://localhost:8081`

### Step 2: Build & Run Frontend

```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev
```

Frontend runs on `http://localhost:5173`

### Step 3: Test the System

```bash
# In a new terminal, test API endpoints

# Get portfolio
curl http://localhost:8081/portfolio

# Get risk alerts
curl http://localhost:8081/api/v1/risk/summary

# Get market prices
curl http://localhost:8081/api/v1/market/prices

# Get client risks
curl http://localhost:8081/api/v1/risk/clients/1
```

### Step 4: Open Frontend

Open browser to `http://localhost:5173`

---

## Key Features

### 1. Real-Time Price Updates
- Stock prices update every 5-10 seconds
- Realistic random walk model
- Volatility: 1-3% per update

### 2. Risk Detection
- **Concentration Risk**: Single stock >20%
- **Allocation Drift**: Deviation from target >5%
- **Portfolio Drop**: Daily loss >3%

### 3. AI-Powered Insights
- Professional explanations
- Actionable recommendations
- Risk disclaimers included

### 4. Professional Dashboard
- Portfolio overview
- Risk alerts
- AI insights
- Market data
- Client leaderboard

---

## API Endpoints

### Portfolio
```
GET  /portfolio                          - Get default portfolio
GET  /api/v1/portfolios/{clientId}       - Get client portfolio
GET  /api/v1/portfolios/{clientId}/allocation - Get allocation breakdown
```

### Risk
```
GET  /api/v1/risk/clients/{clientId}     - Get client risk alerts
GET  /api/v1/risk/summary                - Get firm-wide risk summary
GET  /api/v1/risk/high-risk-clients      - Get high-risk clients
```

### Market
```
GET  /api/v1/market/prices               - Get all prices
GET  /api/v1/market/prices/{symbol}      - Get specific price
GET  /api/v1/market/stocks               - Get all stocks
GET  /api/v1/market/sectors              - Get sector breakdown
```

### Dashboard
```
GET  /dashboard                          - Get dashboard data
GET  /leaderboard                        - Get client leaderboard
```

---

## Testing

### Manual Testing

```bash
# Test concentration risk
curl http://localhost:8081/api/v1/risk/clients/1/concentration

# Test market data
curl http://localhost:8081/api/v1/market/prices/AAPL

# Test AI insight
curl http://localhost:8081/portfolio/ai-insight

# Test dashboard
curl http://localhost:8081/dashboard
```

### Run Unit Tests

```bash
# Backend tests
mvn test

# Frontend tests
cd frontend
npm test
```

---

## Configuration

### Environment Variables

```bash
# OpenAI API Key (optional)
export OPENAI_API_KEY=sk-your-api-key

# Spring Profile
export SPRING_PROFILES_ACTIVE=dev

# AWS Configuration (for AWS deployment)
export AWS_REGION=us-east-1
export AWS_ACCESS_KEY_ID=your-key
export AWS_SECRET_ACCESS_KEY=your-secret
```

### Application Properties

Edit `src/main/resources/application.properties`:

```properties
spring.application.name=portfolio-risk-system
server.port=8081

# OpenAI Configuration
openai.api-key=${OPENAI_API_KEY:}
openai.model=gpt-4o-mini
openai.base-url=https://api.openai.com/v1

# Logging
logging.level.root=INFO
logging.level.com.ramya.portfoliorisksystem=DEBUG
```

---

## Project Structure

```
portfolio-risk-system/
├── src/main/java/com/ramya/portfoliorisksystem/
│   ├── config/                    # Configuration classes
│   │   ├── DynamoDBConfig.java
│   │   └── OpenAIProperties.java
│   ├── controller/                # REST controllers
│   │   ├── PortfolioController.java
│   │   ├── RiskController.java
│   │   └── MarketDataController.java
│   ├── service/                   # Business logic
│   │   ├── PortfolioService.java
│   │   ├── RiskDetectionService.java
│   │   ├── MarketDataSimulationService.java
│   │   ├── AIServiceEnhanced.java
│   │   └── EventPublishingService.java
│   ├── model/                     # Data models
│   │   ├── Client.java
│   │   ├── Portfolio.java
│   │   ├── RiskAlert.java
│   │   └── AIInsight.java
│   └── PortfolioRiskSystemApplication.java
├── frontend/
│   ├── src/
│   │   ├── pages/                 # React pages
│   │   ├── components/            # React components
│   │   ├── api/                   # API client
│   │   └── App.jsx
│   └── package.json
├── pom.xml                        # Maven configuration
├── ARCHITECTURE.md                # Architecture documentation
├── EVENT_SCHEMAS.md               # Event definitions
├── API_CONTRACT.md                # API specification
├── PHASE2_IMPLEMENTATION.md       # Phase 2 details
└── DEPLOYMENT_GUIDE.md            # Deployment instructions
```

---

## Common Tasks

### Add a New Stock

Edit `src/main/java/com/ramya/portfoliorisksystem/seeder/BrandSeeder.java`:

```java
stocks.add(new Stock("NEWSTOCK", "New Company", "Technology", 100.00));
```

### Change Risk Thresholds

Edit `src/main/java/com/ramya/portfoliorisksystem/service/RiskDetectionService.java`:

```java
private static final double CONCENTRATION_THRESHOLD = 25.0;  // Changed from 20%
```

### Modify Price Simulation

Edit `src/main/java/com/ramya/portfoliorisksystem/service/MarketDataSimulationService.java`:

```java
private static final double VOLATILITY_MAX = 0.05;  // Changed from 0.03 (5% instead of 3%)
```

### Update AI Prompt

Edit `src/main/java/com/ramya/portfoliorisksystem/service/AIServiceEnhanced.java`:

```java
private String buildPrompt(...) {
    return String.format("""
        Your custom prompt here...
    """, ...);
}
```

---

## Troubleshooting

### Backend Won't Start

```bash
# Check Java version
java -version

# Check port 8081 is available
lsof -i :8081

# Check Maven installation
mvn -version

# Clean and rebuild
mvn clean install -DskipTests
```

### Frontend Won't Start

```bash
# Check Node version
node -version

# Clear npm cache
npm cache clean --force

# Reinstall dependencies
rm -rf node_modules package-lock.json
npm install

# Check port 5173 is available
lsof -i :5173
```

### API Calls Failing

```bash
# Check backend is running
curl http://localhost:8081/portfolio

# Check CORS is enabled
curl -H "Origin: http://localhost:5173" http://localhost:8081/portfolio

# Check logs
tail -f logs/application.log
```

### Price Updates Not Happening

```bash
# Check scheduled tasks are enabled
# Verify @EnableScheduling is on main application class

# Check logs for price update messages
grep "Price updated" logs/application.log

# Manually trigger price update
curl -X POST http://localhost:8081/api/v1/market/reset-prices
```

---

## Next Steps

1. **Explore the Dashboard**: Open http://localhost:5173 and navigate through pages
2. **Review API Documentation**: See `API_CONTRACT.md`
3. **Understand Architecture**: See `ARCHITECTURE.md`
4. **Deploy to AWS**: See `DEPLOYMENT_GUIDE.md`
5. **Customize for Your Needs**: Modify stocks, thresholds, and AI prompts

---

## Performance Tips

### Local Development

- Use `mvn spring-boot:run` for fast reload
- Use `npm run dev` for hot module replacement
- Check logs in real-time: `tail -f logs/application.log`

### Production

- Enable caching in API Gateway
- Use DynamoDB on-demand billing
- Enable CloudFront for frontend
- Set up CloudWatch alarms
- Use Lambda reserved concurrency

---

## Support

- **Documentation**: See `*.md` files in root directory
- **API Docs**: See `API_CONTRACT.md`
- **Architecture**: See `ARCHITECTURE.md`
- **Issues**: Check logs and troubleshooting section

---

## What's Next?

### Phase 3: Event System
- EventBridge rules configuration
- SQS dead-letter queues
- Event replay capability

### Phase 4: AI Integration
- OpenAI API integration
- Advanced prompt engineering
- Response caching

### Phase 5: Frontend Enhancement
- Real-time WebSocket updates
- Advanced charting
- Risk visualization

### Phase 6: Testing & Deployment
- Unit tests
- Integration tests
- CloudFormation templates
- CI/CD pipeline

---

## License

MIT License - See LICENSE file

---

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

---

## Contact

- **Email**: support@beautyinvest.com
- **Website**: https://beautyinvest.com
- **GitHub**: https://github.com/ramya/portfolio-risk-system

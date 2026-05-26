# Phase 3 Implementation Guide - Practical Roadmap

**Date**: May 26, 2026  
**Project**: AI-Driven Real-Time Portfolio Risk Alert System  
**Purpose**: Detailed, actionable implementation guide for Phase 3  

---

## Quick Reference

### What's Already Done ✅
- 7 backend microservices (2,500+ LOC)
- 11 REST API endpoints
- 5 event types defined
- Event publishing infrastructure
- Risk detection algorithms
- Database schema
- Frontend structure (5 pages)
- Comprehensive documentation

### What Needs to Be Done 🔄
- Event system deployment (EventBridge, SQS, Lambda)
- Frontend luxury UI (BLACK + GOLD theme)
- Real-time data updates
- OpenAI API integration
- Comprehensive testing
- AWS deployment

### Timeline
- Week 1: Event System (2 weeks effort)
- Week 2: Frontend Foundation (2-3 weeks effort)
- Week 3: AI Integration (1-2 weeks effort)
- Week 4: Testing & Deployment (1 week effort)

---

## Week 1: Event System Deployment

### Day 1-2: EventBridge Configuration

**Goal**: Set up event routing infrastructure

**Files to Create**:
```
src/main/java/com/ramya/portfoliorisksystem/config/EventBridgeConfig.java
src/main/resources/eventbridge-rules.json
src/main/resources/eventbridge-patterns.json
```

**Implementation Steps**:

1. Create EventBridgeConfig class:
```java
@Configuration
public class EventBridgeConfig {
    
    @Bean
    public EventBridgeClient eventBridgeClient() {
        return EventBridgeClient.builder()
            .region(Region.US_EAST_1)
            .build();
    }
    
    public void createEventBus() {
        // Create custom event bus for portfolio risk system
    }
    
    public void createEventRules() {
        // Rule 1: PriceUpdated → Risk Service
        // Rule 2: RiskThresholdBreached → AI Service
        // Rule 3: AIInsightGenerated → Notification Service
    }
}
```

2. Define event patterns:
```json
{
  "rules": [
    {
      "name": "route-price-updated",
      "pattern": {
        "source": ["market-data-service"],
        "detail-type": ["PriceUpdated"]
      },
      "targets": ["risk-service-lambda"]
    },
    {
      "name": "route-risk-to-ai",
      "pattern": {
        "source": ["risk-service"],
        "detail-type": ["RiskThresholdBreached"]
      },
      "targets": ["ai-service-lambda"]
    }
  ]
}
```

3. Set up dead-letter queues:
```java
public void createDeadLetterQueues() {
    // Create DLQ for failed events
    // Configure DLQ retention (24 hours)
    // Set up CloudWatch alarms
}
```

**Testing**:
- Publish test events
- Verify routing
- Check dead-letter queue

**Deliverable**: EventBridge rules configured and tested

---

### Day 3-4: SQS Queue Setup

**Goal**: Create message queues for service decoupling

**Files to Create**:
```
src/main/java/com/ramya/portfoliorisksystem/config/SQSConfig.java
src/main/resources/sqs-queues.json
```

**Implementation Steps**:

1. Create SQSConfig class:
```java
@Configuration
public class SQSConfig {
    
    @Bean
    public SqsClient sqsClient() {
        return SqsClient.builder()
            .region(Region.US_EAST_1)
            .build();
    }
    
    public void createQueues() {
        // risk-detection-queue
        // ai-insights-queue
        // notification-queue
        // Each with DLQ
    }
}
```

2. Configure queue policies:
```json
{
  "queues": [
    {
      "name": "risk-detection-queue",
      "visibilityTimeout": 300,
      "messageRetentionPeriod": 86400,
      "deadLetterTargetArn": "arn:aws:sqs:us-east-1:123456789:risk-detection-dlq"
    }
  ]
}
```

3. Set up message processing:
```java
@Service
public class QueueMessageProcessor {
    
    @Autowired
    private SqsClient sqsClient;
    
    @Scheduled(fixedDelay = 1000)
    public void processMessages() {
        // Poll queue
        // Process messages
        // Delete on success
        // Send to DLQ on failure
    }
}
```

**Testing**:
- Send test messages
- Verify processing
- Check DLQ handling

**Deliverable**: SQS queues created and operational

---

### Day 5: Lambda Packaging

**Goal**: Package services as Lambda functions

**Files to Create**:
```
src/main/java/com/ramya/portfoliorisksystem/lambda/RiskDetectionHandler.java
src/main/java/com/ramya/portfoliorisksystem/lambda/AIServiceHandler.java
src/main/java/com/ramya/portfoliorisksystem/lambda/NotificationHandler.java
scripts/package-lambda.sh
scripts/deploy-lambda.sh
```

**Implementation Steps**:

1. Create Lambda handlers:
```java
public class RiskDetectionHandler implements RequestHandler<EventBridgeEvent, String> {
    
    @Override
    public String handleRequest(EventBridgeEvent event, Context context) {
        // Extract event data
        // Call RiskDetectionService
        // Publish RiskThresholdBreached event
        // Return success/failure
        return "Success";
    }
}
```

2. Create packaging script:
```bash
#!/bin/bash
# Build JAR
mvn clean package -DskipTests

# Create Lambda deployment package
mkdir -p lambda-package
cp target/portfolio-risk-system-0.0.1-SNAPSHOT.jar lambda-package/
cd lambda-package
jar xf portfolio-risk-system-0.0.1-SNAPSHOT.jar
rm portfolio-risk-system-0.0.1-SNAPSHOT.jar
zip -r ../lambda-deployment.zip .
```

3. Create deployment script:
```bash
#!/bin/bash
# Deploy to AWS Lambda
aws lambda create-function \
  --function-name risk-detection-service \
  --runtime java17 \
  --role arn:aws:iam::123456789:role/lambda-execution-role \
  --handler com.ramya.portfoliorisksystem.lambda.RiskDetectionHandler \
  --zip-file fileb://lambda-deployment.zip
```

**Testing**:
- Test local execution
- Test AWS deployment
- Verify event processing

**Deliverable**: Lambda functions packaged and deployed

---

## Week 2: Frontend Enhancement

### Day 1-2: Luxury UI Components

**Goal**: Create reusable BLACK + GOLD components

**Files to Create/Modify**:
```
frontend/src/components/ui/Card.jsx (ENHANCE)
frontend/src/components/ui/Button.jsx (NEW)
frontend/src/components/ui/Badge.jsx (NEW)
frontend/src/components/ui/Alert.jsx (NEW)
frontend/src/components/ui/Gradient.jsx (NEW)
frontend/src/index.css (ENHANCE)
```

**Color Palette**:
```css
:root {
  --color-black: #000000;
  --color-gold: #D4AF37;
  --color-gold-light: #E8D4A8;
  --color-gold-dark: #B8860B;
  --color-bg-dark: #0a0a0a;
  --color-text-light: #f5f5f5;
  --color-accent: #D4AF37;
}
```

**Implementation**:

1. Enhanced Card component:
```jsx
export function Card({ children, className = "", hover = true }) {
  return (
    <div className={`
      bg-black border border-gold rounded-lg p-6
      ${hover ? 'hover:border-gold-light hover:shadow-lg' : ''}
      transition-all duration-300
      ${className}
    `}>
      {children}
    </div>
  );
}
```

2. Button component:
```jsx
export function Button({ children, variant = "primary", ...props }) {
  const variants = {
    primary: "bg-gold text-black hover:bg-gold-light",
    secondary: "border border-gold text-gold hover:bg-gold hover:text-black",
    ghost: "text-gold hover:bg-gold hover:bg-opacity-10"
  };
  
  return (
    <button className={`
      px-4 py-2 rounded-lg font-semibold
      transition-all duration-300
      ${variants[variant]}
    `} {...props}>
      {children}
    </button>
  );
}
```

3. Badge component:
```jsx
export function Badge({ children, severity = "low" }) {
  const colors = {
    low: "bg-green-900 text-green-200",
    medium: "bg-yellow-900 text-yellow-200",
    high: "bg-orange-900 text-orange-200",
    critical: "bg-red-900 text-red-200"
  };
  
  return (
    <span className={`
      px-3 py-1 rounded-full text-sm font-semibold
      ${colors[severity]}
    `}>
      {children}
    </span>
  );
}
```

**Testing**:
- Visual inspection
- Responsive testing
- Animation smoothness

**Deliverable**: Reusable UI component library

---

### Day 3-4: Dashboard Enhancement

**Goal**: Build real-time dashboard

**Files to Modify**:
```
frontend/src/pages/HomePage.jsx
frontend/src/components/cards/DashboardSummaryCard.jsx
frontend/src/components/cards/RiskAlertsCard.jsx
frontend/src/hooks/useRealTimeData.js (NEW)
```

**Implementation**:

1. Real-time data hook:
```jsx
export function useRealTimeData(interval = 5000) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await fetch('/api/v1/risk/summary');
        const result = await response.json();
        setData(result);
        setError(null);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };
    
    fetchData();
    const intervalId = setInterval(fetchData, interval);
    return () => clearInterval(intervalId);
  }, [interval]);
  
  return { data, loading, error };
}
```

2. Enhanced HomePage:
```jsx
export default function HomePage() {
  const { data, loading, error } = useRealTimeData();
  
  if (loading) return <LoadingSpinner />;
  if (error) return <ErrorAlert message={error} />;
  
  return (
    <div className="space-y-6">
      <DashboardSummaryCard data={data} />
      <RiskAlertsCard alerts={data?.alerts} />
      <TopRiskExposuresCard exposures={data?.exposures} />
      <AIInsightCard insight={data?.latestInsight} />
    </div>
  );
}
```

**Testing**:
- Verify data updates every 5 seconds
- Check error handling
- Monitor performance

**Deliverable**: Functional dashboard with real-time updates

---

### Day 5: Market Page

**Goal**: Build live market ticker

**Files to Modify**:
```
frontend/src/pages/MarketPage.jsx
frontend/src/components/Ticker.jsx
frontend/src/components/ui/TickerHoverCard.jsx
frontend/src/components/charts/MarketPricesChart.jsx
```

**Implementation**:

1. Live ticker component:
```jsx
export function Ticker() {
  const [prices, setPrices] = useState({});
  const [hoveredSymbol, setHoveredSymbol] = useState(null);
  
  useEffect(() => {
    const fetchPrices = async () => {
      const response = await fetch('/api/v1/market/prices');
      const data = await response.json();
      setPrices(data.prices);
    };
    
    fetchPrices();
    const interval = setInterval(fetchPrices, 5000);
    return () => clearInterval(interval);
  }, []);
  
  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
      {Object.entries(prices).map(([symbol, price]) => (
        <TickerCard
          key={symbol}
          symbol={symbol}
          price={price}
          onHover={() => setHoveredSymbol(symbol)}
        />
      ))}
      {hoveredSymbol && <TickerHoverCard symbol={hoveredSymbol} />}
    </div>
  );
}
```

2. Ticker card:
```jsx
function TickerCard({ symbol, price, onHover }) {
  const change = price.change;
  const changePercent = price.changePercent;
  const isPositive = change >= 0;
  
  return (
    <Card
      className="cursor-pointer"
      onMouseEnter={onHover}
    >
      <div className="flex justify-between items-center">
        <div>
          <h3 className="text-gold font-bold">{symbol}</h3>
          <p className="text-2xl font-bold text-white">${price.price.toFixed(2)}</p>
        </div>
        <div className={isPositive ? "text-green-400" : "text-red-400"}>
          <p>{isPositive ? "+" : ""}{change.toFixed(2)}</p>
          <p>{isPositive ? "+" : ""}{changePercent.toFixed(2)}%</p>
        </div>
      </div>
    </Card>
  );
}
```

**Testing**:
- Verify price updates
- Check hover functionality
- Monitor performance

**Deliverable**: Functional market page with live ticker

---

## Week 3: AI Integration

### Day 1-2: OpenAI API Integration

**Goal**: Integrate OpenAI API for insights

**Files to Create**:
```
src/main/java/com/ramya/portfoliorisksystem/service/OpenAIService.java
src/main/java/com/ramya/portfoliorisksystem/config/OpenAIConfig.java
src/main/resources/application-openai.properties
```

**Implementation**:

1. OpenAI configuration:
```java
@Configuration
public class OpenAIConfig {
    
    @Value("${openai.api.key}")
    private String apiKey;
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    @Bean
    public OpenAIService openAIService() {
        return new OpenAIService(apiKey, restTemplate());
    }
}
```

2. OpenAI service:
```java
@Service
public class OpenAIService {
    
    private final String apiKey;
    private final RestTemplate restTemplate;
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    
    public String generateInsight(String prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, Object> body = new HashMap<>();
            body.put("model", "gpt-4o-mini");
            body.put("messages", List.of(
                Map.of("role", "user", "content", prompt)
            ));
            body.put("temperature", 0.7);
            body.put("max_tokens", 500);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(API_URL, request, Map.class);
            
            List<Map> choices = (List<Map>) response.getBody().get("choices");
            Map message = (Map) choices.get(0).get("message");
            return (String) message.get("content");
        } catch (Exception e) {
            logger.error("OpenAI API error", e);
            return generateFallbackInsight(prompt);
        }
    }
    
    private String generateFallbackInsight(String prompt) {
        // Rule-based fallback logic
        return "Unable to generate AI insight at this time. Please try again later.";
    }
}
```

**Testing**:
- Test API calls
- Test error handling
- Test fallback logic

**Deliverable**: Working OpenAI integration

---

### Day 3-4: Prompt Engineering

**Goal**: Create professional prompts

**Files to Create**:
```
src/main/java/com/ramya/portfoliorisksystem/service/PromptService.java
src/main/resources/prompts/risk-explanation.txt
src/main/resources/prompts/recommendation.txt
```

**Implementation**:

1. Prompt service:
```java
@Service
public class PromptService {
    
    public String buildRiskExplanationPrompt(RiskAlert alert) {
        return String.format("""
            Analyze this portfolio risk alert and provide a professional explanation:
            
            Risk Type: %s
            Severity: %s
            Current Value: %.2f%%
            Threshold: %.2f%%
            Message: %s
            
            Provide a concise explanation (2-3 sentences) of:
            1. What this risk means
            2. Why it matters
            3. One suggested action
            
            Format as JSON with keys: explanation, suggestedAction, severity
            Include disclaimer: "This is AI-generated guidance only."
            """, alert.getRiskType(), alert.getSeverity(), 
                alert.getCurrentValue(), alert.getThreshold(), alert.getMessage());
    }
    
    public String buildRecommendationPrompt(Client client, List<RiskAlert> alerts) {
        return String.format("""
            Based on this client's portfolio and risk alerts, provide recommendations:
            
            Client: %s
            Portfolio Value: $%.2f
            Active Alerts: %d
            
            Provide 2-3 specific, actionable recommendations to reduce risk.
            Format as JSON with key: recommendations (array of strings)
            """, client.getName(), client.getPortfolioValue(), alerts.size());
    }
}
```

**Testing**:
- Test prompt generation
- Verify response parsing
- Check output quality

**Deliverable**: Professional prompts

---

### Day 5: Response Caching

**Goal**: Implement caching for AI responses

**Files to Create**:
```
src/main/java/com/ramya/portfoliorisksystem/service/CacheService.java
src/main/java/com/ramya/portfoliorisksystem/config/CacheConfig.java
```

**Implementation**:

1. Cache configuration:
```java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("aiInsights", "riskAlerts");
    }
}
```

2. Cache service:
```java
@Service
public class CacheService {
    
    @Cacheable(value = "aiInsights", key = "#clientId + '-' + #riskType")
    public String getOrGenerateInsight(String clientId, String riskType, RiskAlert alert) {
        return openAIService.generateInsight(buildPrompt(alert));
    }
    
    @CacheEvict(value = "aiInsights", key = "#clientId + '-' + #riskType")
    public void invalidateInsightCache(String clientId, String riskType) {
        // Cache invalidated
    }
}
```

**Testing**:
- Verify caching works
- Check cache hit rate
- Monitor performance improvement

**Deliverable**: Caching system

---

## Week 4: Testing & Deployment

### Day 1: Unit Testing

**Goal**: Write comprehensive unit tests

**Files to Create**:
```
src/test/java/com/ramya/portfoliorisksystem/service/RiskDetectionServiceTest.java
src/test/java/com/ramya/portfoliorisksystem/service/AIServiceTest.java
src/test/java/com/ramya/portfoliorisksystem/controller/RiskControllerTest.java
```

**Example Test**:
```java
@SpringBootTest
class RiskDetectionServiceTest {
    
    @Autowired
    private RiskDetectionService riskDetectionService;
    
    @Test
    void testConcentrationRiskDetection() {
        Client client = createTestClient();
        List<RiskAlert> alerts = riskDetectionService.detectRisks(client);
        
        assertNotNull(alerts);
        assertTrue(alerts.stream()
            .anyMatch(a -> "CONCENTRATION".equals(a.getRiskType())));
    }
}
```

**Target**: 80%+ code coverage

---

### Day 2: Integration Testing

**Goal**: Test service integration

**Files to Create**:
```
src/test/java/com/ramya/portfoliorisksystem/integration/EventSystemTest.java
src/test/java/com/ramya/portfoliorisksystem/integration/EndToEndTest.java
```

---

### Day 3: Load Testing

**Goal**: Test system performance

**Files to Create**:
```
scripts/load-test.sh
src/test/java/com/ramya/portfoliorisksystem/LoadTest.java
```

---

### Day 4-5: AWS Deployment

**Goal**: Deploy to AWS

**Files to Create**:
```
infrastructure/cloudformation/main.yaml
infrastructure/cloudformation/eventbridge.yaml
infrastructure/cloudformation/sqs.yaml
infrastructure/cloudformation/lambda.yaml
scripts/deploy-aws.sh
```

---

## Success Checklist

### Week 1 ✅
- [ ] EventBridge rules created
- [ ] SQS queues set up
- [ ] Lambda functions packaged
- [ ] Events flowing end-to-end

### Week 2 ✅
- [ ] Luxury UI components created
- [ ] Dashboard displaying real-time data
- [ ] Market page with live ticker
- [ ] All pages responsive

### Week 3 ✅
- [ ] OpenAI API integrated
- [ ] Prompts generating quality responses
- [ ] Caching working
- [ ] Error handling robust

### Week 4 ✅
- [ ] 80%+ code coverage
- [ ] All tests passing
- [ ] Load testing successful
- [ ] Deployed to AWS

---

## Common Issues & Solutions

| Issue | Solution |
|-------|----------|
| EventBridge events not routing | Check event pattern matching, verify IAM permissions |
| Lambda timeout | Increase timeout, optimize code, check dependencies |
| Frontend not updating | Check API endpoints, verify CORS, check network tab |
| OpenAI rate limiting | Implement caching, queue requests, use fallback logic |
| DynamoDB throttling | Enable auto-scaling, optimize queries |

---

## Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [React Documentation](https://react.dev)
- [AWS Documentation](https://docs.aws.amazon.com)
- [OpenAI API Documentation](https://platform.openai.com/docs)


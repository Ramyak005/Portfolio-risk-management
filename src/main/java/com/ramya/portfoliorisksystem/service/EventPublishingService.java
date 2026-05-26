package com.ramya.portfoliorisksystem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * EventPublishingService
 * 
 * Publishes events to EventBridge for event-driven architecture.
 * In production, this would integrate with AWS EventBridge.
 * For local development, events are logged and stored in memory.
 * 
 * Event Types:
 * - PriceUpdated: Stock price changes
 * - PortfolioRevalued: Portfolio value changes
 * - RiskThresholdBreached: Risk condition detected
 * - AIInsightGenerated: AI insight created
 */
@Service
public class EventPublishingService {

    private static final Logger logger = LoggerFactory.getLogger(EventPublishingService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Publishes a PriceUpdated event
     */
    public void publishPriceUpdated(String symbol, double price, double previousPrice, 
                                    double dayHigh, double dayLow, double openPrice, long volume) {
        Map<String, Object> data = new HashMap<>();
        data.put("symbol", symbol);
        data.put("price", price);
        data.put("previousPrice", previousPrice);
        data.put("change", price - previousPrice);
        data.put("changePercent", ((price - previousPrice) / previousPrice) * 100);
        data.put("dayHigh", dayHigh);
        data.put("dayLow", dayLow);
        data.put("openPrice", openPrice);
        data.put("volume", volume);

        publishEvent("PriceUpdated", "market-data-service", data);
    }

    /**
     * Publishes a PortfolioRevalued event
     */
    public void publishPortfolioRevalued(String clientId, String clientName, double portfolioValue,
                                         double previousValue, Map<String, Integer> holdings,
                                         Map<String, Double> allocation, double diversificationScore,
                                         String topRiskExposure) {
        Map<String, Object> data = new HashMap<>();
        data.put("clientId", clientId);
        data.put("clientName", clientName);
        data.put("portfolioValue", portfolioValue);
        data.put("previousValue", previousValue);
        data.put("change", portfolioValue - previousValue);
        data.put("changePercent", ((portfolioValue - previousValue) / previousValue) * 100);
        data.put("holdings", holdings);
        data.put("allocation", allocation);
        data.put("diversificationScore", diversificationScore);
        data.put("topRiskExposure", topRiskExposure);

        publishEvent("PortfolioRevalued", "portfolio-service", data);
    }

    /**
     * Publishes a RiskThresholdBreached event
     */
    public void publishRiskThresholdBreached(String clientId, String clientName, String riskType,
                                             String severity, double threshold, double currentValue,
                                             String message, java.util.List<String> affectedAssets,
                                             double portfolioValue, double exposureValue) {
        Map<String, Object> data = new HashMap<>();
        data.put("clientId", clientId);
        data.put("clientName", clientName);
        data.put("riskType", riskType);
        data.put("severity", severity);
        data.put("threshold", threshold);
        data.put("currentValue", currentValue);
        data.put("message", message);
        data.put("affectedAssets", affectedAssets);
        data.put("portfolioValue", portfolioValue);
        data.put("exposureValue", exposureValue);
        data.put("detectionMethod", "deterministic-algorithm");
        data.put("isRecurring", false);

        publishEvent("RiskThresholdBreached", "risk-service", data);
    }

    /**
     * Publishes an AIInsightGenerated event
     */
    public void publishAIInsightGenerated(String clientId, String riskEventId, String severity,
                                          String explanation, String suggestedAction,
                                          String disclaimer, String model, long processingTimeMs,
                                          Map<String, Integer> tokenUsage, double confidence) {
        Map<String, Object> data = new HashMap<>();
        data.put("clientId", clientId);
        data.put("riskEventId", riskEventId);
        data.put("severity", severity);
        data.put("explanation", explanation);
        data.put("suggestedAction", suggestedAction);
        data.put("disclaimer", disclaimer);
        data.put("model", model);
        data.put("processingTimeMs", processingTimeMs);
        data.put("tokenUsage", tokenUsage);
        data.put("confidence", confidence);

        publishEvent("AIInsightGenerated", "ai-insight-service", data);
    }

    /**
     * Generic event publishing method
     * 
     * In production, this would call AWS EventBridge:
     * 
     * PutEventsRequest request = PutEventsRequest.builder()
     *     .entries(PutEventsRequestEntry.builder()
     *         .source(source)
     *         .detailType(eventType)
     *         .detail(objectMapper.writeValueAsString(event))
     *         .build())
     *     .build();
     * eventBridgeClient.putEvents(request);
     */
    private void publishEvent(String eventType, String source, Map<String, Object> data) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventId", UUID.randomUUID().toString());
            event.put("eventType", eventType);
            event.put("timestamp", Instant.now().toString());
            event.put("source", source);
            event.put("version", "1.0");
            event.put("data", data);

            String eventJson = objectMapper.writeValueAsString(event);
            logger.info("Event published: {} from {}", eventType, source);
            logger.debug("Event payload: {}", eventJson);

            // TODO: In production, publish to EventBridge
            // For now, just log the event
        } catch (Exception e) {
            logger.error("Failed to publish event: {}", eventType, e);
        }
    }
}

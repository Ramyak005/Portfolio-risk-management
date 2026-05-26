package com.ramya.portfoliorisksystem.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.EventBridgeEvent;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequest;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequestEntry;
import software.amazon.awssdk.services.eventbridge.model.PutEventsResponse;

import java.time.Instant;
import java.util.Collections;

/**
 * Lambda Handler for AI Service
 * 
 * Processes RiskThresholdBreached events from EventBridge and:
 * - Generates AI-powered insights
 * - Publishes AIInsightGenerated events
 * - Handles errors and fallback logic
 * 
 * @author Portfolio Risk System
 * @version 1.0.0
 */
public class AIServiceHandler implements RequestHandler<EventBridgeEvent, String> {
    
    private static final Logger logger = LoggerFactory.getLogger(AIServiceHandler.class);
    private static final Gson gson = new Gson();
    private static final EventBridgeClient eventBridgeClient = EventBridgeClient.builder().build();
    
    /**
     * Handle EventBridge event for AI insight generation
     * 
     * @param event EventBridge event containing risk alert
     * @param context Lambda context
     * @return Success or failure message
     */
    @Override
    public String handleRequest(EventBridgeEvent event, Context context) {
        try {
            logger.info("Received risk event: {}", event.getDetail());
            
            // Extract event data
            JsonObject detail = gson.fromJson(event.getDetail(), JsonObject.class);
            String symbol = detail.get("symbol").getAsString();
            String riskType = detail.get("riskType").getAsString();
            String severity = detail.get("severity").getAsString();
            String clientId = detail.get("clientId").getAsString();
            
            logger.info("Generating AI insight for risk: {} - {}", riskType, severity);
            
            // Generate AI insight
            String insight = generateInsight(symbol, riskType, severity);
            
            // Publish AIInsightGenerated event
            publishAIEvent(clientId, symbol, riskType, insight);
            logger.info("AI insight generated and event published");
            
            return "Success";
        } catch (Exception e) {
            logger.error("Error processing risk event", e);
            return "Error: " + e.getMessage();
        }
    }
    
    /**
     * Generate AI-powered insight
     * 
     * @param symbol Stock symbol
     * @param riskType Type of risk
     * @param severity Risk severity
     * @return AI-generated insight
     */
    private String generateInsight(String symbol, String riskType, String severity) {
        try {
            // In production, this would call OpenAI API
            // For now, use rule-based fallback
            
            String explanation = generateExplanation(symbol, riskType, severity);
            String suggestedAction = generateAction(riskType);
            
            JsonObject insight = new JsonObject();
            insight.addProperty("explanation", explanation);
            insight.addProperty("suggestedAction", suggestedAction);
            insight.addProperty("severity", severity);
            insight.addProperty("disclaimer", "This is AI-generated guidance only. Not financial advice.");
            
            return insight.toString();
        } catch (Exception e) {
            logger.error("Error generating insight", e);
            return generateFallbackInsight(symbol, riskType);
        }
    }
    
    /**
     * Generate explanation for risk
     * 
     * @param symbol Stock symbol
     * @param riskType Type of risk
     * @param severity Risk severity
     * @return Explanation text
     */
    private String generateExplanation(String symbol, String riskType, String severity) {
        switch (riskType) {
            case "CONCENTRATION":
                return String.format("Portfolio has excessive concentration in %s (>20%%). " +
                    "This increases portfolio volatility and risk.", symbol);
            case "ALLOCATION_DRIFT":
                return String.format("Portfolio allocation has drifted >5%% from target. " +
                    "%s exposure has changed significantly.", symbol);
            case "PRICE_DROP":
                return String.format("Stock %s has experienced a significant price drop. " +
                    "Portfolio value may be affected.", symbol);
            default:
                return String.format("Risk detected in %s. Please review portfolio allocation.", symbol);
        }
    }
    
    /**
     * Generate suggested action
     * 
     * @param riskType Type of risk
     * @return Suggested action
     */
    private String generateAction(String riskType) {
        switch (riskType) {
            case "CONCENTRATION":
                return "Consider reducing position size to below 20% of portfolio value.";
            case "ALLOCATION_DRIFT":
                return "Rebalance portfolio to align with target allocation.";
            case "PRICE_DROP":
                return "Review position and consider stop-loss or hedging strategies.";
            default:
                return "Review portfolio and consider rebalancing.";
        }
    }
    
    /**
     * Generate fallback insight when AI service is unavailable
     * 
     * @param symbol Stock symbol
     * @param riskType Type of risk
     * @return Fallback insight
     */
    private String generateFallbackInsight(String symbol, String riskType) {
        JsonObject insight = new JsonObject();
        insight.addProperty("explanation", "Risk detected in " + symbol + ". Please review.");
        insight.addProperty("suggestedAction", "Contact your financial advisor for guidance.");
        insight.addProperty("severity", "MEDIUM");
        insight.addProperty("disclaimer", "This is AI-generated guidance only. Not financial advice.");
        return insight.toString();
    }
    
    /**
     * Publish AIInsightGenerated event
     * 
     * @param clientId Client ID
     * @param symbol Stock symbol
     * @param riskType Type of risk
     * @param insight AI-generated insight
     */
    private void publishAIEvent(String clientId, String symbol, String riskType, String insight) {
        try {
            // Create AI event
            JsonObject aiDetail = new JsonObject();
            aiDetail.addProperty("clientId", clientId);
            aiDetail.addProperty("symbol", symbol);
            aiDetail.addProperty("riskType", riskType);
            aiDetail.addProperty("insight", insight);
            aiDetail.addProperty("timestamp", Instant.now().toString());
            
            // Publish to EventBridge
            PutEventsRequestEntry entry = PutEventsRequestEntry.builder()
                .source("ai-service")
                .detailType("AIInsightGenerated")
                .detail(aiDetail.toString())
                .build();
            
            PutEventsRequest request = PutEventsRequest.builder()
                .entries(Collections.singletonList(entry))
                .build();
            
            PutEventsResponse response = eventBridgeClient.putEvents(request);
            logger.info("Published AI event: {} entries, {} failed", 
                response.entries().size(), response.failedEntryCount());
        } catch (Exception e) {
            logger.error("Error publishing AI event", e);
        }
    }
}

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
 * Lambda Handler for Risk Detection Service
 * 
 * Processes PriceUpdated events from EventBridge and:
 * - Detects risk conditions
 * - Publishes RiskThresholdBreached events
 * - Handles errors and retries
 * 
 * @author Portfolio Risk System
 * @version 1.0.0
 */
public class RiskDetectionHandler implements RequestHandler<EventBridgeEvent, String> {
    
    private static final Logger logger = LoggerFactory.getLogger(RiskDetectionHandler.class);
    private static final Gson gson = new Gson();
    private static final EventBridgeClient eventBridgeClient = EventBridgeClient.builder().build();
    
    /**
     * Handle EventBridge event for risk detection
     * 
     * @param event EventBridge event containing price update
     * @param context Lambda context
     * @return Success or failure message
     */
    @Override
    public String handleRequest(EventBridgeEvent event, Context context) {
        try {
            logger.info("Received event: {}", event.getDetail());
            
            // Extract event data
            JsonObject detail = gson.fromJson(event.getDetail(), JsonObject.class);
            String symbol = detail.get("symbol").getAsString();
            double price = detail.get("price").getAsDouble();
            String clientId = detail.get("clientId").getAsString();
            
            logger.info("Processing price update: {} = ${}", symbol, price);
            
            // Detect risks (simplified for Lambda)
            boolean riskDetected = detectRisk(symbol, price, clientId);
            
            if (riskDetected) {
                // Publish RiskThresholdBreached event
                publishRiskEvent(symbol, price, clientId);
                logger.info("Risk detected and event published for: {}", symbol);
            }
            
            return "Success";
        } catch (Exception e) {
            logger.error("Error processing event", e);
            return "Error: " + e.getMessage();
        }
    }
    
    /**
     * Detect risk conditions
     * 
     * @param symbol Stock symbol
     * @param price Current price
     * @param clientId Client ID
     * @return True if risk detected
     */
    private boolean detectRisk(String symbol, double price, String clientId) {
        // Simplified risk detection logic
        // In production, this would call the RiskDetectionService
        
        // Example: Detect if price dropped more than 5%
        double threshold = 95.0; // 5% drop
        return price < threshold;
    }
    
    /**
     * Publish RiskThresholdBreached event
     * 
     * @param symbol Stock symbol
     * @param price Current price
     * @param clientId Client ID
     */
    private void publishRiskEvent(String symbol, double price, String clientId) {
        try {
            // Create risk event
            JsonObject riskDetail = new JsonObject();
            riskDetail.addProperty("symbol", symbol);
            riskDetail.addProperty("price", price);
            riskDetail.addProperty("clientId", clientId);
            riskDetail.addProperty("riskType", "PRICE_DROP");
            riskDetail.addProperty("severity", "HIGH");
            riskDetail.addProperty("timestamp", Instant.now().toString());
            
            // Publish to EventBridge
            PutEventsRequestEntry entry = PutEventsRequestEntry.builder()
                .source("risk-service")
                .detailType("RiskThresholdBreached")
                .detail(riskDetail.toString())
                .build();
            
            PutEventsRequest request = PutEventsRequest.builder()
                .entries(Collections.singletonList(entry))
                .build();
            
            PutEventsResponse response = eventBridgeClient.putEvents(request);
            logger.info("Published risk event: {} entries, {} failed", 
                response.entries().size(), response.failedEntryCount());
        } catch (Exception e) {
            logger.error("Error publishing risk event", e);
        }
    }
}

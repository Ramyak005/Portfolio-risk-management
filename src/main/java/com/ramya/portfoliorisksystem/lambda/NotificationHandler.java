package com.ramya.portfoliorisksystem.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.EventBridgeEvent;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Lambda Handler for Notification Service
 * 
 * Processes AIInsightGenerated events from EventBridge and:
 * - Stores notifications in DynamoDB
 * - Sends alerts to users
 * - Handles errors and retries
 * 
 * @author Portfolio Risk System
 * @version 1.0.0
 */
public class NotificationHandler implements RequestHandler<EventBridgeEvent, String> {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationHandler.class);
    private static final Gson gson = new Gson();
    private static final DynamoDbClient dynamoDbClient = DynamoDbClient.builder().build();
    private static final String NOTIFICATIONS_TABLE = "notifications";
    
    /**
     * Handle EventBridge event for notification processing
     * 
     * @param event EventBridge event containing AI insight
     * @param context Lambda context
     * @return Success or failure message
     */
    @Override
    public String handleRequest(EventBridgeEvent event, Context context) {
        try {
            logger.info("Received AI insight event: {}", event.getDetail());
            
            // Extract event data
            JsonObject detail = gson.fromJson(event.getDetail(), JsonObject.class);
            String clientId = detail.get("clientId").getAsString();
            String symbol = detail.get("symbol").getAsString();
            String riskType = detail.get("riskType").getAsString();
            String insight = detail.get("insight").getAsString();
            
            logger.info("Processing notification for client: {} - {}", clientId, symbol);
            
            // Store notification in DynamoDB
            storeNotification(clientId, symbol, riskType, insight);
            
            // Send alert (in production, this would send email/SMS/push notification)
            sendAlert(clientId, symbol, riskType);
            
            logger.info("Notification processed and stored");
            
            return "Success";
        } catch (Exception e) {
            logger.error("Error processing notification", e);
            return "Error: " + e.getMessage();
        }
    }
    
    /**
     * Store notification in DynamoDB
     * 
     * @param clientId Client ID
     * @param symbol Stock symbol
     * @param riskType Type of risk
     * @param insight AI-generated insight
     */
    private void storeNotification(String clientId, String symbol, String riskType, String insight) {
        try {
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("clientId", AttributeValue.builder().s(clientId).build());
            item.put("notificationId", AttributeValue.builder().s(generateId()).build());
            item.put("symbol", AttributeValue.builder().s(symbol).build());
            item.put("riskType", AttributeValue.builder().s(riskType).build());
            item.put("insight", AttributeValue.builder().s(insight).build());
            item.put("timestamp", AttributeValue.builder().s(Instant.now().toString()).build());
            item.put("read", AttributeValue.builder().bool(false).build());
            
            PutItemRequest request = PutItemRequest.builder()
                .tableName(NOTIFICATIONS_TABLE)
                .item(item)
                .build();
            
            PutItemResponse response = dynamoDbClient.putItem(request);
            logger.info("Stored notification in DynamoDB for client: {}", clientId);
        } catch (Exception e) {
            logger.error("Error storing notification in DynamoDB", e);
        }
    }
    
    /**
     * Send alert to user
     * 
     * @param clientId Client ID
     * @param symbol Stock symbol
     * @param riskType Type of risk
     */
    private void sendAlert(String clientId, String symbol, String riskType) {
        try {
            // In production, this would send:
            // - Email notification
            // - SMS notification
            // - Push notification
            // - In-app notification
            
            String message = String.format(
                "Alert: Risk detected in %s (%s) for client %s",
                symbol, riskType, clientId
            );
            
            logger.info("Sending alert: {}", message);
            
            // TODO: Implement actual notification sending
            // - Email via SES
            // - SMS via SNS
            // - Push via SNS
            
        } catch (Exception e) {
            logger.error("Error sending alert", e);
        }
    }
    
    /**
     * Generate unique notification ID
     * 
     * @return Unique ID
     */
    private String generateId() {
        return "notif-" + System.currentTimeMillis() + "-" + 
            (int)(Math.random() * 10000);
    }
}

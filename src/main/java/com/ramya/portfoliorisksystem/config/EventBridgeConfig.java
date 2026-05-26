package com.ramya.portfoliorisksystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EventBridge Configuration for Portfolio Risk System
 * 
 * Configures AWS EventBridge for event-driven architecture:
 * - Event bus creation
 * - Event routing rules
 * - Dead-letter queue configuration
 * - Event pattern matching
 * 
 * @author Portfolio Risk System
 * @version 1.0.0
 */
@Configuration
public class EventBridgeConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(EventBridgeConfig.class);
    
    private static final String EVENT_BUS_NAME = "portfolio-risk-system";
    private static final String REGION = "us-east-1";
    
    /**
     * Create EventBridge client bean
     * 
     * @return EventBridgeClient configured for AWS
     */
    @Bean
    public EventBridgeClient eventBridgeClient() {
        logger.info("Initializing EventBridge client for region: {}", REGION);
        return EventBridgeClient.builder()
            .build();
    }
    
    /**
     * Initialize EventBridge infrastructure
     * Creates event bus and routing rules
     * 
     * @param eventBridgeClient EventBridge client
     */
    @Bean
    public EventBridgeInitializer eventBridgeInitializer(EventBridgeClient eventBridgeClient) {
        return new EventBridgeInitializer(eventBridgeClient);
    }
    
    /**
     * EventBridge Initializer - Creates infrastructure on startup
     */
    public static class EventBridgeInitializer {
        
        private final EventBridgeClient eventBridgeClient;
        private static final Logger logger = LoggerFactory.getLogger(EventBridgeInitializer.class);
        
        public EventBridgeInitializer(EventBridgeClient eventBridgeClient) {
            this.eventBridgeClient = eventBridgeClient;
            initializeEventBridge();
        }
        
        /**
         * Initialize EventBridge infrastructure
         */
        private void initializeEventBridge() {
            try {
                logger.info("Initializing EventBridge infrastructure...");
                
                // Create event bus
                createEventBus();
                
                // Create routing rules
                createRoutingRules();
                
                logger.info("EventBridge infrastructure initialized successfully");
            } catch (Exception e) {
                logger.error("Error initializing EventBridge infrastructure", e);
                // Continue - EventBridge may already exist
            }
        }
        
        /**
         * Create custom event bus for portfolio risk system
         */
        private void createEventBus() {
            try {
                CreateEventBusRequest request = CreateEventBusRequest.builder()
                    .name(EVENT_BUS_NAME)
                    .build();
                
                eventBridgeClient.createEventBus(request);
                logger.info("Created event bus: {}", EVENT_BUS_NAME);
            } catch (EventBridgeException e) {
                if (e.awsErrorDetails() != null && 
                    e.awsErrorDetails().errorMessage().contains("already exists")) {
                    logger.info("Event bus already exists: {}", EVENT_BUS_NAME);
                } else {
                    logger.error("Error creating event bus", e);
                }
            }
        }
        
        /**
         * Create routing rules for event types
         */
        private void createRoutingRules() {
            // Rule 1: PriceUpdated → Risk Detection Service
            createRule(
                "route-price-updated-to-risk",
                "PriceUpdated events route to Risk Detection Service",
                "{\"source\":[\"market-data-service\"],\"detail-type\":[\"PriceUpdated\"]}"
            );
            
            // Rule 2: RiskThresholdBreached → AI Service
            createRule(
                "route-risk-to-ai",
                "Risk threshold breached events route to AI Service",
                "{\"source\":[\"risk-service\"],\"detail-type\":[\"RiskThresholdBreached\"]}"
            );
            
            // Rule 3: AIInsightGenerated → Notification Service
            createRule(
                "route-ai-to-notification",
                "AI insight events route to Notification Service",
                "{\"source\":[\"ai-service\"],\"detail-type\":[\"AIInsightGenerated\"]}"
            );
            
            logger.info("Created routing rules for all event types");
        }
        
        /**
         * Create a routing rule
         * 
         * @param ruleName Name of the rule
         * @param description Description of the rule
         * @param eventPattern Event pattern for matching
         */
        private void createRule(String ruleName, String description, String eventPattern) {
            try {
                PutRuleRequest request = PutRuleRequest.builder()
                    .name(ruleName)
                    .description(description)
                    .eventBusName(EVENT_BUS_NAME)
                    .eventPattern(eventPattern)
                    .state(RuleState.ENABLED)
                    .build();
                
                eventBridgeClient.putRule(request);
                logger.info("Created rule: {} with pattern: {}", ruleName, eventPattern);
            } catch (EventBridgeException e) {
                logger.error("Error creating rule: {}", ruleName, e);
            }
        }
    }
    
    /**
     * Event publishing configuration
     * 
     * @return Event bus name for publishing
     */
    public static String getEventBusName() {
        return EVENT_BUS_NAME;
    }
    
    /**
     * Get region for EventBridge
     * 
     * @return AWS region
     */
    public static String getRegion() {
        return REGION;
    }
}

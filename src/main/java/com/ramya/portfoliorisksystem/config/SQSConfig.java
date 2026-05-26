package com.ramya.portfoliorisksystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * SQS Configuration for Portfolio Risk System
 * 
 * Configures AWS SQS for message queuing:
 * - Queue creation
 * - Dead-letter queue setup
 * - Queue policies
 * - Message retention configuration
 * 
 * @author Portfolio Risk System
 * @version 1.0.0
 */
@Configuration
public class SQSConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(SQSConfig.class);
    
    // Queue names
    public static final String RISK_DETECTION_QUEUE = "risk-detection-queue";
    public static final String AI_INSIGHTS_QUEUE = "ai-insights-queue";
    public static final String NOTIFICATION_QUEUE = "notification-queue";
    
    // Queue configuration
    private static final int VISIBILITY_TIMEOUT = 300; // 5 minutes
    private static final int MESSAGE_RETENTION_PERIOD = 86400; // 24 hours
    private static final int MAX_RECEIVE_COUNT = 3;
    
    /**
     * Create SQS client bean
     * 
     * @return SqsClient configured for AWS
     */
    @Bean
    public SqsClient sqsClient() {
        logger.info("Initializing SQS client");
        return SqsClient.builder()
            .build();
    }
    
    /**
     * Initialize SQS infrastructure
     * Creates queues and dead-letter queues
     * 
     * @param sqsClient SQS client
     */
    @Bean
    public SQSInitializer sqsInitializer(SqsClient sqsClient) {
        return new SQSInitializer(sqsClient);
    }
    
    /**
     * SQS Initializer - Creates infrastructure on startup
     */
    public static class SQSInitializer {
        
        private final SqsClient sqsClient;
        private static final Logger logger = LoggerFactory.getLogger(SQSInitializer.class);
        
        public SQSInitializer(SqsClient sqsClient) {
            this.sqsClient = sqsClient;
            initializeSQS();
        }
        
        /**
         * Initialize SQS infrastructure
         */
        private void initializeSQS() {
            try {
                logger.info("Initializing SQS infrastructure...");
                
                // Create dead-letter queues first
                createDeadLetterQueue(RISK_DETECTION_QUEUE + "-dlq");
                createDeadLetterQueue(AI_INSIGHTS_QUEUE + "-dlq");
                createDeadLetterQueue(NOTIFICATION_QUEUE + "-dlq");
                
                // Create main queues with DLQ references
                createQueue(RISK_DETECTION_QUEUE, RISK_DETECTION_QUEUE + "-dlq");
                createQueue(AI_INSIGHTS_QUEUE, AI_INSIGHTS_QUEUE + "-dlq");
                createQueue(NOTIFICATION_QUEUE, NOTIFICATION_QUEUE + "-dlq");
                
                logger.info("SQS infrastructure initialized successfully");
            } catch (Exception e) {
                logger.error("Error initializing SQS infrastructure", e);
                // Continue - queues may already exist
            }
        }
        
        /**
         * Create a dead-letter queue
         * 
         * @param queueName Name of the dead-letter queue
         */
        private void createDeadLetterQueue(String queueName) {
            try {
                CreateQueueRequest request = CreateQueueRequest.builder()
                    .queueName(queueName)
                    .attributes(Map.of(
                        QueueAttributeName.MESSAGE_RETENTION_PERIOD, 
                        String.valueOf(MESSAGE_RETENTION_PERIOD)
                    ))
                    .build();
                
                CreateQueueResponse response = sqsClient.createQueue(request);
                logger.info("Created dead-letter queue: {} with URL: {}", queueName, response.queueUrl());
            } catch (QueueNameExistsException e) {
                logger.info("Dead-letter queue already exists: {}", queueName);
            } catch (SqsException e) {
                logger.error("Error creating dead-letter queue: {}", queueName, e);
            }
        }
        
        /**
         * Create a main queue with dead-letter queue reference
         * 
         * @param queueName Name of the main queue
         * @param dlqName Name of the dead-letter queue
         */
        private void createQueue(String queueName, String dlqName) {
            try {
                // Get DLQ ARN
                String dlqArn = getQueueArn(dlqName);
                
                // Create redrive policy
                String redrivePolicy = String.format(
                    "{\"deadLetterTargetArn\":\"%s\",\"maxReceiveCount\":%d}",
                    dlqArn, MAX_RECEIVE_COUNT
                );
                
                // Create main queue
                Map<QueueAttributeName, String> attributes = new HashMap<>();
                attributes.put(QueueAttributeName.VISIBILITY_TIMEOUT, String.valueOf(VISIBILITY_TIMEOUT));
                attributes.put(QueueAttributeName.MESSAGE_RETENTION_PERIOD, String.valueOf(MESSAGE_RETENTION_PERIOD));
                attributes.put(QueueAttributeName.REDRIVE_POLICY, redrivePolicy);
                
                CreateQueueRequest request = CreateQueueRequest.builder()
                    .queueName(queueName)
                    .attributes(attributes)
                    .build();
                
                CreateQueueResponse response = sqsClient.createQueue(request);
                logger.info("Created queue: {} with URL: {} and DLQ: {}", 
                    queueName, response.queueUrl(), dlqName);
            } catch (QueueNameExistsException e) {
                logger.info("Queue already exists: {}", queueName);
            } catch (SqsException e) {
                logger.error("Error creating queue: {}", queueName, e);
            }
        }
        
        /**
         * Get queue ARN from queue name
         * 
         * @param queueName Name of the queue
         * @return Queue ARN
         */
        private String getQueueArn(String queueName) {
            try {
                GetQueueUrlRequest urlRequest = GetQueueUrlRequest.builder()
                    .queueName(queueName)
                    .build();
                
                GetQueueUrlResponse urlResponse = sqsClient.getQueueUrl(urlRequest);
                String queueUrl = urlResponse.queueUrl();
                
                GetQueueAttributesRequest attrRequest = GetQueueAttributesRequest.builder()
                    .queueUrl(queueUrl)
                    .attributeNames(QueueAttributeName.QUEUE_ARN)
                    .build();
                
                GetQueueAttributesResponse attrResponse = sqsClient.getQueueAttributes(attrRequest);
                return attrResponse.attributes().get(QueueAttributeName.QUEUE_ARN);
            } catch (Exception e) {
                logger.error("Error getting queue ARN for: {}", queueName, e);
                return "";
            }
        }
    }
    
    /**
     * Get queue URL by name
     * 
     * @param sqsClient SQS client
     * @param queueName Name of the queue
     * @return Queue URL
     */
    public static String getQueueUrl(SqsClient sqsClient, String queueName) {
        try {
            GetQueueUrlRequest request = GetQueueUrlRequest.builder()
                .queueName(queueName)
                .build();
            
            GetQueueUrlResponse response = sqsClient.getQueueUrl(request);
            return response.queueUrl();
        } catch (Exception e) {
            logger.error("Error getting queue URL for: {}", queueName, e);
            return null;
        }
    }
    
    private static final Logger logger = LoggerFactory.getLogger(SQSConfig.class);
}

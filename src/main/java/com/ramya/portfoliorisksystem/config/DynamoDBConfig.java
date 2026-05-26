package com.ramya.portfoliorisksystem.config;

import org.springframework.context.annotation.Configuration;

/**
 * DynamoDB Configuration for Portfolio Risk System
 * 
 * Tables:
 * 1. portfolios - Client portfolio holdings
 * 2. market_prices - Current stock prices
 * 3. risk_alerts - Risk threshold breach events
 * 4. ai_insights - AI-generated insights
 * 5. events - Event audit trail
 * 
 * This configuration is for reference. In production, use AWS CloudFormation
 * or Terraform to manage DynamoDB tables.
 */
@Configuration
public class DynamoDBConfig {

    /**
     * Table: portfolios
     * 
     * Primary Key:
     *   PK: clientId (String)
     *   SK: timestamp (String, ISO-8601)
     * 
     * Attributes:
     *   clientName (String)
     *   portfolioValue (Number)
     *   holdings (Map<String, Number>) - symbol -> shares
     *   allocation (Map<String, Number>) - symbol -> percentage
     *   targetAllocation (Map<String, Number>)
     *   diversificationScore (Number)
     *   topRiskExposure (String)
     *   lastUpdated (String, ISO-8601)
     * 
     * GSI:
     *   clientName-index: PK=clientName, SK=timestamp
     * 
     * TTL: None (permanent storage)
     * 
     * Billing Mode: PAY_PER_REQUEST (on-demand)
     */
    public static final String PORTFOLIOS_TABLE = "portfolios";

    /**
     * Table: market_prices
     * 
     * Primary Key:
     *   PK: symbol (String)
     * 
     * Attributes:
     *   price (Number)
     *   previousPrice (Number)
     *   change (Number)
     *   changePercent (Number)
     *   dayHigh (Number)
     *   dayLow (Number)
     *   openPrice (Number)
     *   volume (Number)
     *   lastUpdated (String, ISO-8601)
     * 
     * TTL: None (always current)
     * 
     * Billing Mode: PAY_PER_REQUEST
     */
    public static final String MARKET_PRICES_TABLE = "market_prices";

    /**
     * Table: risk_alerts
     * 
     * Primary Key:
     *   PK: clientId (String)
     *   SK: timestamp (String, ISO-8601)
     * 
     * Attributes:
     *   alertId (String, UUID)
     *   riskType (String) - CONCENTRATION, DRIFT, PORTFOLIO_DROP
     *   severity (String) - LOW, MEDIUM, HIGH, CRITICAL
     *   message (String)
     *   affectedAssets (List<String>)
     *   threshold (Number)
     *   currentValue (Number)
     *   portfolioValue (Number)
     *   exposureValue (Number)
     *   isActive (Boolean)
     * 
     * GSI:
     *   riskType-timestamp-index: PK=riskType, SK=timestamp
     *   severity-timestamp-index: PK=severity, SK=timestamp
     * 
     * TTL: 7776000 (90 days)
     * 
     * Billing Mode: PAY_PER_REQUEST
     */
    public static final String RISK_ALERTS_TABLE = "risk_alerts";

    /**
     * Table: ai_insights
     * 
     * Primary Key:
     *   PK: clientId (String)
     *   SK: timestamp (String, ISO-8601)
     * 
     * Attributes:
     *   insightId (String, UUID)
     *   riskEventId (String, UUID)
     *   severity (String)
     *   explanation (String)
     *   suggestedAction (String)
     *   disclaimer (String)
     *   model (String)
     *   confidence (Number)
     *   processingTimeMs (Number)
     *   tokenUsage (Map)
     * 
     * GSI:
     *   riskEventId-index: PK=riskEventId
     * 
     * TTL: 7776000 (90 days)
     * 
     * Billing Mode: PAY_PER_REQUEST
     */
    public static final String AI_INSIGHTS_TABLE = "ai_insights";

    /**
     * Table: events
     * 
     * Primary Key:
     *   PK: eventId (String, UUID)
     *   SK: timestamp (String, ISO-8601)
     * 
     * Attributes:
     *   eventType (String) - PriceUpdated, PortfolioRevalued, RiskThresholdBreached, AIInsightGenerated
     *   source (String) - service name
     *   version (String)
     *   data (Map) - event payload
     * 
     * GSI:
     *   eventType-timestamp-index: PK=eventType, SK=timestamp
     *   source-timestamp-index: PK=source, SK=timestamp
     * 
     * TTL: 2592000 (30 days for most events, 7776000 for risk events)
     * 
     * Billing Mode: PAY_PER_REQUEST
     */
    public static final String EVENTS_TABLE = "events";

    /**
     * CloudFormation Template for DynamoDB Tables
     * 
     * Usage:
     *   aws cloudformation create-stack \
     *     --stack-name portfolio-risk-system \
     *     --template-body file://dynamodb-tables.yaml
     */
    public static final String CLOUDFORMATION_TEMPLATE = """
        AWSTemplateFormatVersion: '2010-09-09'
        Description: 'DynamoDB Tables for Portfolio Risk System'
        
        Resources:
          PortfoliosTable:
            Type: AWS::DynamoDB::Table
            Properties:
              TableName: portfolios
              BillingMode: PAY_PER_REQUEST
              AttributeDefinitions:
                - AttributeName: clientId
                  AttributeType: S
                - AttributeName: timestamp
                  AttributeType: S
                - AttributeName: clientName
                  AttributeType: S
              KeySchema:
                - AttributeName: clientId
                  KeyType: HASH
                - AttributeName: timestamp
                  KeyType: RANGE
              GlobalSecondaryIndexes:
                - IndexName: clientName-index
                  KeySchema:
                    - AttributeName: clientName
                      KeyType: HASH
                    - AttributeName: timestamp
                      KeyType: RANGE
                  Projection:
                    ProjectionType: ALL
              StreamSpecification:
                StreamViewType: NEW_AND_OLD_IMAGES
              Tags:
                - Key: Environment
                  Value: production
                - Key: Application
                  Value: portfolio-risk-system
        
          MarketPricesTable:
            Type: AWS::DynamoDB::Table
            Properties:
              TableName: market_prices
              BillingMode: PAY_PER_REQUEST
              AttributeDefinitions:
                - AttributeName: symbol
                  AttributeType: S
              KeySchema:
                - AttributeName: symbol
                  KeyType: HASH
              Tags:
                - Key: Environment
                  Value: production
        
          RiskAlertsTable:
            Type: AWS::DynamoDB::Table
            Properties:
              TableName: risk_alerts
              BillingMode: PAY_PER_REQUEST
              AttributeDefinitions:
                - AttributeName: clientId
                  AttributeType: S
                - AttributeName: timestamp
                  AttributeType: S
                - AttributeName: riskType
                  AttributeType: S
                - AttributeName: severity
                  AttributeType: S
              KeySchema:
                - AttributeName: clientId
                  KeyType: HASH
                - AttributeName: timestamp
                  KeyType: RANGE
              GlobalSecondaryIndexes:
                - IndexName: riskType-timestamp-index
                  KeySchema:
                    - AttributeName: riskType
                      KeyType: HASH
                    - AttributeName: timestamp
                      KeyType: RANGE
                  Projection:
                    ProjectionType: ALL
                - IndexName: severity-timestamp-index
                  KeySchema:
                    - AttributeName: severity
                      KeyType: HASH
                    - AttributeName: timestamp
                      KeyType: RANGE
                  Projection:
                    ProjectionType: ALL
              TimeToLiveSpecification:
                AttributeName: ttl
                Enabled: true
              Tags:
                - Key: Environment
                  Value: production
        
          AIInsightsTable:
            Type: AWS::DynamoDB::Table
            Properties:
              TableName: ai_insights
              BillingMode: PAY_PER_REQUEST
              AttributeDefinitions:
                - AttributeName: clientId
                  AttributeType: S
                - AttributeName: timestamp
                  AttributeType: S
                - AttributeName: riskEventId
                  AttributeType: S
              KeySchema:
                - AttributeName: clientId
                  KeyType: HASH
                - AttributeName: timestamp
                  KeyType: RANGE
              GlobalSecondaryIndexes:
                - IndexName: riskEventId-index
                  KeySchema:
                    - AttributeName: riskEventId
                      KeyType: HASH
                  Projection:
                    ProjectionType: ALL
              TimeToLiveSpecification:
                AttributeName: ttl
                Enabled: true
              Tags:
                - Key: Environment
                  Value: production
        
          EventsTable:
            Type: AWS::DynamoDB::Table
            Properties:
              TableName: events
              BillingMode: PAY_PER_REQUEST
              AttributeDefinitions:
                - AttributeName: eventId
                  AttributeType: S
                - AttributeName: timestamp
                  AttributeType: S
                - AttributeName: eventType
                  AttributeType: S
                - AttributeName: source
                  AttributeType: S
              KeySchema:
                - AttributeName: eventId
                  KeyType: HASH
                - AttributeName: timestamp
                  KeyType: RANGE
              GlobalSecondaryIndexes:
                - IndexName: eventType-timestamp-index
                  KeySchema:
                    - AttributeName: eventType
                      KeyType: HASH
                    - AttributeName: timestamp
                      KeyType: RANGE
                  Projection:
                    ProjectionType: ALL
                - IndexName: source-timestamp-index
                  KeySchema:
                    - AttributeName: source
                      KeyType: HASH
                    - AttributeName: timestamp
                      KeyType: RANGE
                  Projection:
                    ProjectionType: ALL
              TimeToLiveSpecification:
                AttributeName: ttl
                Enabled: true
              Tags:
                - Key: Environment
                  Value: production
        """;
}

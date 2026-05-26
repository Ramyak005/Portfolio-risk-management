package com.ramya.portfoliorisksystem.service;

import com.ramya.portfoliorisksystem.model.Client;
import com.ramya.portfoliorisksystem.model.RiskAlert;
import com.ramya.portfoliorisksystem.model.Stock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * RiskDetectionService
 * 
 * Implements deterministic risk detection algorithms:
 * 1. Concentration Risk: Single stock >20% of portfolio
 * 2. Allocation Drift: Current allocation differs from target by >5%
 * 3. Portfolio Drop: Daily loss >3%
 * 
 * All thresholds are configurable and deterministic (no randomness).
 */
@Service
public class RiskDetectionService {

    private static final Logger logger = LoggerFactory.getLogger(RiskDetectionService.class);

    // Risk thresholds (configurable)
    private static final double CONCENTRATION_THRESHOLD = 20.0;  // 20%
    private static final double ALLOCATION_DRIFT_THRESHOLD = 5.0;  // 5%
    private static final double PORTFOLIO_DROP_THRESHOLD = 3.0;  // 3%

    @Autowired
    private PortfolioDataService dataService;

    @Autowired
    private EventPublishingService eventPublishingService;

    /**
     * Detects all risk conditions for a client
     */
    public List<RiskAlert> detectRisks(Client client) {
        List<RiskAlert> alerts = new ArrayList<>();

        // Check concentration risk
        alerts.addAll(detectConcentrationRisk(client));

        // Check allocation drift
        alerts.addAll(detectAllocationDrift(client));

        // Check portfolio drop
        alerts.addAll(detectPortfolioDrop(client));

        return alerts;
    }

    /**
     * Detects concentration risk: Single stock >20% of portfolio
     */
    private List<RiskAlert> detectConcentrationRisk(Client client) {
        List<RiskAlert> alerts = new ArrayList<>();
        Map<String, Stock> stockBySymbol = dataService.getStockBySymbol();
        double portfolioValue = client.getPortfolioValue();

        for (Map.Entry<String, Integer> holding : client.getHoldings().entrySet()) {
            String symbol = holding.getKey();
            int shares = holding.getValue();
            Stock stock = stockBySymbol.get(symbol);

            if (stock != null) {
                double holdingValue = shares * stock.getPrice();
                double exposurePercent = (holdingValue / portfolioValue) * 100;

                if (exposurePercent > CONCENTRATION_THRESHOLD) {
                    String severity = calculateSeverity(exposurePercent, CONCENTRATION_THRESHOLD);
                    String message = String.format("%s exposure at %.2f%% (threshold: %.2f%%)",
                            symbol, exposurePercent, CONCENTRATION_THRESHOLD);

                    RiskAlert alert = new RiskAlert();
                    alert.setAlertId(UUID.randomUUID().toString());
                    alert.setClientId(client.getClientId());
                    alert.setRiskType("CONCENTRATION");
                    alert.setSeverity(severity);
                    alert.setMessage(message);
                    alert.setThreshold(CONCENTRATION_THRESHOLD);
                    alert.setCurrentValue(exposurePercent);
                    alert.setPortfolioValue(portfolioValue);
                    alert.setExposureValue(holdingValue);
                    alert.setTimestamp(Instant.now().toString());
                    alert.setActive(true);

                    alerts.add(alert);

                    // Publish event
                    List<String> affectedAssets = new ArrayList<>();
                    affectedAssets.add(symbol);
                    eventPublishingService.publishRiskThresholdBreached(
                            client.getClientId(),
                            client.getName(),
                            "CONCENTRATION",
                            severity,
                            CONCENTRATION_THRESHOLD,
                            exposurePercent,
                            message,
                            affectedAssets,
                            portfolioValue,
                            holdingValue
                    );

                    logger.warn("Concentration risk detected for {}: {} at {:.2f}%",
                            client.getClientId(), symbol, exposurePercent);
                }
            }
        }

        return alerts;
    }

    /**
     * Detects allocation drift: Current allocation differs from target by >5%
     */
    private List<RiskAlert> detectAllocationDrift(Client client) {
        List<RiskAlert> alerts = new ArrayList<>();

        // For now, we don't have target allocations in the model
        // This would be implemented when target allocations are added
        // TODO: Implement allocation drift detection when target allocations are available

        return alerts;
    }

    /**
     * Detects portfolio drop: Daily loss >3%
     */
    private List<RiskAlert> detectPortfolioDrop(Client client) {
        List<RiskAlert> alerts = new ArrayList<>();

        // For now, we don't have previous portfolio values
        // This would be implemented with historical data
        // TODO: Implement portfolio drop detection when historical data is available

        return alerts;
    }

    /**
     * Calculates severity based on how much threshold is exceeded
     * 
     * LOW: 0-2% over threshold
     * MEDIUM: 2-5% over threshold
     * HIGH: 5-10% over threshold
     * CRITICAL: >10% over threshold
     */
    private String calculateSeverity(double currentValue, double threshold) {
        double excess = currentValue - threshold;
        double excessPercent = (excess / threshold) * 100;

        if (excessPercent <= 2) {
            return "LOW";
        } else if (excessPercent <= 5) {
            return "MEDIUM";
        } else if (excessPercent <= 10) {
            return "HIGH";
        } else {
            return "CRITICAL";
        }
    }

    /**
     * Gets current risk level for a client
     * 
     * CRITICAL: Any critical alerts
     * HIGH: Any high alerts
     * MEDIUM: Any medium alerts
     * LOW: Only low alerts or no alerts
     */
    public String getRiskLevel(Client client) {
        List<RiskAlert> alerts = detectRisks(client);

        for (RiskAlert alert : alerts) {
            if ("CRITICAL".equals(alert.getSeverity())) {
                return "CRITICAL";
            }
        }

        for (RiskAlert alert : alerts) {
            if ("HIGH".equals(alert.getSeverity())) {
                return "HIGH";
            }
        }

        for (RiskAlert alert : alerts) {
            if ("MEDIUM".equals(alert.getSeverity())) {
                return "MEDIUM";
            }
        }

        return alerts.isEmpty() ? "LOW" : "LOW";
    }

    /**
     * Checks if a specific risk condition is breached
     */
    public boolean isConcentrationRiskBreached(Client client) {
        Map<String, Stock> stockBySymbol = dataService.getStockBySymbol();
        double portfolioValue = client.getPortfolioValue();

        for (Map.Entry<String, Integer> holding : client.getHoldings().entrySet()) {
            Stock stock = stockBySymbol.get(holding.getKey());
            if (stock != null) {
                double holdingValue = holding.getValue() * stock.getPrice();
                double exposurePercent = (holdingValue / portfolioValue) * 100;
                if (exposurePercent > CONCENTRATION_THRESHOLD) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Gets concentration risk details for a client
     */
    public Map<String, Double> getConcentrationRiskDetails(Client client) {
        Map<String, Stock> stockBySymbol = dataService.getStockBySymbol();
        double portfolioValue = client.getPortfolioValue();
        Map<String, Double> concentrations = new java.util.HashMap<>();

        for (Map.Entry<String, Integer> holding : client.getHoldings().entrySet()) {
            String symbol = holding.getKey();
            Stock stock = stockBySymbol.get(symbol);
            if (stock != null) {
                double holdingValue = holding.getValue() * stock.getPrice();
                double exposurePercent = (holdingValue / portfolioValue) * 100;
                concentrations.put(symbol, exposurePercent);
            }
        }

        return concentrations;
    }
}

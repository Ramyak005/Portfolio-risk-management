package com.ramya.portfoliorisksystem.util;

import com.ramya.portfoliorisksystem.model.RiskExposure;
import com.ramya.portfoliorisksystem.model.Stock;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Reusable portfolio analytics: valuation, diversification scoring,
 * and dollar-weighted risk exposure calculations.
 */
public final class PortfolioAnalyticsUtil {

    private PortfolioAnalyticsUtil() {
    }

    /**
     * Computes total portfolio value from holdings and current market prices.
     */
    public static double calculatePortfolioValue(Map<String, Integer> holdings,
                                                  Map<String, Stock> stockBySymbol) {
        double total = 0;

        for (Map.Entry<String, Integer> entry : holdings.entrySet()) {
            Stock stock = stockBySymbol.get(entry.getKey());
            if (stock != null) {
                total += entry.getValue() * stock.getPrice();
            }
        }

        return Math.round(total * 100.0) / 100.0;
    }

    /**
     * Diversification score (0-100) combining:
     * - Position count (more holdings = higher score)
     * - Sector spread (more sectors = higher score)
     * - Concentration penalty (lower max weight = higher score)
     *
     * Uses inverse HHI concept: concentrated portfolios score lower.
     */
    public static double calculateDiversificationScore(Map<String, Integer> holdings,
                                                        Map<String, Stock> stockBySymbol) {
        if (holdings.isEmpty()) {
            return 0;
        }

        double totalValue = calculatePortfolioValue(holdings, stockBySymbol);
        if (totalValue <= 0) {
            return 0;
        }

        // Herfindahl-Hirschman Index on dollar weights
        double hhi = 0;
        Set<String> sectors = new HashSet<>();
        double maxWeight = 0;

        for (Map.Entry<String, Integer> entry : holdings.entrySet()) {
            Stock stock = stockBySymbol.get(entry.getKey());
            if (stock == null) {
                continue;
            }

            double weight = (entry.getValue() * stock.getPrice()) / totalValue;
            hhi += weight * weight;
            maxWeight = Math.max(maxWeight, weight);
            sectors.add(stock.getSector());
        }

        // Convert HHI (0-1) to diversification component; lower HHI = better
        double hhiScore = (1 - hhi) * 50;

        // Reward breadth of holdings and sectors
        double holdingScore = Math.min(holdings.size() * 3.0, 25);
        double sectorScore = Math.min(sectors.size() * 4.0, 25);

        // Penalize heavy single-stock concentration
        double concentrationPenalty = maxWeight > 0.40 ? (maxWeight - 0.40) * 50 : 0;

        double score = hhiScore + holdingScore + sectorScore - concentrationPenalty;
        return Math.round(Math.max(0, Math.min(100, score)) * 10.0) / 10.0;
    }

    /**
     * Returns dollar-weighted exposure for each held stock, sorted descending.
     */
    public static List<RiskExposure> calculateRiskExposures(Map<String, Integer> holdings,
                                                           Map<String, Stock> stockBySymbol) {
        double totalValue = calculatePortfolioValue(holdings, stockBySymbol);
        List<RiskExposure> exposures = new ArrayList<>();

        if (totalValue <= 0) {
            return exposures;
        }

        for (Map.Entry<String, Integer> entry : holdings.entrySet()) {
            Stock stock = stockBySymbol.get(entry.getKey());
            if (stock == null) {
                continue;
            }

            double positionValue = entry.getValue() * stock.getPrice();
            double exposurePercent = (positionValue / totalValue) * 100;

            exposures.add(new RiskExposure(
                    entry.getKey(),
                    Math.round(exposurePercent * 10.0) / 10.0,
                    Math.round(positionValue * 100.0) / 100.0
            ));
        }

        exposures.sort(Comparator.comparingDouble(RiskExposure::getExposurePercent).reversed());
        return exposures;
    }

    /**
     * Returns the highest single-stock exposure percentage for a portfolio.
     */
    public static double calculateTopRiskExposure(Map<String, Integer> holdings,
                                                   Map<String, Stock> stockBySymbol) {
        List<RiskExposure> exposures = calculateRiskExposures(holdings, stockBySymbol);
        return exposures.isEmpty() ? 0 : exposures.get(0).getExposurePercent();
    }

    /**
     * Aggregates firm-wide risk by summing position values per stock across all clients.
     */
    public static List<RiskExposure> calculateFirmWideTopExposures(
            List<Map<String, Integer>> allHoldings,
            Map<String, Stock> stockBySymbol,
            int limit) {

        Map<String, Double> aggregatedValues = new HashMap<>();

        for (Map<String, Integer> holdings : allHoldings) {
            for (Map.Entry<String, Integer> entry : holdings.entrySet()) {
                Stock stock = stockBySymbol.get(entry.getKey());
                if (stock != null) {
                    double value = entry.getValue() * stock.getPrice();
                    aggregatedValues.merge(entry.getKey(), value, Double::sum);
                }
            }
        }

        double totalFirmValue = aggregatedValues.values().stream()
                .mapToDouble(Double::doubleValue).sum();

        if (totalFirmValue <= 0) {
            return List.of();
        }

        List<RiskExposure> exposures = new ArrayList<>();
        for (Map.Entry<String, Double> entry : aggregatedValues.entrySet()) {
            double percent = (entry.getValue() / totalFirmValue) * 100;
            exposures.add(new RiskExposure(
                    entry.getKey(),
                    Math.round(percent * 10.0) / 10.0,
                    Math.round(entry.getValue() * 100.0) / 100.0
            ));
        }

        exposures.sort(Comparator.comparingDouble(RiskExposure::getExposurePercent).reversed());
        return exposures.subList(0, Math.min(limit, exposures.size()));
    }
}

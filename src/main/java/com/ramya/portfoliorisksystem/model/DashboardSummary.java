package com.ramya.portfoliorisksystem.model;

import java.util.List;

/**
 * Firm-wide aggregation metrics for the dashboard overview.
 */
public class DashboardSummary {

    private int totalClients;
    private int totalStocks;
    private double totalAssetsUnderManagement;
    private double averagePortfolioValue;
    private double averageDiversificationScore;
    private List<LeaderboardEntry> topPortfolios;
    private List<RiskExposure> topRiskExposures;
    private ClientMetrics featuredClient;

    public DashboardSummary(int totalClients, int totalStocks,
                            double totalAssetsUnderManagement,
                            double averagePortfolioValue,
                            double averageDiversificationScore,
                            List<LeaderboardEntry> topPortfolios,
                            List<RiskExposure> topRiskExposures,
                            ClientMetrics featuredClient) {
        this.totalClients = totalClients;
        this.totalStocks = totalStocks;
        this.totalAssetsUnderManagement = totalAssetsUnderManagement;
        this.averagePortfolioValue = averagePortfolioValue;
        this.averageDiversificationScore = averageDiversificationScore;
        this.topPortfolios = topPortfolios;
        this.topRiskExposures = topRiskExposures;
        this.featuredClient = featuredClient;
    }

    public int getTotalClients() {
        return totalClients;
    }

    public int getTotalStocks() {
        return totalStocks;
    }

    public double getTotalAssetsUnderManagement() {
        return totalAssetsUnderManagement;
    }

    public double getAveragePortfolioValue() {
        return averagePortfolioValue;
    }

    public double getAverageDiversificationScore() {
        return averageDiversificationScore;
    }

    public List<LeaderboardEntry> getTopPortfolios() {
        return topPortfolios;
    }

    public List<RiskExposure> getTopRiskExposures() {
        return topRiskExposures;
    }

    public ClientMetrics getFeaturedClient() {
        return featuredClient;
    }
}

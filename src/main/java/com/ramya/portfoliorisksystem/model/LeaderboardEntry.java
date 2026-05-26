package com.ramya.portfoliorisksystem.model;

/**
 * Ranked portfolio entry for the value-based leaderboard.
 */
public class LeaderboardEntry {

    private int rank;
    private int clientId;
    private String clientName;
    private double portfolioValue;
    private double diversificationScore;
    private double topRiskExposure;

    public LeaderboardEntry(int rank, int clientId, String clientName,
                            double portfolioValue, double diversificationScore,
                            double topRiskExposure) {
        this.rank = rank;
        this.clientId = clientId;
        this.clientName = clientName;
        this.portfolioValue = portfolioValue;
        this.diversificationScore = diversificationScore;
        this.topRiskExposure = topRiskExposure;
    }

    public int getRank() {
        return rank;
    }

    public int getClientId() {
        return clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public double getPortfolioValue() {
        return portfolioValue;
    }

    public double getDiversificationScore() {
        return diversificationScore;
    }

    public double getTopRiskExposure() {
        return topRiskExposure;
    }
}

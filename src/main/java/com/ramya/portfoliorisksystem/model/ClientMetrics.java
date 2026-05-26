package com.ramya.portfoliorisksystem.model;

/**
 * Lightweight metrics for the featured/default client shown on the dashboard.
 */
public class ClientMetrics {

    private int clientId;
    private String clientName;
    private double diversificationScore;
    private double topRiskExposure;

    public ClientMetrics(int clientId, String clientName,
                         double diversificationScore, double topRiskExposure) {
        this.clientId = clientId;
        this.clientName = clientName;
        this.diversificationScore = diversificationScore;
        this.topRiskExposure = topRiskExposure;
    }

    public int getClientId() {
        return clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public double getDiversificationScore() {
        return diversificationScore;
    }

    public double getTopRiskExposure() {
        return topRiskExposure;
    }
}

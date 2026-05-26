package com.ramya.portfoliorisksystem.model;

import java.util.Map;

/**
 * A client profile with computed portfolio metrics for list and detail views.
 */
public class Client {

    private int id;
    private String name;
    private double portfolioValue;
    private Map<String, Integer> holdings;
    private double diversificationScore;
    private double topRiskExposure;

    public Client(int id, String name, double portfolioValue,
                  Map<String, Integer> holdings,
                  double diversificationScore, double topRiskExposure) {
        this.id = id;
        this.name = name;
        this.portfolioValue = portfolioValue;
        this.holdings = holdings;
        this.diversificationScore = diversificationScore;
        this.topRiskExposure = topRiskExposure;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPortfolioValue() {
        return portfolioValue;
    }

    public Map<String, Integer> getHoldings() {
        return holdings;
    }

    public double getDiversificationScore() {
        return diversificationScore;
    }

    public double getTopRiskExposure() {
        return topRiskExposure;
    }
}

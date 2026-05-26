package com.ramya.portfoliorisksystem.model;

import java.util.Map;

public class Portfolio {

    private String clientName;
    private double portfolioValue;
    private Map<String, Integer> holdings;

    public Portfolio(String clientName,
                     double portfolioValue,
                     Map<String, Integer> holdings) {

        this.clientName = clientName;
        this.portfolioValue = portfolioValue;
        this.holdings = holdings;
    }

    public String getClientName() {
        return clientName;
    }

    public double getPortfolioValue() {
        return portfolioValue;
    }

    public Map<String, Integer> getHoldings() {
        return holdings;
    }
}
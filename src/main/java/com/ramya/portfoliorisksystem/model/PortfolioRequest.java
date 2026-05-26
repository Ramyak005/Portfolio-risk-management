package com.ramya.portfoliorisksystem.model;

import java.util.Map;

public class PortfolioRequest {

    private String clientName;

    private Map<String, Integer> holdings;

    public PortfolioRequest() {
    }

    public PortfolioRequest(String clientName,
                            Map<String, Integer> holdings) {

        this.clientName = clientName;
        this.holdings = holdings;
    }

    public String getClientName() {
        return clientName;
    }

    public Map<String, Integer> getHoldings() {
        return holdings;
    }
}
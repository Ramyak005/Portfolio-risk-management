package com.ramya.portfoliorisksystem.model;

public class RiskAlert {

    private String riskType;
    private String message;

    public RiskAlert(String riskType, String message) {
        this.riskType = riskType;
        this.message = message;
    }

    public String getRiskType() {
        return riskType;
    }

    public String getMessage() {
        return message;
    }
}
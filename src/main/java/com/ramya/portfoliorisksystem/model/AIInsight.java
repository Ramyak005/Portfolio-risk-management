package com.ramya.portfoliorisksystem.model;

public class AIInsight {

    private String insight;
    private String recommendation;

    public AIInsight(String insight, String recommendation) {
        this.insight = insight;
        this.recommendation = recommendation;
    }

    public String getInsight() {
        return insight;
    }

    public String getRecommendation() {
        return recommendation;
    }
}
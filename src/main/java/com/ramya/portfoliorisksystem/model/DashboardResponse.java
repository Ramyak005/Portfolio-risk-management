package com.ramya.portfoliorisksystem.model;

import java.util.List;

public class DashboardResponse {

    private Portfolio portfolio;
    private List<RiskAlert> risks;
    private AIInsight aiInsight;
    private List<MarketPrice> marketPrices;
    private DashboardSummary summary;

    public DashboardResponse(
            Portfolio portfolio,
            List<RiskAlert> risks,
            AIInsight aiInsight,
            List<MarketPrice> marketPrices,
            DashboardSummary summary) {

        this.portfolio = portfolio;
        this.risks = risks;
        this.aiInsight = aiInsight;
        this.marketPrices = marketPrices;
        this.summary = summary;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public List<RiskAlert> getRisks() {
        return risks;
    }

    public AIInsight getAiInsight() {
        return aiInsight;
    }

    public List<MarketPrice> getMarketPrices() {
        return marketPrices;
    }

    public DashboardSummary getSummary() {
        return summary;
    }
}

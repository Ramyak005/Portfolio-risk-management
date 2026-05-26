package com.ramya.portfoliorisksystem.model;

import java.util.List;

public class ClientAnalysisResponse {

    private Portfolio portfolio;
    private List<RiskAlert> risks;
    private AIInsight aiInsight;
    private ClientMetrics clientMetrics;

    public ClientAnalysisResponse(
            Portfolio portfolio,
            List<RiskAlert> risks,
            AIInsight aiInsight,
            ClientMetrics clientMetrics) {
        this.portfolio = portfolio;
        this.risks = risks;
        this.aiInsight = aiInsight;
        this.clientMetrics = clientMetrics;
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

    public ClientMetrics getClientMetrics() {
        return clientMetrics;
    }
}

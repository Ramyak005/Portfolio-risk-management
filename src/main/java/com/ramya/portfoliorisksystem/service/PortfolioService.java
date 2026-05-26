package com.ramya.portfoliorisksystem.service;

import com.ramya.portfoliorisksystem.model.AIInsight;
import com.ramya.portfoliorisksystem.model.Client;
import com.ramya.portfoliorisksystem.model.ClientAnalysisResponse;
import com.ramya.portfoliorisksystem.model.ClientMetrics;
import com.ramya.portfoliorisksystem.model.DashboardSummary;
import com.ramya.portfoliorisksystem.model.LeaderboardEntry;
import com.ramya.portfoliorisksystem.model.MarketPrice;
import com.ramya.portfoliorisksystem.model.Portfolio;
import com.ramya.portfoliorisksystem.model.PortfolioRequest;
import com.ramya.portfoliorisksystem.model.RiskAlert;
import com.ramya.portfoliorisksystem.model.RiskExposure;
import com.ramya.portfoliorisksystem.model.Stock;
import com.ramya.portfoliorisksystem.util.PortfolioAnalyticsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PortfolioService {

    private static final double HIGH_EXPOSURE_THRESHOLD = 20.0;

    @Autowired
    private PortfolioDataService dataService;

    @Autowired
    private AIService aiService;

    /**
     * Legacy endpoint: returns the default demo client portfolio (Ramya, id=1).
     */
    public Portfolio getPortfolio() {
        Client client = dataService.getDefaultClient();
        return toPortfolio(client);
    }

    public Portfolio getPortfolioForClient(int clientId) {
        return dataService.getClientById(clientId)
                .map(this::toPortfolio)
                .orElse(null);
    }

    public List<Client> getAllClients() {
        return dataService.getAllClients();
    }

    public Client getClient(int clientId) {
        return dataService.getClientById(clientId).orElse(null);
    }

    public List<Stock> getAllStocks() {
        return dataService.getAllStocks();
    }

    public List<LeaderboardEntry> getLeaderboard() {
        return dataService.getLeaderboard();
    }

    /**
     * Risk alerts based on dollar-weighted exposure (not share count).
     * Flags any position exceeding 20% of portfolio value.
     */
    public List<RiskAlert> checkRisk() {
        return checkRiskForClient(dataService.getDefaultClient());
    }

    public List<RiskAlert> checkRiskForClient(Client client) {
        if (client == null) {
            return List.of(new RiskAlert("UNKNOWN", "Client not found"));
        }

        List<RiskExposure> exposures = PortfolioAnalyticsUtil.calculateRiskExposures(
                client.getHoldings(), dataService.getStockBySymbol());

        List<RiskAlert> alerts = new ArrayList<>();

        for (RiskExposure exposure : exposures) {
            if (exposure.getExposurePercent() > HIGH_EXPOSURE_THRESHOLD) {
                alerts.add(new RiskAlert(
                        "HIGH_EXPOSURE",
                        exposure.getStock() + " exposure exceeds 20% ("
                                + exposure.getExposurePercent() + "%)"
                ));
            }
        }

        if (alerts.isEmpty()) {
            alerts.add(new RiskAlert("LOW_RISK", "Portfolio risk is normal"));
        }

        return alerts;
    }

    /**
     * Generates AI-powered portfolio recommendations via OpenAI, with rule-based fallback.
     */
    public AIInsight getAIInsight() {
        return getAIInsightForClient(dataService.getDefaultClient());
    }

    public AIInsight getAIInsightForClient(Client client) {
        return aiService.generateRecommendation(client);
    }

    public ClientAnalysisResponse getClientAnalysis(int clientId) {
        Client client = getClient(clientId);
        if (client == null) {
            return null;
        }

        ClientMetrics metrics = new ClientMetrics(
                client.getId(),
                client.getName(),
                client.getDiversificationScore(),
                client.getTopRiskExposure()
        );

        return new ClientAnalysisResponse(
                toPortfolio(client),
                checkRiskForClient(client),
                getAIInsightForClient(client),
                metrics
        );
    }

    public List<MarketPrice> getMarketPrices() {
        return dataService.getAllStocks().stream()
                .map(stock -> new MarketPrice(stock.getSymbol(), stock.getPrice()))
                .toList();
    }

    /**
     * Aggregated firm-wide metrics for the enhanced dashboard.
     */
    public DashboardSummary getDashboardSummary() {
        Client featured = dataService.getDefaultClient();
        ClientMetrics featuredMetrics = new ClientMetrics(
                featured.getId(),
                featured.getName(),
                featured.getDiversificationScore(),
                featured.getTopRiskExposure()
        );

        return new DashboardSummary(
                dataService.getAllClients().size(),
                dataService.getAllStocks().size(),
                Math.round(dataService.getTotalAssetsUnderManagement() * 100.0) / 100.0,
                dataService.getAveragePortfolioValue(),
                dataService.getAverageDiversificationScore(),
                dataService.getTopPortfolios(dataService.getLeaderboardTopN()),
                dataService.getFirmWideTopExposures(),
                featuredMetrics
        );
    }

    public Portfolio calculatePortfolio(PortfolioRequest request) {
        Map<String, Stock> stockBySymbol = dataService.getStockBySymbol();
        double totalValue = PortfolioAnalyticsUtil.calculatePortfolioValue(
                request.getHoldings(), stockBySymbol);

        return new Portfolio(
                request.getClientName(),
                totalValue,
                request.getHoldings()
        );
    }

    private Portfolio toPortfolio(Client client) {
        return new Portfolio(
                client.getName(),
                client.getPortfolioValue(),
                client.getHoldings()
        );
    }
}

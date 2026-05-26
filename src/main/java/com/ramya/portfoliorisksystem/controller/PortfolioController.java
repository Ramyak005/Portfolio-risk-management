package com.ramya.portfoliorisksystem.controller;

import com.ramya.portfoliorisksystem.model.AIInsight;
import com.ramya.portfoliorisksystem.model.Client;
import com.ramya.portfoliorisksystem.model.ClientAnalysisResponse;
import com.ramya.portfoliorisksystem.model.DashboardResponse;
import com.ramya.portfoliorisksystem.model.LeaderboardEntry;
import com.ramya.portfoliorisksystem.model.MarketPrice;
import com.ramya.portfoliorisksystem.model.Portfolio;
import com.ramya.portfoliorisksystem.model.PortfolioRequest;
import com.ramya.portfoliorisksystem.model.RiskAlert;
import com.ramya.portfoliorisksystem.model.Stock;
import com.ramya.portfoliorisksystem.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PortfolioController {

    @Autowired
    private PortfolioService portfolioService;

    // --- Legacy endpoints (unchanged paths, enhanced data) ---

    @GetMapping("/portfolio")
    public Portfolio getPortfolio() {
        return portfolioService.getPortfolio();
    }

    @GetMapping("/portfolio/risk")
    public List<RiskAlert> checkRisk() {
        return portfolioService.checkRisk();
    }

    @GetMapping("/portfolio/ai-insight")
    public AIInsight getAIInsight() {
        return portfolioService.getAIInsight();
    }

    @GetMapping("/market-prices")
    public List<MarketPrice> getMarketPrices() {
        return portfolioService.getMarketPrices();
    }

    @GetMapping("/dashboard")
    public DashboardResponse getDashboard() {
        return new DashboardResponse(
                portfolioService.getPortfolio(),
                portfolioService.checkRisk(),
                portfolioService.getAIInsight(),
                portfolioService.getMarketPrices(),
                portfolioService.getDashboardSummary()
        );
    }

    @PostMapping("/portfolio/calculate")
    public Portfolio calculatePortfolio(@RequestBody PortfolioRequest request) {
        return portfolioService.calculatePortfolio(request);
    }

    // --- New simulation endpoints ---

    @GetMapping("/clients")
    public List<Client> getClients() {
        return portfolioService.getAllClients();
    }

    @GetMapping("/clients/{id}")
    public ResponseEntity<Client> getClient(@PathVariable int id) {
        Client client = portfolioService.getClient(id);
        if (client == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(client);
    }

    @GetMapping("/clients/{id}/portfolio")
    public ResponseEntity<Portfolio> getClientPortfolio(@PathVariable int id) {
        Portfolio portfolio = portfolioService.getPortfolioForClient(id);
        if (portfolio == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(portfolio);
    }

    @GetMapping("/clients/{id}/risk")
    public ResponseEntity<List<RiskAlert>> getClientRisk(@PathVariable int id) {
        Client client = portfolioService.getClient(id);
        if (client == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(portfolioService.checkRiskForClient(client));
    }

    @GetMapping("/clients/{id}/ai-insight")
    public ResponseEntity<AIInsight> getClientAIInsight(@PathVariable int id) {
        Client client = portfolioService.getClient(id);
        if (client == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(portfolioService.getAIInsightForClient(client));
    }

    @GetMapping("/clients/{id}/analysis")
    public ResponseEntity<ClientAnalysisResponse> getClientAnalysis(@PathVariable int id) {
        ClientAnalysisResponse analysis = portfolioService.getClientAnalysis(id);
        if (analysis == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(analysis);
    }

    @GetMapping("/stocks")
    public List<Stock> getStocks() {
        return portfolioService.getAllStocks();
    }

    @GetMapping("/leaderboard")
    public List<LeaderboardEntry> getLeaderboard() {
        return portfolioService.getLeaderboard();
    }
}

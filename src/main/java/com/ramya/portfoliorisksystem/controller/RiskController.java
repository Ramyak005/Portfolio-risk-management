package com.ramya.portfoliorisksystem.controller;

import com.ramya.portfoliorisksystem.model.Client;
import com.ramya.portfoliorisksystem.model.RiskAlert;
import com.ramya.portfoliorisksystem.service.PortfolioDataService;
import com.ramya.portfoliorisksystem.service.RiskDetectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * RiskController
 * 
 * Endpoints for risk detection and analysis
 */
@RestController
@RequestMapping("/api/v1/risk")
public class RiskController {

    @Autowired
    private RiskDetectionService riskDetectionService;

    @Autowired
    private PortfolioDataService portfolioDataService;

    /**
     * GET /api/v1/risk/clients/{clientId}
     * Get all risk alerts for a client
     */
    @GetMapping("/clients/{clientId}")
    public ResponseEntity<Map<String, Object>> getClientRisks(@PathVariable String clientId) {
        Optional<Client> clientOpt = portfolioDataService.getClientById(Integer.parseInt(clientId));

        if (clientOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Client client = clientOpt.get();
        List<RiskAlert> alerts = riskDetectionService.detectRisks(client);
        String riskLevel = riskDetectionService.getRiskLevel(client);

        Map<String, Object> response = new HashMap<>();
        response.put("clientId", clientId);
        response.put("riskLevel", riskLevel);
        response.put("alertCount", alerts.size());
        response.put("alerts", alerts);

        // Summary by severity
        Map<String, Long> severitySummary = new HashMap<>();
        severitySummary.put("CRITICAL", alerts.stream().filter(a -> "CRITICAL".equals(a.getSeverity())).count());
        severitySummary.put("HIGH", alerts.stream().filter(a -> "HIGH".equals(a.getSeverity())).count());
        severitySummary.put("MEDIUM", alerts.stream().filter(a -> "MEDIUM".equals(a.getSeverity())).count());
        severitySummary.put("LOW", alerts.stream().filter(a -> "LOW".equals(a.getSeverity())).count());
        response.put("severitySummary", severitySummary);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/risk/clients/{clientId}/concentration
     * Get concentration risk details for a client
     */
    @GetMapping("/clients/{clientId}/concentration")
    public ResponseEntity<Map<String, Object>> getConcentrationRisk(@PathVariable String clientId) {
        Optional<Client> clientOpt = portfolioDataService.getClientById(Integer.parseInt(clientId));

        if (clientOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Client client = clientOpt.get();
        boolean isBreached = riskDetectionService.isConcentrationRiskBreached(client);
        Map<String, Double> concentrations = riskDetectionService.getConcentrationRiskDetails(client);

        Map<String, Object> response = new HashMap<>();
        response.put("clientId", clientId);
        response.put("isBreached", isBreached);
        response.put("threshold", 20.0);
        response.put("concentrations", concentrations);

        // Find max concentration
        double maxConcentration = concentrations.values().stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(0.0);
        response.put("maxConcentration", maxConcentration);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/risk/summary
     * Get firm-wide risk summary
     */
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getRiskSummary() {
        List<Client> clients = portfolioDataService.getAllClients();

        Map<String, Long> riskLevelCounts = new HashMap<>();
        riskLevelCounts.put("CRITICAL", 0L);
        riskLevelCounts.put("HIGH", 0L);
        riskLevelCounts.put("MEDIUM", 0L);
        riskLevelCounts.put("LOW", 0L);

        int totalAlerts = 0;
        for (Client client : clients) {
            String riskLevel = riskDetectionService.getRiskLevel(client);
            riskLevelCounts.put(riskLevel, riskLevelCounts.get(riskLevel) + 1);

            List<RiskAlert> alerts = riskDetectionService.detectRisks(client);
            totalAlerts += alerts.size();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("clientCount", clients.size());
        response.put("totalAlerts", totalAlerts);
        response.put("riskLevelDistribution", riskLevelCounts);
        response.put("avgAlertsPerClient", clients.isEmpty() ? 0 : (double) totalAlerts / clients.size());

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/risk/high-risk-clients
     * Get clients with high or critical risk
     */
    @GetMapping("/high-risk-clients")
    public ResponseEntity<List<Map<String, Object>>> getHighRiskClients() {
        List<Client> clients = portfolioDataService.getAllClients();
        List<Map<String, Object>> highRiskClients = new java.util.ArrayList<>();

        for (Client client : clients) {
            String riskLevel = riskDetectionService.getRiskLevel(client);
            if ("HIGH".equals(riskLevel) || "CRITICAL".equals(riskLevel)) {
                Map<String, Object> clientInfo = new HashMap<>();
                clientInfo.put("clientId", client.getClientId());
                clientInfo.put("name", client.getName());
                clientInfo.put("portfolioValue", client.getPortfolioValue());
                clientInfo.put("riskLevel", riskLevel);

                List<RiskAlert> alerts = riskDetectionService.detectRisks(client);
                clientInfo.put("alertCount", alerts.size());
                clientInfo.put("alerts", alerts);

                highRiskClients.add(clientInfo);
            }
        }

        return ResponseEntity.ok(highRiskClients);
    }
}

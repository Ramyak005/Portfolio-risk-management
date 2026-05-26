package com.ramya.portfoliorisksystem.service;

import com.ramya.portfoliorisksystem.config.OpenAIProperties;
import com.ramya.portfoliorisksystem.model.AIInsight;
import com.ramya.portfoliorisksystem.model.Client;
import com.ramya.portfoliorisksystem.model.RiskAlert;
import com.ramya.portfoliorisksystem.model.RiskExposure;
import com.ramya.portfoliorisksystem.model.Stock;
import com.ramya.portfoliorisksystem.util.PortfolioAnalyticsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AIService {

    private static final Logger log = LoggerFactory.getLogger(AIService.class);
    private static final double HIGH_EXPOSURE_THRESHOLD = 20.0;

    private final OpenAIProperties openAIProperties;
    private final RestClient openAiRestClient;
    private final PortfolioDataService dataService;
    private final ConcurrentHashMap<Integer, AIInsight> recommendationCache = new ConcurrentHashMap<>();

    @Autowired
    public AIService(OpenAIProperties openAIProperties,
                     RestClient openAiRestClient,
                     PortfolioDataService dataService) {
        this.openAIProperties = openAIProperties;
        this.openAiRestClient = openAiRestClient;
        this.dataService = dataService;
    }

    public AIInsight generateRecommendation(Client client) {
        if (client == null) {
            return new AIInsight("No client data available", "Select a valid client");
        }

        if (!openAIProperties.isConfigured()) {
            return generateRuleBasedRecommendation(client);
        }

        return recommendationCache.computeIfAbsent(client.getId(), id -> {
            try {
                return callOpenAI(client);
            } catch (Exception ex) {
                log.warn("OpenAI recommendation failed for client {}: {}", client.getId(), ex.getMessage());
                return generateRuleBasedRecommendation(client);
            }
        });
    }

    public void clearCacheForClient(int clientId) {
        recommendationCache.remove(clientId);
    }

    private AIInsight callOpenAI(Client client) {
        Map<String, Stock> stockBySymbol = dataService.getStockBySymbol();
        List<RiskExposure> exposures = PortfolioAnalyticsUtil.calculateRiskExposures(
                client.getHoldings(), stockBySymbol);
        List<RiskAlert> alerts = buildRiskAlerts(exposures);

        String userPrompt = buildPrompt(client, exposures, alerts);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", openAIProperties.getModel());
        requestBody.put("temperature", 0.4);
        requestBody.put("messages", List.of(
                Map.of(
                        "role", "system",
                        "content", """
                                You are a professional portfolio risk advisor. Analyze the client's holdings \
                                and respond using exactly this plain-text format with no extra lines or markdown:
                                INSIGHT: <1-2 sentences summarizing portfolio health and key risks>
                                RECOMMENDATION: <1-2 sentences of specific, actionable advice>"""
                ),
                Map.of("role", "user", "content", userPrompt)
        ));

        String responseBody = openAiRestClient.post()
                .uri("/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + openAIProperties.getApiKey())
                .body(requestBody)
                .retrieve()
                .body(String.class);

        String content = extractMessageContent(responseBody);
        return parsePlainTextResponse(content);
    }

    private String extractMessageContent(String responseBody) {
        if (responseBody == null || responseBody.isBlank()) {
            throw new IllegalStateException("Empty OpenAI response");
        }

        String marker = "\"content\":\"";
        int start = responseBody.lastIndexOf(marker);
        if (start < 0) {
            throw new IllegalStateException("Missing content in OpenAI response");
        }

        start += marker.length();
        StringBuilder content = new StringBuilder();
        for (int i = start; i < responseBody.length(); i++) {
            char c = responseBody.charAt(i);
            if (c == '\\' && i + 1 < responseBody.length()) {
                char next = responseBody.charAt(i + 1);
                switch (next) {
                    case 'n' -> content.append('\n');
                    case 'r' -> content.append('\r');
                    case 't' -> content.append('\t');
                    case '"', '\\', '/' -> content.append(next);
                    default -> content.append(next);
                }
                i++;
            } else if (c == '"') {
                break;
            } else {
                content.append(c);
            }
        }

        return content.toString().trim();
    }

    private AIInsight parsePlainTextResponse(String content) {
        String insight = extractLabeledValue(content, "INSIGHT:");
        String recommendation = extractLabeledValue(content, "RECOMMENDATION:");

        if (insight.isBlank()) {
            insight = "Unable to generate insight";
        }
        if (recommendation.isBlank()) {
            recommendation = "Review portfolio allocation";
        }

        return new AIInsight(insight, recommendation);
    }

    private String extractLabeledValue(String text, String label) {
        int labelIndex = text.indexOf(label);
        if (labelIndex < 0) {
            return "";
        }

        int valueStart = labelIndex + label.length();
        int nextLabelIndex = text.length();

        for (String otherLabel : List.of("INSIGHT:", "RECOMMENDATION:")) {
            if (otherLabel.equals(label)) {
                continue;
            }
            int otherIndex = text.indexOf(otherLabel, valueStart);
            if (otherIndex >= 0) {
                nextLabelIndex = Math.min(nextLabelIndex, otherIndex);
            }
        }

        return text.substring(valueStart, nextLabelIndex).trim();
    }

    private String buildPrompt(Client client, List<RiskExposure> exposures, List<RiskAlert> alerts) {
        Map<String, Stock> stockBySymbol = dataService.getStockBySymbol();
        StringBuilder prompt = new StringBuilder();
        prompt.append("Client: ").append(client.getName()).append('\n');
        prompt.append("Portfolio value: $").append(String.format("%.2f", client.getPortfolioValue())).append('\n');
        prompt.append("Diversification score: ").append(client.getDiversificationScore()).append("/100\n");
        prompt.append("Top position exposure: ").append(client.getTopRiskExposure()).append("%\n\n");
        prompt.append("Holdings:\n");

        for (RiskExposure exposure : exposures) {
            Stock stock = stockBySymbol.get(exposure.getStock());
            String sector = stock != null ? stock.getSector() : "Unknown";
            prompt.append("- ").append(exposure.getStock())
                    .append(" (").append(sector).append("): ")
                    .append(String.format("%.1f", exposure.getExposurePercent()))
                    .append("% of portfolio, $")
                    .append(String.format("%.2f", exposure.getPositionValue()))
                    .append('\n');
        }

        prompt.append("\nRisk alerts:\n");
        for (RiskAlert alert : alerts) {
            prompt.append("- [").append(alert.getRiskType()).append("] ")
                    .append(alert.getMessage()).append('\n');
        }

        return prompt.toString();
    }

    private List<RiskAlert> buildRiskAlerts(List<RiskExposure> exposures) {
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

    AIInsight generateRuleBasedRecommendation(Client client) {
        double score = client.getDiversificationScore();
        double topRisk = client.getTopRiskExposure();

        String insight;
        String recommendation;

        if (score >= 70) {
            insight = "Portfolio shows strong diversification across sectors and positions";
            recommendation = "Maintain current allocation; consider periodic rebalancing";
        } else if (score >= 40) {
            insight = "Portfolio has moderate diversification with some concentration risk";
            recommendation = "Consider adding holdings in underrepresented sectors";
        } else {
            insight = "Portfolio is heavily concentrated in a few positions";
            recommendation = "Consider diversifying holdings across multiple sectors";
        }

        if (topRisk > 30) {
            insight += " (top position at " + topRisk + "% exposure)";
        }

        return new AIInsight(insight, recommendation);
    }
}

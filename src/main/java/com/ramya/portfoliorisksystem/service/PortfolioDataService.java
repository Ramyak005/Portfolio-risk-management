package com.ramya.portfoliorisksystem.service;

import com.ramya.portfoliorisksystem.model.Client;
import com.ramya.portfoliorisksystem.model.LeaderboardEntry;
import com.ramya.portfoliorisksystem.model.RiskExposure;
import com.ramya.portfoliorisksystem.model.Stock;
import com.ramya.portfoliorisksystem.seed.BrandSeeder;
import com.ramya.portfoliorisksystem.seed.ClientSeeder;
import com.ramya.portfoliorisksystem.util.PortfolioAnalyticsUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PortfolioDataService {

    private static final int LEADERBOARD_TOP_N = 10;
    private static final int FIRM_EXPOSURE_TOP_N = 5;

    private final BrandSeeder brandSeeder;
    private final ClientSeeder clientSeeder;

    private List<Stock> stocks = new ArrayList<>();
    private List<Client> clients = new ArrayList<>();

    private Map<String, Stock> stockBySymbol = new LinkedHashMap<>();
    private Map<Integer, Client> clientById = new LinkedHashMap<>();

    public PortfolioDataService(
            BrandSeeder brandSeeder,
            ClientSeeder clientSeeder
    ) {
        this.brandSeeder = brandSeeder;
        this.clientSeeder = clientSeeder;
    }

    @PostConstruct
    public void initialize() {

        // Load fashion + beauty brands
        stocks = brandSeeder.generateBrands();

        stockBySymbol = stocks.stream()
                .collect(Collectors.toMap(
                        Stock::getSymbol,
                        s -> s,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

        // Load seeded clients
        clients = clientSeeder.generateClients();

        clientById = clients.stream()
                .collect(Collectors.toMap(
                        Client::getId,
                        c -> c,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    public List<Stock> getAllStocks() {
        return List.copyOf(stocks);
    }

    public List<Client> getAllClients() {
        return List.copyOf(clients);
    }

    public Optional<Client> getClientById(int id) {
        return Optional.ofNullable(clientById.get(id));
    }

    public Client getDefaultClient() {
        return clientById.get(1);
    }

    public Map<String, Stock> getStockBySymbol() {
        return stockBySymbol;
    }

    public List<LeaderboardEntry> getLeaderboard() {

        List<Client> ranked = clients.stream()
                .sorted(
                        Comparator.comparingDouble(
                                Client::getPortfolioValue
                        ).reversed()
                )
                .toList();

        List<LeaderboardEntry> entries = new ArrayList<>();

        for (int i = 0; i < ranked.size(); i++) {

            Client client = ranked.get(i);

            entries.add(
                    new LeaderboardEntry(
                            i + 1,
                            client.getId(),
                            client.getName(),
                            client.getPortfolioValue(),
                            client.getDiversificationScore(),
                            client.getTopRiskExposure()
                    )
            );
        }

        return entries;
    }

    public List<LeaderboardEntry> getTopPortfolios(int limit) {
        return getLeaderboard()
                .subList(0, Math.min(limit, clients.size()));
    }

    public List<RiskExposure> getFirmWideTopExposures() {

        List<Map<String, Integer>> allHoldings = clients.stream()
                .map(Client::getHoldings)
                .toList();

        return PortfolioAnalyticsUtil.calculateFirmWideTopExposures(
                allHoldings,
                stockBySymbol,
                FIRM_EXPOSURE_TOP_N
        );
    }

    public double getTotalAssetsUnderManagement() {

        return clients.stream()
                .mapToDouble(Client::getPortfolioValue)
                .sum();
    }

    public double getAveragePortfolioValue() {

        return clients.isEmpty()
                ? 0
                : Math.round(
                getTotalAssetsUnderManagement()
                        / clients.size()
                        * 100.0
        ) / 100.0;
    }

    public double getAverageDiversificationScore() {

        return clients.isEmpty()
                ? 0
                : Math.round(
                clients.stream()
                        .mapToDouble(
                                Client::getDiversificationScore
                        )
                        .average()
                        .orElse(0)
                        * 10.0
        ) / 10.0;
    }

    public int getLeaderboardTopN() {
        return LEADERBOARD_TOP_N;
    }
}
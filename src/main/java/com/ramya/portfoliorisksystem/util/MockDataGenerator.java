package com.ramya.portfoliorisksystem.util;

import com.ramya.portfoliorisksystem.model.Stock;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Generates realistic mock market data: 30 stocks across sectors
 * and 100 client portfolios with varied diversification profiles.
 */
public final class MockDataGenerator {

    private static final Random RANDOM = new Random(42L);

    private static final String[][] STOCK_CATALOG = {
            {"AAPL", "Apple Inc.", "Technology", "185"},
            {"MSFT", "Microsoft Corp.", "Technology", "420"},
            {"GOOGL", "Alphabet Inc.", "Technology", "175"},
            {"AMZN", "Amazon.com Inc.", "Technology", "195"},
            {"META", "Meta Platforms", "Technology", "520"},
            {"NVDA", "NVIDIA Corp.", "Technology", "880"},
            {"TSLA", "Tesla Inc.", "Technology", "245"},
            {"AMD", "Advanced Micro Devices", "Technology", "165"},
            {"INTC", "Intel Corp.", "Technology", "42"},
            {"CRM", "Salesforce Inc.", "Technology", "310"},
            {"JPM", "JPMorgan Chase", "Finance", "210"},
            {"BAC", "Bank of America", "Finance", "38"},
            {"GS", "Goldman Sachs", "Finance", "480"},
            {"V", "Visa Inc.", "Finance", "280"},
            {"MA", "Mastercard Inc.", "Finance", "480"},
            {"JNJ", "Johnson & Johnson", "Healthcare", "155"},
            {"UNH", "UnitedHealth Group", "Healthcare", "520"},
            {"PFE", "Pfizer Inc.", "Healthcare", "28"},
            {"ABBV", "AbbVie Inc.", "Healthcare", "175"},
            {"WMT", "Walmart Inc.", "Consumer", "68"},
            {"PG", "Procter & Gamble", "Consumer", "165"},
            {"KO", "Coca-Cola Co.", "Consumer", "62"},
            {"PEP", "PepsiCo Inc.", "Consumer", "170"},
            {"MCD", "McDonald's Corp.", "Consumer", "290"},
            {"XOM", "Exxon Mobil", "Energy", "115"},
            {"CVX", "Chevron Corp.", "Energy", "155"},
            {"CAT", "Caterpillar Inc.", "Industrial", "340"},
            {"BA", "Boeing Co.", "Industrial", "185"},
            {"T", "AT&T Inc.", "Telecom", "21"},
            {"DIS", "Walt Disney Co.", "Entertainment", "115"}
    };

    private static final String[] FIRST_NAMES = {
            "Ramya", "Alex", "Jordan", "Taylor", "Morgan", "Casey", "Riley", "Quinn",
            "Avery", "Blake", "Cameron", "Drew", "Emery", "Finley", "Harper", "Jamie",
            "Kendall", "Logan", "Parker", "Reese", "Sage", "Skyler", "Spencer", "Sydney"
    };

    private static final String[] LAST_NAMES = {
            "Chen", "Patel", "Johnson", "Williams", "Garcia", "Martinez", "Anderson",
            "Thomas", "Jackson", "White", "Harris", "Clark", "Lewis", "Walker", "Hall",
            "Allen", "Young", "King", "Wright", "Scott", "Green", "Baker", "Adams", "Nelson"
    };

    private MockDataGenerator() {
    }

    /**
     * Builds the stock universe with randomized prices around realistic baselines.
     * Prices vary ±15% to simulate live market conditions.
     */
    public static List<Stock> generateStocks() {
        List<Stock> stocks = new ArrayList<>();

        for (String[] entry : STOCK_CATALOG) {
            double basePrice = Double.parseDouble(entry[3]);
            // Apply random variance while keeping prices realistic
            double variance = 0.85 + (RANDOM.nextDouble() * 0.30);
            double price = Math.round(basePrice * variance * 100.0) / 100.0;

            stocks.add(new Stock(entry[0], entry[1], entry[2], price));
        }

        return stocks;
    }

    /**
     * Generates 100 clients with diversified holdings.
     * Portfolio strategy rotates between concentrated, balanced, and broad profiles
     * to create realistic distribution across the client base.
     */
    public static List<MockClientData> generateClients(List<Stock> stocks, int count) {
        List<MockClientData> clients = new ArrayList<>();
        List<String> symbols = stocks.stream().map(Stock::getSymbol).toList();

        for (int i = 1; i <= count; i++) {
            String name = buildClientName(i);
            Map<String, Integer> holdings = generateHoldings(symbols, i);
            clients.add(new MockClientData(i, name, holdings));
        }

        return clients;
    }

    private static String buildClientName(int id) {
        // First client keeps the original demo name for backward compatibility
        if (id == 1) {
            return "Ramya";
        }
        String first = FIRST_NAMES[RANDOM.nextInt(FIRST_NAMES.length)];
        String last = LAST_NAMES[RANDOM.nextInt(LAST_NAMES.length)];
        return first + " " + last;
    }

    /**
     * Creates holdings with three portfolio archetypes:
     * - Concentrated (3-4 stocks, same sector bias)
     * - Balanced (6-8 stocks, multi-sector)
     * - Broad (10-14 stocks, high diversification)
     */
    private static Map<String, Integer> generateHoldings(List<String> symbols, int clientId) {
        Map<String, Integer> holdings = new LinkedHashMap<>();
        int profile = clientId % 3;

        int holdingCount = switch (profile) {
            case 0 -> 3 + RANDOM.nextInt(2);   // concentrated
            case 1 -> 6 + RANDOM.nextInt(3);   // balanced
            default -> 10 + RANDOM.nextInt(5); // broad
        };

        // Shuffle symbol selection deterministically per client
        List<String> shuffled = new ArrayList<>(symbols);
        java.util.Collections.shuffle(shuffled, new Random(clientId * 31L));

        for (int i = 0; i < holdingCount && i < shuffled.size(); i++) {
            String symbol = shuffled.get(i);
            // Share counts vary to create different portfolio values
            int shares = 5 + RANDOM.nextInt(196);
            holdings.put(symbol, shares);
        }

        return holdings;
    }

    /**
     * Lightweight holder for generated client seed data before metrics are computed.
     */
    public static class MockClientData {
        private final int id;
        private final String name;
        private final Map<String, Integer> holdings;

        public MockClientData(int id, String name, Map<String, Integer> holdings) {
            this.id = id;
            this.name = name;
            this.holdings = holdings;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Map<String, Integer> getHoldings() {
            return holdings;
        }
    }
}

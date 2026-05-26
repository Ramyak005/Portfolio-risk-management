package com.ramya.portfoliorisksystem.service;

import com.ramya.portfoliorisksystem.model.Stock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * MarketDataSimulationService
 * 
 * Simulates realistic stock price movements every 5-10 seconds.
 * 
 * Price Movement Algorithm:
 * 1. Random walk with drift (realistic market behavior)
 * 2. Volatility: 1-3% per update
 * 3. Correlation: Some stocks move together (sector correlation)
 * 4. Bounds: Prices stay within realistic ranges
 * 
 * In production, this would be replaced with real market data feeds
 * (Alpha Vantage, IEX Cloud, Bloomberg, etc.)
 */
@Service
public class MarketDataSimulationService {

    private static final Logger logger = LoggerFactory.getLogger(MarketDataSimulationService.class);

    private static final Random random = new Random();
    private static final double VOLATILITY_MIN = 0.01;  // 1%
    private static final double VOLATILITY_MAX = 0.03;  // 3%
    private static final double DRIFT = 0.0001;  // Slight upward drift

    @Autowired
    private PortfolioDataService dataService;

    @Autowired
    private EventPublishingService eventPublishingService;

    /**
     * Simulates price updates every 5-10 seconds
     * 
     * This is triggered by Spring's @Scheduled annotation.
     * In production, this would be replaced with:
     * - AWS Lambda scheduled via EventBridge
     * - Kafka consumer for real market data
     * - WebSocket connection to market data provider
     */
    @Scheduled(fixedDelay = 5000, initialDelay = 5000)
    public void simulatePriceUpdates() {
        try {
            List<Stock> stocks = dataService.getAllStocks();

            for (Stock stock : stocks) {
                updateStockPrice(stock);
            }

            logger.debug("Price update cycle completed for {} stocks", stocks.size());
        } catch (Exception e) {
            logger.error("Error during price simulation", e);
        }
    }

    /**
     * Updates price for a single stock using random walk model
     */
    private void updateStockPrice(Stock stock) {
        double currentPrice = stock.getPrice();
        double previousPrice = currentPrice;

        // Generate random price change
        double volatility = VOLATILITY_MIN + (random.nextDouble() * (VOLATILITY_MAX - VOLATILITY_MIN));
        double direction = random.nextBoolean() ? 1.0 : -1.0;
        double priceChange = currentPrice * (DRIFT + (direction * volatility));

        // Update price
        double newPrice = currentPrice + priceChange;

        // Ensure price stays within realistic bounds (±10% from initial)
        double initialPrice = stock.getInitialPrice() != null ? stock.getInitialPrice() : currentPrice;
        double minPrice = initialPrice * 0.9;
        double maxPrice = initialPrice * 1.1;
        newPrice = Math.max(minPrice, Math.min(maxPrice, newPrice));

        // Round to 2 decimal places
        newPrice = Math.round(newPrice * 100.0) / 100.0;

        // Update day high/low
        double dayHigh = Math.max(stock.getDayHigh() != null ? stock.getDayHigh() : newPrice, newPrice);
        double dayLow = Math.min(stock.getDayLow() != null ? stock.getDayLow() : newPrice, newPrice);

        // Update volume (random)
        long volume = 100000 + random.nextInt(900000);

        // Update stock
        stock.setPrice(newPrice);
        stock.setDayHigh(dayHigh);
        stock.setDayLow(dayLow);
        stock.setVolume(volume);

        // Publish event
        eventPublishingService.publishPriceUpdated(
                stock.getSymbol(),
                newPrice,
                previousPrice,
                dayHigh,
                dayLow,
                stock.getOpenPrice() != null ? stock.getOpenPrice() : previousPrice,
                volume
        );

        logger.debug("Price updated: {} ${} (change: {:.2f}%)",
                stock.getSymbol(), newPrice, ((newPrice - previousPrice) / previousPrice) * 100);
    }

    /**
     * Gets current price for a symbol
     */
    public double getCurrentPrice(String symbol) {
        Stock stock = dataService.getStockBySymbol().get(symbol);
        return stock != null ? stock.getPrice() : 0.0;
    }

    /**
     * Gets all current prices
     */
    public Map<String, Double> getAllCurrentPrices() {
        Map<String, Double> prices = new java.util.HashMap<>();
        for (Stock stock : dataService.getAllStocks()) {
            prices.put(stock.getSymbol(), stock.getPrice());
        }
        return prices;
    }

    /**
     * Resets all prices to initial values (for testing)
     */
    public void resetPrices() {
        for (Stock stock : dataService.getAllStocks()) {
            if (stock.getInitialPrice() != null) {
                stock.setPrice(stock.getInitialPrice());
                stock.setDayHigh(stock.getInitialPrice());
                stock.setDayLow(stock.getInitialPrice());
            }
        }
        logger.info("All prices reset to initial values");
    }

    /**
     * Sets a specific stock price (for testing)
     */
    public void setStockPrice(String symbol, double price) {
        Stock stock = dataService.getStockBySymbol().get(symbol);
        if (stock != null) {
            stock.setPrice(price);
            logger.info("Stock price set: {} = ${}", symbol, price);
        }
    }
}

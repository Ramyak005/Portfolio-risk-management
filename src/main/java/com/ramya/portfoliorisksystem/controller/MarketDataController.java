package com.ramya.portfoliorisksystem.controller;

import com.ramya.portfoliorisksystem.model.MarketPrice;
import com.ramya.portfoliorisksystem.model.Stock;
import com.ramya.portfoliorisksystem.service.MarketDataSimulationService;
import com.ramya.portfoliorisksystem.service.PortfolioDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MarketDataController
 * 
 * Endpoints for market data and price information
 */
@RestController
@RequestMapping("/api/v1/market")
public class MarketDataController {

    @Autowired
    private MarketDataSimulationService marketDataSimulationService;

    @Autowired
    private PortfolioDataService portfolioDataService;

    /**
     * GET /api/v1/market/prices
     * Get all current market prices
     */
    @GetMapping("/prices")
    public ResponseEntity<Map<String, Object>> getAllPrices(
            @RequestParam(required = false) String symbols,
            @RequestParam(defaultValue = "100") int limit) {

        List<Stock> stocks = portfolioDataService.getAllStocks();
        List<MarketPrice> prices = new ArrayList<>();

        for (Stock stock : stocks) {
            if (prices.size() >= limit) break;

            // Filter by symbols if provided
            if (symbols != null && !symbols.contains(stock.getSymbol())) {
                continue;
            }

            MarketPrice price = new MarketPrice();
            price.setSymbol(stock.getSymbol());
            price.setPrice(stock.getPrice());
            price.setChange(stock.getPrice() - (stock.getOpenPrice() != null ? stock.getOpenPrice() : stock.getPrice()));
            price.setChangePercent((price.getChange() / (stock.getOpenPrice() != null ? stock.getOpenPrice() : stock.getPrice())) * 100);
            price.setLastUpdated(Instant.now().toString());

            prices.add(price);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("prices", prices);
        response.put("count", prices.size());
        response.put("timestamp", Instant.now().toString());

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/market/prices/{symbol}
     * Get price for specific symbol
     */
    @GetMapping("/prices/{symbol}")
    public ResponseEntity<Map<String, Object>> getPrice(@PathVariable String symbol) {
        Stock stock = portfolioDataService.getStockBySymbol().get(symbol);

        if (stock == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("symbol", stock.getSymbol());
        response.put("price", stock.getPrice());
        response.put("previousPrice", stock.getOpenPrice() != null ? stock.getOpenPrice() : stock.getPrice());
        response.put("change", stock.getPrice() - (stock.getOpenPrice() != null ? stock.getOpenPrice() : stock.getPrice()));
        response.put("changePercent", ((stock.getPrice() - (stock.getOpenPrice() != null ? stock.getOpenPrice() : stock.getPrice())) / (stock.getOpenPrice() != null ? stock.getOpenPrice() : stock.getPrice())) * 100);
        response.put("dayHigh", stock.getDayHigh());
        response.put("dayLow", stock.getDayLow());
        response.put("openPrice", stock.getOpenPrice());
        response.put("volume", stock.getVolume());
        response.put("lastUpdated", Instant.now().toString());

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/market/stocks
     * Get all available stocks
     */
    @GetMapping("/stocks")
    public ResponseEntity<Map<String, Object>> getAllStocks() {
        List<Stock> stocks = portfolioDataService.getAllStocks();

        Map<String, Object> response = new HashMap<>();
        response.put("stocks", stocks);
        response.put("count", stocks.size());

        // Group by sector
        Map<String, List<Stock>> bySector = new HashMap<>();
        for (Stock stock : stocks) {
            bySector.computeIfAbsent(stock.getSector(), k -> new ArrayList<>()).add(stock);
        }
        response.put("bySector", bySector);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/market/stocks/{symbol}
     * Get stock details
     */
    @GetMapping("/stocks/{symbol}")
    public ResponseEntity<Stock> getStock(@PathVariable String symbol) {
        Stock stock = portfolioDataService.getStockBySymbol().get(symbol);

        if (stock == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(stock);
    }

    /**
     * POST /api/v1/market/reset-prices
     * Reset all prices to initial values (for testing)
     */
    @PostMapping("/reset-prices")
    public ResponseEntity<Map<String, String>> resetPrices() {
        marketDataSimulationService.resetPrices();

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "All prices reset to initial values");

        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/market/set-price
     * Set a specific stock price (for testing)
     */
    @PostMapping("/set-price")
    public ResponseEntity<Map<String, Object>> setPrice(
            @RequestParam String symbol,
            @RequestParam double price) {

        Stock stock = portfolioDataService.getStockBySymbol().get(symbol);
        if (stock == null) {
            return ResponseEntity.notFound().build();
        }

        marketDataSimulationService.setStockPrice(symbol, price);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("symbol", symbol);
        response.put("price", price);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/market/sectors
     * Get sector breakdown
     */
    @GetMapping("/sectors")
    public ResponseEntity<Map<String, Object>> getSectors() {
        List<Stock> stocks = portfolioDataService.getAllStocks();
        Map<String, List<Stock>> bySector = new HashMap<>();

        for (Stock stock : stocks) {
            bySector.computeIfAbsent(stock.getSector(), k -> new ArrayList<>()).add(stock);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("sectors", bySector);
        response.put("sectorCount", bySector.size());

        return ResponseEntity.ok(response);
    }
}

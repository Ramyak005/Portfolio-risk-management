package com.ramya.portfoliorisksystem.model;

/**
 * Represents a tradable stock in the market universe with sector metadata
 * used for diversification and risk analytics.
 */
public class Stock {

    private String symbol;
    private String name;
    private String sector;
    private double price;

    public Stock(String symbol, String name, String sector, double price) {
        this.symbol = symbol;
        this.name = name;
        this.sector = sector;
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public String getSector() {
        return sector;
    }

    public double getPrice() {
        return price;
    }
}
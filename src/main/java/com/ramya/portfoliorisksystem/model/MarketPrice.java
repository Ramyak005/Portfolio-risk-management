package com.ramya.portfoliorisksystem.model;

public class MarketPrice {

    private String stock;
    private double price;

    public MarketPrice(String stock, double price) {
        this.stock = stock;
        this.price = price;
    }

    public String getStock() {
        return stock;
    }

    public double getPrice() {
        return price;
    }
}
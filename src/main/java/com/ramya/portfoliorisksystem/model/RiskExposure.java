package com.ramya.portfoliorisksystem.model;

/**
 * Dollar-weighted exposure for a single stock position.
 */
public class RiskExposure {

    private String stock;
    private double exposurePercent;
    private double positionValue;

    public RiskExposure(String stock, double exposurePercent, double positionValue) {
        this.stock = stock;
        this.exposurePercent = exposurePercent;
        this.positionValue = positionValue;
    }

    public String getStock() {
        return stock;
    }

    public double getExposurePercent() {
        return exposurePercent;
    }

    public double getPositionValue() {
        return positionValue;
    }
}

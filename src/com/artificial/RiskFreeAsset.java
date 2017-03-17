package com.artificial;

public class RiskFreeAsset extends Stock {
    public static final RiskFreeAsset TEN_YEAR_T_BILLS = new RiskFreeAsset("^TNX");
    public static final RiskFreeAsset[] VALUES = {TEN_YEAR_T_BILLS};

    private RiskFreeAsset(String symbol) {
        super(symbol);
    }

    @Override
    public double getAnnualReturn() {
        return getPrice();
    }
}

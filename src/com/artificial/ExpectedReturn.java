package com.artificial;

public class ExpectedReturn {
    public static double stockEquityPremiumEarningBased(final Stock stock) {
        return (stock.getEps() / stock.getPrice()) * 100;
    }

    public static double stockEquityPremiumDividendBased(final Stock stock, final int years) {
        return stock.getDividendYield() + stock.getHistory(years).getAvgDividendGrowthRate();
    }

}

package com.artificial;

public class MarketIndex extends Stock {
    public static final MarketIndex SP_500 = new MarketIndex("^GSPC");
    public static final MarketIndex DOW_JONES = new MarketIndex("^DJI");
    public static final MarketIndex NASDAQ = new MarketIndex("^IXIC");
    public static final MarketIndex NASDAQ_100 = new MarketIndex("^NDX");
    public static final MarketIndex[] VALUES = {SP_500, DOW_JONES, NASDAQ, NASDAQ_100};

    public MarketIndex(final String symbol) {
        super(symbol);
    }

}

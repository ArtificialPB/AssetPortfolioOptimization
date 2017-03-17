package com.artificial;

import com.artificial.parsers.Parser;

public enum QuoteParameter {
    OPEN(
            "Open"
    ),
    EARNINGS_PER_SHARE(
            "EarningsShare"
    ),
    NAME(
            "Name"
    ),
    DIVIDENDS_PER_SHARE(
            "DividendsShare"
    ),
    DIVIDEND_YIELD(
            "DividendYield"
    ),;
    private final String yahooKey;

    QuoteParameter(String yahooKey) {
        this.yahooKey = yahooKey;
    }

    public String getKey(final Parser parser) {
        switch (parser.getParserName()) {
            case Parser.YAHOO_FINANCE:
                return yahooKey;
        }
        //should not get to this point
        return null;
    }
}

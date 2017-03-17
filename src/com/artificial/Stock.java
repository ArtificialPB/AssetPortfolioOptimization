package com.artificial;

import com.artificial.json.JsonObject;
import com.artificial.json.JsonValue;

import java.io.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Stock implements Security, Asset {
    public static final String SYMBOLS_KEY = "SYMBOLS_KEY";
    public static final String QUOTE_URL = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20(" + SYMBOLS_KEY + ")&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
    public static final String SYMBOL_SEPARATOR = "%22";
    private final Map<DateKey, AssetHistory> stockHistory = new HashMap<>();
    private String symbol, name;
    private double eps, price, dividends, dividendYield;
    private boolean loaded = true;

    public Stock(String symbol) {
        this.symbol = symbol;
        try {
            final JsonObject parent = JsonObject.readFrom(getQuoteData());
            if (!validateQuote(parent)) {
                throw new Exception("Quote for " + symbol + " is not valid!");
            }
            final JsonObject quote = parent.get("query").asObject().get("results").asObject().get("quote").asObject();
            //percent
            final JsonValue eps = quote.get("EarningsShare");
            final JsonValue price = quote.get("Open");
            final JsonValue dividends = quote.get("DividendShare");
            final JsonValue name = quote.get("Name");
            //percent
            final JsonValue dividendYield = quote.get("DividendYield");

            this.eps = eps.isNull() ? 0 : Double.parseDouble(eps.asString());
            this.price = price.isNull() ? 0 : Double.parseDouble(price.asString());
            this.dividends = dividends.isNull() ? 0 : Double.parseDouble(dividends.asString());
            this.name = name.isNull() ? "" : name.asString();
            this.dividendYield = dividendYield.isNull() ? 0 : Double.parseDouble(dividendYield.asString());
        } catch (Exception e) {
            e.printStackTrace();
            loaded = false;
        }
    }

    private boolean validateQuote(final JsonObject quote) {
        if (quote.get("error") != null) {
            return false;
        }
        return !quote.get("query").asObject().get("results").asObject().get("quote").asObject().get("BookValue").isNull();
    }

    private String getQuoteData() throws IOException {
        final Calendar curr = Calendar.getInstance();
        final String fileName = symbol + "_QUOTE_" + new Date(curr.get(Calendar.YEAR), curr.get(Calendar.MONTH) + 1, curr.get(Calendar.DAY_OF_MONTH));
        final File file = FileManager.getFile(fileName);
        if (file.exists()) {
            String ret = "";
            try (final BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String s;
                while ((s = reader.readLine()) != null) {
                    ret += s;
                }
            }
            return ret;
        } else {
            file.createNewFile();
        }
        final String symbolData = SYMBOL_SEPARATOR + symbol + SYMBOL_SEPARATOR;
        final String data = HttpUtil.downloadString(QUOTE_URL.replace(SYMBOLS_KEY, symbolData));
        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(data);
        }
        return data;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public double getDividendYield() {
        return dividendYield;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public String getName() {
        return name;
    }

    public double getEps() {
        return eps;
    }

    public double getPrice() {
        return price;
    }

    public double getDividends() {
        return dividends;
    }

    @Override
    public AssetHistory getHistory(final Date start, final Date end) {
        final DateKey key = new DateKey(start, end);
        if (stockHistory.containsKey(key)) {
            return stockHistory.get(key);
        }
        final AssetHistory history = new AssetHistory(symbol, start, end);
        stockHistory.put(key, history);
        return history;
    }

    @Override
    public String toString() {
        return getName();
    }
}

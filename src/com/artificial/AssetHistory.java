package com.artificial;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AssetHistory {
    private final String symbol;
    private final Date startDate, endDate;
    private List<Price> price_history = null;
    private List<Dividend> dividend_history = null;

    public AssetHistory(String symbol, Date startDate, Date endDate) {
        this.symbol = symbol.toUpperCase();
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getSymbol() {
        return symbol;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public List<Price> getPriceHistory() {
        if (price_history == null) {
            loadPriceHistory();
        }
        return price_history;
    }

    public List<Dividend> getDividendHistory() {
        if (dividend_history == null) {
            loadDividendHistory();
        }
        return dividend_history;
    }

    @NumberData(type = NumberType.PERCENT)
    public double[] getMonthlyEarnings() {
        final List<Price> histories = getPriceHistory();
        if (histories == null) {
            return null;
        }
        //oldest data is not included, because it would have 0% earnings
        final double[] earnings = new double[histories.size() - 1];
        for (int i = histories.size() - 2; i >= 0; i--) {
            final Price previous = histories.get(i + 1);
            final Price current = histories.get(i);
            earnings[i] = ((current.getAdjustedClose() / previous.getAdjustedClose()) - 1) * 100;
        }
        return earnings;
    }

    @NumberData(type = NumberType.PERCENT)
    public double getAvgDividendGrowthRate() {
        final List<Dividend> histories = getDividendHistory();
        if (histories == null) {
            return 0;
        }
        final double[] grouped = groupDivByYear(histories);
        //oldest data is not included, because it would have 0%
        double totalGrowth = 0;
        for (int i = grouped.length - 2; i >= 0; i--) {
            final double previous = grouped[i + 1];
            final double current = grouped[i];
            totalGrowth += ((current / previous) - 1) * 100;
        }
        return totalGrowth / grouped.length - 1;
    }

    private double[] groupDivByYear(final List<Dividend> dividends) {
        final Map<Integer, Double> year_dividend = new LinkedHashMap<>();
        for (Dividend d : dividends) {
            final int year = d.getDate().getYear();
            final double amount = d.getAmount();
            if (year_dividend.containsKey(year)) {
                year_dividend.put(year, year_dividend.get(year) + amount);
            } else {
                year_dividend.put(year, amount);
            }
        }
        int i = 0;
        final double[] ret = new double[year_dividend.size()];
        for (Integer key : year_dividend.keySet()) {
            ret[i++] = year_dividend.get(key);
        }
        return ret;

    }

    private void loadPriceHistory() {
        final File file = FileManager.getFile(Price.fileName(symbol, startDate, endDate));
        if (!file.exists()) {
            try {
                HttpUtil.downloadFile(Price.getUrl(symbol, startDate, endDate), file);
            } catch (IOException e) {
                Debug.info("Failed to download price history for " + symbol + "!");
                e.printStackTrace();
                return;
            }
        }
        price_history = generatePriceHistory(file);
    }

    private List<Price> generatePriceHistory(final File file) {
        final List<Price> ret = new LinkedList<>();
        try (final BufferedReader reader = new BufferedReader(new FileReader(file))) {
            //Read first line to skip header
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                final String[] data = line.split(",");
                //yyyy-mm-dd
                final String[] dateData = data[0].split("-");
                final Date date = new Date(Integer.parseInt(dateData[0]), Integer.parseInt(dateData[1]), Integer.parseInt(dateData[2]));
                ret.add(
                        new Price(
                                date,
                                Double.parseDouble(data[1]),
                                Double.parseDouble(data[2]),
                                Double.parseDouble(data[3]),
                                Double.parseDouble(data[4]),
                                Long.parseLong(data[5]),
                                Double.parseDouble(data[6])
                        )
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return ret;
    }

    private void loadDividendHistory() {
        final File file = FileManager.getFile(Dividend.fileName(symbol, startDate, endDate));
        if (!file.exists()) {
            try {
                HttpUtil.downloadFile(Dividend.getUrl(symbol, startDate, endDate), file);
            } catch (IOException e) {
                Debug.info("Failed to download dividend history for " + symbol + "!");
                e.printStackTrace();
                return;
            }
        }
        dividend_history = generateDividendHistory(file);
    }

    private List<Dividend> generateDividendHistory(final File file) {
        final List<Dividend> ret = new LinkedList<>();
        try (final BufferedReader reader = new BufferedReader(new FileReader(file))) {
            //Read first line to skip header
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                final String[] data = line.split(",");
                //yyyy-mm-dd
                final String[] dateData = data[0].split("-");
                final Date date = new Date(Integer.parseInt(dateData[0]), Integer.parseInt(dateData[1]), Integer.parseInt(dateData[2]));
                ret.add(
                        new Dividend(
                                date,
                                Double.parseDouble(data[1])
                        )
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return ret;
    }

    public static class Price {
        //symbol - mm/dd/yyyy - mm/dd/yyy
        //months set as index (december = 11, january = 0)
        private static final String PRICE_HISTORY_URL = "https://chart.finance.yahoo.com/table.csv?s=%s&a=%d&b=%d&c=%d&d=%d&e=%d&f=%d&g=m&ignore=.csv";
        private final Date date;
        private final long volume;
        private final double open, high, low, close, adjustedClose;

        public Price(Date date, double open, double high, double low, double close, long volume, double adjustedClose) {
            this.date = date;
            this.open = open;
            this.high = high;
            this.low = low;
            this.close = close;
            this.volume = volume;
            this.adjustedClose = adjustedClose;
        }

        private static String getUrl(final String symbol, final Date startDate, final Date endDate) {
            return String.format(
                    PRICE_HISTORY_URL,
                    symbol,
                    startDate.getMonth() - 1,
                    startDate.getDay(),
                    startDate.getYear(),
                    endDate.getMonth() - 1,
                    endDate.getDay(),
                    endDate.getYear()
            );
        }

        private static String fileName(final String symbol, final Date startDate, final Date endDate) {
            return symbol + "_Price_" + startDate + "-" + endDate;
        }

        public Date getDate() {
            return date;
        }

        public long getVolume() {
            return volume;
        }

        public double getOpen() {
            return open;
        }

        public double getHigh() {
            return high;
        }

        public double getLow() {
            return low;
        }

        public double getClose() {
            return close;
        }

        public double getAdjustedClose() {
            return adjustedClose;
        }

        @Override
        public String toString() {
            return "[com.artificial.AssetHistory.Price] date=" + date + ", open=" + open + ", high=" + high + ", low=" + low + ", close=" + close + ", volume=" + volume + ", adjClose=" + adjustedClose;
        }
    }

    public static final class Dividend {
        //same format as price
        private static final String DIVIDEND_HISTORY_URL = "https://chart.finance.yahoo.com/table.csv?s=%s&a=%d&b=%d&c=%d&d=%d&e=%d&f=%d&g=v&ignore=.csv";
        private final Date date;
        private final double amount;

        public Dividend(Date date, double amount) {
            this.date = date;
            this.amount = amount;
        }

        private static String getUrl(final String symbol, final Date startDate, final Date endDate) {
            return String.format(
                    DIVIDEND_HISTORY_URL,
                    symbol,
                    startDate.getMonth() - 1,
                    startDate.getDay(),
                    startDate.getYear(),
                    endDate.getMonth() - 1,
                    endDate.getDay(),
                    endDate.getYear()
            );
        }

        private static String fileName(final String symbol, final Date startDate, final Date endDate) {
            return symbol + "_Div_" + startDate + "-" + endDate;
        }

        public Date getDate() {
            return date;
        }

        public double getAmount() {
            return amount;
        }
    }
}

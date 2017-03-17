package com.artificial;

import org.math.plot.Plot2DPanel;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Portfolio {
    private final Map<Stock, Double> stockWeightMap = new LinkedHashMap<>();

    private static void drawRiskFreeAsset(final Plot2DPanel plot, final Portfolio p, final RiskFreeAsset riskFreeAsset) {
        final int size = (int) ((p.getMaxStandardDeviation(3) * 1.1) + 0.5);
        final double[] x = new double[size], y = new double[size];
        final double riskFreeReturn = riskFreeAsset.getAnnualReturn();
        final double gradient = (p.getAnnualReturn() - riskFreeReturn) / p.getStandardDeviation();
        for (int i = 0; i < size; i++) {
            x[i] = i;
            //y = n + angle * x
            y[i] = riskFreeReturn + gradient * i;
        }
        plot.addLinePlot("Risk-free asset", Color.RED, x, y);
    }

    public double getAnnualReturn() {
        return getAnnualReturn(Asset.DEFAULT_YEARS);
    }

    public double getAnnualReturn(final int years) {
        double ret = 0;
        for (final Stock s : stockWeightMap.keySet()) {
            ret += stockWeightMap.get(s) * s.getAnnualReturn(years);
        }
        return ret;
    }

    public double getSharpeRatio() {
        return getSharpeRatio(Asset.DEFAULT_YEARS);
    }

    public double getSharpeRatio(final int years) {
        return (Statistics.annualToMonthlyRate(getAnnualReturn(years)) - Statistics.annualToMonthlyRate(RiskFreeAsset.TEN_YEAR_T_BILLS.getAnnualReturn())) / (getStandardDeviation(years));
    }

    public double getBeta() {
        return getBeta(Asset.DEFAULT_YEARS);
    }

    public double getBeta(final int years) {
        double beta = 0;
        for (final Stock s : stockWeightMap.keySet()) {
            beta += stockWeightMap.get(s) * s.getBeta(years, MarketIndex.SP_500);
        }
        return beta;
    }

    public void addStock(final Stock s, final double weight) {
        stockWeightMap.put(s, weight);
    }

    public void addStock(final Stock s) {
        stockWeightMap.put(s, 0d);
        Debug.info(s.getName() + " - " + s.getAnnualReturn());
    }

    public void removeStock(final Stock stock) {
        stockWeightMap.remove(stock);
    }

    public Map<Stock, Double> getStockWeights() {
        return new HashMap<>(stockWeightMap);
    }

   /* public double getVariance(final int years) {
        double total = 0;
        for (final com.artificial.Stock s1 : stocks) {
            final double w1 = stockWeightMap.get(s1);
            for (final com.artificial.Stock s2 : stocks) {
                total += stockWeightMap.get(s2) * w1 * com.artificial.Statistics.covariance(s1.getHistory(years).getMonthlyEarnings(), s2.getHistory(years).getMonthlyEarnings());
            }
        }
        return total;
    }*/

    public void setWeight(final Stock s, final double weight) {
        if (!stockWeightMap.containsKey(s)) {
            return;
        }
        stockWeightMap.replace(s, weight);
    }

    public double getMaxAssetReturn(final int years) {
        double max = 0;
        for (Stock s : stockWeightMap.keySet()) {
            final double r;
            if ((r = s.getAnnualReturn(years)) > max) {
                max = r;
            }
        }
        return max;
    }

    public double getMaxStandardDeviation(final int years) {
        double max = 0;
        for (Stock s : stockWeightMap.keySet()) {
            final double r;
            if ((r = Statistics.standardDeviation(s.getHistory(years).getMonthlyEarnings())) > max) {
                max = r;
            }
        }
        return max;
    }

    public double getVariance(final int years) {
        final Set<Stock> set;
        final Stock[] stocks = (set = stockWeightMap.keySet()).toArray(new Stock[set.size()]);
        final double[] weightsLinear = new double[stocks.length];
        final double[][] covariances = new double[stocks.length][];
        final double[][] weightsVertical = new double[stocks.length][1];
        for (int i = 0; i < stocks.length; i++) {
            final Stock s1 = stocks[i];
            weightsLinear[i] = stockWeightMap.get(s1);
            weightsVertical[i][0] = weightsLinear[i];
            final double[] iCov = new double[stocks.length];
            for (int j = 0; j < stocks.length; j++) {
                final Stock s2 = stocks[j];
                iCov[j] = Statistics.covariance(s1.getHistory(years).getMonthlyEarnings(), s2.getHistory(years).getMonthlyEarnings());
            }
            covariances[i] = iCov;
        }
        final Matrix m1 = new Matrix(weightsLinear), m2 = new Matrix(covariances), m3 = new Matrix(weightsVertical);
        return m1.multiply(m2).multiply(m3).get(0, 0);
    }

    /*portfolio risk*/
    public double getStandardDeviation() {
        return getStandardDeviation(Asset.DEFAULT_YEARS);
    }

    /*portfolio risk*/
    public double getStandardDeviation(final int years) {
        return Math.sqrt(getVariance(years));
    }

    //Elton, Gruber; Modern portfolio theory and investment analysis (1991)
    public void optimizationOptimalRiskyPortfolio(final RiskFreeAsset riskFree, final Plot2DPanel plot) {
        final int size;
        final Stock[] stocks = stockWeightMap.keySet().toArray(new Stock[(size = stockWeightMap.keySet().size())]);
        final double[] returns = new double[size];
        final double rf = riskFree.getAnnualReturn();
        final Matrix cov = new Matrix(covarianceMatrix()).multiply(new Matrix(correlationMatrix()));
        for (int i = 0; i < size; i++) {
            returns[i] = (stocks[i].getAnnualReturn() - rf);
        }
        final double[] determinants = new double[size];
        for (int i = 0; i < size; i++) {
            determinants[i] = cov.replaceColumn(i, returns).determinant();
        }
        double total_det = 0;
        for (final double d : determinants) {
            total_det += d;
        }
        for (int i = 0; i < size; i++) {
            stockWeightMap.put(stocks[i], (determinants[i] / total_det));
        }
        //drawRiskFreeAsset(plot, this, riskFree);
    }

    private void optimizationOptimalRiskyPortfolio(final RiskFreeAsset riskFree, final Matrix cov) {
        final int size;
        final Stock[] stocks = stockWeightMap.keySet().toArray(new Stock[(size = stockWeightMap.keySet().size())]);
        final double[] returns = new double[size];
        final double rf = riskFree.getAnnualReturn();
        for (int i = 0; i < size; i++) {
            returns[i] = (stocks[i].getAnnualReturn() - rf);
        }
        final double[] determinants = new double[size];
        for (int i = 0; i < size; i++) {
            determinants[i] = cov.replaceColumn(i, returns).determinant();
        }
        double total_det = 0;
        for (final double d : determinants) {
            total_det += d;
        }
        for (int i = 0; i < size; i++) {
            stockWeightMap.put(stocks[i], (determinants[i] / total_det));
        }
    }

    //Based on http://www.calculatinginvestor.com/2011/06/07/efficient-frontier-1/
    public void efficientFrontier(final Plot2DPanel plot) {
        final long start = System.currentTimeMillis();
        final Matrix cov = new Matrix(covarianceMatrix());
        final Matrix covInverse = cov.inverse();
        final Matrix returnsMatrix = new Matrix(expectedReturnsMatrix());
        final Matrix unityMatrix = Matrix.unitMatrix(returnsMatrix.getRows(), returnsMatrix.getColumns(), 1);
        final Matrix A = unityMatrix.transpose().multiply(covInverse).multiply(unityMatrix);
        final Matrix B = unityMatrix.transpose().multiply(covInverse).multiply(returnsMatrix);
        final Matrix C = returnsMatrix.transpose().multiply(covInverse).multiply(returnsMatrix);
        final Matrix D = A.multiply(C).subtract(B.power(2));
        final int size = 700;
        final double[][] arr = new double[1][size];
        int currVal = -(size / 2);
        for (int i = 0; i < size; i++) {
            arr[0][i] = (currVal++ + 1d) / 10d;
        }
        final Matrix mu = new Matrix(arr);
        final double[][] arr2 = new double[1][size];
        for (int i = 0; i < arr[0].length; i++) {
            arr2[0][i] = arr[0][i] * arr[0][i];
        }
        final Matrix muPower = new Matrix(arr2);
        final Matrix minVar = ((A.multiply(muPower)).add(B.multiply(-2).multiply(mu).add(C.get(0, 0)))).divide(D.get(0, 0));
        final double[] minStd = new double[minVar.getColumns()];
        for (int i = 0; i < minVar.getColumns(); i++) {
            minStd[i] = Math.sqrt(Math.min(Integer.MAX_VALUE, minVar.get(0, i)));
            Debug.info(minStd[i]);
        }
        final double[] portfolioReturns = arr[0];
        Debug.info("Optimized in " + (System.currentTimeMillis() - start) + "ms");

        plot.addLinePlot("Frontier", Color.GREEN, minStd, portfolioReturns);
        final Stock[] stocks = stockWeightMap.keySet().toArray(new Stock[stockWeightMap.size()]);
        final double[] stockStd = new double[stocks.length];
        final double[] stockReturn = new double[stocks.length];
        for (int i = 0; i < stocks.length; i++) {
            stockStd[i] = stocks[i].getStandardDeviation();
            stockReturn[i] = stocks[i].getAnnualReturn();
        }
        plot.addScatterPlot("Individual", Color.RED, stockStd, stockReturn);
    }

    private double[][] covarianceMatrix() {
        final Stock[] stocks = stockWeightMap.keySet().toArray(new Stock[stockWeightMap.size()]);
        final double[][] ret = new double[stocks.length][stocks.length];
        for (int i = 0; i < stocks.length; i++) {
            final Stock s1 = stocks[i];
            for (int j = 0; j < stocks.length; j++) {
                final Stock s2 = stocks[j];
                ret[i][j] = Statistics.covariance(s1.getHistory().getMonthlyEarnings(), s2.getHistory().getMonthlyEarnings());
            }
        }
        return ret;
    }

    private double[][] correlationMatrix() {
        final Stock[] stocks = stockWeightMap.keySet().toArray(new Stock[stockWeightMap.size()]);
        final double[][] ret = new double[stocks.length][stocks.length];
        for (int i = 0; i < stocks.length; i++) {
            final Stock s1 = stocks[i];
            for (int j = 0; j < stocks.length; j++) {
                final Stock s2 = stocks[j];
                ret[i][j] = LinearRegression.Simple.correlationCoefficient(s1.getHistory().getMonthlyEarnings(), s2.getHistory().getMonthlyEarnings());
            }
        }
        return ret;
    }

    private double[][] expectedReturnsMatrix() {
        final Stock[] stocks = stockWeightMap.keySet().toArray(new Stock[stockWeightMap.size()]);
        final double[][] ret = new double[stocks.length][1];
        for (int i = 0; i < stocks.length; i++) {
            final Stock s1 = stocks[i];
            ret[i][0] = s1.getAnnualReturn();
        }
        return ret;
    }

}

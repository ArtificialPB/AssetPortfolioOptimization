package com.artificial;

import java.util.Calendar;

public interface Asset {
    int DEFAULT_YEARS = 3;

    @NumberData(Period.MONTHLY)
    default double getStandardDeviation(final int years) {
        final AssetHistory history = getHistory(years);
        final double[] earnings = history.getMonthlyEarnings();
        return Statistics.standardDeviation(earnings);
    }

    @NumberData(Period.MONTHLY)
    default double getStandardDeviation() {
        return getStandardDeviation(DEFAULT_YEARS);
    }

    default double getBeta() {
        return getBeta(DEFAULT_YEARS);
    }

    default double getBeta(final MarketIndex index) {
        return getBeta(DEFAULT_YEARS, index);
    }

    default double getBeta(final int years) {
        return getBeta(years, MarketIndex.SP_500);
    }

    default double getBeta(final int years, final MarketIndex index) {
        return LinearRegression.Simple.regressionCoefficient(index.getHistory(years).getMonthlyEarnings(), getHistory(years).getMonthlyEarnings());
    }

    @NumberData(Period.ANNUAL)
    default double getAnnualReturn(final int years) {
        return Statistics.monthlyToAnnualRate(Statistics.geometricAveragePercents(getHistory(years).getMonthlyEarnings()));
    }

    @NumberData(Period.ANNUAL)
    default double getAnnualReturn() {
        return getAnnualReturn(DEFAULT_YEARS);
    }

    AssetHistory getHistory(final Date start, final Date end);

    default AssetHistory getHistory(final int years) {
        final Calendar curr = Calendar.getInstance();
        final int year = curr.get(Calendar.YEAR), month = curr.get(Calendar.MONTH) + 1, day = curr.get(Calendar.DAY_OF_MONTH);
        return getHistory(new Date(year - years, month, day), new Date(year, month, day));
    }

    default AssetHistory getHistory() {
        return getHistory(Asset.DEFAULT_YEARS);
    }

    @NumberData(Period.MONTHLY)
    default double getSharpeRatio() {
        return getSharpeRatio(Asset.DEFAULT_YEARS);
    }

    @NumberData(Period.MONTHLY)
    default double getSharpeRatio(final int years) {
        return getSharpeRatio(RiskFreeAsset.TEN_YEAR_T_BILLS, years);
    }

    @NumberData(Period.MONTHLY)
    default double getSharpeRatio(final RiskFreeAsset riskFree, final int years) {
        final double[] earnings = getHistory(years).getMonthlyEarnings();
        final double averageReturn = Statistics.geometricAveragePercents(earnings);
        //We turn monthly data to annual
        return (Statistics.annualToMonthlyRate(averageReturn) - Statistics.annualToMonthlyRate(riskFree.getAnnualReturn())) / (Statistics.standardDeviation(earnings));
    }

}

package com.artificial;

public class LinearRegression {
    public static class Simple {
        /*beta*/
        public static double regressionCoefficient(final double[] data_x, final double[] data_y) {
            return Statistics.covariance(data_x, data_y) / Statistics.variance(data_x);
        }

        public static double regressionConstant(final double[] data_x, final double[] data_y) {
            return Statistics.meanAverage(data_y) - regressionCoefficient(data_x, data_y) * Statistics.meanAverage(data_x);
        }

        /*R^2 - R squared*/
        public static double determinationCoefficient(final double[] data_x, final double[] data_y) {
            return Math.pow(Statistics.covariance(data_x, data_y), 2) / (Statistics.variance(data_x) * Statistics.variance(data_y));
        }

        public static double correlationCoefficient(final double[] data_x, final double[] data_y) {
            return Statistics.covariance(data_x, data_y) / (Statistics.standardDeviation(data_x) * Statistics.standardDeviation(data_y));
        }

    }
}

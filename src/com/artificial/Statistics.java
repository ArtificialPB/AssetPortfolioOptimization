package com.artificial;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Statistics {
    public static double meanAverage(final double[] data) {
        double sum = 0;
        for (double d : data) {
            sum += d;
        }
        return sum / data.length;
    }

    public static double geometricAverage(final double[] data) {
        double total = 1;
        for (double d : data) {
            total = (total * d);
        }
        return Math.pow(total, 1.0 / data.length);
    }

    public static double geometricAveragePercents(final double[] data) {
        double total = 1;
        for (double d : data) {
            total = (total * (100 + d));
        }
        return Math.pow(total, 1.0 / data.length) - 100;
    }

    public static double variance(final double[] data) {
        final double mean = meanAverage(data);
        double var = 0;
        for (final double d : data) {
            var += Math.pow(d - mean, 2.0);
        }
        return var / data.length;
    }

    /*i.e. -3% - > (100% - 3%) = 97% */
    public static double[] absoluteToRelativePercent(final double[] data) {
        final double[] ret = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            ret[i] = (100 + data[i]);
        }
        return ret;
    }

    public static double standardDeviation(final double[] data) {
        return Math.sqrt(variance(data));
    }

    public static double covariance(final double[] data_x, final double[] data_y) {
        final int size = Math.min(data_x.length, data_y.length);
        final double mean_x = meanAverage(Arrays.copyOf(data_x, size)), mean_y = meanAverage(Arrays.copyOf(data_y, size));
        double cov = 0;
        for (int i = 0; i < size; i++) {
            final double x = data_x[i], y = data_y[i];
            cov += ((y - mean_y) * (x - mean_x));
        }
        return cov / size;
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T[]> uniqueCombinations(T[] arr, int len) {
        final List<T[]> results = new LinkedList<>();
        for (int i = 0; i < arr.length; i++) {
            final T[] partial = (T[]) Array.newInstance(arr.getClass().getComponentType(), len);
            partial[0] = arr[i];
            uniqueCombinationsRecursive(arr, partial, len, len - 1, i + 1, results);
        }
        return results;
    }

    private static <T> void uniqueCombinationsRecursive(T[] arr, T[] partial, int origLen, int nextLen, int startPosition, List<T[]> results) {
        if (nextLen == 0) {
            results.add(partial);
            return;
        }
        for (int i = startPosition; i <= arr.length - nextLen; i++) {
            final T[] partial2 = Arrays.copyOf(partial, origLen);
            partial2[partial.length - nextLen] = arr[i];
            uniqueCombinationsRecursive(arr, partial2, origLen, nextLen - 1, i + 1, results);
        }
    }

    public static double annualToMonthlyRate(final double annual) {
        return (Math.pow((1.0 + (annual / 100.0)), 1.0 / 12.0) - 1.0) * 100.0;
    }

    public static double monthlyToAnnualRate(final double monthly) {
        return (Math.pow((monthly / 100.0) + 1.0, 12.0) - 1.0) * 100;
    }

}

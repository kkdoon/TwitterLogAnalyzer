package org.twitter.analytics.core;

/**
 * Created by kkdoon on 11/4/16.
 */
public class AverageCalculator {

    public static double calculate(double prevAvg, long num, long count) {
        if(count > 0) {
            prevAvg += (num - prevAvg) / count;
            return  prevAvg;
        }
        return 0.0;
    }
}

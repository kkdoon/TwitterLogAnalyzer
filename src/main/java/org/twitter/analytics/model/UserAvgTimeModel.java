package org.twitter.analytics.model;

/**
 * Created by kkdoon on 11/4/16.
 */
public class UserAvgTimeModel {
    private double average;
    private long count;

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}

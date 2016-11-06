package org.twitter.analytics.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * Stores average value for given tick.
 */
public class UserAvgTimeModel {
    private String userId;
    private double average;
    @JsonIgnore
    private UserTick oldTick;
    @JsonIgnore
    private UserTick newTick;
    @JsonIgnore
    private long count;

    public UserAvgTimeModel(String userId) {
        this.userId = userId;
        this.average = 0.0;
        this.count = 0;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

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

    public UserTick getNewTick() {
        return newTick;
    }

    public void setNewTick(UserTick newTick) {
        this.newTick = newTick;
    }

    public UserTick getOldTick() {
        return oldTick;
    }

    public void setOldTick(UserTick oldTick) {
        this.oldTick = oldTick;
    }
}

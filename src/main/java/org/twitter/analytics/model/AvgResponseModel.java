package org.twitter.analytics.model;

/**
 * Created by kkdoon on 11/5/16.
 */
public class AvgResponseModel {
    private Long interval;
    private UserTick nextUserTick;

    public AvgResponseModel() {
        interval = null;
        nextUserTick = null;
    }

    public Long getInterval() {
        return interval;
    }

    public void setInterval(Long interval) {
        this.interval = interval;
    }

    public UserTick getNextUserTick() {
        return nextUserTick;
    }

    public void setNextUserTick(UserTick nextUserTick) {
        this.nextUserTick = nextUserTick;
    }
}

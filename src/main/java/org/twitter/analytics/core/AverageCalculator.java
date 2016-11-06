package org.twitter.analytics.core;

import org.apache.log4j.Logger;
import org.twitter.analytics.model.AvgResponseModel;
import org.twitter.analytics.model.UserAvgTimeModel;
import org.twitter.analytics.model.UserTick;
import org.twitter.analytics.policy.DefaultOCPolicy;
import org.twitter.analytics.policy.OCPolicy;
import org.twitter.analytics.util.PolicyFactoryUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kkdoon on 11/4/16.
 */
public class AverageCalculator {
    private final static Logger LOG = Logger.getLogger(AverageCalculator.class);
    private Map<String, UserAvgTimeModel> map;
    private OCPolicy policy;

    public AverageCalculator(OCPolicy policy) {
        map = new HashMap<String, UserAvgTimeModel>();
        this.policy = policy;
    }

    public void addTick(UserTick tick) {
        if (tick == null) return;

        if (!map.containsKey(tick.getUserID())) {
            map.put(tick.getUserID(), new UserAvgTimeModel(tick.getUserID()));
        }

        UserAvgTimeModel avgObj = map.get(tick.getUserID());

        if (avgObj.getOldTick() == null) {
            avgObj.setOldTick(tick);
        } else {
            avgObj.setNewTick(tick);
            // Calculate avg
            AvgResponseModel response = policy.calculate(avgObj.getOldTick(), avgObj.getNewTick());
            handlePolicyRespone(response, avgObj);
        }
    }

    private void handlePolicyRespone(AvgResponseModel response, UserAvgTimeModel avgObj) {
        if (response != null) {
            if (response.getInterval() != null) {
                setAverage(avgObj.getUserId(), response.getInterval());
            }
            // Setting the open tick for next interval
            if (response.getNextUserTick() != null) {
                avgObj.setOldTick(response.getNextUserTick());
            } else {
                avgObj.setOldTick(null);
            }
        } else {
            avgObj.setOldTick(null);
        }
        avgObj.setNewTick(null);
    }

    private void setAverage(String userId, long interval) {
        UserAvgTimeModel avgObj = map.get(userId);
        long count = avgObj.getCount() + 1;
        avgObj.setAverage(calculateAverage(avgObj.getAverage(), interval, count));
        avgObj.setCount(count);
    }

    public double getAverage(String userId) {
        UserAvgTimeModel obj = map.get(userId);
        return obj != null ? obj.getAverage() : 0.0;
    }

    public UserAvgTimeModel getAverageModel(String userId) {
        return map.get(userId);
    }

    private double calculateAverage(double prevAvg, long num, long count) {
        if (count > 0) {
            prevAvg += (num - prevAvg) / count;
            return prevAvg;
        }
        return 0.0;
    }

    public void flushAvg(String userId) {
        UserAvgTimeModel obj = map.get(userId);
        if (obj != null && obj.getOldTick() != null) {
            LOG.info("Flushing values for userId " + userId);
            AvgResponseModel response = policy.calculate(obj.getOldTick(), obj.getNewTick());
            handlePolicyRespone(response, obj);
        }
    }
}

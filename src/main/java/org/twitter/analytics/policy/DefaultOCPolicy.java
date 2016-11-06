package org.twitter.analytics.policy;

import org.twitter.analytics.core.AverageCalculator;
import org.twitter.analytics.model.Operation;
import org.twitter.analytics.model.UserAvgTimeModel;
import org.twitter.analytics.model.UserModel;

/**
 * Created by kkdoon on 11/5/16.
 */
public class DefaultOCPolicy implements OCPolicy {

    public UserModel calculate(UserModel oldTick, UserModel newTick, UserAvgTimeModel avgObj) {
        if (oldTick == null || newTick == null) {
            System.out.println("Skipping tick pair as sufficient info not provided " + (avgObj != null ? " user: " + avgObj.getUserId() : ""));
            return null;
        }

        UserModel nextTick = null;
        Operation op1 = Operation.valueOf(oldTick.getOperation());
        Operation op2 = Operation.valueOf(newTick.getOperation());
        if (Operation.open == op1 && Operation.close == op2) {
            long interval = newTick.getTimestamp() - oldTick.getTimestamp();
            calculateAvg(interval, avgObj);
        } else if (Operation.open == op1 && Operation.open == op2) {
            long interval = newTick.getTimestamp() - oldTick.getTimestamp();
            calculateAvg(interval, avgObj);
            // Setting next open tick
            nextTick = newTick;
        } else if (Operation.close == op1 && Operation.open == op2) {
            // Setting next open tick and not setting any average
            nextTick = newTick;
        } else if (Operation.close == op1 && Operation.close == op2) {
            long interval = newTick.getTimestamp() - oldTick.getTimestamp();
            calculateAvg(interval, avgObj);
        } else {
            // Action types cannot be resolved
            System.out.println("Skipping tick pair as cannot determine action types");
        }

        return nextTick;
    }

    private static void calculateAvg(long interval, UserAvgTimeModel avgObj) {
        long count = avgObj.getCount() + 1;
        avgObj.setAverage(AverageCalculator.calculate(avgObj.getAverage(), interval, count));
        avgObj.setCount(count);
    }
}

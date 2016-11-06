package org.twitter.analytics.policy;

import org.apache.log4j.Logger;
import org.twitter.analytics.model.AvgResponseModel;
import org.twitter.analytics.model.Operation;
import org.twitter.analytics.model.UserTick;

/**
 * Default policy to handle open/close pairs.
 */
public class DefaultOCPolicy implements OCPolicy {
    private final static Logger LOG = Logger.getLogger(DefaultOCPolicy.class);

    public AvgResponseModel calculate(final UserTick oldTick, final UserTick newTick) {
        if (oldTick == null || newTick == null) {
            //LOG.warn("Skipping tick pair as sufficient info not provided " + (avgObj != null ? " user: " + avgObj.getUserId() : ""));
            LOG.warn("Skipping tick pair as sufficient info not provided");
            return null;
        }

        AvgResponseModel response = new AvgResponseModel();
        Operation op1 = Operation.valueOf(oldTick.getOperation());
        Operation op2 = Operation.valueOf(newTick.getOperation());

        if (Operation.open == op1 && Operation.close == op2) {
            response.setInterval(newTick.getTimestamp() - oldTick.getTimestamp());
            //calculateAvg(interval, avgObj);
        } else if (Operation.open == op1 && Operation.open == op2) {
            response.setInterval(newTick.getTimestamp() - oldTick.getTimestamp());
            //calculateAvg(interval, avgObj);
            // Setting next open tick
            //nextTick = newTick;
            response.setNextUserTick(newTick);
        } else if (Operation.close == op1 && Operation.open == op2) {
            // Setting next open tick and not setting any average
            //nextTick = newTick;
            response.setNextUserTick(newTick);
        } else if (Operation.close == op1 && Operation.close == op2) {
            //long interval = newTick.getTimestamp() - oldTick.getTimestamp();
            //calculateAvg(interval, avgObj);
            response.setInterval(newTick.getTimestamp() - oldTick.getTimestamp());
        } else {
            // Action types cannot be resolved
            LOG.warn("Skipping tick pair as cannot determine action types");
            return null;
        }

        return response;
    }

    /*private static void calculateAvg(long interval, UserAvgTimeModel avgObj) {
        long count = avgObj.getCount() + 1;
        avgObj.setAverage(AverageCalculator.calculate(avgObj.getAverage(), interval, count));
        avgObj.setCount(count);
    }*/

}

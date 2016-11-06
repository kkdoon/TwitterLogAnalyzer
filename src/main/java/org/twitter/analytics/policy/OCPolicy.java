package org.twitter.analytics.policy;

import org.twitter.analytics.model.AvgResponseModel;
import org.twitter.analytics.model.UserAvgTimeModel;
import org.twitter.analytics.model.UserTick;

/**
 * Created by kkdoon on 11/5/16.
 */
public interface OCPolicy {
    AvgResponseModel calculate(final UserTick user1, final UserTick user2);
}

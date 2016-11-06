package org.twitter.analytics.policy;

import org.twitter.analytics.model.AvgResponseModel;
import org.twitter.analytics.model.UserAvgTimeModel;
import org.twitter.analytics.model.UserTick;

/**
 * Policy interface.
 */
public interface OCPolicy {
    AvgResponseModel calculate(final UserTick user1, final UserTick user2);
}

package org.twitter.analytics.policy;

import org.twitter.analytics.model.AvgResponseModel;
import org.twitter.analytics.model.UserAvgTimeModel;
import org.twitter.analytics.model.UserModel;

/**
 * Created by kkdoon on 11/5/16.
 */
public interface OCPolicy {
    UserModel calculate(UserModel user1, UserModel user2, UserAvgTimeModel avgObj);
}

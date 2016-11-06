package org.twitter.analytics.comparator;

import org.twitter.analytics.model.UserTick;
import org.twitter.analytics.util.ParseUtil;

import java.util.Comparator;

/**
 * Comparator class to compare 2 lines containing user activity data.
 */
public class UserLineComparator implements Comparator<String> {

    public int compare(String o1, String o2) {
        UserTick user1 = ParseUtil.parseLogFileLine(o1);
        UserTick user2 = ParseUtil.parseLogFileLine(o2);
        if (user1 != null && user2 != null) {
            int result = user1.getUserID().compareTo(user2.getUserID());
            if (result != 0) {
                return result;
            }
            return Long.compare(user1.getTimestamp(), user2.getTimestamp());
        }
        if (user1 != null) {
            return -1;
        }
        return 1;
    }
}

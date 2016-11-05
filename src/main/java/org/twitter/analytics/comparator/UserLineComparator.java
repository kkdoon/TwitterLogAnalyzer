package org.twitter.analytics.comparator;

import org.twitter.analytics.model.UserModel;
import org.twitter.analytics.util.ParseUtil;

import java.util.Comparator;

/**
 * Created by kkdoon on 11/5/16.
 */
public class UserLineComparator implements Comparator<String> {

    public int compare(String o1, String o2) {
        UserModel user1 = ParseUtil.parseLogFileLine(o1);
        UserModel user2 = ParseUtil.parseLogFileLine(o2);
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
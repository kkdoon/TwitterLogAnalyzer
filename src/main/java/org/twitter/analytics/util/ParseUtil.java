package org.twitter.analytics.util;

import org.twitter.analytics.model.UserTick;

/**
 * Created by kkdoon on 11/5/16.
 */
public class ParseUtil {

    public static UserTick parseLogFileLine(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] cols = line.split(",");
        if (cols.length >= 3) {
            try {
                UserTick obj = new UserTick();
                obj.setUserID(cols[0].trim());
                obj.setTimestamp(Long.parseLong(cols[1].trim()));
                obj.setOperation(cols[2].trim());
                return obj;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}

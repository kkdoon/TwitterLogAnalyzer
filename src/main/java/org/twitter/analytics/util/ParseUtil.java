package org.twitter.analytics.util;

import org.twitter.analytics.model.UserModel;

/**
 * Created by kkdoon on 11/5/16.
 */
public class ParseUtil {

    public static UserModel parseLogFileLine(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] cols = line.split(",");
        if (cols.length >= 3) {
            UserModel obj = new UserModel();
            obj.setUserID(cols[0].trim());
            obj.setTimestamp(Long.parseLong(cols[1].trim()));
            obj.setOperation(cols[2].trim());
            return obj;
        }
        return null;
    }
}

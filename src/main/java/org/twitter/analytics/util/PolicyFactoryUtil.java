package org.twitter.analytics.util;

import org.twitter.analytics.policy.OCPolicy;

/**
 * Created by kkdoon on 11/5/16.
 */
public class PolicyFactoryUtil {
    private static final String POLICY_PACKAGE = "org.twitter.analytics.policy";

    public static OCPolicy getPolicyInstance(String policyName) {
        if (policyName == null) return null;
        try {
            String className = POLICY_PACKAGE + "." + policyName;
            Class c = Class.forName(className);
            System.out.println("Using policy: " + policyName);
            return (OCPolicy) c.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

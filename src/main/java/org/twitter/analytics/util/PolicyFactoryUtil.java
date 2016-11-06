package org.twitter.analytics.util;

import org.apache.log4j.Logger;
import org.twitter.analytics.policy.OCPolicy;

/**
 * Created by kkdoon on 11/5/16.
 */
public class PolicyFactoryUtil {
    private final static Logger LOG = Logger.getLogger(PolicyFactoryUtil.class);

    public static OCPolicy getPolicyInstance(String policyName) {
        if (policyName == null) return null;
        try {
            String className = Constants.POLICY_PACKAGE + "." + policyName;
            Class c = Class.forName(className);
            LOG.info("Initialized policy: " + policyName);
            return (OCPolicy) c.newInstance();
        } catch (Exception e) {
            LOG.error("Unable to initialize policy " + policyName, e);
        }
        return null;
    }
}

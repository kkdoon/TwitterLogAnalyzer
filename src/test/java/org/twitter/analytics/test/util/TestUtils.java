package org.twitter.analytics.test.util;

import org.junit.Assert;
import org.junit.Test;
import org.twitter.analytics.model.UserTick;
import org.twitter.analytics.policy.OCPolicy;
import org.twitter.analytics.util.ParseUtil;
import org.twitter.analytics.util.PolicyFactoryUtil;

/**
 * Created by kkdoon on 11/6/16.
 */
public class TestUtils {
    @Test
    public void testParseUtil1() {
        String line = " 3  , 50 , open ";
        UserTick tickActual = ParseUtil.parseLogFileLine(line);
        UserTick tickExpected = new UserTick();
        tickExpected.setUserID("3");
        tickExpected.setTimestamp(50);
        tickExpected.setOperation("open");

        Assert.assertEquals(tickExpected.getUserID(), tickActual.getUserID());
        Assert.assertEquals(tickExpected.getTimestamp(), tickActual.getTimestamp());
        Assert.assertEquals(tickExpected.getOperation(), tickActual.getOperation());
    }

    @Test
    public void testParseUtil2() {
        String line = " user3  , 501 , open, extra ";
        UserTick tickActual = ParseUtil.parseLogFileLine(line);
        UserTick tickExpected = new UserTick();
        tickExpected.setUserID("user3");
        tickExpected.setTimestamp(501);
        tickExpected.setOperation("open");

        Assert.assertEquals(tickExpected.getUserID(), tickActual.getUserID());
        Assert.assertEquals(tickExpected.getTimestamp(), tickActual.getTimestamp());
        Assert.assertEquals(tickExpected.getOperation(), tickActual.getOperation());
    }

    @Test
    public void testParseUtil3() {
        String line = " user3  , abcd , open, extra ";
        UserTick tickActual = ParseUtil.parseLogFileLine(line);
        Assert.assertNull(tickActual);
    }

    @Test
    public void testPolicyFactory(){
        Object obj = PolicyFactoryUtil.getPolicyInstance("DefaultOCPolicy");
        if(obj instanceof OCPolicy){
            Assert.assertTrue(true);
        }else{
            Assert.assertTrue(false);
        }
    }
}

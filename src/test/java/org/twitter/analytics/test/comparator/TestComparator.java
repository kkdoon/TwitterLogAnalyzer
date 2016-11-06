package org.twitter.analytics.test.comparator;

import org.junit.Assert;
import org.junit.Test;
import org.twitter.analytics.comparator.UserLineComparator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by kkdoon on 11/6/16.
 */
public class TestComparator {
    @Test
    public void testUserLineComparator1() {
        List<String> list1 = new ArrayList<String>();
        list1.add("3,50,open");
        list1.add("3,10,open");
        list1.add("2,30,close");
        list1.add("1,10,open");
        list1.add("1,-10,close");

        // Copy of first list
        List<String> list2 = new ArrayList<String>();
        list2.add("1,-10,close");
        list2.add("1,10,open");
        list2.add("2,30,close");
        list2.add("3,10,open");
        list2.add("3,50,open");

        Collections.sort(list1, new UserLineComparator());
        int counter = 0;
        for (String line : list1) {
            Assert.assertEquals(line, list2.get(counter++));
        }
    }

    @Test
    public void testUserLineComparator2() {
        List<String> list1 = new ArrayList<String>();
        list1.add("3,50,open");
        list1.add("3,10,open");
        list1.add(null);

        // Copy of first list
        List<String> list2 = new ArrayList<String>();
        list2.add("3,10,open");
        list2.add("3,50,open");
        list2.add(null);

        Collections.sort(list1, new UserLineComparator());
        int counter = 0;
        for (String line : list1) {
            Assert.assertEquals(line, list2.get(counter++));
        }
    }

    @Test
    public void testUserLineComparator3() {
        List<String> list1 = new ArrayList<String>();
        list1.add(null);
        list1.add("3,50,open");
        list1.add("3,10,open");

        // Copy of first list
        List<String> list2 = new ArrayList<String>();
        list2.add("3,10,open");
        list2.add("3,50,open");
        list2.add(null);

        Collections.sort(list1, new UserLineComparator());
        int counter = 0;
        for (String line : list1) {
            Assert.assertEquals(line, list2.get(counter++));
        }
    }
}

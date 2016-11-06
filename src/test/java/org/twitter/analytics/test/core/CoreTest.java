package org.twitter.analytics.test.core;

import org.junit.Assert;
import org.junit.Test;
import org.twitter.analytics.comparator.AvgLogFileComparator;
import org.twitter.analytics.comparator.UserLineComparator;
import org.twitter.analytics.core.AverageCalculator;
import org.twitter.analytics.core.MergeSortFile;
import org.twitter.analytics.core.SortedAvgWriter;
import org.twitter.analytics.iterator.BufferedReaderIterator;
import org.twitter.analytics.model.UserTick;
import org.twitter.analytics.policy.DefaultOCPolicy;
import org.twitter.analytics.util.FileUtil;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by kkdoon on 11/6/16.
 */
public class CoreTest {
    @Test
    public void testAvgCalculator1() {
        AverageCalculator obj = new AverageCalculator(new DefaultOCPolicy());
        UserTick tick1 = new UserTick();
        tick1.setUserID("1");
        tick1.setTimestamp(1);
        tick1.setOperation("open");
        obj.addTick(tick1);

        UserTick tick2 = new UserTick();
        tick2.setUserID("1");
        tick2.setTimestamp(2);
        tick2.setOperation("close");
        obj.addTick(tick2);

        UserTick tick3 = new UserTick();
        tick3.setUserID("2");
        tick3.setTimestamp(1);
        tick3.setOperation("open");
        obj.addTick(tick3);

        UserTick tick4 = new UserTick();
        tick4.setUserID("2");
        tick4.setTimestamp(2);
        tick4.setOperation("close");
        obj.addTick(tick4);

        Assert.assertEquals(obj.getAverage("1"), 1.0, 0.0);
        Assert.assertEquals(obj.getAverage("2"), 1.0, 0.0);
    }

    @Test
    public void testAvgCalculator2() {
        AverageCalculator obj = new AverageCalculator(new DefaultOCPolicy());
        UserTick tick1 = new UserTick();
        tick1.setUserID("1");
        tick1.setTimestamp(1);
        tick1.setOperation("open");
        obj.addTick(tick1);

        UserTick tick2 = new UserTick();
        tick2.setUserID("1");
        tick2.setTimestamp(2);
        tick2.setOperation("open");
        obj.addTick(tick2);

        UserTick tick3 = new UserTick();
        tick3.setUserID("1");
        tick3.setTimestamp(3);
        tick3.setOperation("close");
        obj.addTick(tick3);

        UserTick tick4 = new UserTick();
        tick4.setUserID("1");
        tick4.setTimestamp(4);
        tick4.setOperation("close");
        obj.addTick(tick4);

        obj.flushAvg("1");

        Assert.assertEquals(obj.getAverage("1"), 1.0, 0.0);
    }

    @Test
    public void testMergeSort() throws IOException {
        String sortFile = "/Users/kkdoon/Documents/IntelliJ_Workspace/TwitterLogAnalyzer/src/main/resources/temp/sort.txt";
        FileUtil.deleteFile(sortFile);
        MergeSortFile obj = new MergeSortFile("/Users/kkdoon/Documents/IntelliJ_Workspace/TwitterLogAnalyzer/src/main/resources/data/testInput.txt", "/Users/kkdoon/Documents/IntelliJ_Workspace/TwitterLogAnalyzer/src/main/resources/temp/", sortFile, 2, 30);
        obj.process(new UserLineComparator(), new AvgLogFileComparator());

        BufferedReader reader = FileUtil.openFile(sortFile);
        BufferedReaderIterator brIter = new BufferedReaderIterator(reader, 30);
        String[] list = {"u1, 1, open",
                "u1, 3, close",
                "u2, 2, open",
                "u2, 6, open",
                "u3, 4, open",
                "u3, 5, close"};
        int index = 0;
        for (String line : brIter) {
            Assert.assertEquals(list[index++], line);
        }
    }

    @Test
    public void testSortedWriter() throws IOException {
        String sortFile = "/Users/kkdoon/Documents/IntelliJ_Workspace/TwitterLogAnalyzer/src/main/resources/temp/sort.txt";
        String resultFile = "/Users/kkdoon/Documents/IntelliJ_Workspace/TwitterLogAnalyzer/src/main/resources/temp/result.txt";
        SortedAvgWriter obj = new SortedAvgWriter(sortFile, resultFile, new DefaultOCPolicy(), 30);
        obj.process();
        BufferedReader reader = FileUtil.openFile(resultFile);
        BufferedReaderIterator brIter = new BufferedReaderIterator(reader, 30);
        String expected = "[{\"userId\":\"u1\",\"average\":2.0},{\"userId\":\"u2\",\"average\":4.0},{\"userId\":\"u3\",\"average\":1.0}]";
        String actual = brIter.iterator().next();
        Assert.assertEquals(expected, actual);
    }


}

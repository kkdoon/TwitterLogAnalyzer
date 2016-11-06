package org.twitter.analytics.test.iterator;

import org.junit.Assert;
import org.junit.Test;
import org.twitter.analytics.iterator.BufferedReaderIterator;
import org.twitter.analytics.util.FileUtil;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by kkdoon on 11/6/16.
 */
public class IteratorTest {

    @Test
    public void testBufferedReaderIterator() throws FileNotFoundException {
        BufferedReader reader = FileUtil.openFile("/Users/kkdoon/Documents/IntelliJ_Workspace/TwitterLogAnalyzer/src/main/resources/data/testInput.txt");
        BufferedReaderIterator iterable = new BufferedReaderIterator(reader, 30);
        String[] list = {"u1, 1, open",
                "u2, 2, open",
                "u1, 3, close",
                "u3, 4, open",
                "u3, 5, close",
                "u2, 6, open"};
        int index = 0;
        Iterator iterator = iterable.iterator();
        while (iterator.hasNext()) {
            Assert.assertEquals(list[index++], iterator.next());
        }
    }

    @Test
    public void testBufferedReaderIterator2() throws FileNotFoundException {
        BufferedReader reader = FileUtil.openFile("/Users/kkdoon/Documents/IntelliJ_Workspace/TwitterLogAnalyzer/src/main/resources/data/testInput.txt");
        BufferedReaderIterator iterable = new BufferedReaderIterator(reader, 30);
        String line = iterable.getCurrentLineForComparison();

        String actual = "u1, 1, open";
        Assert.assertEquals(line, actual);

        line = iterable.iterator().next();
        Assert.assertEquals(line, actual);
    }
}

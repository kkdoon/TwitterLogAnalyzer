package org.twitter.analytics.comparator;

import org.twitter.analytics.iterator.BufferedReaderIterator;

import java.util.Comparator;

/**
 * Comparator class to compare lines from 2 files. The line contains user activity data.
 */
public class AvgLogFileComparator implements Comparator<BufferedReaderIterator> {

    UserLineComparator comparator;

    public AvgLogFileComparator() {
        comparator = new UserLineComparator();
    }

    public int compare(BufferedReaderIterator o1, BufferedReaderIterator o2) {
        String val1 = o1.getCurrentLineForComparison();
        String val2 = o2.getCurrentLineForComparison();

        return comparator.compare(val1, val2);
    }
}

package org.twitter.analytics.comparator;

import org.twitter.analytics.iterator.BufferedReaderIterator;

import java.util.Comparator;

/**
 * Created by kkdoon on 11/5/16.
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

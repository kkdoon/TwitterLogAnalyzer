package org.twitter.analytics.comparator;

import org.twitter.analytics.iterator.BufferedReaderIterator;

import java.util.Comparator;

/**
 * Created by kkdoon on 11/5/16.
 */
public class AvgLogFileComparator implements Comparator<BufferedReaderIterator> {

    public int compare(BufferedReaderIterator o1, BufferedReaderIterator o2) {
        String val1 = o1.getCurrentLineForComparison();
        String val2 = o2.getCurrentLineForComparison();

        if (val1 != null && val2 != null) {
            String[] cols1 = val1.split(",");
            String[] cols2 = val2.split(",");

            int result = cols1[0].trim().compareTo(cols2[0].trim());
            if (result != 0) {
                return result;
            }
            return Integer.compare(Integer.parseInt(cols1[1].trim()), Integer.parseInt(cols2[1].trim()));
        }
        if (val1 != null) {
            return -1;
        }
        return 1;
    }
}

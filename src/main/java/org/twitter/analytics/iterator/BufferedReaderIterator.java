package org.twitter.analytics.iterator;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;

/**
 * Iterator class to read lines from given file sequentially.
 */
public class BufferedReaderIterator implements Iterable<String> {
    private final static Logger LOG = Logger.getLogger(BufferedReaderIterator.class);
    private int maxCharsRead;
    private BufferedReader r;

    public BufferedReaderIterator(BufferedReader r, int maxCharsRead) {
        this.r = r;
        this.maxCharsRead = maxCharsRead;
    }

    @Override
    public Iterator<String> iterator() {
        return new Iterator<String>() {

            @Override
            public boolean hasNext() {
                try {
                    r.mark(1);
                    if (r.read() < 0) {
                        return false;
                    }
                    r.reset();
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            public String next() {
                try {
                    return r.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

        };
    }

    /**
     * Reads current line from file and resets the iterator to point back to current line
     *
     * @return
     */
    public String getCurrentLineForComparison() {
        try {
            r.mark(maxCharsRead);
            String line = r.readLine();
            r.reset();
            return line;
        } catch (IOException e) {
            LOG.error("Unable to get current line from iterator", e);
            return null;
        }
    }
}

package org.twitter.analytics.iterator;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created by kkdoon on 11/4/16.
 */
public class BufferedReaderIterator implements Iterable<String> {

    private static  final  int MAX_CHARS_READ = 100;
    private BufferedReader r;

    public BufferedReaderIterator(BufferedReader r) {
        this.r = r;
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

    public String getCurrentLineForComparison(){
        try {
            r.mark(MAX_CHARS_READ);
            String line = r.readLine();
            r.reset();
            return line;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}

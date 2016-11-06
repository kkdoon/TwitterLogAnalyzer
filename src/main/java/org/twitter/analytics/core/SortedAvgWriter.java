package org.twitter.analytics.core;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.twitter.analytics.iterator.BufferedReaderIterator;
import org.twitter.analytics.model.UserTick;
import org.twitter.analytics.policy.OCPolicy;
import org.twitter.analytics.util.FileUtil;
import org.twitter.analytics.util.ParseUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Core class which reads sorted file and calculates average on user data and stores in output file.
 */
public class SortedAvgWriter {
    private final static Logger LOG = Logger.getLogger(SortedAvgWriter.class);
    private String sortOutput;
    private String resultOutput;
    private int maxCharsRead;
    private String prevUser, currentUser;
    private AverageCalculator calculator;
    private ObjectMapper mapper;
    private boolean isFirst;

    public SortedAvgWriter(String sortOutput, String resultOutput, OCPolicy policy, int maxCharsRead) {
        this.sortOutput = sortOutput;
        this.resultOutput = resultOutput;
        this.maxCharsRead = maxCharsRead;
        prevUser = null;
        currentUser = null;
        calculator = new AverageCalculator(policy);
        mapper = new ObjectMapper();
        isFirst = true;
    }

    public void process() {
        BufferedReader br = null;
        try {
            br = FileUtil.openFile(sortOutput);
            BufferedReaderIterator brIter = new BufferedReaderIterator(br, maxCharsRead);
            BufferedWriter writer = null;
            try {
                writer = FileUtil.getFileWriter(resultOutput);
                writer.write("[");
                for (String line : brIter) {
                    handleLine(line, writer);
                }
                // Writing average value for last user
                writeResponse(writer);
            } catch (IOException e) {
                LOG.error("Error while writing to result file", e);
            } finally {
                try {
                    writer.write("]");
                } catch (IOException e) {
                    LOG.error("Error while writing to result file", e);
                }
                FileUtil.closeFile(writer);
            }
        } catch (IOException e) {
            LOG.error("Error while opening sorted file", e);
        } finally {
            FileUtil.closeFile(br);
        }
    }

    private void handleLine(String line, BufferedWriter writer) throws IOException {
        if (line == null) return;

        UserTick curr = ParseUtil.parseLogFileLine(line);
        if (curr == null) {
            LOG.warn("Skipping line as cannot be parsed: " + line);
            return;
        }

        currentUser = curr.getUserID();

        if (prevUser == null) {
            prevUser = currentUser;
            calculator.addTick(curr);
            return;
        }

        if (prevUser.equals(currentUser)) {
            calculator.addTick(curr);
        } else {
            writeResponse(writer);
            prevUser = currentUser;
            calculator.addTick(curr);
        }
    }

    private void writeResponse(BufferedWriter writer) throws IOException {
        // Handling case when no close tick provided
        calculator.flushAvg(prevUser);
        // Write user average data
        if (!isFirst) {
            writer.write("," + mapper.writeValueAsString(calculator.getAverageModel(prevUser)));
        } else {
            writer.write(mapper.writeValueAsString(calculator.getAverageModel(prevUser)));
            isFirst = false;
        }
    }
}

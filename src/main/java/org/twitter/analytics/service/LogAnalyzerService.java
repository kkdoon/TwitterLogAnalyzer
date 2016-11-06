package org.twitter.analytics.service;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.twitter.analytics.comparator.AvgLogFileComparator;
import org.twitter.analytics.comparator.UserLineComparator;
import org.twitter.analytics.core.AverageCalculator;
import org.twitter.analytics.core.MergeSortFile;
import org.twitter.analytics.iterator.BufferedReaderIterator;
import org.twitter.analytics.model.UserTick;
import org.twitter.analytics.policy.DefaultOCPolicy;
import org.twitter.analytics.policy.OCPolicy;
import org.twitter.analytics.util.FileUtil;
import org.twitter.analytics.util.ParseUtil;
import org.twitter.analytics.util.PolicyFactoryUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class LogAnalyzerService {
    private final static Logger LOG = Logger.getLogger(LogAnalyzerService.class);
    private static final int MAX_LINE_PER_FILE;
    private static OCPolicy policy;

    static {
        try {
            MAX_LINE_PER_FILE = Integer.parseInt(System.getProperty("MAX_LINE_FILE"));
            String policyName = System.getProperty("POLICY_NAME");
            policy = PolicyFactoryUtil.getPolicyInstance(policyName);
            if (policy == null) {
                LOG.info("Initializing the default policy");
                policy = new DefaultOCPolicy();
            }
        } catch (Exception e) {
            LOG.error("Unable to initialize system properties", e);
            throw new RuntimeException("Unable to initialize system properties");
        }
    }


    public static void main(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("Please provide input file path and destination path");
        }

        String input = args[0].trim();
        String sortOutput = args[1].trim() + "sortOutput.txt";
        String resultOutput = args[1].trim() + "finalOutput.txt";
        LOG.info("Input file: " + input + "\n Sort output file:" + sortOutput + "\n Result output file:" + resultOutput);

        MergeSortFile obj = new MergeSortFile(input, args[1].trim(), sortOutput, MAX_LINE_PER_FILE);
        obj.process(new UserLineComparator(), new AvgLogFileComparator());
        BufferedReader br = null;
        try {
            br = FileUtil.openFile(sortOutput);
            BufferedReaderIterator brIter = new BufferedReaderIterator(br);
            BufferedWriter writer = null;
            ObjectMapper mapper;
            try {
                writer = FileUtil.getFileWriter(resultOutput);
                writer.write("[");
                AverageCalculator calculator = new AverageCalculator();
                mapper = new ObjectMapper();
                String prevUser = null, currentUser;
                boolean isFirst = true;
                for (String line : brIter) {
                    UserTick curr = ParseUtil.parseLogFileLine(line);
                    if (curr == null) {
                        LOG.warn("Skipping line as cannot be parsed: " + line);
                        continue;
                    }

                    currentUser = curr.getUserID();

                    if (prevUser == null) {
                        prevUser = currentUser;
                        calculator.addTick(curr);
                        continue;
                    }

                    if (prevUser.equals(currentUser)) {
                        calculator.addTick(curr);
                    } else {
                        // Handling case when no close tick provided
                        calculator.flushAvg(prevUser);
                        // Write user average data
                        if (!isFirst) {
                            writer.write("," + mapper.writeValueAsString(calculator.getAverageModel(prevUser)));
                        } else {
                            writer.write(mapper.writeValueAsString(calculator.getAverageModel(prevUser)));
                            isFirst = false;
                        }
                        prevUser = currentUser;
                        calculator.addTick(curr);
                    }
                }
                // Writing average value for last user
                calculator.flushAvg(prevUser);
                if (!isFirst) {
                    writer.write("," + mapper.writeValueAsString(calculator.getAverageModel(prevUser)));
                } else {
                    mapper.writeValueAsString(calculator.getAverageModel(prevUser));
                }
            } catch (IOException e) {
                LOG.error("Error while calculating average", e);
            } finally {
                try {
                    writer.write("]");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                FileUtil.closeFile(writer);
            }
        } catch (IOException e) {
            LOG.error("Error while opening sorted file", e);
        } finally {
            FileUtil.closeFile(br);
        }
        LOG.info("Log analyzer service finished. Please read file " + resultOutput + " for final results");
    }
}

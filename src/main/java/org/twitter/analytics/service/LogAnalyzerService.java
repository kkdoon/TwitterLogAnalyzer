package org.twitter.analytics.service;

import org.apache.log4j.Logger;
import org.twitter.analytics.comparator.AvgLogFileComparator;
import org.twitter.analytics.comparator.UserLineComparator;
import org.twitter.analytics.core.MergeSortFile;
import org.twitter.analytics.core.SortedAvgWriter;
import org.twitter.analytics.policy.DefaultOCPolicy;
import org.twitter.analytics.policy.OCPolicy;
import org.twitter.analytics.util.Constants;
import org.twitter.analytics.util.PolicyFactoryUtil;

/**
 * Service to calculate user average value from given log file. Service sorts the given file and then calculates the average value.
 */
public class LogAnalyzerService {
    private final static Logger LOG = Logger.getLogger(LogAnalyzerService.class);
    private static final int MAX_LINE_PER_FILE, MAX_CHARS_READ;
    private static OCPolicy policy;

    static {
        try {
            MAX_LINE_PER_FILE = Integer.parseInt(System.getProperty("MAX_LINE_FILE"));
            MAX_CHARS_READ = Integer.parseInt(System.getProperty("MAX_CHARS_READ"));
            String policyName = System.getProperty("POLICY_NAME");
            policy = PolicyFactoryUtil.getPolicyInstance(policyName);
            if (policy == null) {
                LOG.info("Initializing default policy");
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
        String sortOutput = args[1].trim() + Constants.SORT_OUTPUT_FILE_NAME;
        String resultOutput = args[1].trim() + Constants.RESULT_OUTPUT_FILE_NAME;
        LOG.info("Input file: " + input + "\n Sort output file:" + sortOutput + "\n Result output file:" + resultOutput);

        // Sorting log file
        MergeSortFile obj = new MergeSortFile(input, args[1].trim(), sortOutput, MAX_LINE_PER_FILE, MAX_CHARS_READ);
        obj.process(new UserLineComparator(), new AvgLogFileComparator());

        // Calculating average from sorted log file
        SortedAvgWriter objWriter = new SortedAvgWriter(sortOutput, resultOutput, policy, MAX_CHARS_READ);
        objWriter.process();

        LOG.info("Log analyzer service finished. Please read file " + resultOutput + " for final results");
    }
}

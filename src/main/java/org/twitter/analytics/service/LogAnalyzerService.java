package org.twitter.analytics.service;

import org.codehaus.jackson.map.ObjectMapper;
import org.twitter.analytics.comparator.AvgLogFileComparator;
import org.twitter.analytics.comparator.UserLineComparator;
import org.twitter.analytics.core.AverageCalculator;
import org.twitter.analytics.core.MergeSortFile;
import org.twitter.analytics.iterator.BufferedReaderIterator;
import org.twitter.analytics.model.AvgResponseModel;
import org.twitter.analytics.model.UserAvgTimeModel;
import org.twitter.analytics.model.UserModel;
import org.twitter.analytics.policy.DefaultOCPolicy;
import org.twitter.analytics.policy.OCPolicy;
import org.twitter.analytics.util.FileUtil;
import org.twitter.analytics.util.ParseUtil;
import org.twitter.analytics.util.PolicyFactoryUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;

public class LogAnalyzerService {
    private static final int MAX_LINE_PER_FILE;
    private static OCPolicy policy;

    static {
        try {
            MAX_LINE_PER_FILE = Integer.parseInt(System.getProperty("MAX_LINE_FILE"));
            String policyName = System.getProperty("POLICY_NAME");
            policy = PolicyFactoryUtil.getPolicyInstance(policyName);
            if (policy == null) {
                System.out.println("Initializing the default policy");
                policy = new DefaultOCPolicy();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                mapper = new ObjectMapper();
                String prevUser = null, currentUser;
                UserAvgTimeModel avgObj = null;
                UserModel[] userTicks = new UserModel[2];
                Arrays.fill(userTicks, null);
                boolean isFirst = true;
                for (String line : brIter) {
                    UserModel curr = ParseUtil.parseLogFileLine(line);
                    if (curr == null) {
                        continue;
                    }

                    currentUser = curr.getUserID();

                    if (prevUser == null) {
                        prevUser = currentUser;
                        userTicks[0] = curr;
                        avgObj = new UserAvgTimeModel(currentUser);
                        continue;
                    }

                    if (prevUser.equals(currentUser)) {
                        if (userTicks[0] == null) {
                            userTicks[0] = curr;
                        } else {
                            userTicks[1] = curr;
                            //calculate avg
                            //calculateAvg(userTicks, avgObj);
                            UserModel nextTick = policy.calculate(userTicks[0], userTicks[1], avgObj);
                            Arrays.fill(userTicks, null);
                            // Setting the open tick for next interval
                            if (nextTick != null) {
                                userTicks[0] = nextTick;
                            }
                        }
                    } else {
                        // Handling case when no close tick provided
                        policy.calculate(userTicks[0], userTicks[1], avgObj);
                        // Write average data
                        if (!isFirst) {
                            writer.write("," + mapper.writeValueAsString(avgObj));
                        } else {
                            writer.write(mapper.writeValueAsString(avgObj));
                            isFirst = false;
                        }
                        Arrays.fill(userTicks, null);
                        userTicks[0] = curr;
                        prevUser = currentUser;
                        avgObj = new UserAvgTimeModel(currentUser);
                    }
                }
                // Writing last userId value
                if (userTicks[1] != null) {
                    writer.write(mapper.writeValueAsString(avgObj));
                    writer.newLine();
                } else {
                    // Handling case when no close tick provided
                    policy.calculate(userTicks[0], userTicks[1], avgObj);
                    if (!isFirst) {
                        writer.write("," + mapper.writeValueAsString(avgObj));
                    } else {
                        writer.write(mapper.writeValueAsString(avgObj));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    writer.write("]");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                FileUtil.closeFile(writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileUtil.closeFile(br);
        }
    }
}

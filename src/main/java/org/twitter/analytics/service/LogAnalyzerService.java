package org.twitter.analytics.service;

import org.codehaus.jackson.map.ObjectMapper;
import org.twitter.analytics.comparator.AvgLogFileComparator;
import org.twitter.analytics.comparator.UserLineComparator;
import org.twitter.analytics.core.AverageCalculator;
import org.twitter.analytics.core.MergeSortFile;
import org.twitter.analytics.iterator.BufferedReaderIterator;
import org.twitter.analytics.model.UserAvgTimeModel;
import org.twitter.analytics.model.UserModel;
import org.twitter.analytics.util.FileUtil;
import org.twitter.analytics.util.ParseUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;

public class LogAnalyzerService {
    private static final int MAX_LINE_PER_FILE = 10;

    public static void main(String[] args) {
        String input = "/Users/kkdoon/Documents/IntelliJ_Workspace/TwitterLogAnalyzer/src/main/resources/data/input.txt";
        String output = "/Users/kkdoon/Documents/IntelliJ_Workspace/TwitterLogAnalyzer/src/main/resources/data/output.txt";
        String resultOutput = "/Users/kkdoon/Documents/IntelliJ_Workspace/TwitterLogAnalyzer/src/main/resources/data/finalOutput.txt";

        MergeSortFile obj = new MergeSortFile(input, output, MAX_LINE_PER_FILE);
        obj.process(new UserLineComparator(), new AvgLogFileComparator());
        BufferedReader br = null;
        try {
            br = FileUtil.openFile(output);
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
                            calculateAvg(userTicks, avgObj);
                            Arrays.fill(userTicks, null);
                        }
                    } else {
                        // Write average data
                        if (!isFirst) {
                            writer.write("," + mapper.writeValueAsString(avgObj));
                        } else {
                            writer.write(mapper.writeValueAsString(avgObj));
                            isFirst = false;
                        }
                        // TODO: Policy to deal with remaining data
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
                    // TODO: Policy to deal with remaining data
                    if (!isFirst) {
                        writer.write("," + mapper.writeValueAsString(avgObj));
                    } else {
                        writer.write(mapper.writeValueAsString(avgObj));
                        isFirst = false;
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

    private static void calculateAvg(UserModel[] ticks, UserAvgTimeModel avgObj) {
        long interval = calculateInterval(ticks[0], ticks[1]);
        //interval = curr.getTimestamp() - openVal;
        long count = avgObj.getCount() + 1;
        avgObj.setAverage(AverageCalculator.calculate(avgObj.getAverage(), interval, count));
        avgObj.setCount(count);
    }

    private static long calculateInterval(UserModel prev, UserModel curr) {
        return curr.getTimestamp() - prev.getTimestamp();
    }
}

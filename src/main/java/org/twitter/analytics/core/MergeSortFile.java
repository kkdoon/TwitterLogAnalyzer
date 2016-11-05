package org.twitter.analytics.core;

import org.twitter.analytics.comparator.AvgLogFileComparator;
import org.twitter.analytics.comparator.UserLineComparator;
import org.twitter.analytics.iterator.BufferedReaderIterator;
import org.twitter.analytics.util.FileUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by kkdoon on 11/4/16.
 */
public class MergeSortFile {

    private String inputFile, outputFile;
    private int maxLines;
    private List<String> tempFiles;

    private MergeSortFile() {

    }

    public MergeSortFile(String inputFile, String outputFile, int maxLines) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.maxLines = maxLines;
        this.tempFiles = new ArrayList<String>();
    }

    public void process(Comparator<String> comparator1, Comparator<BufferedReaderIterator> comparator2) {
        partitionFiles(comparator1);
        mergeFiles(comparator2);
    }

    private void partitionFiles(Comparator<String> comparator) {
        int count = 0, totalCount = 0;
        BufferedReader br = null;
        List<String> tempList;
        try {
            br = FileUtil.openFile(inputFile);
            tempList = new ArrayList<String>();
            BufferedReaderIterator brIter = new BufferedReaderIterator(br);
            for (String line : brIter) {
                totalCount++;
                if (count >= maxLines) {
                    Collections.sort(tempList, comparator);
                    // Write data to temp file
                    tempFiles.add(FileUtil.createTempFile());
                    System.out.println("Temp file:" + tempFiles.get(tempFiles.size() - 1));
                    BufferedWriter writer = null;
                    try {
                        writer = FileUtil.getFileWriter(tempFiles.get(tempFiles.size() - 1));
                        for (String writeLine : tempList) {
                            if (writeLine != null && !writeLine.isEmpty()) {
                                writer.write(writeLine);
                                writer.newLine();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        FileUtil.closeFile(writer);
                        count = 0;
                        tempList.clear();
                    }
                }
                tempList.add(line);
                count++;
            }
            System.out.println("Total:" + totalCount);
            System.out.println("Total list:" + tempList.size());
            if (tempList.size() > 0) {
                Collections.sort(tempList);
                // Write data to temp file
                tempFiles.add(FileUtil.createTempFile());
                System.out.println("Final Temp file:" + tempFiles.get(tempFiles.size() - 1));
                BufferedWriter writer = null;
                try {
                    writer = FileUtil.getFileWriter(tempFiles.get(tempFiles.size() - 1));
                    for (String writeLine : tempList) {
                        if (writeLine != null && !writeLine.isEmpty()) {
                            writer.write(writeLine);
                            writer.newLine();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    FileUtil.closeFile(writer);
                    count = 0;
                    tempList.clear();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileUtil.closeFile(br);
        }
    }

    private void mergeFiles(Comparator<BufferedReaderIterator> comparator) {
        PriorityQueue<BufferedReaderIterator> queue = new PriorityQueue<BufferedReaderIterator>(tempFiles.size(), comparator);
        BufferedReader[] fileReaders = new BufferedReader[tempFiles.size()];
        int count = 0;
        for (String tempFile : tempFiles) {
            try {
                fileReaders[count] = FileUtil.openFile(tempFile);
                queue.offer(new BufferedReaderIterator(fileReaders[count]));
                count++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        BufferedWriter writer = null;
        try {
            writer = FileUtil.getFileWriter(outputFile);
            System.out.println("Writing to file:" + outputFile);
            while (queue.size() > 0) {
                BufferedReaderIterator currIter = queue.poll();
                writer.write(currIter.iterator().next());
                writer.newLine();
                if (currIter.iterator().hasNext()) {
                    queue.offer(currIter);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileUtil.closeFile(writer);
            for (BufferedReader br : fileReaders) {
                FileUtil.closeFile(br);
            }
        }
    }

    public static void main(String[] args) {
        String input = "/Users/kkdoon/Documents/IntelliJ_Workspace/TwitterLogAnalyzer/src/main/resources/data/input.txt";
        String output = "/Users/kkdoon/Documents/IntelliJ_Workspace/TwitterLogAnalyzer/src/main/resources/data/output.txt";
        MergeSortFile obj = new MergeSortFile(input, output, 10);
        obj.process(new UserLineComparator(), new AvgLogFileComparator());
        BufferedReader br = null;
        try {
            br = FileUtil.openFile(output);
            BufferedReaderIterator brIter = new BufferedReaderIterator(br);
            for (String line : brIter) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileUtil.closeFile(br);
        }
    }
}

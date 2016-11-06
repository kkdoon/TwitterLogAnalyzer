package org.twitter.analytics.core;

import org.apache.log4j.Logger;
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
    private final static Logger LOG = Logger.getLogger(MergeSortFile.class);
    private String inputFile, tempPath, outputFile;
    private int maxLines, maxCharsRead;
    private List<String> tempFiles;

    // Private constructor to force client to use parametrized constructor
    private MergeSortFile() {
    }

    public MergeSortFile(String inputFile, String tempPath, String outputFile, int maxLines, int maxCharsRead) {
        this.inputFile = inputFile;
        this.tempPath = tempPath;
        this.outputFile = outputFile;
        this.maxLines = maxLines;
        this.maxCharsRead = maxCharsRead;
        this.tempFiles = new ArrayList<String>();
    }

    public void process(Comparator<String> comparator1, Comparator<BufferedReaderIterator> comparator2) {
        partitionFiles(comparator1);
        mergeFiles(comparator2);
    }

    private void partitionFiles(Comparator<String> comparator) {
        LOG.info("Starting file partitioning");
        int count = 0;
        BufferedReader br = null;
        List<String> tempList;
        try {
            br = FileUtil.openFile(inputFile);
            tempList = new ArrayList<String>();
            BufferedReaderIterator brIter = new BufferedReaderIterator(br, maxCharsRead);
            for (String line : brIter) {
                if (count >= maxLines) {
                    writePartitionFile(tempList, comparator);
                    count = 0;
                }
                tempList.add(line);
                count++;
            }
            if (tempList.size() > 0) {
                writePartitionFile(tempList, comparator);
            }
        } catch (IOException e) {
            LOG.error("Error while partitioning files", e);
        } finally {
            FileUtil.closeFile(br);
        }
        LOG.info("File partitioning ended");
    }

    private void writePartitionFile(List<String> tempList, Comparator<String> comparator) throws IOException {
        Collections.sort(tempList, comparator);
        // Write data to temp file
        tempFiles.add(FileUtil.createTempFile(tempPath));
        if (LOG.isDebugEnabled()) {
            LOG.debug("Temp file:" + tempFiles.get(tempFiles.size() - 1));
        }
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
            LOG.error("Error while partitioning file", e);
        } finally {
            FileUtil.closeFile(writer);
            tempList.clear();
        }
    }

    private void mergeFiles(Comparator<BufferedReaderIterator> comparator) {
        PriorityQueue<BufferedReaderIterator> queue = new PriorityQueue<BufferedReaderIterator>(tempFiles.size(), comparator);
        BufferedReader[] fileReaders = new BufferedReader[tempFiles.size()];
        int count = 0;
        for (String tempFile : tempFiles) {
            try {
                fileReaders[count] = FileUtil.openFile(tempFile);
                queue.offer(new BufferedReaderIterator(fileReaders[count], maxCharsRead));
                count++;
            } catch (IOException e) {
                LOG.error("Error while merging files", e);
            }
        }
        LOG.info("Starting file merge among " + tempFiles.size() + " files");
        BufferedWriter writer = null;
        try {
            writer = FileUtil.getFileWriter(outputFile);
            while (queue.size() > 0) {
                BufferedReaderIterator currIter = queue.poll();
                writer.write(currIter.iterator().next());
                writer.newLine();
                if (currIter.iterator().hasNext()) {
                    queue.offer(currIter);
                }
            }
        } catch (IOException e) {
            LOG.error("Error while merging files", e);
        } finally {
            FileUtil.closeFile(writer);
            for (BufferedReader br : fileReaders) {
                FileUtil.closeFile(br);
            }
        }
        LOG.info("File merge ended");
    }
}

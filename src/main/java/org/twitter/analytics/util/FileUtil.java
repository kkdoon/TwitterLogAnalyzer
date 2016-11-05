package org.twitter.analytics.util;

import java.io.*;

public class FileUtil {

    public static BufferedReader openFile(String filePath) throws FileNotFoundException {
        if (filePath != null) {
            return new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
        }
        return null;
    }

    public static BufferedWriter getFileWriter(String filePath) throws IOException {
        if (filePath != null) {
            return new BufferedWriter(new FileWriter(filePath, false));
        }
        return null;
    }

    public static void closeFile(BufferedReader br) {
        if (br != null) {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeFile(BufferedWriter br) {
        if (br != null) {
            try {
                br.flush();
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String createTempFile() throws IOException {
        File tempFile = File.createTempFile("log", ".tmp", new File("/Users/kkdoon/Documents/IntelliJ_Workspace/TwitterLogAnalyzer/src/main/resources/temp"));
        tempFile.deleteOnExit();
        return tempFile.getAbsolutePath();
    }

    public static void deleteFile(String file) {
        if (file != null) {
            File delFile = new File(file);
            delFile.delete();
        }
    }
}

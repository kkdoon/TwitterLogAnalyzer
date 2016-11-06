# TwitterLogAnalyzer
Twitter Log Analysis Library

- This code base contains a general purpose library for log data analysis. 
- Currently the library supports service to calculate average user time.
- Library uses external merge sort to sort the log file. This approach helps in optimizing the memory available and 
uses disk storage for computation.
- Custom analysis can be carried out on the sorted file. Currently, average user activity time is calculated using the sorted file.
- Default policy is used to handle open/close tick values.

Code tested with following example:
- Generated 1.4GB data file (7.9 million records)
- Each line of log file stores around 18 bytes of data
- JVM Heap memory set to 128 MB
- Maximum lines per file set to 1 million lines

Run Instructions:

- Maven project. To build jar use: mvn install
- Go to target folder and use jar "twitter-logs-analytics-1.0-SNAPSHOT-jar-with-dependencies.jar"
OR
- Go to folder "jar" and use jar "twitter-logs-analytics-1.0-SNAPSHOT-jar-with-dependencies.jar"

- Use script (runLogAnalysisService.sh) inside "jar" folder to run the jar file
- Script takes in 3 arguments: Number of lines per file, path to input file, output file directory path
OR
- Use following command to run jar file:
java -DMAX_LINE_FILE={number of lines} -DMAX_CHARS_READ=30 -DPOLICY_NAME=DefaultOCPolicy -Xmx{memory size} -cp twitter-logs-analytics-1.0-SNAPSHOT-jar-with-dependencies.jar org.twitter.analytics.service.LogAnalyzerService {inputFilePath} {outputPath}

Current Approach:
- Sort-based approach is used to calculate average value.
- Sorting helps in sequentially arranging the values for a particular user in one file. This way in order to calculate average value only 2 ticks
need to be considered at a time (low memory usage). If sorting was not used then we would have to maintain the open tick value in process
memory, until the next tick is found. In case, the next tick value for a number of users is available after a million records, then in worst case
the process can go out of memory.
- Since the file is sorted and stored in disk, only 2 records are stored in memory at a time for a particular user to calculate the average.
During the merge-sort phase available memory is used to sort the file in chunks.

Alternative Approach:
- Alternatively, first the entire file can be scanned to count the total number of users as well as the number of entries per user.
- Based on the count above, an adequate number of files can be created. These files will be similar to shards, where each file stores
a particular number of user's data and the shard file vs user mapping is maintained.
- Therefore, in the initial phase, based on maximum number of lines, data is stored in memory, and then based on the shard configuration,
data is written to the appropriate file.
- Now data can be read from one file at a time, where data for all the users in the file is maintained in memory. Since, each file
consists of data for limited number of users, the process will be able to operate within the memory constraints.

Code Structure:
- main: Contains source code and resources
    - java: Contains source code. Service folder contains the entry point to the program (LogAnalyzerService class).
    - resources:
        - data: Contains code coverage report (index.html), sample input/output file, sample log file generated
        - filegen: Contains script to populate large dataset to test source code for memory conditions
        - scripts: Contains script to start the java process
        - temp: Contains sample files generated during test phase
- test: Contains JUnit test cases
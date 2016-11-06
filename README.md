# TwitterLogAnalyzer
Twitter Log Analysis Library

- This code base contains a general purpose library for log data analysis. 
- Currently the library supports service to calculate average user time.
- Library uses external merge sort to sort the log file. This approach helps in optimizing the memory available and 
uses disk storage for computation.
- Custom analysis can be carried out on the sorted file.

Example:
- Each line of log file stores 35 bytes of data
- Memory available is 100 MB
- Approx. maximum lines per file should be set to 2.85 million lines

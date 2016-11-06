# TwitterLogAnalyzer
Twitter Log Analysis Library

- This code base contains a general purpose library for log data analysis. 
- Currently the library supports service to calculate average user time.
- Library uses external merge sort to sort the log file. This approach helps in optimizing the memory available and 
uses disk storage for computation.
- Custom analysis can be carried out on the sorted file.

Code tested with following example:
- Generated 1.4GB data file (7.9 million records)
- Each line of log file stores around 18 bytes of data
- Heap memory set to 128 MB
- Maximum lines per file set to 1 million lines

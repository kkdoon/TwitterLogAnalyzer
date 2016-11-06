#!/bin/sh
maxLine=$1
inputFile=$2
outputFile=$3

java -DMAX_LINE_FILE=$maxLine -DMAX_CHARS_READ=30 -DPOLICY_NAME=DefaultOCPolicy -Xmx128m -cp twitter-logs-analytics-1.0-SNAPSHOT-jar-with-dependencies.jar org.twitter.analytics.service.LogAnalyzerService $inputFile $outputFile
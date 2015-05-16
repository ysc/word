#!/bin/bash

SETCOLOR_FAILURE="echo -en \\033[1;31m"
SETCOLOR_NORMAL="echo -en \\033[0;39m"

if [ $# = 0 ] ;then
    $SETCOLOR_FAILURE
    echo Usage:
    echo -removeStopWord to specify remove stop word.
    echo -textFile=text.txt to specify text for word frequency statistics, can specify more than one option.
    echo -statisticsResultFile=statistics-result.txt to specify the file path for saving statistics result.
    echo -segmentationAlgorithm=MinimalWordCount to specify the algorithm of segmentation.
    $SETCOLOR_NORMAL
    exit
fi

mvn -Dmaven.test.skip clean install dependency:copy-dependencies
export JAVA_OPTS="-Xms3g -Xmx3g"
export CLASS_PATH="target/classes:target/dependency/slf4j-api-1.6.4.jar:target/dependency/logback-classic-0.9.28.jar:target/dependency/logback-core-0.9.28.jar"
export EXECUTOR="java $JAVA_OPTS -cp $CLASS_PATH"
exec $EXECUTOR org.apdplat.word.WordFrequencyStatistics $*

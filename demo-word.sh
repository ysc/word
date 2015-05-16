#!/bin/bash
mvn -Dmaven.test.skip clean install dependency:copy-dependencies
export JAVA_OPTS="-Xms1200m -Xmx1200m"
export CLASS_PATH="target/classes:target/dependency/slf4j-api-1.6.4.jar:target/dependency/logback-classic-0.9.28.jar:target/dependency/logback-core-0.9.28.jar"
export EXECUTOR="java $JAVA_OPTS -cp $CLASS_PATH"
exec $EXECUTOR org.apdplat.word.WordSegmenter utf-8
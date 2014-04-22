@echo off
call mvn clean install dependency:copy-dependencies
set JAVA_OPTS=-Xms1200m -Xmx1200m
set CLASS_PATH=target/classes;target/dependency/slf4j-api-1.6.4.jar;target/dependency/logback-classic-0.9.28.jar;target/dependency/logback-core-0.9.28.jar
set EXECUTOR=java %JAVA_OPTS% -cp %CLASS_PATH%
call %EXECUTOR% org.apdplat.word.WordSegmenter gbk
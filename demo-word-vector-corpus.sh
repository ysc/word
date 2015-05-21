#!/bin/bash
mvn -Dmaven.test.skip clean install dependency:copy-dependencies
export JAVA_OPTS="-Xms12000m -Xmx12000m"
export CLASS_PATH="target/classes:target/dependency/slf4j-api-1.6.4.jar:target/dependency/logback-classic-0.9.28.jar:target/dependency/logback-core-0.9.28.jar"
export EXECUTOR="java $JAVA_OPTS -cp $CLASS_PATH"
# 1、从语料库中提取词，词之间以空格分隔，内容保存到文件 data/word.txt
$EXECUTOR org.apdplat.word.corpus.ExtractText data/word.txt
# 2、对分好词的文件 data/word.txt 建立词向量，将词向量保存到文件 data/vector.txt，将词汇表保存到文件 data/vocabulary.txt
# 上下文长度为
export CONTEXT_WINDOW_LENGTH=2
# 词向量长度
export VECTOR_LENGTH=30
$EXECUTOR org.apdplat.word.vector.Word2Vector data/word.txt data/vector.txt data/vocabulary.txt $CONTEXT_WINDOW_LENGTH $VECTOR_LENGTH
# 3、计算不同词向量之间的相似度，控制台编码为UTF-8
$EXECUTOR org.apdplat.word.vector.Distance data/vector.txt utf-8
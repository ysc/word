#!/bin/bash
# 在这里指定文本文件的路径或在命令行参数中指定
export TEXT_FILE=text.txt

if [ "$1" != "" ]; then
  TEXT_FILE=$1;
fi

if [ ! -f $TEXT_FILE ]; then
  echo 文本文件$TEXT_FILE不存在
  exit 1
fi

mvn clean install dependency:copy-dependencies
export JAVA_OPTS="-Xms12000m -Xmx12000m"
export CLASS_PATH="target/classes:target/dependency/slf4j-api-1.6.4.jar:target/dependency/logback-classic-0.9.28.jar:target/dependency/logback-core-0.9.28.jar"
export EXECUTOR="java $JAVA_OPTS -cp $CLASS_PATH"
# 1、对文件$TEXT_FILE进行分词，词之间以空格分隔，内容保存到文件target/word.txt
$EXECUTOR org.apdplat.word.WordSegmenter file $TEXT_FILE target/word.txt
# 2、对分好词的文件target/word.txt建立词向量，将词向量保存到文件target/vector.txt，将词汇表保存到文件target/vocabulary.txt
# 上下文长度为
export CONTEXT_WINDOW_LENGTH=2
# 词向量长度
export VECTOR_LENGTH=30
$EXECUTOR org.apdplat.word.vector.Word2Vector target/word.txt target/vector.txt target/vocabulary.txt $CONTEXT_WINDOW_LENGTH $VECTOR_LENGTH
# 3、计算不同词向量之间的相似度，控制台编码为UTF-8
$EXECUTOR org.apdplat.word.vector.Distance target/vector.txt utf-8
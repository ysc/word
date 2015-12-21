package org.apdplat.word.analysis;

import org.apdplat.word.WordSegmenter;
import org.apdplat.word.corpus.Bigram;
import org.apdplat.word.segmentation.Word;
import org.apdplat.word.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 判定句子是有意义的人话的可能性
 * 假设使用有限集合的字符随机生成一句话，如何来判断这句话是有意义的人话的可能性呢？
 * 为了降低难度，可以把“有限集合的字符”降低难度变成“有限集合的词”。
 * 假设常用汉字有3000，随机生成的句子长度为10，则可生成的总的句子数目为：
 * 3000*3000... 也就是3000自乘10次。
 * 所谓智能，是指人的智慧和行动能力，如果计算机不能真正解决这个问题，怎么能谈得上智能呢？
 * 随处可以见到的什么什么智能，什么什么又通过图灵测试，浮躁之风让人目不暇接。
 * Created by ysc on 12/21/15.
 */
public class SentenceIdentify {
    private static final Logger LOGGER = LoggerFactory.getLogger(SentenceIdentify.class);
    private static final List<String> WORDS = new ArrayList<>();
    static {
        try {
            Utils.readResource("/dic.txt").forEach(WORDS::add);
        }catch (Exception e){
            LOGGER.error("load words failed", e);
        }
    }

    public static float identify(String sentence){
        List<Word> words = WordSegmenter.segWithStopWords(sentence);
        System.out.println("随机单词: "+words);
        System.out.println("生成句子: "+sentence);
        return Bigram.sentenceScore(words);
    }

    public static List<String> generateRandomSentences(int count){
        List<String> sentences = new ArrayList<>();
        for(int i=0; i<count; i++){
            StringBuilder sentence = new StringBuilder();
            int len = new Random(System.nanoTime()).nextInt(5)+5;
            for(int j=0; j<len; j++){
                sentence.append(WORDS.get(new Random(System.nanoTime()).nextInt(WORDS.size())));
            }
            sentences.add(sentence.toString());
            sentence.setLength(0);
        }
        return sentences;
    }

    private static void run(String encoding) {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, encoding))){
            String line = null;
            while((line = reader.readLine()) != null){
                if("exit".equals(line)){
                    System.exit(0);
                    LOGGER.info("退出");
                    return;
                }
                if(line.trim().equals("")){
                    continue;
                }
                processSentence(line.split(" "));
                showUsage();
            }
        } catch (IOException ex) {
            LOGGER.error("程序中断：", ex);
        }
    }

    private static void showUsage() {
        System.out.println("");
        System.out.println("********************************************");
        System.out.println("用法: 输入句子并回车");
        System.out.println("输入exit退出程序");
        System.out.println("********************************************");
    }

    private static void processSentence(String[] args) {
        for (String item : args){
            System.out.println("句子概率: " + identify(item));
        }
    }

    public static List<Map.Entry<String, Float>> evaluation(List<String> sentences){
        Map<String, Float> map = new ConcurrentHashMap<>();
        sentences.parallelStream().forEach(sentence -> {
            float score = identify(sentence);
            map.put(sentence, score);
        });
        return map.entrySet().stream().sorted((a,b)->b.getValue().compareTo(a.getValue())).collect(Collectors.toList());
    }

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("我爱读书");
        list.add("我爱学习");
        list.add("我是一个人");
        list.add("我是一个男人你是一个女人");
        list.add("中话眼录学打了啊一有");
        list.add("天我滑去人够");
        list.addAll(generateRandomSentences(94));
        AtomicInteger i = new AtomicInteger();
        evaluation(list).forEach(entry->{
            System.out.println(i.incrementAndGet() + ". 句子: " + entry.getKey() + ", 概率: " + entry.getValue());
        });
        String encoding = "utf-8";
        if(args==null || args.length == 0){
            showUsage();
            run(encoding);
        }else if(Charset.isSupported(args[0])){
            showUsage();
            run(args[0]);
        }else{
            processSentence(args);
            //非交互模式，退出JVM
            System.exit(0);
        }
    }
}

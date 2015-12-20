package org.apdplat.word.analysis;

import org.apdplat.word.WordSegmenter;
import org.apdplat.word.corpus.Bigram;
import org.apdplat.word.segmentation.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

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

    public static float identify(String sentence){
        List<Word> words = WordSegmenter.segWithStopWords(sentence);
        System.out.println("随机单词: "+words);
        System.out.println("生成句子: "+sentence);
        return Bigram.bigram(words);
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
        LOGGER.info("");
        LOGGER.info("********************************************");
        LOGGER.info("用法: 输入句子并回车");
        LOGGER.info("输入exit退出程序");
        LOGGER.info("********************************************");
    }

    private static void processSentence(String[] args) {
        for (String item : args){
            System.out.println("句子概率: " + identify(item));
        }
    }

    public static void main(String[] args) {
        System.out.println("句子概率: " + identify("我爱读书"));
        System.out.println("句子概率: " + identify("我爱学习"));
        System.out.println("句子概率: " + identify("我是一个人"));
        System.out.println("句子概率: " + identify("我是一个男人你是一个女人"));
        System.out.println("句子概率: " + identify("中话眼录学打了啊一有"));
        System.out.println("句子概率: " + identify("天我滑去人够"));
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

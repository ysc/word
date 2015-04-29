/**
 * 
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.apdplat.word.segmentation.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apdplat.word.corpus.Bigram;
import org.apdplat.word.corpus.Trigram;
import org.apdplat.word.dictionary.Dictionary;
import org.apdplat.word.dictionary.DictionaryFactory;
import org.apdplat.word.recognition.PersonName;
import org.apdplat.word.segmentation.Segmentation;
import org.apdplat.word.segmentation.Word;
import org.apdplat.word.recognition.Punctuation;
import org.apdplat.word.segmentation.WordRefiner;
import org.apdplat.word.util.WordConfTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 分词算法抽象类
 * @author 杨尚川
 */
public abstract class AbstractSegmentation  implements Segmentation{
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
    protected static final Dictionary DIC = DictionaryFactory.getDictionary();
    protected static final boolean PERSON_NAME_RECOGNIZE = "true".equals(WordConfTools.get("person.name.recognize", "true"));
    protected static final boolean KEEP_WHITESPACE = "true".equals(WordConfTools.get("keep.whitespace", "false"));
    protected static final boolean KEEP_PUNCTUATION = "true".equals(WordConfTools.get("keep.punctuation", "false"));
    private static final int INTERCEPT_LENGTH = WordConfTools.getInt("intercept.length", 16);
    private static final String NGRAM = WordConfTools.get("ngram", "bigram");
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(WordConfTools.getInt("thread.pool.size", 4));
    public abstract List<Word> segImpl(String text);
    /**
     * 是否启用ngram
     * @return 是或否
     */
    public boolean ngramEnabled(){
        return "bigram".equals(NGRAM) || "trigram".equals(NGRAM);
    }
    /**
     * 利用ngram进行评分
     * @param sentences 多个分词结果
     * @return 评分后的结果
     */
    public Map<List<Word>, Float> ngram(List<Word>... sentences){
        if("bigram".equals(NGRAM)){
            return Bigram.bigram(sentences);
      
        }
        if("trigram".equals(NGRAM)){
            return Trigram.trigram(sentences);
        }
        return null;
    }
    /**
     * 分词时截取的字符串的最大长度
     * @return 
     */
    public int getInterceptLength(){
        if(DIC.getMaxLength() > INTERCEPT_LENGTH){
            return DIC.getMaxLength();
        }
        return INTERCEPT_LENGTH;
    }
    /**
     * 默认分词算法实现：
     * 1、把要分词的文本根据标点符号进行分割
     * 2、对分割后的文本进行分词
     * 3、组合分词结果
     * @param text 文本
     * @return 分词结果
     */
    @Override
    public List<Word> seg(String text) {
        List<Word> result = new ArrayList<>();
        List<String> sentences = Punctuation.seg(text, KEEP_PUNCTUATION);
        if(sentences.size() == 1){
            result = segSentence(sentences.get(0));
        }else {
            //如果是多个句子，可以利用多线程提升分词速度
            List<Future<List<Word>>> futures = new ArrayList<>(sentences.size());
            for (String sentence : sentences) {
                futures.add(submit(sentence));
            }
            sentences.clear();
            for (Future<List<Word>> future : futures) {
                List<Word> words;
                try {
                    words = future.get();
                    if (words != null) {
                        result.addAll(words);
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    LOGGER.error("获取分词结果失败", ex);
                }
            }
            futures.clear();
        }
        //对分词结果进行refine
        LOGGER.debug("对分词结果进行refine之前：{}",result);
        List<Word> finalResult = refine(result);
        LOGGER.debug("对分词结果进行refine之后：{}",finalResult);
        result.clear();
        return finalResult;
    }

    /**
     * 先拆词，再组词
     * @param words
     * @return
     */
    private List<Word> refine(List<Word> words){
        List<Word> result = new ArrayList<>(words.size());
        //一：拆词
        for(Word word : words){
            List<Word> splitWords = WordRefiner.split(word);
            if(splitWords==null){
                result.add(word);
            }else{
                LOGGER.debug("词： "+word.getText()+" 被拆分为："+splitWords);
                result.addAll(splitWords);
            }
        }
        LOGGER.debug("对分词结果进行refine阶段的拆词之后：{}",result);
        //二：组词
        if(result.size()<2){
            return result;
        }
        int combineMaxLength = WordConfTools.getInt("word.refine.combine.max.length", 3);
        if(combineMaxLength < 2){
            combineMaxLength = 2;
        }
        List<Word> finalResult = new ArrayList<>(result.size());
        for(int i=0; i<result.size(); i++){
            List<Word> toCombineWords = null;
            Word combinedWord = null;
            for(int j=2; j<=combineMaxLength; j++){
                int to = i+j;
                if(to > result.size()){
                    to = result.size();
                }
                toCombineWords = result.subList(i, to);
                combinedWord = WordRefiner.combine(toCombineWords);
                if(combinedWord != null){
                    i += j;
                    i--;
                    break;
                }
            }
            if(combinedWord == null){
                finalResult.add(result.get(i));
            }else{
                LOGGER.debug("词： "+toCombineWords+" 被合并为："+combinedWord);
                finalResult.add(combinedWord);
            }
        }
        return finalResult;
    }
    private Future<List<Word>> submit(final String sentence){
        return EXECUTOR_SERVICE.submit(new Callable<List<Word>>(){
            @Override
            public List<Word> call() {
                return segSentence(sentence);
            }
        });
    }
    private List<Word> segSentence(final String sentence){
        if(sentence.length() == 1){
            if(KEEP_WHITESPACE){
                List<Word> result = new ArrayList<>(1);
                result.add(new Word(sentence));
                return result;
            }else{
                if(!isWhiteSpace(sentence.charAt(0))){
                    List<Word> result = new ArrayList<>(1);
                    result.add(new Word(sentence));
                    return result;
                }
            }
        }
        if(sentence.length() > 1){
            List<Word> list = segImpl(sentence);
            if(list != null){
                if(PERSON_NAME_RECOGNIZE){
                    list = PersonName.recognize(list);
                }
                return list;
            }else{
                LOGGER.error("文本 "+sentence+" 没有获得分词结果");
            }
        }
        return null;
    }
    /**
     * 将识别出的词放入队列
     * @param result 队列
     * @param text 文本
     * @param start 词开始索引
     * @param len 词长度
     */
    protected void addWord(List<Word> result, String text, int start, int len){
        Word word = getWord(text, start, len);
        if(word != null){
            result.add(word);
        }
    }
    /**
     * 将识别出的词入栈
     * @param result 栈
     * @param text 文本
     * @param start 词开始索引
     * @param len 词长度
     */
    protected void addWord(Stack<Word> result, String text, int start, int len){
        Word word = getWord(text, start, len);
        if(word != null){
            result.push(word);
        }
    }    
    /**
     * 获取一个已经识别的词
     * @param text 文本
     * @param start 词开始索引
     * @param len 词长度
     * @return 词或空
     */
    protected Word getWord(String text, int start, int len){
        Word word = new Word(text.substring(start, start+len).toLowerCase());
        //方便编译器优化
        if(KEEP_WHITESPACE){
            //保留空白字符
            return word;
        }else{
            //忽略空白字符，包括：空格、全角空格、\t、\n                
            if(len > 1){
                //长度大于1，不会是空白字符
                return word;
            }else{
                //长度为1，只要非空白字符
                if(!(isWhiteSpace(text, start, len))){
                    //不是空白字符，保留
                    return word;           
                }
            }
        }
        return null;
    }
    /**
     * 判断索引下标为start的字符是否为空白字符
     * 这个方法只用在这里
     * 为了速度，不检查索引下标是否越界
     * @param text 文本
     * @param start 索引下标
     * @param len 长度
     * @return 是否
     */
    protected boolean isWhiteSpace(String text, int start, int len){
        return isWhiteSpace(text.charAt(start));
    }
    /**
     * 判断指定的字符是否是空白字符
     * @param c 字符
     * @return 是否是空白字符
     */
    protected boolean isWhiteSpace(char c){
        return c == ' ' || c == '　' || c == '\t' || c == '\n';
    }
    public static void main(String[] args){

    }
}

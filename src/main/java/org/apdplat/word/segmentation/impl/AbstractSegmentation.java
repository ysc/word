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
import org.apdplat.word.corpus.Bigram;
import org.apdplat.word.corpus.Trigram;
import org.apdplat.word.dictionary.Dictionary;
import org.apdplat.word.dictionary.DictionaryFactory;
import org.apdplat.word.recognition.PersonName;
import org.apdplat.word.segmentation.Segmentation;
import org.apdplat.word.segmentation.Word;
import org.apdplat.word.recognition.Punctuation;
import org.apdplat.word.util.WordConfTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 分词算法抽象类
 * @author 杨尚川
 */
public abstract class AbstractSegmentation  implements Segmentation{
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractSegmentation.class);
    protected static final Dictionary DIC = DictionaryFactory.getDictionary();
    protected static final boolean PERSON_NAME_RECOGNIZE = "true".equals(WordConfTools.get("person.name.recognize", "true"));
    protected static final boolean KEEP_WHITESPACE = "true".equals(WordConfTools.get("keep.whitespace", "false"));
    protected static final boolean KEEP_PUNCTUATION = "true".equals(WordConfTools.get("keep.punctuation", "false"));
    private static final int INTERCEPT_LENGTH = WordConfTools.getInt("intercept.length", 16);
    private static final String NGRAM = WordConfTools.get("ngram", "bigram");
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
        for(String sentence : sentences){
            if(sentence.length() == 1){
                if(KEEP_WHITESPACE){
                    result.add(new Word(sentence));
                }else{
                    if(!isWhiteSpace(sentence.charAt(0))){
                        result.add(new Word(sentence));
                    }
                }
            }
            if(sentence.length() > 1){
                List<Word> list = segImpl(sentence);
                if(list != null){
                    if(PERSON_NAME_RECOGNIZE){
                        list = PersonName.recognize(list);
                    }
                    result.addAll(list);
                }else{
                    LOGGER.error("文本 "+sentence+" 没有获得分词结果");
                }
            }
        }
        sentences.clear();
        return result;
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

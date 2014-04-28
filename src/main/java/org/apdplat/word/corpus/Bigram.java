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

package org.apdplat.word.corpus;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apdplat.word.segmentation.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 二元语法模型
 * @author 杨尚川
 */
public class Bigram {
    private static final Logger LOGGER = LoggerFactory.getLogger(Bigram.class);
    private static final GramTrie GRAM_TRIE = new GramTrie();
    /**
     * 含有语境的二元模型分值算法
     * 计算多种分词结果的分值
     * 利用获得的二元模型分值重新计算分词结果的分值
     * 补偿细粒度切分获得分值而粗粒度切分未获得分值的情况
     * @param sentences 多种分词结果
     * @return 分词结果及其对应的分值
     */
    public static Map<List<Word>, Float> bigram(List<Word>... sentences){
        Map<List<Word>, Float> map = new HashMap<>();
        Map<String, Float> bigramScores = new HashMap<>();
        //两个连续的bigram补偿粗粒度分值
        //如：美国, 加州, 大学，如果美国, 加州和加州, 大学有分值
        //则美国加州大学也会获得分值
        Map<String, Float> twoBigramScores = new HashMap<>();
        //1、计算多种分词结果的分值
        for(List<Word> sentence : sentences){
            float score=0;
            //计算其中一种分词结果的分值
            if(sentence.size() > 1){
                String last="";
                for(int i=0; i<sentence.size()-1; i++){
                    String first = sentence.get(i).getText();
                    String second = sentence.get(i+1).getText();
                    float bigramScore = getScore(first, second);
                    if(bigramScore > 0){
                        if(last.endsWith(first)){
                            twoBigramScores.put(last+second, bigramScores.get(last)+bigramScore);
                            last="";
                        }
                        last = first+second;
                        bigramScores.put(last, bigramScore);
                        score += bigramScore;
                    }
                }
            }
            map.put(sentence, score);
        }
        //2、利用获得的二元模型分值重新计算分词结果的分值
        //补偿细粒度切分获得分值而粗粒度切分未获得分值的情况
        //计算多种分词结果的分值
        if(bigramScores.size() > 0 || twoBigramScores.size() > 0){
            for(List<Word> sentence : map.keySet()){
                //计算其中一种分词结果的分值
                for(Word word : sentence){
                    Float bigramScore = bigramScores.get(word.getText());
                    Float twoBigramScore = twoBigramScores.get(word.getText());
                    Float[] array = {bigramScore, twoBigramScore};
                    for(Float score : array){
                        if(score !=null && score > 0){
                            LOGGER.debug(word.getText()+" 获得分值："+score);
                            float value = map.get(sentence);
                            value += score;
                            map.put(sentence, value);
                        }                    
                    }
                }
            }
        }
        
        return map;
    }
    /**
     * 计算分词结果的二元模型分值
     * @param words 分词结果
     * @return 二元模型分值
     */
    public static float bigram(List<Word> words){
        if(words.size() > 1){
            float score=0;
            for(int i=0; i<words.size()-1; i++){
                score += getScore(words.get(i).getText(), words.get(i+1).getText());
            }
            return score;
        }
        return 0;
    }
    /**
     * 获取两个词一前一后紧挨着同时出现在语料库中的分值
     * @param first 前一个词
     * @param second 后一个词
     * @return 同时出现的分值
     */
    public static float getScore(String first, String second) {
        float value = GRAM_TRIE.get(first+":"+second);
        if(value > 0){
            value = (float)Math.sqrt(value);
            LOGGER.debug("二元模型 "+first+":"+second+" 获得分值："+value);
        }
        return value;
    }
    static{
        try{
            LOGGER.info("开始加载bigram文件");
            long start = System.currentTimeMillis();
            String bigramPath = System.getProperty("bigram.path");
            InputStream in = null;
            if(bigramPath == null){
                in = Bigram.class.getClassLoader().getResourceAsStream("bigram.txt");
                LOGGER.info("从类路径bigram.txt加载默认二元语法模型");
            }else{
                bigramPath = bigramPath.trim();
                if(bigramPath.startsWith("classpath:")){
                    in = Bigram.class.getClassLoader().getResourceAsStream(bigramPath.replace("classpath:", ""));
                }else{
                    in = new FileInputStream(bigramPath);
                }                    
            }
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(in,"utf-8"));){
                String line;
                while( (line = reader.readLine()) != null ){
                    //去除首尾空白字符
                    line = line.trim();
                    //忽略空行
                    if(!"".equals(line)){
                        String[] attr = line.split("\\s+");
                        GRAM_TRIE.put(attr[0], Integer.parseInt(attr[1]));
                    }
                }
            }
            long cost = System.currentTimeMillis() - start;
            LOGGER.info("成功加载bigram文件，耗时："+cost+" 毫秒");
        }catch (IOException ex) {
            System.err.println("bigram文件装载失败:"+ex.getMessage());
            throw new RuntimeException(ex);
        }
    }
}
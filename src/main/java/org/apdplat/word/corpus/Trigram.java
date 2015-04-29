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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apdplat.word.segmentation.Word;
import org.apdplat.word.util.AutoDetector;
import org.apdplat.word.util.GenericTrie;
import org.apdplat.word.util.ResourceLoader;
import org.apdplat.word.util.WordConfTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 三元语法模型
 * @author 杨尚川
 */
public class Trigram {
    private static final Logger LOGGER = LoggerFactory.getLogger(Trigram.class);
    private static final GenericTrie<Integer> GENERIC_TRIE = new GenericTrie();
    static{
        reload();
    }
    public static void reload(){
        AutoDetector.loadAndWatch(new ResourceLoader(){

            @Override
            public void clear() {
                GENERIC_TRIE.clear();
            }

            @Override
            public void load(List<String> lines) {
                LOGGER.info("初始化trigram");
                int count=0;
                for(String line : lines){
                    try{
                        String[] attr = line.split("\\s+");
                        GENERIC_TRIE.put(attr[0], Integer.parseInt(attr[1]));
                        count++;
                    }catch(Exception e){
                        LOGGER.error("错误的trigram数据："+line);
                    }
                }
                LOGGER.info("trigram初始化完毕，trigram数据条数："+count);
            }

            @Override
            public void add(String line) {
                try{
                    String[] attr = line.split("\\s+");
                    GENERIC_TRIE.put(attr[0], Integer.parseInt(attr[1]));
                }catch(Exception e){
                    LOGGER.error("错误的trigram数据："+line);
                }
            }

            @Override
            public void remove(String line) {
                try{
                    String[] attr = line.split("\\s+");
                    GENERIC_TRIE.remove(attr[0]);
                }catch(Exception e){
                    LOGGER.error("错误的trigram数据："+line);
                }
            }
        
        }, WordConfTools.get("trigram.path", "classpath:trigram.txt"));
    }
    /**
     * 一次性计算多种分词结果的三元模型分值
     * @param sentences 多种分词结果
     * @return 分词结果及其对应的分值
     */
    public static Map<List<Word>, Float> trigram(List<Word>... sentences){
        Map<List<Word>, Float> map = new HashMap<>();
        //计算多种分词结果的分值
        for(List<Word> sentence : sentences){
            if(map.get(sentence) != null){
                //相同的分词结果只计算一次分值
                continue;
            }
            float score=0;
            //计算其中一种分词结果的分值
            if(sentence.size() > 2){
                for(int i=0; i<sentence.size()-2; i++){
                    String first = sentence.get(i).getText();
                    String second = sentence.get(i+1).getText();
                    String third = sentence.get(i+2).getText();
                    float trigramScore = getScore(first, second, third);
                    if(trigramScore > 0){
                        score += trigramScore;
                    }
                }
            }
            map.put(sentence, score);
        }
        
        return map;
    }
    /**
     * 计算分词结果的三元模型分值
     * @param words 分词结果
     * @return 三元模型分值
     */
    public static float trigram(List<Word> words){
        if(words.size() > 2){
            float score=0;
            for(int i=0; i<words.size()-2; i++){
                score += Trigram.getScore(words.get(i).getText(), words.get(i+1).getText(), words.get(i+2).getText());
            }
            return score;
        }
        return 0;
    }
    /**
     * 获取三个词前后紧挨着同时出现在语料库中的分值
     * @param first 第一个词
     * @param second 第二个词
     * @param third 第三个词
     * @return 同时出现的分值
     */
    public static float getScore(String first, String second, String third) {
        Integer value = GENERIC_TRIE.get(first+":"+second+":"+third);
        float score = 0;
        if(value != null){
            score = (float)Math.sqrt(value.intValue());
            LOGGER.debug("三元模型 "+first+":"+second+":"+third+" 获得分值："+score);
        }
        return score;
    }
}
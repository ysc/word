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

import java.util.List;
import org.apdplat.word.segmentation.Word;
import org.apdplat.word.util.AutoDetector;
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
    private static final GramTrie GRAM_TRIE = new GramTrie();
    static{
        reload();
    }
    public static void reload(){
        AutoDetector.loadAndWatch(new ResourceLoader(){

            @Override
            public void clear() {
                GRAM_TRIE.clear();
            }

            @Override
            public void load(List<String> lines) {
                LOGGER.info("初始化trigram");
                int count=0;
                for(String line : lines){
                    try{
                        String[] attr = line.split("\\s+");
                        GRAM_TRIE.put(attr[0], Integer.parseInt(attr[1]));
                        count++;
                    }catch(Exception e){
                        LOGGER.error("错误的trigram数据："+line);
                    }
                }
                LOGGER.info("trigram初始化完毕，trigram数据条数："+count);
            }
        
        }, WordConfTools.get("trigram.path", "classpath:trigram.txt"));
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
        float value = GRAM_TRIE.get(first+":"+second+":"+third);
        if(value > 0){
            value = (float)Math.sqrt(value);
            LOGGER.debug("三元模型 "+first+":"+second+":"+third+" 获得分值："+value);
        }
        return value;
    }
}
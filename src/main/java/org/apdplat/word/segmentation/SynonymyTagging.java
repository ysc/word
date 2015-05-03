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

package org.apdplat.word.segmentation;

import org.apdplat.word.util.AutoDetector;
import org.apdplat.word.util.GenericTrie;
import org.apdplat.word.util.ResourceLoader;
import org.apdplat.word.util.WordConfTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 同义标注
 * @author 杨尚川
 */
public class SynonymyTagging {
    private SynonymyTagging(){}

    private static final Logger LOGGER = LoggerFactory.getLogger(SynonymyTagging.class);
    private static final GenericTrie<String[]> GENERIC_TRIE = new GenericTrie<>();
    static{
        reload();
    }
    public static void reload(){
        AutoDetector.loadAndWatch(new ResourceLoader() {

            @Override
            public void clear() {
                GENERIC_TRIE.clear();
            }

            @Override
            public void load(List<String> lines) {
                LOGGER.info("初始化Synonymy");
                int count = 0;
                for (String line : lines) {
                    try {
                        String[] attr = line.split("\\s+");
                        if(attr!=null && attr.length>1) {
                            for(String item : attr) {
                                GENERIC_TRIE.put(item.trim(), attr);
                                count++;
                            }
                        }else{
                            LOGGER.error("错误的Synonymy数据：" + line);
                        }
                    } catch (Exception e) {
                        LOGGER.error("错误的Synonymy数据：" + line);
                    }
                }
                LOGGER.info("Synonymy初始化完毕，数据条数：" + count);
            }

            @Override
            public void add(String line) {
                try {
                    String[] attr = line.split("\\s+");
                    if(attr!=null && attr.length>1) {
                        for(String item : attr) {
                            GENERIC_TRIE.put(item.trim(), attr);
                        }
                    }else{
                        LOGGER.error("错误的Synonymy数据：" + line);
                    }
                } catch (Exception e) {
                    LOGGER.error("错误的Synonymy数据：" + line);
                }
            }

            @Override
            public void remove(String line) {
                try {
                    String[] attr = line.split("\\s+");
                    if(attr!=null && attr.length>1) {
                        for(String item : attr) {
                            GENERIC_TRIE.remove(item.trim());
                        }
                    }else{
                        LOGGER.error("错误的Synonymy数据：" + line);
                    }
                } catch (Exception e) {
                    LOGGER.error("错误的Synonymy数据：" + line);
                }
            }

        }, WordConfTools.get("word.synonym.path", "classpath:word_synonym.txt"));
    }
    public static void process(List<Word> words){
        LOGGER.debug("对分词结果进行同义标注之前：{}", words);
        //同义标注
        for(Word word : words){
            String[] synonym = GENERIC_TRIE.get(word.getText());
            if(synonym!=null && synonym.length>1){
                //有同义词
                List<Word> synonymList = toWord(synonym);
                synonymList.remove(word);
                word.setSynonym(synonymList);
            }
        }
        LOGGER.debug("对分词结果进行同义标注之后：{}", words);
    }
    private static List<Word> toWord(String[] words){
        List<Word> result = new ArrayList<>(words.length);
        for (String word : words){
            result.add(new Word(word));
        }
        return result;
    }

    public static void main(String[] args) {
        List<Word> words = SegmentationFactory.getSegmentation(SegmentationAlgorithm.BidirectionalMaximumMatching).seg("楚离陌千方百计为无情找回记忆");
        System.out.println(words);
        SynonymyTagging.process(words);
        System.out.println(words);
        words = SegmentationFactory.getSegmentation(SegmentationAlgorithm.BidirectionalMaximumMatching).seg("手劲大的老人往往更长寿");
        System.out.println(words);
        SynonymyTagging.process(words);
        System.out.println(words);
    }
}

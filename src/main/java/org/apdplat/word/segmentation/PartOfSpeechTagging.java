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
import org.apdplat.word.util.ResourceLoader;
import org.apdplat.word.util.WordConfTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 词性标注
 * @author 杨尚川
 */
public class PartOfSpeechTagging {
    private PartOfSpeechTagging(){}

    private static final Logger LOGGER = LoggerFactory.getLogger(PartOfSpeechTagging.class);
    private static final PartOfSpeechTrie PART_OF_SPEECH_TRIE = new PartOfSpeechTrie();
    static{
        reload();
    }
    public static void reload(){
        AutoDetector.loadAndWatch(new ResourceLoader() {

            @Override
            public void clear() {
                PART_OF_SPEECH_TRIE.clear();
            }

            @Override
            public void load(List<String> lines) {
                LOGGER.info("初始化词性标注器");
                int count = 0;
                for (String line : lines) {
                    try {
                        String[] attr = line.split(":");
                        PART_OF_SPEECH_TRIE.put(attr[0], attr[1]);
                        count++;
                    } catch (Exception e) {
                        LOGGER.error("错误的词性数据：" + line);
                    }
                }
                LOGGER.info("词性标注器初始化完毕，词性数据条数：" + count);
            }

            @Override
            public void add(String line) {
                try {
                    String[] attr = line.split("\\s+");
                    PART_OF_SPEECH_TRIE.put(attr[0], attr[1]);
                } catch (Exception e) {
                    LOGGER.error("错误的词性数据：" + line);
                }
            }

            @Override
            public void remove(String line) {
                try {
                    String[] attr = line.split(":");
                    PART_OF_SPEECH_TRIE.remove(attr[0]);
                } catch (Exception e) {
                    LOGGER.error("错误的词性数据：" + line);
                }
            }

        }, WordConfTools.get("part.of.speech.path", "classpath:part_of_speech.txt"));
    }
    public static void process(List<Word> words){
        words.parallelStream().forEach(word->{
            word.setPartOfSpeech(PartOfSpeech.valueOf(PART_OF_SPEECH_TRIE.get(word.getText())));
        });
    }
}

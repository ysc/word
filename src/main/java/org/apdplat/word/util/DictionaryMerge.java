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

package org.apdplat.word.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apdplat.word.recognition.RecognitionTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 词典合并清理
 * 去除单字词
 * 去除非中文词
 * @author 杨尚川
 */
public class DictionaryMerge {    
    private static final Logger LOGGER = LoggerFactory.getLogger(DictionaryMerge.class);
    public static void main(String[] args) throws IOException{
        List<String> sources = new ArrayList<>();
        sources.add("src/main/resources/dic.txt");
        sources.add("target/dic.txt");
        String target = "src/main/resources/dic.txt";
        merge(sources, target);
    }
    /**
     * 把多个词典合并为一个
     * @param sources 多个待词典
     * @param target 合并后的词典
     * @throws IOException 
     */
    public static void merge(List<String> sources, String target) throws IOException{
        List<String> lines = new ArrayList<>();
        //读取所有需要合并的词典
        for(String source : sources){
            lines.addAll(Files.readAllLines(Paths.get(source), Charset.forName("utf-8")));
        }
        Set<String> set = new HashSet<>();
        for(String line : lines){
            line = line.trim();
            if(line.length() > 7 
                    || !Utils.isChineseCharAndLengthAtLeastTwo(line) 
                    || RecognitionTool.recog(line)){
                LOGGER.info("过滤："+line);
                continue;
            }
            set.add(line);
        }
        LOGGER.info("合并词数："+lines.size());
        LOGGER.info("保留词数："+set.size());
        lines.clear();
        List<String> list = new ArrayList<>();
        list.addAll(set);
        set.clear();
        Collections.sort(list);
        Files.write(Paths.get(target), list, Charset.forName("utf-8"));
    }
}

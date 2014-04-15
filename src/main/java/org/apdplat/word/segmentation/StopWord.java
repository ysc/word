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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 停用词判定
 * @author 杨尚川
 */
public class StopWord {
    private static final Logger LOGGER = LoggerFactory.getLogger(StopWord.class);
    private static final Set<String> stopwords = new HashSet<>();
    static{
        loadStopWords();
    }
    public static boolean is(String word){
        return stopwords.contains(word);
    }
    public static void main(String[] args){
        LOGGER.info("停用词：");
        for(String w : stopwords){
            LOGGER.info(w);
        }
    }
    public static void loadStopWords(){
        try {
            long start = System.currentTimeMillis();
            String stopwordsPath = System.getProperty("stopwords.path");
            InputStream in = null;
            if(stopwordsPath == null){
                in = StopWord.class.getClassLoader().getResourceAsStream("stopwords.txt");
                LOGGER.info("从类路径stopwords.txt加载停用词");
            }else{
                stopwordsPath = stopwordsPath.trim();
                LOGGER.info("加载停用词："+stopwordsPath);
                if(stopwordsPath.startsWith("classpath:")){
                    in = StopWord.class.getClassLoader().getResourceAsStream(stopwordsPath.replace("classpath:", ""));
                }else{
                    in = new FileInputStream(stopwordsPath);
                }                    
            }
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(in,"utf-8"))){
                String line;
                while((line = reader.readLine()) != null){
                    line = line.trim();
                    if("".equals(line) || line.startsWith("#")){
                        continue;
                    }
                    stopwords.add(line);
                }
            }
            long cost = System.currentTimeMillis() - start;
            LOGGER.info("完成初始化停用词，耗时"+cost+" 毫秒，停用词数目："+stopwords.size());
        } catch (IOException ex) {
            System.err.println("停用词装载失败:"+ex.getMessage());
            throw new RuntimeException(ex);
        }
    }
}
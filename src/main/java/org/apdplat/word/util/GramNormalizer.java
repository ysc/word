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
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * N元模型规范化
 * 对N元模型的词频进行归一化处理
 * 去除N元模型中包含非中文字符的记录
 * @author 杨尚川
 */
public class GramNormalizer {
    private static final Logger LOGGER = LoggerFactory.getLogger(GramNormalizer.class);
    private static final NumberFormat FORMAT = NumberFormat.getNumberInstance();
    static{
        FORMAT.setMaximumFractionDigits(4);
    }
    public static void main(String[] args) throws IOException{
        uniformAndNormForBigramAndTrigram();
    }
    /**
     * 对二元模型和三元模型进行归一化，然后去除非中文字符记录
     */
    public static void uniformAndNormForBigramAndTrigram(){
        try{
            String src = "src/main/resources/bigram.txt";
            String dst = "src/main/resources/bigram.txt";
            uniform(src, dst, 2);
            norm(src, dst, 2);
            src = "src/main/resources/trigram.txt";
            dst = "src/main/resources/trigram.txt";
            uniform(src, dst, 3);
            norm(src, dst, 3);
        }catch(Exception e){
            LOGGER.info("模型规范化失败："+e.getMessage());
        }
    }
    /**
     * 对N元模型的词频进行归一化处理
     * @param src 模型输入路径
     * @param dst 模型保存路径
     * @param n 几元模型
     * @throws IOException 
     */
    public static void uniform(String src, String dst, int n) throws IOException{
        List<String> lines = Files.readAllLines(Paths.get(src), Charset.forName("utf-8"));
        Map<String, Float> map = new HashMap<>();
        float maxCount=0;
        //找到最大出现次数
        for(String line : lines){
            String[] attr = line.split(" -> ");
            if(attr == null || attr.length != 2){
                LOGGER.error("错误数据："+line);
                continue;
            }
            String key = attr[0];
            String value = attr[1];
            String[] words = key.split(":");
            if(words == null || words.length != n || value == null){
                LOGGER.error("错误数据："+line);
                continue;
            }
            if(value.indexOf(".") != -1){
                LOGGER.debug("已经做过归一化处理，忽略...");
                //不用处理了，程序返回
                return ;
            }
            float count = Float.parseFloat(value);
            if(count > maxCount){
                maxCount = count;
            }
            map.put(key, count);
        }
        LOGGER.info(n+"元模型出现最大次数："+maxCount);
        //归一化
        maxCount = (float)Math.sqrt(maxCount);
        for(String key : map.keySet()){
            Float count = map.get(key);
            Float score = (float)Math.sqrt(count)/maxCount;
            map.put(key, score);
        }
        List<String> list = new ArrayList<>(map.size());
        //把map转换为list
        for(String key : map.keySet()){
            list.add(key+" -> "+FORMAT.format(map.get(key)));
        }
        map.clear();
        //排序
        Collections.sort(list);
        LOGGER.info("总的模型数："+list.size());
        Files.write(Paths.get(dst), list, Charset.forName("utf-8"));
    }
    /**
     * 去除N元模型中包含非中文字符的记录
     * @param src 模型输入路径
     * @param dst 模型保存路径
     * @param n 几元模型
     * @throws IOException 
     */
    public static void norm(String src, String dst, int n) throws IOException{
        List<String> lines = Files.readAllLines(Paths.get(src), Charset.forName("utf-8"));
        int len = lines.size();
        int error=0;
        int filte=0;
        Iterator<String> iter = lines.iterator();
        while(iter.hasNext()){
            String line = iter.next();
            String[] attr = line.split(" -> ");
            if(attr == null || attr.length != 2){
                LOGGER.error("错误数据："+line);
                error++;
                iter.remove();
                continue;
            }
            String key = attr[0];
            String[] words = key.split(":");
            if(words == null || words.length != n){
                LOGGER.error("错误数据："+line);
                error++;
                iter.remove();
                continue;
            }
            for(int i=0; i<n; i++){
                if(!Utils.isChineseCharAndLengthAtLeastOne(words[i])){
                    LOGGER.debug("过滤模型："+line);
                    filte++;
                    iter.remove();
                    break;
                }
            }            
        }
        LOGGER.info("总的模型数："+len);
        LOGGER.info("保留模型数："+lines.size());
        LOGGER.info("错误模型数："+error);
        LOGGER.info("过滤模型数："+filte);
        Files.write(Paths.get(dst), lines, Charset.forName("utf-8"));
    }
}
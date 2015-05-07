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

package org.apdplat.word.vector;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apdplat.word.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 计算词和词的相似性
 * @author 杨尚川
 */
public class Distance {
    private static final Logger LOGGER = LoggerFactory.getLogger(Distance.class);
    public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException, IOException{
        String model = "target/vector.txt";
        String encoding = "gbk";
        if(args.length == 1){
            model = args[0];
        }
        if(args.length == 2){
            model = args[0];
            encoding = args[1];
        }
        Map<String, String> map = new HashMap<>();
        LOGGER.info("开始初始化模型");
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(model),"utf-8"))){
            String line = null;
            while((line = reader.readLine()) != null){
                String[] attr = line.split(" : ");
                if(attr==null || attr.length != 2){
                    LOGGER.error("错误数据："+line);
                    continue;
                }
                String key = attr[0];
                String value = attr[1];
                value = value.substring(1, value.length()-1);
                map.put(key, value);
            }
        }
        LOGGER.info("模型初始化完成");        
        LOGGER.info("输入要查询的词或（exit）命令离开：");
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, encoding))){
            String line = null;
            while((line = reader.readLine()) != null){
                if("exit".equals(line)){
                    return;
                }
                String value = map.get(line);
                if(value == null){
                    LOGGER.info("没有对应的词："+line);
                }else{
                    LOGGER.info(line+"：");
                    LOGGER.info("----------------------------------------------------------");
                    List<String> list = distance(map, value, 15);
                    for(String element : list){
                        LOGGER.info("\t"+element);
                    }                    
                    LOGGER.info("----------------------------------------------------------");
                }
            }
        }
    }
    
    private static List<String> distance(Map<String, String> map, String words, int limit){
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("计算词向量：" + words);
            LOGGER.debug("限制结果数目：" + limit);
        }
        Map<String, Float> wordVec = new HashMap<>();
        String[] ws = words.split(", ");
        for(String w : ws){
            String[] attr = w.split(" ");
            String k = attr[0];
            float v = Float.parseFloat(attr[1]);
            wordVec.put(k, v);
        }       
        Map<String, Float> result = new HashMap<>();
        float max=0;
        for(String key : map.keySet()){
            //词向量
            String value = map.get(key);
            String[] elements = value.split(", ");
            Map<String, Float> vec = new HashMap<>();
            for(String element : elements){
                String[] attr = element.split(" ");
                String k = attr[0];
                float v = Float.parseFloat(attr[1]);
                vec.put(k, v);
            }
            //计算距离
            float score=0;
            int times=0;
            for(String out : wordVec.keySet()){
                for(String in : vec.keySet()){
                    if(out.equals(in)){
                        //有交集
                        score += wordVec.get(out) * vec.get(in);
                        times++;
                    }
                }
            }
            //忽略没有交集的词
            if(score > 0){
                score *= times;
                result.put(key, score);
                if(score > max){
                    max = score;
                }
            }
        }
        if(max == 0){
            if(LOGGER.isDebugEnabled()) {
                LOGGER.debug("没有相似词");
            }
            return Collections.emptyList();
        }
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("最大分值：" + max);
            LOGGER.debug("相似词数：" + result.size());
        }
        //分值归一化
        for(String key : result.keySet()){
            float value = result.get(key);
            value /= max;
            result.put(key, value);
        }
        //排序
        List<Entry<String, Float>> list = Utils.getSortedMapByValue(result);
        //限制结果数目
        if(limit > list.size()){
            limit = list.size();
        }
        //转换为字符串
        List<String> retValue = new ArrayList<>(limit);
        for(int i=0; i< limit; i++){
            retValue.add(list.get(i).getKey()+" "+list.get(i).getValue());
        }
        return retValue;
    }
}

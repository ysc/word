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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 获取配置信息的工具类
 * @author 杨尚川
 */
public class WordConfTools {
    private static final Logger LOGGER = LoggerFactory.getLogger(WordConfTools.class);
    private static final Map<String, String> conf = new HashMap<>();
    public static String get(String key, String defaultValue){
        LOGGER.info("获取配置项："+key);
        return conf.get(key) == null ? defaultValue : conf.get(key);
    }
    public static String get(String key){
        LOGGER.info("获取配置项："+key);
        return conf.get(key);
    }
    static{
        LOGGER.info("开始加载配置文件");
        long start = System.currentTimeMillis();
        try(BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        WordConfTools.class.getClassLoader().getResourceAsStream("word.conf"),"utf-8"))){
            String line;
            while((line = reader.readLine()) != null){
                line = line.trim();                
                if("".equals(line) || line.startsWith("#")){
                    continue;
                }
                String[] attr = line.split("=");
                if(attr != null && attr.length == 2){
                    conf.put(attr[0], attr[1]);
                }
            }
        } catch (IOException ex) {
            System.err.println("配置文件加载失败:"+ex.getMessage());
            throw new RuntimeException(ex);
        }
        long cost = System.currentTimeMillis() - start;
        LOGGER.info("配置文件加载完毕，耗时"+cost+" 毫秒，配置项数目："+conf.size());
        LOGGER.info("配置信息：");
        for(String key : conf.keySet()){
            LOGGER.info(key+"="+conf.get(key));
        }
    }
    public static void main(String[] args){
    }
}
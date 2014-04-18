package org.apdplat.word.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.slf4j.LoggerFactory;

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

/**
 * 判断一个字符是否是标点符号
 * @author 杨尚川
 */
public class Punctuation {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Punctuation.class);
    private static char[] chars = null;
    static{
        init();
    }
    private static void init(){
        try{
            String path = WordConfTools.get("punctuation.path", "classpath:punctuation.txt");
            path = path.trim();
            LOGGER.info("初始化标点符号资源："+path);
            InputStream in = null;
            if(path.startsWith("classpath:")){
                in = Punctuation.class.getClassLoader().getResourceAsStream(path.replace("classpath:", ""));
            }else{
                in = new FileInputStream(path);
            }
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(in,"utf-8"))){
                List<String> list = new ArrayList<>();
                String line;
                while((line = reader.readLine()) != null){
                    line = line.trim();
                    if("".equals(line) || line.startsWith("#")){
                        continue;
                    }
                    if(line.length() == 1){
                        list.add(line);
                    }
                }
                Collections.sort(list);
                int len = list.size();
                LOGGER.info("开始构造标点符号有序字符数组："+list);
                chars = new char[len];
                for(int i=0; i<len; i++){
                    chars[i] = list.get(i).charAt(0);
                }
                list.clear();
            }
        }catch (IOException ex) {
            LOGGER.error("加载标点符号失败：", ex);
        }
    }
    /**
     * 判断一个字符是否是标点符号
     * @param _char 字符
     * @return 是否是标点符号
     */
    public static boolean is(char _char){
        int index = Arrays.binarySearch(chars, _char);
        return index >= 0;
    }
    public static void main(String[] args){
        LOGGER.info("标点符号资源");
        LOGGER.info(", : "+is(','));
    }
}
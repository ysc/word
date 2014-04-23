package org.apdplat.word.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
                Set<Character> set = new HashSet<>();
                String line;
                while((line = reader.readLine()) != null){
                    line = line.trim();
                    if("".equals(line) || line.startsWith("#")){
                        continue;
                    }
                    if(line.length() == 1){
                        set.add(line.charAt(0));
                    }else{
                        LOGGER.warn("长度不为一的标点符号："+line);
                    }
                }
                //增加空白字符
                set.add(' ');
                set.add('　');
                set.add('\t');
                set.add('\n');
                List<Character> list = new ArrayList<>();
                list.addAll(set);
                Collections.sort(list);
                int len = list.size();
                LOGGER.info("开始构造标点符号有序字符数组："+list);
                chars = new char[len];
                for(int i=0; i<len; i++){
                    chars[i] = list.get(i);
                }
                list.clear();
            }
        }catch (IOException ex) {
            LOGGER.error("加载标点符号失败：", ex);
        }
    }
    /**
     * 判断文本中是否包含标点符号
     * @param text
     * @return 
     */
    public static boolean has(String text){
        for(char c : text.toCharArray()){
            if(is(c)){
                return true;
            }
        }
        return false;
    }
    /**
     * 将一段文本根据标点符号分割为多个不包含标点符号的文本
     * @param text 文本
     * @param withPunctuation 是否保留标点符号
     * @return 文本列表
     */
    public static List<String> seg(String text, boolean withPunctuation){
        List<String> list = new ArrayList<>();
        int start = 0;
        char[] array = text.toCharArray();
        int len = array.length;
        for(int i=0; i<len; i++){
            if(Punctuation.is(array[i])){
                if(i > start){
                    list.add(text.substring(start, i));
                    //下一句开始索引
                    start = i+1;
                }else{
                    //跳过标点符号
                    start++;
                }
                if(withPunctuation){
                    list.add(Character.toString(array[i]));
                }
            }
        }
        if(len - start > 0){
            list.add(text.substring(start, len));
        }
        return list;
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
        LOGGER.info("  : "+is(' '));
        LOGGER.info("　 : "+is('　'));
        LOGGER.info("\t : "+is('\t'));
        LOGGER.info("\n : "+is('\n'));
        String text= "APDPlat的雏形可以追溯到2008年，并于4年后即2012年4月9日在GITHUB开源 。APDPlat在演化的过程中，经受住了众多项目的考验，一直追求简洁优雅，一直对架构、设计和代码进行重构优化。 ";
        for(String s : Punctuation.seg(text, true)){
            LOGGER.info(s);
        }
    }
}
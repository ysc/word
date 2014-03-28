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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 二元语法模型
 * @author 杨尚川
 */
public class Bigram {
    private static final Map<String, Integer> BIGRAM = new HashMap<>();
    /**
     * 获取两个词一前一后紧挨着同时出现在语料库中的词数
     * @param first 前一个词
     * @param second 后一个词
     * @return 同时出现的词数
     */
    public static int getCount(String first, String second) {
        Integer value = BIGRAM.get(first+":"+second);
        if(value == null){
            value = 0;
        }
        return value;
    }
    static{
        try{
            System.out.println("开始加载bigram文件");
            long start = System.currentTimeMillis();
            String bigramPath = System.getProperty("bigram.path");
            InputStream in = null;
            if(bigramPath == null){
                in = Bigram.class.getClassLoader().getResourceAsStream("bigram.txt");
                System.out.println("从类路径bigram.txt加载默认二元语法模型");
            }else{
                bigramPath = bigramPath.trim();
                if(bigramPath.startsWith("classpath:")){
                    in = Bigram.class.getClassLoader().getResourceAsStream(bigramPath.replace("classpath:", ""));
                }else{
                    in = new FileInputStream(bigramPath);
                }                    
            }
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(in,"utf-8"));){
                String line;
                while( (line = reader.readLine()) != null ){
                    //去除首尾空白字符
                    line = line.trim();
                    //忽略空行
                    if(!"".equals(line)){
                        String[] attr = line.split(" -> ");
                        BIGRAM.put(attr[0], Integer.parseInt(attr[1]));
                    }
                }
            }
            long cost = System.currentTimeMillis() - start;
            System.out.println("成功加载bigram文件，耗时："+cost+" 毫秒");
        }catch (IOException ex) {
            System.err.println("bigram文件装载失败:"+ex.getMessage());
            throw new RuntimeException(ex);
        }
    }
}
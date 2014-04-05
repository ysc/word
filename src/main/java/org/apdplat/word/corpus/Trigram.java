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
 * 三元语法模型
 * @author 杨尚川
 */
public class Trigram {
    private static final Map<String, Float> TRIGRAM = new HashMap<>();
    /**
     * 获取三个词前后紧挨着同时出现在语料库中的分值
     * @param first 第一个词
     * @param second 第二个词
     * @param third 第三个词
     * @return 同时出现的分值
     */
    public static float getScore(String first, String second, String third) {
        Float value = TRIGRAM.get(first+":"+second+":"+third);
        if(value == null){
            value = 0f;
        }
        return value;
    }
    static{
        try{
            System.out.println("开始加载trigram文件");
            long start = System.currentTimeMillis();
            String trigramPath = System.getProperty("trigram.path");
            InputStream in = null;
            if(trigramPath == null){
                in = Trigram.class.getClassLoader().getResourceAsStream("trigram.txt");
                System.out.println("从类路径trigram.txt加载默认三元语法模型");
            }else{
                trigramPath = trigramPath.trim();
                if(trigramPath.startsWith("classpath:")){
                    in = Trigram.class.getClassLoader().getResourceAsStream(trigramPath.replace("classpath:", ""));
                }else{
                    in = new FileInputStream(trigramPath);
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
                        TRIGRAM.put(attr[0], Float.parseFloat(attr[1]));
                    }
                }
            }
            long cost = System.currentTimeMillis() - start;
            System.out.println("成功加载trigram文件，耗时："+cost+" 毫秒");
        }catch (IOException ex) {
            System.err.println("trigram文件装载失败:"+ex.getMessage());
            throw new RuntimeException(ex);
        }
    }
}
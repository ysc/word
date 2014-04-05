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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * N元模型规范化
 * 对N元模型的词频进行归一化处理
 * 去除N元模型中包含非中文字符的记录
 * @author 杨尚川
 */
public class GramNormalizer {
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
            System.out.println("模型规范化失败："+e.getMessage());
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
        Map<String, Float> counts = new HashMap<>();
        //统计n-1前缀完全相同的总数
        for(String line : lines){
            String[] attr = line.split(" -> ");
            if(attr == null || attr.length != 2){
                System.out.println("错误数据："+line);
                continue;
            }
            String key = attr[0];
            String value = attr[1];
            String[] words = key.split(":");
            if(words == null || words.length != n || value == null){
                System.out.println("错误数据："+line);
                continue;
            }
            if(value.indexOf(".") != -1){
                System.out.println("已经做过归一化处理，忽略...");
                //不用处理了，程序返回
                return ;
            }
            Float frequence = Float.parseFloat(value);
            map.put(key, frequence);
            String base = getBase(words);
            Float count = counts.get(base);
            if(count == null){
                count = frequence;
            }else{
                count += frequence;
            }
            counts.put(base, count);
        }
        //对n-1前缀相同的情况重新计算分值
        for(String key : map.keySet()){
            String base = getBase(key.split(":"));
            Float count = counts.get(base);
            Float value = map.get(key);
            Float score = value/count;
            map.put(key, score);
        }
        counts.clear();
        List<String> list = new ArrayList<>(map.size());
        //把map转换为list
        for(String key : map.keySet()){
            list.add(key+" -> "+map.get(key));
        }
        map.clear();
        //排序
        Collections.sort(list);
        System.out.println("总的模型数："+list.size());
        Files.write(Paths.get(dst), list, Charset.forName("utf-8"));
    }
    /**
     * 获取N元模型的N-1前缀
     * @param words N元模型
     * @return N-1前缀
     */
    private static String getBase(String[] words) {
        StringBuilder base = new StringBuilder();
        for(int i=0; i<words.length-1; i++){
            base.append(words[i]);
        }
        return base.toString();
    }
    /**
     * 去除N元模型中包含非中文字符的记录
     * @param src 模型输入路径
     * @param dst 模型保存路径
     * @param n 几元模型
     * @throws IOException 
     */
    public static void norm(String src, String dst, int n) throws IOException{
        //至少出现1次中文字符，且以中文字符开头和结束
        Pattern pattern = Pattern.compile("^[\\u4e00-\\u9fa5]+$");
        List<String> lines = Files.readAllLines(Paths.get(src), Charset.forName("utf-8"));
        int len = lines.size();
        int error=0;
        int filte=0;
        Iterator<String> iter = lines.iterator();
        while(iter.hasNext()){
            String line = iter.next();
            String[] attr = line.split(" -> ");
            if(attr == null || attr.length != 2){
                System.out.println("错误数据："+line);
                error++;
                iter.remove();
                continue;
            }
            String key = attr[0];
            String[] words = key.split(":");
            if(words == null || words.length != n){
                System.out.println("错误数据："+line);
                error++;
                iter.remove();
                continue;
            }
            for(int i=0; i<n; i++){
                if(!pattern.matcher(words[i]).find()){
                    System.out.println("过滤模型："+line);
                    filte++;
                    iter.remove();
                    break;
                }
            }            
        }
        System.out.println("总的模型数："+len);
        System.out.println("保留模型数："+lines.size());
        System.out.println("错误模型数："+error);
        System.out.println("过滤模型数："+filte);
        Files.write(Paths.get(dst), lines, Charset.forName("utf-8"));
    }
}
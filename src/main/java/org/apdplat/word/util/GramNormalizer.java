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
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * N元模型规范化
 * 去除N元模型中包含非中文字符的记录
 * @author 杨尚川
 */
public class GramNormalizer {
    public static void main(String[] args) throws IOException{
        String src = "src/main/resources/bigram.txt";
        String dst = "src/main/resources/bigram.txt";
        norm(src, dst, 2);
        src = "src/main/resources/trigram.txt";
        dst = "src/main/resources/trigram.txt";
        norm(src, dst, 3);
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
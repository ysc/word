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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 词典合并清理
 * 去除单字词
 * 去除非中文词
 * @author 杨尚川
 */
public class DictionaryMerge {
    public static void main(String[] args) throws IOException{
        //至少出现两次中文字符，且以中文字符开头和结束
        Pattern pattern = Pattern.compile("^[\\u4e00-\\u9fa5]{2,}$");
        List<String> lines = Files.readAllLines(Paths.get("dic1.txt"), Charset.forName("utf-8"));
        lines.addAll(Files.readAllLines(Paths.get("dic2.txt"), Charset.forName("utf-8")));
        Set<String> set = new HashSet<>();
        for(String line : lines){
            line = line.replace("[", "").replace("]", "");
            if(!pattern.matcher(line).find()){
                System.out.println("过滤："+line);
                continue;
            }
            set.add(line);
        }
        System.out.println("词数："+lines.size());
        System.out.println("保留词数："+set.size());
        List<String> list = new ArrayList<>();
        list.addAll(set);
        Collections.sort(list);
        Files.write(Paths.get("dic.txt"), list, Charset.forName("utf-8"));
    }
}

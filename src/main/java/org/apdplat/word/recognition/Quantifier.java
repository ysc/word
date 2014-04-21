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

package org.apdplat.word.recognition;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import org.apdplat.word.util.WordConfTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数量词识别
 * @author 杨尚川
 */
public class Quantifier {
private static final Logger LOGGER = LoggerFactory.getLogger(PersonName.class);
    private static final Set<Character> quantifiers=new HashSet<>();
    static{
        loadNumberSuffix();
    }
    private static void loadNumberSuffix(){
        try{
            String path = WordConfTools.get("quantifier.path", "classpath:quantifier.txt");
            path = path.trim();
            LOGGER.info("初始化数量词："+path);
            InputStream in = null;
            if(path.startsWith("classpath:")){
                in = PersonName.class.getClassLoader().getResourceAsStream(path.replace("classpath:", ""));
            }else{
                in = new FileInputStream(path);
            }
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(in,"utf-8"))){
                String line;
                while((line = reader.readLine()) != null){
                    line = line.trim();
                    if(line.startsWith("#")){
                        continue;
                    }
                    if(line.length() == 1){
                        char _char = line.charAt(0);
                        if(quantifiers.contains(_char)){
                            LOGGER.info("配置文件有重复项："+line);
                        }
                        quantifiers.add(_char);
                    }else{
                        LOGGER.info("忽略不合法配置项："+line);
                    }
                }
            }
        }catch(IOException e){
            LOGGER.error("加载数量词出错：", e);
        }
    }
    public static boolean is(char _char){
        return quantifiers.contains(_char);
    }
    public static void main(String[] args){
        int i=1;
        for(char quantifier : quantifiers){
            LOGGER.info((i++)+" : "+quantifier);
        }
    }
}

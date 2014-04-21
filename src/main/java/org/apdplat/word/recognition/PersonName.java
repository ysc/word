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
 * 人名识别
 * @author 杨尚川
 */
public class PersonName {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonName.class);
    private static final Set<String> surname1=new HashSet<>();
    private static final Set<String> surname2=new HashSet<>();
    static{
        loadSurname();
    }
    private static void loadSurname(){
        try{
            String path = WordConfTools.get("surname.path", "classpath:surname.txt");
            path = path.trim();
            LOGGER.info("初始化百家姓："+path);
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
                    if(line.length()==1){
                        surname1.add(line);
                    }else if(line.length()==2){
                        surname2.add(line);
                    }else{
                       LOGGER.error("错误的姓："+line);
                    }
                }
            }
        }catch(IOException e){
            LOGGER.error("加载百家姓出错：", e);
        }
    }
    public static boolean is(String text){
        return (surname1.contains(text.substring(0, 1)) && text.length()<=3)
                || (surname2.contains(text.substring(0, 2))  && text.length()<=4);
    }
    public static void main(String[] args){
        int i=1;
        for(String str : surname1){
            LOGGER.info((i++)+" : "+str);
        }
        for(String str : surname2){
            LOGGER.info((i++)+" : "+str);
        }
        LOGGER.info("杨尚川："+is("杨尚川"));
        LOGGER.info("杨尚川爱读书："+is("杨尚川爱读书"));
    }
}
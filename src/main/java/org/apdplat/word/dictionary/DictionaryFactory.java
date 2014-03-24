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

package org.apdplat.word.dictionary;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;
import org.apdplat.word.dictionary.impl.TrieV4;

/**
 * 词典工厂
 * 通过系统属性指定词典实现类（dic.class）和词典文件（dic.path）
 * 默认实现词典实现类（org.apdplat.word.dictionary.impl.TrieV3）
 * 和词典文件（当前目录下的dic.txt）
 * @author 杨尚川
 */
public final class DictionaryFactory {
    private DictionaryFactory(){}
    public static final Dictionary getDictionary(){
        return DictionaryHolder.DIC;
    }
    private static final class DictionaryHolder{
        private static final Dictionary DIC;
        static{
            try {
                System.out.println("开始初始化词典");
                long start = System.currentTimeMillis();
                //选择词典实现，可以通过参数选择不同的实现
                String dicClass = System.getProperty("dic.class");
                if(dicClass == null){
                    dicClass = "org.apdplat.word.dictionary.impl.TrieV4";
                }
                System.out.println("dic.class="+dicClass);
                DIC = (Dictionary)Class.forName(dicClass).newInstance();
                //选择词典
                String dicPath = System.getProperty("dic.path");
                InputStream in = null;
                if(dicPath == null){
                    in = DictionaryFactory.class.getClassLoader().getResourceAsStream("dic.txt");
                    System.out.println("从类路径dic.txt加载默认词典");
                }else{
                    dicPath = dicPath.trim();
                    System.out.println("加载词典："+dicPath);
                    if(dicPath.startsWith("classpath:")){
                        in = DictionaryFactory.class.getClassLoader().getResourceAsStream(dicPath.replace("classpath:", ""));
                    }else{
                        in = new FileInputStream(dicPath);
                    }                    
                }
                //统计词数
                int wordCount=0;
                //统计平均词长
                int totalLength=0;
                //统计词长分布
                Map<Integer,Integer> map = new TreeMap<>();
                try(BufferedReader reader = new BufferedReader(new InputStreamReader(in,"utf-8"))){
                    String line;
                    while((line = reader.readLine()) != null){
                        line = line.trim();
                        if("".equals(line) || line.startsWith("#")){
                            continue;
                        }
                        wordCount++;
                        //加入词典
                        DIC.add(line);
                        //统计不同长度的词的数目
                        int len = line.length();
                        totalLength+=len;
                        Integer value = map.get(len);
                        if(value==null){
                            value=1;
                        }else{
                            value++;
                        }
                        map.put(len, value);
                    }
                }
                long cost = System.currentTimeMillis() - start;
                System.out.println("完成初始化词典，耗时"+cost+" 毫秒，词数目："+wordCount);
                System.out.println("词典最大词长："+DIC.getMaxLength());
                for(int len : map.keySet()){
                    if(len<10){
                        System.out.println("词长  "+len+" 的词数为："+map.get(len));
                    }else{
                        System.out.println("词长 "+len+" 的词数为："+map.get(len));
                    }
                }
                System.out.println("词典平均词长："+(float)totalLength/wordCount);
                if(DIC instanceof TrieV4){
                    TrieV4 trieV4 = (TrieV4)DIC;
                    trieV4.showConflict();
                }
            } catch (IOException | ClassNotFoundException | IllegalAccessException | InstantiationException ex) {
                System.err.println("词典装载失败:"+ex.getMessage());
                throw new RuntimeException(ex);
            }
        }
    }
}

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apdplat.word.segmentation.Word;
import org.apdplat.word.util.AutoDetector;
import org.apdplat.word.util.ResourceLoader;
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
        AutoDetector.loadAndWatch(new ResourceLoader(){

            @Override
            public void clear() {
                surname1.clear();
                surname2.clear();
            }

            @Override
            public void load(List<String> lines) {
                LOGGER.info("初始化百家姓");
                for(String line : lines){
                    if(line.length()==1){
                        surname1.add(line);
                    }else if(line.length()==2){
                        surname2.add(line);
                    }else{
                       LOGGER.error("错误的姓："+line);
                    }
                }
                LOGGER.info("百家姓初始化完毕，单姓个数："+surname1.size()+"，复姓个数："+surname2.size());
            }
        
        }, WordConfTools.get("surname.path", "classpath:surname.txt"));
    }
    /**
     * 获取所有的姓
     * @return 有序列表
     */
    public static List<String> getSurnames(){
        List<String> result = new ArrayList<>();
        result.addAll(surname1);
        result.addAll(surname2);
        Collections.sort(result);
        return result;
    }
    /**
     * 如果文本为人名，则返回姓
     * @param text 文本
     * @return 姓或空文本
     */
    public static String getSurname(String text){
        if(is(text)){
            //优先识别复姓
            if(isSurname(text.substring(0, 2))){
                return text.substring(0, 2);
            }
            if(isSurname(text.substring(0, 1))){
                return text.substring(0, 1);
            }
        }
        return "";
    }
    /**
     * 判断文本是不是百家姓
     * @param text 文本
     * @return 是否
     */
    public static boolean isSurname(String text){
        return surname1.contains(text) || surname2.contains(text);
    }
    /**
     * 人名判定
     * @param text 文本
     * @return 是或否
     */
    public static boolean is(String text){
        int len = text.length();
        //单姓为二字或三字
        //复姓为三字或四字
        if(len < 2){
            //长度小于2肯定不是姓名
            return false;
        }
        if(len == 2){
            //如果长度为2，则第一个字符必须是姓
            return surname1.contains(text.substring(0, 1));
        }
        if(len == 3){
            //如果长度为3
            //要么是单姓
            //要么是复姓
            return surname1.contains(text.substring(0, 1)) || surname2.contains(text.substring(0, 2));
        }
        if(len == 4){
            //如果长度为4，只能是复姓
            return surname2.contains(text.substring(0, 2));
        }
        return false;
    }
    /**
     * 对分词结果进行处理，识别人名
     * @param words 待识别分词结果
     * @return 识别后的分词结果
     */
    public static List<Word> recognize(List<Word> words){
        int len = words.size();
        if(len < 2){
            return words;
        }
        List<Word> result = new ArrayList<>();
        for(int i=0; i<len-1; i++){
            String second = words.get(i+1).getText();
            if(second.length() > 1){
                result.add(new Word(words.get(i).getText()));
                result.add(new Word(words.get(i+1).getText()));
                i++;
                if(i == len-2){
                    result.add(new Word(words.get(i+1).getText()));
                }
                continue;
            }
            String first = words.get(i).getText();
            if(isSurname(first)){
                String third = "";
                if(i+2 < len && words.get(i+2).getText().length()==1){
                    third = words.get(i+2).getText();                    
                }
                String text = first+second+third;
                if(is(text)){
                    LOGGER.debug("识别到人名："+text);
                    result.add(new Word(text));
                    i++;
                    if(!"".equals(third)){
                        i++;
                    }
                }else{
                    result.add(new Word(first));
                }
            }else{
                result.add(new Word(first));
            }
            if(i == len-2){
                result.add(new Word(words.get(i+1).getText()));
            }
        }
        return result;
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
        LOGGER.info("欧阳飞燕："+is("欧阳飞燕"));
        LOGGER.info("令狐冲："+is("令狐冲"));
        LOGGER.info("杨尚川爱读书："+is("杨尚川爱读书"));
        List<Word> test = new ArrayList<>();
        test.add(new Word("快"));
        test.add(new Word("来"));
        test.add(new Word("看"));
        test.add(new Word("杨"));
        test.add(new Word("尚"));
        test.add(new Word("川"));
        test.add(new Word("表演"));
        test.add(new Word("魔术"));
        test.add(new Word("了"));
        LOGGER.info(recognize(test).toString());
        
        test = new ArrayList<>();
        test.add(new Word("李"));
        test.add(new Word("世"));
        test.add(new Word("明"));
        test.add(new Word("的"));
        test.add(new Word("昭仪"));
        test.add(new Word("欧阳"));
        test.add(new Word("飞"));
        test.add(new Word("燕"));
        test.add(new Word("其实"));
        test.add(new Word("很"));
        test.add(new Word("厉害"));
        test.add(new Word("呀"));
        test.add(new Word("！"));
        test.add(new Word("比"));
        test.add(new Word("公孙"));
        test.add(new Word("黄"));
        test.add(new Word("后"));
        test.add(new Word("牛"));
        LOGGER.info(recognize(test).toString());
                
        test = new ArrayList<>();
        test.add(new Word("发展"));
        test.add(new Word("中国"));
        test.add(new Word("家兔"));
        test.add(new Word("的"));
        test.add(new Word("计划"));
        LOGGER.info(recognize(test).toString());
        
        test = new ArrayList<>();
        test.add(new Word("杨尚川"));
        test.add(new Word("好"));
        LOGGER.info(recognize(test).toString());
        
        LOGGER.info(getSurname("欧阳锋"));
        LOGGER.info(getSurname("李阳锋"));
    }
}
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

package org.apdplat.word;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.apdplat.word.dictionary.Dictionary;
import org.apdplat.word.dictionary.DictionaryFactory;

/**
 * 基于词典的正向最大匹配算法
 * 基于词典的逆向最大匹配算法
 * @author 杨尚川
 */
public class WordSeg {
    private static final Dictionary DIC = DictionaryFactory.getDictionary();
    
    public static void main(String[] args){
        long start = System.currentTimeMillis();
        List<String> sentences = new ArrayList<>();
        sentences.add("杨尚川是APDPlat应用级产品开发平台的作者");
        sentences.add("研究生命的起源");
        sentences.add("长春市长春节致辞");
        sentences.add("他从马上下来");
        sentences.add("乒乓球拍卖完了");
        sentences.add("咬死猎人的狗");
        sentences.add("大学生活象白纸");
        sentences.add("安徽省合肥市长江路");
        sentences.add("有意见分歧");
        for(String sentence : sentences){
            System.out.println("正向最大匹配: "+seg(sentence));
            System.out.println("逆向最大匹配: "+segReverse(sentence));
        }
        long cost = System.currentTimeMillis() - start;
        System.out.println("cost: "+cost);
    }
    public static List<String> seg(String text){        
        List<String> result = new ArrayList<>();
        int len=DIC.getMaxLength();
        while(text.length()>0){            
            if(text.length()<len){
                len=text.length();
            }
            //取指定的最大长度的文本去词典里面匹配
            String tryWord = text.substring(0, 0+len);
            while(!DIC.contains(tryWord)){
                //如果长度为一且在词典中未找到匹配，则按长度为一切分
                if(tryWord.length()==1){
                    break;
                }
                //如果匹配不到，则长度减一继续匹配
                tryWord=tryWord.substring(0, tryWord.length()-1);
            }
            result.add(tryWord);
            //从待分词文本中去除已经分词的文本
            text=text.substring(tryWord.length());
        }
        return result;
    }
    public static List<String> segReverse(String text){        
        Stack<String> result = new Stack<>();
        int len=DIC.getMaxLength();
        while(text.length()>0){
            if(text.length()<len){
                len=text.length();
            }
            //取指定的最大长度的文本去词典里面匹配
            String tryWord = text.substring(text.length() - len);
            while(!DIC.contains(tryWord)){
                //如果长度为一且在词典中未找到匹配，则按长度为一切分
                if(tryWord.length()==1){
                    break;
                }
                //如果匹配不到，则长度减一继续匹配
                tryWord=tryWord.substring(1);
            }
            result.push(tryWord);
            //从待分词文本中去除已经分词的文本
            text=text.substring(0, text.length()-tryWord.length());
        }
        len=result.size();
        List<String> list = new ArrayList<>(len);
        for(int i=0;i<len;i++){
            list.add(result.pop());
        }
        return list;
    }
}

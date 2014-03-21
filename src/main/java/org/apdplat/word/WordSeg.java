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
        //文本长度
        final int textLen=text.length();
        //从未分词的文本中截取的长度
        int len=DIC.getMaxLength();
        //剩下未分词的文本的索引
        int start=0;
        //只要有词未切分完就一直继续
        while(start<textLen){
            if(len>textLen-start){
                //如果未分词的文本的长度小于截取的长度
                //则缩短截取的长度
                len=textLen-start;
            }
            //用长为len的字符串查词典
            while(!DIC.contains(text, start, len)){
                //如果长度为一且在词典中未找到匹配
                //则按长度为一切分
                if(len==1){
                    break;
                }
                //如果查不到，则长度减一后继续
                len--;
            }
            result.add(text.substring(start, start+len));
            //从待分词文本中向后移动索引，滑过已经分词的文本
            start+=len;
            //每一次成功切词后都要重置截取长度
            len=DIC.getMaxLength();
        }
        return result;
    }
    public static List<String> segReverse(String text){        
        Stack<String> result = new Stack<>();
        //文本长度
        final int textLen=text.length();
        //从未分词的文本中截取的长度
        int len=DIC.getMaxLength();
        //剩下未分词的文本的索引
        int start=textLen-len;
        //处理文本长度小于最大词长的情况
        if(start<0){
            start=0;
        }
        if(len>textLen-start){
            //如果未分词的文本的长度小于截取的长度
            //则缩短截取的长度
            len=textLen-start;
        }
        //只要有词未切分完就一直继续
        while(start>=0 && len>0){
            //用长为len的字符串查词典
            while(!DIC.contains(text, start, len)){
                //如果长度为一且在词典中未找到匹配
                //则按长度为一切分
                if(len==1){
                    break;
                }
                //如果查不到，则长度减一
                //索引向后移动一个字，然后继续
                len--;
                start++;
            }
            result.push(text.substring(start, start+len));
            //每一次成功切词后都要重置截取长度
            len=DIC.getMaxLength();            
            if(len>start){
                //如果未分词的文本的长度小于截取的长度
                //则缩短截取的长度
                len=start;
            }
            //每一次成功切词后都要重置开始索引位置
            //从待分词文本中向前移动最大词长个索引
            //将未分词的文本纳入下次分词的范围
            start-=len;
        }
        len=result.size();
        List<String> list = new ArrayList<>(len);
        for(int i=0;i<len;i++){
            list.add(result.pop());
        }
        return list;
    }
}

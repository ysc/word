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

package org.apdplat.word.segmentation.impl;

import java.util.List;
import java.util.Stack;
import org.apdplat.word.dictionary.Dictionary;
import org.apdplat.word.dictionary.DictionaryFactory;
import org.apdplat.word.segmentation.Segmentation;
import org.apdplat.word.segmentation.Word;
import org.apdplat.word.util.WordConfTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 分词算法抽象类
 * @author 杨尚川
 */
public abstract class AbstractSegmentation  implements Segmentation{
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractSegmentation.class);
    protected static final Dictionary DIC = DictionaryFactory.getDictionary();
    protected static final boolean PERSON_NAME_RECOGNIZE = "true".equals(WordConfTools.get("person.name.recognize", "true"));
    protected static final boolean KEEP_WHITESPACE = "true".equals(WordConfTools.get("keep.whitespace", "false"));

    protected void addWord(List<Word> result, String text, int start, int len){
        //方便编译器优化
        if(KEEP_WHITESPACE){
            //保留空白字符
            result.add(new Word(text.substring(start, start+len).toLowerCase()));
        }else{
            //忽略空白字符，包括：空格、全角空格、\t、\n                
            if(len > 1){
                //长度大于1，不会是空白字符
                result.add(new Word(text.substring(start, start+len).toLowerCase()));
            }else{
                //长度为1，只要非空白字符
                if(!(isWhiteSpace(text, start, len))){
                    //不是空白字符，保留
                    result.add(new Word(text.substring(start, start+len).toLowerCase()));                        
                }
            }
        }
    }
    protected void addWord(Stack<Word> result, String text, int start, int len){
        //方便编译器优化
        if(KEEP_WHITESPACE){
            //保留空白字符
            result.push(new Word(text.substring(start, start+len).toLowerCase()));
        }else{
            //忽略空白字符，包括：空格、全角空格、\t、\n                
            if(len > 1){
                //长度大于1，不会是空白字符
                result.push(new Word(text.substring(start, start+len).toLowerCase()));
            }else{
                //长度为1，只要非空白字符
                if(!(isWhiteSpace(text, start, len))){
                    //不是空白字符，保留
                    result.push(new Word(text.substring(start, start+len).toLowerCase()));                        
                }
            }
        }
    }
    /**
     * 判断索引下标为start的字符是否为空白字符
     * 这个方法只用在这里
     * 为了速度，不检查索引下标是否越界
     * @param text 文本
     * @param start 索引下标
     * @param len 长度
     * @return 是否
     */
    protected boolean isWhiteSpace(String text, int start, int len){
        return isWhiteSpace(text.charAt(start));
    }
    /**
     * 判断指定的字符是否是空白字符
     * @param c 字符
     * @return 是否是空白字符
     */
    protected boolean isWhiteSpace(char c){
        return c == ' ' || c == '　' || c == '\t' || c == '\n';
    }
    public static void main(String[] args){

    }
}

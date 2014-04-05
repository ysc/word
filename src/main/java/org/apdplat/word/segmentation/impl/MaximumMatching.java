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

import java.util.ArrayList;
import java.util.List;
import org.apdplat.word.dictionary.Dictionary;
import org.apdplat.word.dictionary.DictionaryFactory;
import org.apdplat.word.recognition.RecognitionTool;
import org.apdplat.word.segmentation.Segmentation;
import org.apdplat.word.segmentation.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于词典的正向最大匹配算法
 * Dictionary-based maximum matching algorithm
 * @author 杨尚川
 */
public class MaximumMatching implements Segmentation{
    private static final Logger LOGGER = LoggerFactory.getLogger(MaximumMatching.class);
    private static final Dictionary DIC = DictionaryFactory.getDictionary();
    @Override
    public List<Word> seg(String text) {
        List<Word> result = new ArrayList<>();
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
            //用长为len的字符串查词典，并做特殊情况识别
            while(!DIC.contains(text, start, len) && !RecognitionTool.recog(text, start, len)){
                //如果长度为一且在词典中未找到匹配
                //则按长度为一切分
                if(len==1){
                    break;
                }
                //如果查不到，则长度减一后继续
                len--;
            }
            result.add(new Word(text.substring(start, start+len)));
            //从待分词文本中向后移动索引，滑过已经分词的文本
            start+=len;
            //每一次成功切词后都要重置截取长度
            len=DIC.getMaxLength();
        }
        return result;
    }
    public static void main(String[] args){
        String text = "ysc是杨尚川的英文名，杨尚川在2009年获得了系统架构设计师的职称";
        if(args !=null && args.length == 1){
            text = args[0];
        }
        MaximumMatching m = new MaximumMatching();
        LOGGER.info(m.seg(text).toString());
    }
}

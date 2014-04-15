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

package org.apdplat.word.segmentation;

import java.util.List;
import org.apdplat.word.corpus.Bigram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 默认中文分词器
 * 使用二元模型从4种切分结果中选择一种最好的
 * 如果分值都一样，则选择基于词典的逆向最大匹配算法
 * 实验表明，对于汉语来说，逆向最大匹配算法比(正向)最大匹配算法更有效
 * @author 杨尚川
 */
public class WordSegmentation implements Segmentation{
    private static final Logger LOGGER = LoggerFactory.getLogger(WordSegmentation.class);    
    private static final Segmentation MM = SegmentationFactory.getSegmentation(SegmentationAlgorithm.MaximumMatching);
    private static final Segmentation RMM = SegmentationFactory.getSegmentation(SegmentationAlgorithm.ReverseMaximumMatching);
    private static final Segmentation MIM = SegmentationFactory.getSegmentation(SegmentationAlgorithm.MinimumMatching);
    private static final Segmentation RMIM = SegmentationFactory.getSegmentation(SegmentationAlgorithm.ReverseMinimumMatching);
        
    @Override
    public List<Word> seg(String text){
        //逆向最大匹配为默认选择，如果分值都一样的话
        List<Word> words = RMM.seg(text);
        float score = Bigram.bigram(words);
        List<Word> result = words;
        float max = score;
        //正向最大匹配
        words = MM.seg(text);
        score = Bigram.bigram(words);
        //只有正向最大匹配的分值大于逆向最大匹配，才会被选择
        if(score > max){
            result = words;
            max = score;
        }
        //逆向最小匹配
        words = RMIM.seg(text);
        score = Bigram.bigram(words);
        if(score > max){
            result = words;
            max = score;
        }
        //正向最小匹配
        words = MIM.seg(text);
        score = Bigram.bigram(words);
        if(score > max){
            result = words;
            max = score;
        }
        LOGGER.debug("最大分值："+max+", 消歧结果："+result);
        return result;
    }
    public static void main(String[] args){
        Segmentation segmentation = new WordSegmentation();
        LOGGER.info(segmentation.seg("杨尚川是APDPlat应用级产品开发平台的作者").toString());
    }
}
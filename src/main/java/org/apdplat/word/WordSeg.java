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
import org.apdplat.word.corpus.Bigram;
import org.apdplat.word.corpus.Trigram;
import org.apdplat.word.segmentation.Segmentation;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import org.apdplat.word.segmentation.SegmentationFactory;
import org.apdplat.word.segmentation.Word;

/**
 * 中文分词基础入口
 * @author 杨尚川
 */
public class WordSeg {
    private static final Segmentation MM = SegmentationFactory.getSegmentation(SegmentationAlgorithm.MaximumMatching);
    private static final Segmentation RMM = SegmentationFactory.getSegmentation(SegmentationAlgorithm.ReverseMaximumMatching);
    private static final Segmentation MIM = SegmentationFactory.getSegmentation(SegmentationAlgorithm.MinimumMatching);
    private static final Segmentation RMIM = SegmentationFactory.getSegmentation(SegmentationAlgorithm.ReverseMinimumMatching);
    
    /**
     * 默认使用基于词典的逆向最大匹配算法
     * 实验表明，对于汉语来说，逆向最大匹配算法比(正向)最大匹配算法更有效
     * @param text
     * @return 
     */
    public static List<Word> seg(String text){
        return RMM.seg(text);
    }
    
    public static void main(String[] args){
        long start = System.currentTimeMillis();
        List<String> sentences = new ArrayList<>();
        sentences.add("杨尚川是APDPlat应用级产品开发平台的作者");
        sentences.add("他说的确实在理");
        sentences.add("提高人民生活水平");
        sentences.add("他俩儿谈恋爱是从头年元月开始的");
        sentences.add("王府饭店的设施和服务是一流的");
        sentences.add("和服务于三日后裁制完毕，并呈送将军府中");
        sentences.add("研究生命的起源");
        sentences.add("他明天起身去北京");
        sentences.add("在这些企业中国有企业有十个");
        sentences.add("他站起身来");
        sentences.add("他们是来查金泰撞人那件事的");
        sentences.add("行侠仗义的查金泰远近闻名");
        sentences.add("长春市长春节致辞");
        sentences.add("他从马上摔下来了,你马上下来一下");
        sentences.add("乒乓球拍卖完了");
        sentences.add("咬死猎人的狗");
        sentences.add("地面积了厚厚的雪");
        sentences.add("这几块地面积还真不小");
        sentences.add("大学生活象白纸");
        sentences.add("结合成分子式");
        sentences.add("有意见分歧");
        sentences.add("发展中国家兔的计划");
        sentences.add("明天他将来北京");
        sentences.add("税收制度将来会更完善");
        sentences.add("依靠群众才能做好工作");
        sentences.add("现在是施展才能的好机会");
        sentences.add("把手举起来");
        sentences.add("茶杯的把手断了");
        sentences.add("以新的姿态出现在世界东方");
        sentences.add("使节约粮食进一步形成风气");
        sentences.add("反映了一个人的精神面貌");
        sentences.add("美国加州大学的科学家发现");
        sentences.add("我好不挺好");
        sentences.add("木有"); 
        sentences.add("下雨天留客天天留我不留");
        sentences.add("叔叔亲了我妈妈也亲了我");
        for(String sentence : sentences){
            System.out.println("切分句子: "+sentence);
            List<Word> words = MM.seg(sentence);
            System.out.println("正向最大匹配: "+words);
            System.out.println("二元分值："+bigram(words));
            System.out.println("三元分值："+trigram(words));
            words = MIM.seg(sentence);
            System.out.println("正向最小匹配: "+words);
            System.out.println("二元分值："+bigram(words));
            System.out.println("三元分值："+trigram(words));
            words = RMM.seg(sentence);
            System.out.println("逆向最大匹配: "+words);
            System.out.println("二元分值："+bigram(words));
            System.out.println("三元分值："+trigram(words));
            
            words = RMIM.seg(sentence);
            System.out.println("逆向最小匹配: "+words);
            System.out.println("二元分值："+bigram(words));
            System.out.println("三元分值："+trigram(words));
        }
        long cost = System.currentTimeMillis() - start;
        System.out.println("cost: "+cost);
    }
    private static float bigram(List<Word> words){
        if(words.size() > 1){
            float score=0;
            for(int i=0; i<words.size()-1; i++){
                score += Bigram.getScore(words.get(i).getText(), words.get(i+1).getText());
            }
            return score;
        }
        return 0;
    }
    private static float trigram(List<Word> words){
        if(words.size() > 2){
            float score=0;
            for(int i=0; i<words.size()-2; i++){
                score += Trigram.getScore(words.get(i).getText(), words.get(i+1).getText(), words.get(i+2).getText());
            }
            return score;
        }
        return 0;
    }
}

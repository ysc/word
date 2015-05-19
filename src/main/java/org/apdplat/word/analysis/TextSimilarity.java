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

package org.apdplat.word.analysis;

import org.apdplat.word.recognition.StopWord;
import org.apdplat.word.segmentation.Segmentation;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import org.apdplat.word.segmentation.SegmentationFactory;
import org.apdplat.word.segmentation.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 文本相似度
 * @author 杨尚川
 */
public abstract class TextSimilarity implements Similarity{
    protected static final Logger LOGGER = LoggerFactory.getLogger(TextSimilarity.class);
    //默认分词器
    private static final Segmentation SEGMENTATION = SegmentationFactory.getSegmentation(SegmentationAlgorithm.MaxNgramScore);
    //相似性阈值
    protected float thresholdRate = 0.5F;
    //是否忽略停用词
    protected boolean filterStopWord = false;
    /**
     * 文本1和文本2是否相似
     * @param text1 文本1
     * @param text2 文本2
     * @return 是否相似
     */
    @Override
    public boolean isSimilar(String text1, String text2) {
        return similarScore(text1, text2) >= thresholdRate;
    }
    /**
     * 文本1和文本2的相似度分值
     * @param text1 文本1
     * @param text2 文本2
     * @return 相似度分值
     */
    @Override
    public double similarScore(String text1, String text2) {
        if(text1 != null && text2 != null){
            double score = score(text1, text2);
            //取两位小数
            score = (int)(score*100)/(double)100;
            return score;
        }
        return 0;
    }
    private double score(String text1, String text2){
        //分词
        List<Word> words1 = seg(text1);
        List<Word> words2 = seg(text2);
        //词频统计
        Map<Word, AtomicInteger> frequency1 = frequency(words1);
        Map<Word, AtomicInteger> frequency2 = frequency(words2);
        //输出详细信息
        if(LOGGER.isDebugEnabled()){
            showDetail(words1, frequency1);
            showDetail(words2, frequency2);
        }
        //计算相似度分值
        return scoreImpl(frequency1, frequency2);
    }

    protected abstract double scoreImpl(Map<Word, AtomicInteger> frequency1, Map<Word, AtomicInteger> frequency2);

    private List<Word> seg(String text){
        List<Word> words = SEGMENTATION.seg(text);
        if(filterStopWord) {
            //停用词过滤
            StopWord.filterStopWords(words);
        }
        return words;
    }

    private Map<Word, AtomicInteger> frequency(List<Word> words){
        Map<Word, AtomicInteger> frequency =new HashMap<>();
        words.forEach(word->{
            frequency.putIfAbsent(word, new AtomicInteger());
            frequency.get(word).incrementAndGet();
        });
        return frequency;
    }

    private void showDetail(List<Word> words, Map<Word, AtomicInteger> frequency){
        LOGGER.debug("分词结果：");
        LOGGER.debug("\t"+words);
        LOGGER.debug("词频统计：");
        AtomicInteger c = new AtomicInteger();
        frequency
                .entrySet()
                .stream()
                .sorted((a,b)->b.getValue().get()-a.getValue().get())
                .forEach(e->LOGGER.debug("\t"+c.incrementAndGet()+"、"+e.getKey()+"="+e.getValue()));
    }
}

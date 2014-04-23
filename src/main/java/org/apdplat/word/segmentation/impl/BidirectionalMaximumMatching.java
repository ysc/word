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
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apdplat.word.corpus.Bigram;
import org.apdplat.word.segmentation.Segmentation;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import org.apdplat.word.segmentation.SegmentationFactory;
import org.apdplat.word.segmentation.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于词典的双向最大匹配算法
 * Dictionary-based bidirectional maximum matching algorithm
 * @author 杨尚川
 */
public class BidirectionalMaximumMatching implements Segmentation{
    private static final Logger LOGGER = LoggerFactory.getLogger(BidirectionalMaximumMatching.class);
    private static final Segmentation MM = SegmentationFactory.getSegmentation(SegmentationAlgorithm.MaximumMatching);
    private static final Segmentation RMM = SegmentationFactory.getSegmentation(SegmentationAlgorithm.ReverseMaximumMatching);
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();
    @Override
    public List<Word> seg(final String text) {
        //逆向最大匹配
        Future<List<Word>> wordsRMMFuture = EXECUTOR_SERVICE.submit(new Callable<List<Word>>(){
            @Override
            public List<Word> call() throws Exception {
                return RMM.seg(text);
            }            
        });
        //正向最大匹配
        Future<List<Word>> wordsMMFuture = EXECUTOR_SERVICE.submit(new Callable<List<Word>>(){
            @Override
            public List<Word> call() throws Exception {
                return MM.seg(text);
            }            
        });
        List<Word> wordsRMM = null;
        List<Word> wordsMM = null;
        try{
            wordsRMM = wordsRMMFuture.get();
            wordsMM = wordsMMFuture.get();
        }catch(InterruptedException | ExecutionException e){
            LOGGER.error("获取分词结果失败：", e);
            return null;
        }
        //如果分词结果都一样，则直接返回结果
        if(wordsRMM.toString().equals(wordsMM.toString())){            
            return wordsRMM;
        }
        
        //如果分词结果不一样，则利用二元模型消歧
        Map<List<Word>, Float> words = Bigram.bigram(wordsRMM, wordsMM);        
      
        //如果分值都一样，则选择逆向最大匹配
        float score = words.get(wordsRMM);
        LOGGER.debug("逆向最大匹配："+wordsRMM.toString()+" : 二元模型分值="+score);
        //最终结果
        List<Word> result = wordsRMM;
        //最大分值
        float max = score;
        
        score = words.get(wordsMM);
        LOGGER.debug("正向最大匹配："+wordsMM.toString()+" : 二元模型分值="+score);
        //只有正向最大匹配的分值大于逆向最大匹配，才会被选择
        if(score > max){
            result = wordsMM;
            max = score;
        }
        
        LOGGER.debug("最大分值："+max+", 消歧结果："+result);
        return result;
    }
    public static void main(String[] args){
        String text = "他十分惊讶地说：“啊，原来是您，杨尚川！能见到您真是太好了，我有个Nutch问题想向您请教呢！”";
        if(args !=null && args.length == 1){
            text = args[0];
        }
        BidirectionalMaximumMatching m = new BidirectionalMaximumMatching();
        LOGGER.info(m.seg(text).toString());
    }
}

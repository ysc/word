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

import org.apdplat.word.segmentation.Word;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 文本相似度计算
 * 判定方式：余弦相似度，通过计算两个向量的夹角余弦值来评估他们的相似度
 * @author 杨尚川
 */
public class CosineTextSimilarity extends TextSimilarity {
    /**
     * 判定相似度的方式：余弦相似度
     * 余弦夹角原理：
     * 向量a=(x1,y1),向量b=(x2,y2)
     * a.b=x1x2+y1y2
     * |a|=根号[(x1)^2+(y1)^2],|b|=根号[(x2)^2+(y2)^2]
     * @param words1 词列表1
     * @param words2 词列表2
     * @param frequency1 词列表1的词频统计结果
     * @param frequency2 词列表2的词频统计结果
     * @return 相似度分值
     */
    @Override
    protected double scoreImpl(List<Word> words1, List<Word> words2, Map<Word, AtomicInteger> frequency1, Map<Word, AtomicInteger> frequency2) {
        Set<Word> words = new HashSet<>();
        words.addAll(frequency1.keySet());
        words.addAll(frequency2.keySet());
        //向量的维度为words的大小，每一个维度的权重是词频
        //a.b
        AtomicInteger ab = new AtomicInteger();
        //|a|
        AtomicInteger aa = new AtomicInteger();
        //|b|
        AtomicInteger bb = new AtomicInteger();
        //计算
        words
            .stream()
            .forEach(word -> {
                AtomicInteger x1 = frequency1.get(word);
                AtomicInteger x2 = frequency2.get(word);
                if (x1 != null && x2 != null) {
                    //x1x2
                    int oneOfTheDimension = x1.get() * x2.get();
                    //+
                    ab.addAndGet(oneOfTheDimension);
                }
                if (x1 != null) {
                    //(x1)^2
                    int oneOfTheDimension = x1.get() * x1.get();
                    //+
                    aa.addAndGet(oneOfTheDimension);
                }
                if (x2 != null) {
                    //(x2)^2
                    int oneOfTheDimension = x2.get() * x2.get();
                    //+
                    bb.addAndGet(oneOfTheDimension);
                }
            });

        double aaa = Math.sqrt(aa.get());
        double bbb = Math.sqrt(bb.get());
        //使用BigDecimal保证精确计算浮点数
        BigDecimal aabb = BigDecimal.valueOf(aaa).multiply(BigDecimal.valueOf(bbb));
        double cos = ab.get()/aabb.doubleValue();
        return cos;
    }

    public static void main(String[] args) {
        String text1 = "我爱购物";
        String text2 = "我爱读书";
        String text3 = "他是黑客";
        TextSimilarity textSimilarity = new CosineTextSimilarity();
        double score1pk1 = textSimilarity.similarScore(text1, text1);
        double score1pk2 = textSimilarity.similarScore(text1, text2);
        double score1pk3 = textSimilarity.similarScore(text1, text3);
        double score2pk2 = textSimilarity.similarScore(text2, text2);
        double score2pk3 = textSimilarity.similarScore(text2, text3);
        double score3pk3 = textSimilarity.similarScore(text3, text3);
        System.out.println(text1+" 和 "+text1+" 的相似度分值："+score1pk1);
        System.out.println(text1+" 和 "+text2+" 的相似度分值："+score1pk2);
        System.out.println(text1+" 和 "+text3+" 的相似度分值："+score1pk3);
        System.out.println(text2+" 和 "+text2+" 的相似度分值："+score2pk2);
        System.out.println(text2+" 和 "+text3+" 的相似度分值："+score2pk3);
        System.out.println(text3+" 和 "+text3+" 的相似度分值："+score3pk3);
    }
}

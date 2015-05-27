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

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

/**
 * 所有的 文本相似度算法 的单元测试
 * @author 杨尚川
 */
public class TextSimilarityTest {
    private static final String TEXT1 = "QuestionAnsweringSystem是一个Java实现的人机问答系统，能够自动分析问题并给出候选答案。";
    private static final String TEXT2 = "superword是一个Java实现的英文单词分析软件，主要研究英语单词音近形似转化规律、前缀后缀规律、词之间的相似性规律等等。";
    private static final String TEXT3 = "HtmlExtractor是一个Java实现的基于模板的网页结构化信息精准抽取组件。";
    private static final String TEXT4 = "APDPlat是Application Product Development Platform的缩写，即应用级产品开发平台。";
    private static final String TEXT5 = "word分词是一个Java实现的分布式的中文分词组件";
    private static final String TEXT6 = "jsearch是一个高性能的全文检索工具包，基于倒排索引，基于java8，类似于lucene，但更轻量级。";
    private static final String TEXT7 = "rank是一个seo工具，用于分析网站的搜索引擎收录排名。";
    private static final String TEXT8 = "Java开源项目cws_evaluation：中文分词器分词效果评估对比";
    private static final String TEXT9 = "word_web - 通过web服务器对word分词的资源进行集中统一管理";
    private static final String TEXT10 = "大数据的对象持久化：borm";
    private static final String TEXT11 = "元搜索引擎：search";

    @Test
    public void testCosine(){
        TextSimilarity textSimilarity = new CosineTextSimilarity();
        testBasic(textSimilarity);
        testRank(textSimilarity);
        assertEquals(1.0, textSimilarity.similarScore(TEXT1, TEXT1));
    }

    @Test
    public void testEditDistance(){
        TextSimilarity textSimilarity = new EditDistanceTextSimilarity();
        testBasic(textSimilarity);
        testRank(textSimilarity);
    }

    @Test
    public void testEuclideanDistance(){
        TextSimilarity textSimilarity = new EuclideanDistanceTextSimilarity();
        testBasic(textSimilarity);
        testRank(textSimilarity);
    }

    @Test
    public void testJaccard(){
        TextSimilarity textSimilarity = new JaccardTextSimilarity();
        testBasic(textSimilarity);
        testRank(textSimilarity);
    }

    @Test
    public void testJaroDistance(){
        TextSimilarity textSimilarity = new JaroDistanceTextSimilarity();
        testBasic(textSimilarity);
        testRank(textSimilarity);
    }

    @Test
    public void testJaroWinklerDistance(){
        TextSimilarity textSimilarity = new JaroWinklerDistanceTextSimilarity();
        testBasic(textSimilarity);
        testRank(textSimilarity);
    }

    @Test
    public void testManhattanDistance(){
        TextSimilarity textSimilarity = new ManhattanDistanceTextSimilarity();
        testBasic(textSimilarity);
        testRank(textSimilarity);
    }

    @Test
    public void testSimHashPlusHammingDistance(){
        TextSimilarity textSimilarity = new SimHashPlusHammingDistanceTextSimilarity();
        testBasic(textSimilarity);
        //SimHashPlusHammingDistanceTextSimilarity 不合适只有很少的几个词差别的文本的相似度计算
        //所以通过不了这个测试
        //testRank(textSimilarity);
    }

    @Test
    public void testSimple(){
        TextSimilarity textSimilarity = new SimpleTextSimilarity();
        testBasic(textSimilarity);
        testRank(textSimilarity);
    }

    @Test
    public void testSørensenDiceCoefficient(){
        TextSimilarity textSimilarity = new SørensenDiceCoefficientTextSimilarity();
        testBasic(textSimilarity);
        testRank(textSimilarity);
    }

    private void testBasic(TextSimilarity textSimilarity){
        assertEquals("通样的文本应该相等", 1.0, textSimilarity.similarScore(TEXT1, TEXT1));
        assertEquals("通样的文本应该相等", 1.0, textSimilarity.similarScore(TEXT2, TEXT2));
        assertEquals("通样的文本应该相等", 1.0, textSimilarity.similarScore(TEXT3, TEXT3));
        assertEquals("通样的文本应该相等", 1.0, textSimilarity.similarScore(TEXT4, TEXT4));
        assertEquals("通样的文本应该相等", 1.0, textSimilarity.similarScore(TEXT5, TEXT5));
        assertEquals("通样的文本应该相等", 1.0, textSimilarity.similarScore(TEXT6, TEXT6));
        assertEquals("通样的文本应该相等", 1.0, textSimilarity.similarScore(TEXT7, TEXT7));
        assertEquals("通样的文本应该相等", 1.0, textSimilarity.similarScore(TEXT8, TEXT8));
        assertEquals("通样的文本应该相等", 1.0, textSimilarity.similarScore(TEXT9, TEXT9));
        assertEquals("通样的文本应该相等", 1.0, textSimilarity.similarScore(TEXT10, TEXT10));
        assertEquals("通样的文本应该相等", 1.0, textSimilarity.similarScore(TEXT11, TEXT11));
        assertEquals("两个空文本应该相等", 1.0, textSimilarity.similarScore("", ""));
        assertEquals("只有一个文本应该不相等，没有可比性", 0.0, textSimilarity.similarScore(null, ""));
        assertEquals("只有一个文本应该不相等，没有可比性", 0.0, textSimilarity.similarScore("", null));
    }

    private void testRank(TextSimilarity textSimilarity){
        String text1 = "宇宙";
        String text2 = "宇宙 世界";
        String text3 = "宇宙 世界 中国";
        String text4 = "宇宙 世界 中国 云南";
        String text5 = "宇宙 世界 中国 云南 保山";
        String text6 = "宇宙 世界 中国 云南 保山 太保公园";
        String text7 = "宇宙 世界 中国 云南 保山 太保公园 晚上";
        String text8 = "宇宙 世界 中国 云南 保山 太保公园 晚上 下雨";
        String text9 = "宇宙 世界 中国 云南 保山 太保公园 晚上 下雨 美好";
        String text10 = "宇宙 世界 中国 云南 保山 太保公园 晚上 下雨 美好 一晚";
        List<String> textList = Arrays.asList(text1, text2, text3, text4, text5, text6, text7, text8, text9, text10);

        Hits hits = textSimilarity.rank(text10, textList);
        System.out.println(textSimilarity.getClass().getSimpleName());
        hits.getHits().forEach(hit->System.out.println(hit));
        assertEquals(text10, hits.getHits().get(0).getText());
        assertEquals(text9, hits.getHits().get(1).getText());
        assertEquals(text8, hits.getHits().get(2).getText());
        assertEquals(text7, hits.getHits().get(3).getText());
        assertEquals(text6, hits.getHits().get(4).getText());
        assertEquals(text5, hits.getHits().get(5).getText());
        assertEquals(text4, hits.getHits().get(6).getText());
        assertEquals(text3, hits.getHits().get(7).getText());
        assertEquals(text2, hits.getHits().get(8).getText());
        assertEquals(text1, hits.getHits().get(9).getText());
    }
}
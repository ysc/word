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

package org.apdplat.word.elasticsearch;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apdplat.word.lucene.ChineseWordAnalyzer;
import org.elasticsearch.common.inject.Injector;
import org.elasticsearch.common.inject.ModulesBuilder;
import org.elasticsearch.common.settings.SettingsModule;
import org.elasticsearch.env.Environment;
import org.elasticsearch.env.EnvironmentModule;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.IndexNameModule;
import org.elasticsearch.index.settings.IndexSettingsModule;
import org.elasticsearch.indices.analysis.IndicesAnalysisModule;
import org.elasticsearch.indices.analysis.IndicesAnalysisService;
import org.junit.Test;

import static org.elasticsearch.common.settings.ImmutableSettings.Builder.EMPTY_SETTINGS;
import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.index.analysis.AnalysisService;
import org.elasticsearch.index.analysis.TokenizerFactory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * ElasticSearch中文分词索引分析单元测试
 * @author 杨尚川
 */
public class ChineseWordIndicesAnalysisTest {

    @Test
    public void testChineseWordIndicesAnalysis() throws IOException {
        Index index = new Index("test");

        Injector parentInjector = new ModulesBuilder()
                .add(new SettingsModule(EMPTY_SETTINGS), 
                     new EnvironmentModule(new Environment(EMPTY_SETTINGS)), 
                     new IndicesAnalysisModule())
                .createInjector();
        
        Injector injector = new ModulesBuilder().add(
                                new IndexSettingsModule(index, EMPTY_SETTINGS),
                                new IndexNameModule(index),
                                new AnalysisModule(EMPTY_SETTINGS, parentInjector.getInstance(IndicesAnalysisService.class))
                                    .addProcessor(new ChineseWordAnalysisBinderProcessor()))
                            .createChildInjector(parentInjector);

        AnalysisService analysisService = injector.getInstance(AnalysisService.class);

        TokenizerFactory tokenizerFactory = analysisService.tokenizer("word");
        boolean match = (tokenizerFactory instanceof ChineseWordTokenizerFactory);
        assertTrue(match);
        
        Tokenizer tokenizer = tokenizerFactory.create(new StringReader("他说的确实在理"));
        String exp = "[他, 说, 的, 确实, 在理]";
        List<String> result = new ArrayList<>();
        while(tokenizer.incrementToken()){
            CharTermAttribute charTermAttribute = tokenizer.getAttribute(CharTermAttribute.class);
            result.add(charTermAttribute.toString());
        }
        assertEquals(exp, result.toString());

        Analyzer analyzer = analysisService.analyzer("word").analyzer();
        match = (analyzer instanceof ChineseWordAnalyzer);
        assertTrue(match);
        
        TokenStream tokenStream = analyzer.tokenStream("text", "杨尚川是APDPlat应用级产品开发平台的作者");
        exp = "[杨尚川, 是, APDPlat, 应用, 级, 产品, 开发, 平台, 的, 作者]";
        result = new ArrayList<>();
        while(tokenStream.incrementToken()){
            CharTermAttribute charTermAttribute = tokenStream.getAttribute(CharTermAttribute.class);
            result.add(charTermAttribute.toString());
        }
        assertEquals(exp, result.toString());
    }
}
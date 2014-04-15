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

import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.common.component.AbstractComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.analysis.*;
import org.elasticsearch.indices.analysis.IndicesAnalysisService;
import java.io.Reader;
import org.apdplat.word.lucene.ChineseWordAnalyzer;
import org.apdplat.word.lucene.ChineseWordTokenizer;

/**
 * 中文分词索引分析组件
 * @author 杨尚川
 */
public class ChineseWordIndicesAnalysis extends AbstractComponent {
    @Inject
    public ChineseWordIndicesAnalysis(Settings settings, IndicesAnalysisService indicesAnalysisService) {
        super(settings);
        // 注册分析器
        indicesAnalysisService.analyzerProviderFactories()
                .put("word", new PreBuiltAnalyzerProviderFactory("word", AnalyzerScope.INDICES, new ChineseWordAnalyzer()));
        // 注册分词器
        indicesAnalysisService.tokenizerFactories()
                .put("word", new PreBuiltTokenizerFactoryFactory(new TokenizerFactory() {
            @Override
            public String name() {
                return "word";
            }
            @Override
            public Tokenizer create(Reader reader) {
                return new ChineseWordTokenizer(reader);
            }
        }));
    }
}
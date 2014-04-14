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

import org.apdplat.word.lucene.ChineseWordAnalyzer;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;
import org.elasticsearch.index.settings.IndexSettings;

/**
 * 中文分析器工厂
 * @author 杨尚川
 */
public class ChineseWordAnalyzerProvider extends AbstractIndexAnalyzerProvider<ChineseWordAnalyzer> {
    private final ChineseWordAnalyzer analyzer;
    @Inject
    public ChineseWordAnalyzerProvider(Index index, @IndexSettings Settings indexSettings, Environment env, @Assisted String name, @Assisted Settings settings) {
        super(index, indexSettings, name, settings);
        analyzer = new ChineseWordAnalyzer();
    }
    @Override
    public ChineseWordAnalyzer get() {
        return this.analyzer;
    }
}
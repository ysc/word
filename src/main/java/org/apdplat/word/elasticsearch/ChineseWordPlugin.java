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

import org.elasticsearch.common.collect.ImmutableList;
import org.elasticsearch.common.inject.Module;
import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.plugins.AbstractPlugin;
import java.util.Collection;

/**
 * 中文分词组件（word）的ElasticSearch插件
 * @author 杨尚川
 */
public class ChineseWordPlugin extends AbstractPlugin {
    @Override
    public String name() {
        return "word";
    }
    @Override
    public String description() {
        return "中文分词组件（word）";
    }
    @Override
    public Collection<Class<? extends Module>> modules() {
        return ImmutableList.<Class<? extends Module>>of(ChineseWordIndicesAnalysisModule.class);
    }
    public void onModule(AnalysisModule module) {
        module.addProcessor(new ChineseWordAnalysisBinderProcessor());
    }
}
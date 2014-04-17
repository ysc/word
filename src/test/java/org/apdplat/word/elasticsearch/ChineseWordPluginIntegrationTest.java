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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;

/**
 * ElasticSearch中文分词插件集成测试
 * @author 杨尚川
 */
public class ChineseWordPluginIntegrationTest extends ElasticsearchIntegrationTest {
    @Test
    public void testChineseWordAnalyzer() throws ExecutionException, InterruptedException {
        AnalyzeResponse response = client().admin().indices()
                .prepareAnalyze("杨尚川是APDPlat应用级产品开发平台的作者").setAnalyzer("word")
                .execute().get();

        assertThat(response, notNullValue());
        assertThat(response.getTokens().size(), is(7));
        
        String exp = "[杨尚川, apdplat, 应用, 级, 产品开发, 平台, 作者]";
        List<String> result = new ArrayList<>();
        for(AnalyzeResponse.AnalyzeToken token : response.getTokens()){
            result.add(token.getTerm());
        }
        assertThat(result.toString(), equalTo(exp));
    }
    @Test
    public void testChineseWordTokenizer() throws ExecutionException, InterruptedException {
        AnalyzeResponse response = client().admin().indices()
                .prepareAnalyze("他说的确实在理").setTokenizer("word")
                .execute().get();

        assertThat(response, notNullValue());
        assertThat(response.getTokens().size(), is(3));
        
        String exp = "[说, 确实, 在理]";
        List<String> result = new ArrayList<>();
        for(AnalyzeResponse.AnalyzeToken token : response.getTokens()){
            result.add(token.getTerm());
        }
        assertThat(result.toString(), equalTo(exp));
    }
}
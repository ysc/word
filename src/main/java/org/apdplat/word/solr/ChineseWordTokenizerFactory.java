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

package org.apdplat.word.solr;

import java.io.Reader;
import java.util.Map;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeSource;
import org.apdplat.word.lucene.ChineseWordTokenizer;

/**
 * Lucene中文分词器工厂
 * @author 杨尚川
 */
public class ChineseWordTokenizerFactory extends TokenizerFactory {
    public ChineseWordTokenizerFactory(Map<String, String> args){
        super(args);
    }
    @Override
    public Tokenizer create(AttributeSource.AttributeFactory af, Reader reader) {
        return new ChineseWordTokenizer(reader);
    }
}
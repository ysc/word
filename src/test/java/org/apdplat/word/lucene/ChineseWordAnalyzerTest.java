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

package org.apdplat.word.lucene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apdplat.word.util.Utils;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author 杨尚川
 */
public class ChineseWordAnalyzerTest {
    @Test
    public void test1() {
        try{
            Analyzer analyzer = new ChineseWordAnalyzer();
            TokenStream tokenStream = analyzer.tokenStream("text", "杨尚川是APDPlat应用级产品开发平台的作者");
            List<String> words = new ArrayList<>();
            tokenStream.reset();
            while(tokenStream.incrementToken()){
                CharTermAttribute charTermAttribute = tokenStream.getAttribute(CharTermAttribute.class);
                words.add(charTermAttribute.toString());
            }
            tokenStream.close();
            String expResult = "[杨尚川, apdplat, 应用级, 产品, 开发平台, 作者]";
            assertEquals(expResult, words.toString());
        }catch(IOException e){
            fail("分词出错"+e.getMessage());
        }
    }
    @Test
    public void test2() {
        try{
            Analyzer analyzer = new ChineseWordAnalyzer();
            TokenStream tokenStream = analyzer.tokenStream("text", "叔叔亲了我妈妈也亲了我");
            List<String> words = new ArrayList<>();
            tokenStream.reset();
            while(tokenStream.incrementToken()){
                CharTermAttribute charTermAttribute = tokenStream.getAttribute(CharTermAttribute.class);
                words.add(charTermAttribute.toString());
            }
            tokenStream.close();
            String expResult = "[叔叔, 亲了, 妈妈, 亲了]";
            assertEquals(expResult, words.toString());
        }catch(IOException e){
            fail("分词出错"+e.getMessage());
        }
    }
    @Test
    public void test3() {
        Analyzer analyzer = new ChineseWordAnalyzer();
        List<String> sentences = new ArrayList<>();
        sentences.add("杨尚川是APDPlat应用级产品开发平台的作者");
        sentences.add("他说的确实在理");
        sentences.add("提高人民生活水平");
        sentences.add("他俩儿谈恋爱是从头年元月开始的");
        sentences.add("王府饭店的设施和服务是一流的");
        sentences.add("和服务于三日后裁制完毕，并呈送将军府中");
        sentences.add("研究生命的起源");
        sentences.add("他明天起身去北京");
        sentences.add("在这些企业中国有企业有十个");
        sentences.add("他站起身来");
        sentences.add("他们是来查金泰撞人那件事的");
        sentences.add("行侠仗义的查金泰远近闻名");
        sentences.add("长春市长春节致辞");
        sentences.add("他从马上摔下来了,你马上下来一下");
        sentences.add("乒乓球拍卖完了");
        sentences.add("咬死猎人的狗");
        sentences.add("地面积了厚厚的雪");
        sentences.add("这几块地面积还真不小");
        sentences.add("大学生活象白纸");
        sentences.add("结合成分子式");
        sentences.add("有意见分歧");
        sentences.add("发展中国家兔的计划");
        sentences.add("明天他将来北京");
        sentences.add("税收制度将来会更完善");
        sentences.add("依靠群众才能做好工作");
        sentences.add("现在是施展才能的好机会");
        sentences.add("把手举起来");
        sentences.add("茶杯的把手断了");
        sentences.add("以新的姿态出现在世界东方");
        sentences.add("使节约粮食进一步形成风气");
        sentences.add("反映了一个人的精神面貌");
        sentences.add("美国加州大学的科学家发现");
        sentences.add("我好不挺好");
        sentences.add("木有");
        sentences.add("下雨天留客天天留我不留");
        sentences.add("叔叔亲了我妈妈也亲了我");
        sentences.add("白马非马");
        sentences.add("学生会写文章");
        sentences.add("张掖市民陈军");
        sentences.add("张掖市明乐县");
        sentences.add("中华人民共和国万岁万岁万万岁");
        sentences.add("word是一个中文分词项目，作者是杨尚川，杨尚川的英文名叫ysc");
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setUseCompoundFile(false);
        File index = new File("target/indexes");
        Utils.deleteDir(index);
        try (Directory directory = new SimpleFSDirectory(index.toPath());
                IndexWriter indexWriter = new IndexWriter(directory, config)) {
            for(String sentence : sentences){
                Document doc = new Document();
                Field field = new TextField("text", sentence, Field.Store.YES);
                doc.add(field);
                indexWriter.addDocument(doc);
            }
            indexWriter.commit();            
        } catch(Exception e){
            e.printStackTrace();
            fail("索引失败"+e.getMessage());
        }
        try (Directory directory = new SimpleFSDirectory(index.toPath());
                DirectoryReader directoryReader = DirectoryReader.open(directory)) {
                IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
                QueryParser queryParser = new QueryParser("text", analyzer);
                Query query = queryParser.parse("text:杨尚川");
                TopDocs docs = indexSearcher.search(query, Integer.MAX_VALUE);
                assertEquals(2, docs.totalHits);
                assertEquals("word是一个中文分词项目，作者是杨尚川，杨尚川的英文名叫ysc", indexSearcher.doc(docs.scoreDocs[0].doc).get("text"));
                assertEquals("杨尚川是APDPlat应用级产品开发平台的作者", indexSearcher.doc(docs.scoreDocs[1].doc).get("text"));
                
                query = queryParser.parse("text:生命");
                docs = indexSearcher.search(query, Integer.MAX_VALUE);
                assertEquals(1, docs.totalHits);
                assertEquals("研究生命的起源", indexSearcher.doc(docs.scoreDocs[0].doc).get("text"));
        } catch(Exception e){
            fail("搜索失败"+e.getMessage());
        }
    }
}
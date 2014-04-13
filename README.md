Chinese Word Segmentation Component （中文分词组件 - word）



分词使用方法：



	1、对文本进行分词
    List<Word> words = WordSeg.seg("杨尚川是APDPlat应用级产品开发平台的作者");
    System.out.println(words);
    
    输出：
    [杨尚川, 是, APDPlat, 应用, 级, 产品, 开发, 平台, 的, 作者]

	2、对文件进行分词
	String input = "d:/text.txt";
	String output = "d:/word.txt";
	SegFile.segFile(input, output);
	
	
	
Lucene插件：



    Analyzer analyzer = new ChineseWordAnalyzer();
	
	TokenStream tokenStream = analyzer.tokenStream("text", "杨尚川是APDPlat应用级产品开发平台的作者");
	while(tokenStream.incrementToken()){
		CharTermAttribute charTermAttribute = tokenStream.getAttribute(CharTermAttribute.class);
		OffsetAttribute offsetAttribute = tokenStream.getAttribute(OffsetAttribute.class);
		System.out.println(charTermAttribute.toString()+" "+offsetAttribute.startOffset());
	}
	
	Directory directory = new RAMDirectory();
	IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_47, analyzer);
	IndexWriter indexWriter = new IndexWriter(directory, config);
	
	QueryParser queryParser = new QueryParser(Version.LUCENE_47, "text", analyzer);
	Query query = queryParser.parse("text:杨尚川");
	TopDocs docs = indexSearcher.search(query, Integer.MAX_VALUE);
	


Solr插件：

    
	
	将solr-4.7.1/example/solr/collection1/conf/schema.xml文件中所有的
	<tokenizer class="solr.WhitespaceTokenizerFactory"/>和
	<tokenizer class="solr.StandardTokenizerFactory"/>全部替换为
	<tokenizer class="org.apdplat.word.solr.ChineseWordTokenizerFactory"/>
	
	执行 mvn clean install 生成word中文分词组件target/word-1.0.jar
	
	创建目录solr-4.7.1/example/solr/lib，将target/word-1.0.jar文件复制到lib目录


	
词向量：



	从大规模语料中统计一个词的上下文相关词，并用这些上下文相关词组成的向量来表达这个词。
	通过计算词向量的相似性，即可得到词的相似性。
	相似性的假设是建立在如果两个词的上下文相关词越相似，那么这两个词就越相似这个前提下的。
	
	通过运行项目根目录下的脚本demo-word-vector-corpus.bat来体验word项目自带语料库的效果
	
	如果有自己的文本内容，可以使用脚本demo-word-vector-file.bat来对文本分词、建立词向量、计算相似性


	
分词算法文章：


    
   [1、中文分词算法 之 基于词典的正向最大匹配算法](http://yangshangchuan.iteye.com/blog/2031813)
    
   [2、中文分词算法 之 基于词典的逆向最大匹配算法](http://yangshangchuan.iteye.com/blog/2033843)
    
   [3、中文分词算法 之 词典机制性能优化与测试](http://yangshangchuan.iteye.com/blog/2035007)
   
   [4、中文分词算法 之 基于词典的正向最小匹配算法](http://yangshangchuan.iteye.com/blog/2040423)
   
   [5、中文分词算法 之 基于词典的逆向最小匹配算法](http://yangshangchuan.iteye.com/blog/2040431)
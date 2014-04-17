Chinese Word Segmentation Component （中文分词组件 - word）



分词使用方法：



	1、快速体验
	运行项目根目录下的脚本demo-word.bat可以快速体验分词效果
	用法: command [text] [input] [output]
	命令command的可选值为：demo、text、file
	demo
	text 杨尚川是APDPlat应用级产品开发平台的作者
	file d:/text.txt d:/word.txt
	exit
	
	2、对文本进行分词
    List<Word> words = WordSeg.seg("杨尚川是APDPlat应用级产品开发平台的作者");
    System.out.println(words);
    
    输出：
    [杨尚川, 是, APDPlat, 应用, 级, 产品, 开发, 平台, 的, 作者]

	3、对文件进行分词
	String input = "d:/text.txt";
	String output = "d:/word.txt";
	WordSeg.seg(new File(input), new File(output));
	
	4、自定义配置文件
	默认配置文件为类路径下的word.conf，打包在word-x.x.jar中
	自定义配置文件为类路径下的word.local.conf，需要用户自己提供
	如果自定义配置和默认配置相同，自定义配置会覆盖默认配置
	配置文件编码为UTF-8
		
	5、自定义用户词库
	自定义用户词库为一个或多个文件夹或文件，可以使用绝对路径或相对路径
	用户词库由多个词典文件组成，文件编码为UTF-8
	词典文件的格式为文本文件，一行代表一个词
	可以通过系统属性或配置文件的方式来指定路径，多个路径之间用逗号分隔开
	类路径下的词典文件，需要在相对路径前加入前缀classpath:
		
	指定方式有三种：
		指定方式一，编程指定（高优先级）：
			System.setProperty("dic.path", "classpath:dic.txt，d:/custom_dic");
		指定方式二，Java虚拟机启动参数（中优先级）：
			java -Ddic.path=classpath:dic.txt，d:/custom_dic
		指定方式三，配置文件指定（低优先级）：
			使用类路径下的文件word.local.conf来指定配置信息
			dic.path=classpath:dic.txt，d:/custom_dic
 	
	如未指定，则默认使用类路径下的dic.txt词典文件
	
	6、自定义停用词词库
	使用方式和自定义用户词库类似，配置项为：
	stopwords.path=classpath:stopwords.txt，d:/custom_stopwords_dic
		
	7、自动检测词库变化
	可以自动检测自定义用户词库和自定义停用词词库的变化
	包含类路径下的文件和文件夹、非类路径下的绝对路径和相对路径
	如：
	classpath:dic.txt，classpath:custom_dic_dir,
	d:/dic_more.txt，d:/DIC_DIR，D:/DIC2_DIR，my_dic_dir，my_dic_file.txt
	
	classpath:stopwords.txt，classpath:custom_stopwords_dic_dir，
	d:/stopwords_more.txt，d:/STOPWORDS_DIR，d:/STOPWORDS2_DIR，stopwords_dir，remove.txt
	
	
	
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


	
ElasticSearch插件：

    
	
	1、执行命令： mvn clean install dependency:copy-dependencies
	
	2、创建目录elasticsearch-1.1.0/plugins/word
	
	3、将中文分词库文件target/word-1.0.jar和依赖的日志库文件	
		target/dependency/slf4j-api-1.6.4.jar
		target/dependency/logback-core-0.9.28.jar
		target/dependency/logback-classic-0.9.28.jar	
		复制到刚创建的word目录

	4、修改文件elasticsearch-1.1.0/config/elasticsearch.yml，新增如下配置：	
		index.analysis.analyzer.default.type : "word"
	
	5、启动ElasticSearch测试效果，在Chrome浏览器中访问：	
		http://localhost:9200/_analyze?analyzer=word&text=杨尚川是APDPlat应用级产品开发平台的作者

	
		
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
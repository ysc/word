##Java分布式中文分词组件 - word分词



###word分词是一个Java实现的分布式的中文分词组件，提供了多种基于词典的分词算法，并利用ngram模型来消除歧义。能准确识别英文、数字，以及日期、时间等数量词，能识别人名、地名、组织机构名等未登录词。同时提供了Lucene、Solr、ElasticSearch、Luke插件。



###[下载地址](http://pan.baidu.com/s/1dDziDFz)

###[luke集成word分词工具下载](http://pan.baidu.com/s/1bn52ooR)

###[luke集成word分词工具使用说明](http://my.oschina.net/apdplat/blog/397069)
	

###[word 1.0 API](http://apdplat.org/word/apidocs/1.0/)

###[word 1.1 API](http://apdplat.org/word/apidocs/1.1/)
   
   
   
###Maven依赖：



   在pom.xml中指定dependency，可用版本有1.0和1.1：

	<dependencies>
		<dependency>
			<groupId>org.apdplat</groupId>
			<artifactId>word</artifactId>
			<version>1.1</version>
		</dependency>
	</dependencies>

	
	
###分词使用方法：



	1、快速体验
	运行项目根目录下的脚本demo-word.bat可以快速体验分词效果
	用法: command [text] [input] [output]
	命令command的可选值为：demo、text、file
	demo
	text 杨尚川是APDPlat应用级产品开发平台的作者
	file d:/text.txt d:/word.txt
	exit
	
	2、对文本进行分词
	移除停用词：List<Word> words = WordSegmenter.seg("杨尚川是APDPlat应用级产品开发平台的作者");
	保留停用词：List<Word> words = WordSegmenter.segWithStopWords("杨尚川是APDPlat应用级产品开发平台的作者");
				System.out.println(words);
    
    输出：
	移除停用词：[杨尚川, apdplat, 应用级, 产品, 开发平台, 作者]
	保留停用词：[杨尚川, 是, apdplat, 应用级, 产品, 开发平台, 的, 作者]

	3、对文件进行分词
	String input = "d:/text.txt";
	String output = "d:/word.txt";
	移除停用词：WordSegmenter.seg(new File(input), new File(output));
	保留停用词：WordSegmenter.segWithStopWords(new File(input), new File(output));
	
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
			WordConfTools.set("dic.path", "classpath:dic.txt，d:/custom_dic");
			DictionaryFactory.reload();//更改词典路径之后，重新加载词典
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
	
	8、显式指定分词算法
	对文本进行分词时，可显式指定特定的分词算法，如：
	WordSegmenter.seg("APDPlat应用级产品开发平台", SegmentationAlgorithm.BidirectionalMaximumMatching);
	
	SegmentationAlgorithm的可选类型为：	 
	正向最大匹配算法：MaximumMatching
	逆向最大匹配算法：ReverseMaximumMatching
	正向最小匹配算法：MinimumMatching
	逆向最小匹配算法：ReverseMinimumMatching
	双向最大匹配算法：BidirectionalMaximumMatching
	双向最小匹配算法：BidirectionalMinimumMatching
	双向最大最小匹配算法：BidirectionalMaximumMinimumMatching
	全切分算法：FullSegmentation
	
	9、分词效果评估
	运行项目根目录下的脚本evaluation.bat可以对分词效果进行评估
	评估采用的测试文本有253 3709行，共2837 4490个字符
	评估结果位于target/evaluation目录下：
	corpus-text.txt为分好词的人工标注文本，词之间以空格分隔
	test-text.txt为测试文本，是把corpus-text.txt以标点符号分隔为多行的结果
	standard-text.txt为测试文本对应的人工标注文本，作为分词是否正确的标准
	result-text-***.txt，***为各种分词算法名称，这是word分词结果
	perfect-result-***.txt，***为各种分词算法名称，这是分词结果和人工标注标准完全一致的文本
	wrong-result-***.txt，***为各种分词算法名称，这是分词结果和人工标注标准不一致的文本

	10、分布式中文分词器
	1、在自定义配置文件word.conf或word.local.conf中指定所有的配置项*.path使用HTTP资源，同时指定配置项redis.*
	2、配置并启动提供HTTP资源的web服务器，将项目：https://github.com/ysc/word_web部署到tomcat
	3、配置并启动redis服务器
	
	
	
###分词算法效果评估：



	1：word分词 全切分算法：
	分词速度：74.09025 字符/毫秒
	行数完美率：58.79%  行数错误率：41.2%  总的行数：2533709  完美行数：1489713  错误行数：1043996
	字数完美率：49.53% 字数错误率：50.46% 总的字数：28374490 完美字数：14054431 错误字数：14320059
	
	2：word分词 双向最大最小匹配算法：
	分词速度：321.05466 字符/毫秒
	行数完美率：55.31%  行数错误率：44.68%  总的行数：2533709  完美行数：1401582  错误行数：1132127
	字数完美率：45.83% 字数错误率：54.16% 总的字数：28374490 完美字数：13005696 错误字数：15368794
	
	3：word分词 双向最大匹配算法：
	分词速度：505.47778 字符/毫秒
	行数完美率：52.01%  行数错误率：47.98%  总的行数：2533709  完美行数：1317801  错误行数：1215908
	字数完美率：42.42% 字数错误率：57.57% 总的字数：28374490 完美字数：12038414 错误字数：16336076
	
	4：word分词 双向最小匹配算法：
	分词速度：699.2235 字符/毫秒
	行数完美率：46.76%  行数错误率：53.23%  总的行数：2533709  完美行数：1185013  错误行数：1348696
	字数完美率：36.52% 字数错误率：63.47% 总的字数：28374490 完美字数：10365168 错误字数：18009322
	
	5：word分词 逆向最大匹配算法：
	分词速度：1161.7462 字符/毫秒
	行数完美率：46.72%  行数错误率：53.27%  总的行数：2533709  完美行数：1183913  错误行数：1349796
	字数完美率：36.67% 字数错误率：63.32% 总的字数：28374490 完美字数：10407342 错误字数：17967148
	
	6：word分词 正向最大匹配算法：
	分词速度：1212.7405 字符/毫秒
	行数完美率：46.66%  行数错误率：53.33%  总的行数：2533709  完美行数：1182351  错误行数：1351358
	字数完美率：36.73% 字数错误率：63.26% 总的字数：28374490 完美字数：10422209 错误字数：17952281
	
	7：word分词 逆向最小匹配算法：
	分词速度：2134.7043 字符/毫秒
	行数完美率：41.78%  行数错误率：58.21%  总的行数：2533709  完美行数：1058606  错误行数：1475103
	字数完美率：31.68% 字数错误率：68.31% 总的字数：28374490 完美字数：8989797 错误字数：19384693
	
	8：word分词 正向最小匹配算法：
	分词速度：2237.03 字符/毫秒
	行数完美率：36.85%  行数错误率：63.14%  总的行数：2533709  完美行数：933769  错误行数：1599940
	字数完美率：26.85% 字数错误率：73.14% 总的字数：28374490 完美字数：7621334 错误字数：20753156
	

	
###Lucene插件：



	1、构造一个word分析器ChineseWordAnalyzer
    Analyzer analyzer = new ChineseWordAnalyzer();
	
	2、利用word分析器切分文本
	TokenStream tokenStream = analyzer.tokenStream("text", "杨尚川是APDPlat应用级产品开发平台的作者");
	while(tokenStream.incrementToken()){
		CharTermAttribute charTermAttribute = tokenStream.getAttribute(CharTermAttribute.class);
		OffsetAttribute offsetAttribute = tokenStream.getAttribute(OffsetAttribute.class);
		System.out.println(charTermAttribute.toString()+" "+offsetAttribute.startOffset());
	}
	
	3、利用word分析器建立Lucene索引
	Directory directory = new RAMDirectory();
	IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_47, analyzer);
	IndexWriter indexWriter = new IndexWriter(directory, config);
	
	4、利用word分析器查询Lucene索引
	QueryParser queryParser = new QueryParser(Version.LUCENE_47, "text", analyzer);
	Query query = queryParser.parse("text:杨尚川");
	TopDocs docs = indexSearcher.search(query, Integer.MAX_VALUE);
	


###Solr插件：

    
	
	1、下载word-1.1.jar
	下载地址：http://search.maven.org/remotecontent?filepath=org/apdplat/word/1.1/word-1.1.jar
	
	2、创建目录solr-4.7.1/example/solr/lib，将word-1.1.jar复制到lib目录
	
	3、配置schema指定分词器
	将solr-4.7.1/example/solr/collection1/conf/schema.xml文件中所有的
	<tokenizer class="solr.WhitespaceTokenizerFactory"/>和
	<tokenizer class="solr.StandardTokenizerFactory"/>全部替换为
	<tokenizer class="org.apdplat.word.solr.ChineseWordTokenizerFactory"/>
	并移除所有的filter标签
	
	4、如果需要使用特定的分词算法：
	<tokenizer class="org.apdplat.word.solr.ChineseWordTokenizerFactory" segAlgorithm="ReverseMinimumMatching"/>
	segAlgorithm可选值有：	 
	正向最大匹配算法：MaximumMatching
	逆向最大匹配算法：ReverseMaximumMatching
	正向最小匹配算法：MinimumMatching
	逆向最小匹配算法：ReverseMinimumMatching
	双向最大匹配算法：BidirectionalMaximumMatching
	双向最小匹配算法：BidirectionalMinimumMatching
	双向最大最小匹配算法：BidirectionalMaximumMinimumMatching
	全切分算法：FullSegmentation
	如不指定，默认使用双向最大匹配算法：BidirectionalMaximumMatching
	
	5、如果需要指定特定的配置文件：
	<tokenizer class="org.apdplat.word.solr.ChineseWordTokenizerFactory" segAlgorithm="ReverseMinimumMatching"
			conf="C:/solr-4.7.0/example/solr/nutch/conf/word.local.conf"/>
	word.local.conf文件中可配置的内容见 word-1.1.jar 中的word.conf文件
	如不指定，使用默认配置文件，位于 word-1.1.jar 中的word.conf文件

	
	
###ElasticSearch插件：

    
	
	1、打开命令行并切换到elasticsearch的bin目录
		cd elasticsearch-1.2.1/bin
	
	2、运行plugin脚本安装word分词插件：
		plugin -u http://apdplat.org/word/archive/v1.1.zip -i word
	
	3、修改文件elasticsearch-1.2.1/config/elasticsearch.yml，新增如下配置：	
		index.analysis.analyzer.default.type : "word"
		index.analysis.tokenizer.default.type : "word"
	
	4、启动ElasticSearch测试效果，在Chrome浏览器中访问：	
		http://localhost:9200/_analyze?analyzer=word&text=杨尚川是APDPlat应用级产品开发平台的作者
		
	5、自定义配置
		修改配置文件elasticsearch-1.2.1/plugins/word/word.local.conf
		
	6、指定分词算法
		修改文件elasticsearch-1.2.1/config/elasticsearch.yml，新增如下配置：
		index.analysis.analyzer.default.segAlgorithm : "ReverseMinimumMatching"
		index.analysis.tokenizer.default.segAlgorithm : "ReverseMinimumMatching"
		
		这里segAlgorithm可指定的值有：
		正向最大匹配算法：MaximumMatching
		逆向最大匹配算法：ReverseMaximumMatching
		正向最小匹配算法：MinimumMatching
		逆向最小匹配算法：ReverseMinimumMatching
		双向最大匹配算法：BidirectionalMaximumMatching
		双向最小匹配算法：BidirectionalMinimumMatching
		双向最大最小匹配算法：BidirectionalMaximumMinimumMatching
		全切分算法：FullSegmentation
		如不指定，默认使用双向最大匹配算法：BidirectionalMaximumMatching
	


###Luke插件：



	1、下载http://luke.googlecode.com/files/lukeall-4.0.0-ALPHA.jar（国内不能访问）

	2、下载并解压Java中文分词组件word-1.0-bin.zip：http://pan.baidu.com/s/1dDziDFz

	3、将解压后的 Java中文分词组件word-1.0-bin/word-1.0 文件夹里面的4个jar包解压到当前文件夹，用压缩解压工具如winrar打开lukeall-4.0.0-ALPHA.jar，将当前文件夹里面除了.jar、.bat、.html文件外的其他所有文件拖到lukeall-4.0.0-ALPHA.jar里面

	4、执行命令 java -jar lukeall-4.0.0-ALPHA.jar 启动luke，在Search选项卡的Analysis里面就可以选择 org.apdplat.word.lucene.ChineseWordAnalyzer 分词器了

 	5、在Plugins选项卡的Available analyzers found on the current classpath里面也可以选择 org.apdplat.word.lucene.ChineseWordAnalyzer 分词器
	
	
	
###词向量：



	从大规模语料中统计一个词的上下文相关词，并用这些上下文相关词组成的向量来表达这个词。
	通过计算词向量的相似性，即可得到词的相似性。
	相似性的假设是建立在如果两个词的上下文相关词越相似，那么这两个词就越相似这个前提下的。
	
	通过运行项目根目录下的脚本demo-word-vector-corpus.bat来体验word项目自带语料库的效果
	
	如果有自己的文本内容，可以使用脚本demo-word-vector-file.bat来对文本分词、建立词向量、计算相似性
	

	
###分词算法文章：


    
   [1、中文分词算法 之 基于词典的正向最大匹配算法](http://yangshangchuan.iteye.com/blog/2031813)
    
   [2、中文分词算法 之 基于词典的逆向最大匹配算法](http://yangshangchuan.iteye.com/blog/2033843)
    
   [3、中文分词算法 之 词典机制性能优化与测试](http://yangshangchuan.iteye.com/blog/2035007)
   
   [4、中文分词算法 之 基于词典的正向最小匹配算法](http://yangshangchuan.iteye.com/blog/2040423)
   
   [5、中文分词算法 之 基于词典的逆向最小匹配算法](http://yangshangchuan.iteye.com/blog/2040431)
   
   [6、Java开源项目cws_evaluation：中文分词器分词效果评估](https://github.com/ysc/cws_evaluation/)

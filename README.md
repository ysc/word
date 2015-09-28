###Java分布式中文分词组件 - word分词

####word分词是一个Java实现的分布式的中文分词组件，提供了多种基于词典的分词算法，并利用ngram模型来消除歧义。能准确识别英文、数字，以及日期、时间等数量词，能识别人名、地名、组织机构名等未登录词。能通过自定义配置文件来改变组件行为，能自定义用户词库、自动检测词库变化、支持大规模分布式环境，能灵活指定多种分词算法，能使用refine功能灵活控制分词结果，还能使用词频统计、词性标注、同义标注、反义标注、拼音标注等功能。提供了10种分词算法，还提供了10种文本相似度算法，同时还无缝和Lucene、Solr、ElasticSearch、Luke集成。注意：word1.3需要JDK1.8

###API在线文档：

   [word 1.0 API](http://apdplat.org/word/apidocs/1.0/)

   [word 1.1 API](http://apdplat.org/word/apidocs/1.1/)

   [word 1.2 API](http://apdplat.org/word/apidocs/1.2/)

   [word 1.3 API](http://apdplat.org/word/apidocs/1.3/)
      
###[编译好的jar包下载](http://pan.baidu.com/s/1dDziDFz)

###Maven依赖：

   在pom.xml中指定dependency，可用版本有1.0、1.1、1.2、1.3：

	<dependencies>
		<dependency>
			<groupId>org.apdplat</groupId>
			<artifactId>word</artifactId>
			<version>1.3</version>
		</dependency>
	</dependencies>
	
###分词使用方法：

####1、快速体验
	
	运行项目根目录下的脚本demo-word.bat可以快速体验分词效果
	用法: command [text] [input] [output]
	命令command的可选值为：demo、text、file
	demo
	text 杨尚川是APDPlat应用级产品开发平台的作者
	file d:/text.txt d:/word.txt
	exit
	
####2、对文本进行分词

	移除停用词：List<Word> words = WordSegmenter.seg("杨尚川是APDPlat应用级产品开发平台的作者");
	保留停用词：List<Word> words = WordSegmenter.segWithStopWords("杨尚川是APDPlat应用级产品开发平台的作者");
				System.out.println(words);
    
    输出：
	移除停用词：[杨尚川, apdplat, 应用级, 产品, 开发平台, 作者]
	保留停用词：[杨尚川, 是, apdplat, 应用级, 产品, 开发平台, 的, 作者]

####3、对文件进行分词

	String input = "d:/text.txt";
	String output = "d:/word.txt";
	移除停用词：WordSegmenter.seg(new File(input), new File(output));
	保留停用词：WordSegmenter.segWithStopWords(new File(input), new File(output));
	
####4、自定义配置文件

	默认配置文件为类路径下的word.conf，打包在word-x.x.jar中
	自定义配置文件为类路径下的word.local.conf，需要用户自己提供
	如果自定义配置和默认配置相同，自定义配置会覆盖默认配置
	配置文件编码为UTF-8
		
####5、自定义用户词库

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
	
####6、自定义停用词词库

	使用方式和自定义用户词库类似，配置项为：
	stopwords.path=classpath:stopwords.txt，d:/custom_stopwords_dic
		
####7、自动检测词库变化

	可以自动检测自定义用户词库和自定义停用词词库的变化
	包含类路径下的文件和文件夹、非类路径下的绝对路径和相对路径
	如：
	classpath:dic.txt，classpath:custom_dic_dir,
	d:/dic_more.txt，d:/DIC_DIR，D:/DIC2_DIR，my_dic_dir，my_dic_file.txt
	
	classpath:stopwords.txt，classpath:custom_stopwords_dic_dir，
	d:/stopwords_more.txt，d:/STOPWORDS_DIR，d:/STOPWORDS2_DIR，stopwords_dir，remove.txt
	
####8、显式指定分词算法

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
	最少词数算法：MinimalWordCount
	最大Ngram分值算法：MaxNgramScore
	
####9、分词效果评估

	运行项目根目录下的脚本evaluation.bat可以对分词效果进行评估
	评估采用的测试文本有253 3709行，共2837 4490个字符
	评估结果位于target/evaluation目录下：
	corpus-text.txt为分好词的人工标注文本，词之间以空格分隔
	test-text.txt为测试文本，是把corpus-text.txt以标点符号分隔为多行的结果
	standard-text.txt为测试文本对应的人工标注文本，作为分词是否正确的标准
	result-text-***.txt，***为各种分词算法名称，这是word分词结果
	perfect-result-***.txt，***为各种分词算法名称，这是分词结果和人工标注标准完全一致的文本
	wrong-result-***.txt，***为各种分词算法名称，这是分词结果和人工标注标准不一致的文本

####10、分布式中文分词器

	1、在自定义配置文件word.conf或word.local.conf中指定所有的配置项*.path使用HTTP资源，同时指定配置项redis.*
	2、配置并启动提供HTTP资源的web服务器，将项目：https://github.com/ysc/word_web部署到tomcat
	3、配置并启动redis服务器
	
####11、词性标注

	将分词结果作为输入参数，调用PartOfSpeechTagging类的process方法，词性保存在Word类的partOfSpeech字段中
	如下所示：
	List<Word> words = WordSegmenter.segWithStopWords("我爱中国");
	System.out.println("未标注词性："+words);
	//词性标注
	PartOfSpeechTagging.process(words);
	System.out.println("标注词性："+words);
	输出内容：
	未标注词性：[我, 爱, 中国]
    标注词性：[我/r, 爱/v, 中国/ns]
    
####12、refine

    我们看一个切分例子：
    List<Word> words = WordSegmenter.segWithStopWords("我国工人阶级和广大劳动群众要更加紧密地团结在党中央周围");
    System.out.println(words);
    结果如下：
    [我国, 工人阶级, 和, 广大, 劳动群众, 要, 更加, 紧密, 地, 团结, 在, 党中央, 周围]
    假如我们想要的切分结果是：
	[我国, 工人, 阶级, 和, 广大, 劳动, 群众, 要, 更加, 紧密, 地, 团结, 在, 党中央, 周围]
    也就是要把“工人阶级”细分为“工人 阶级”，把“劳动群众”细分为“劳动 群众”，那么我们该怎么办呢？
    我们可以通过在word.refine.path配置项指定的文件classpath:word_refine.txt中增加以下内容：
    工人阶级=工人 阶级
    劳动群众=劳动 群众
	然后，我们对分词结果进行refine：
	words = WordRefiner.refine(words);
	System.out.println(words);
	这样，就能达到我们想要的效果：
	[我国, 工人, 阶级, 和, 广大, 劳动, 群众, 要, 更加, 紧密, 地, 团结, 在, 党中央, 周围]
	
	我们再看一个切分例子：
	List<Word> words = WordSegmenter.segWithStopWords("在实现“两个一百年”奋斗目标的伟大征程上再创新的业绩");
	System.out.println(words);
	结果如下：
	[在, 实现, 两个, 一百年, 奋斗目标, 的, 伟大, 征程, 上, 再创, 新的, 业绩]
	假如我们想要的切分结果是：
	[在, 实现, 两个一百年, 奋斗目标, 的, 伟大征程, 上, 再创, 新的, 业绩]
	也就是要把“两个 一百年”合并为“两个一百年”，把“伟大, 征程”合并为“伟大征程”，那么我们该怎么办呢？
	我们可以通过在word.refine.path配置项指定的文件classpath:word_refine.txt中增加以下内容：
	两个 一百年=两个一百年
	伟大 征程=伟大征程
	然后，我们对分词结果进行refine：
	words = WordRefiner.refine(words);
	System.out.println(words);
	这样，就能达到我们想要的效果：
	[在, 实现, 两个一百年, 奋斗目标, 的, 伟大征程, 上, 再创, 新的, 业绩]
	
####13、同义标注

    List<Word> words = WordSegmenter.segWithStopWords("楚离陌千方百计为无情找回记忆");
    System.out.println(words);
	结果如下：
	[楚离陌, 千方百计, 为, 无情, 找回, 记忆]
	做同义标注：
	SynonymTagging.process(words);
	System.out.println(words);
	结果如下：
	[楚离陌, 千方百计[久有存心, 化尽心血, 想方设法, 费尽心机], 为, 无情, 找回, 记忆[影象]]
	如果启用间接同义词：
	SynonymTagging.process(words, false);
	System.out.println(words);
	结果如下：
	[楚离陌, 千方百计[久有存心, 化尽心血, 想方设法, 费尽心机], 为, 无情, 找回, 记忆[影像, 影象]]
    
    List<Word> words = WordSegmenter.segWithStopWords("手劲大的老人往往更长寿");
	System.out.println(words);
	结果如下：
	[手劲, 大, 的, 老人, 往往, 更, 长寿]
	做同义标注：
	SynonymTagging.process(words);
	System.out.println(words);
	结果如下：
	[手劲, 大, 的, 老人[白叟], 往往[常常, 每每, 经常], 更, 长寿[长命, 龟龄]]
	如果启用间接同义词：
	SynonymTagging.process(words, false);
	System.out.println(words);
	结果如下：
	[手劲, 大, 的, 老人[白叟], 往往[一样平常, 一般, 凡是, 寻常, 常常, 常日, 平凡, 平居, 平常, 平日, 平时, 往常, 日常, 日常平凡, 时常, 普通, 每每, 泛泛, 素日, 经常, 通俗, 通常], 更, 长寿[长命, 龟龄]]

	以词“千方百计”为例：
	可以通过Word的getSynonym()方法获取同义词如：
	System.out.println(word.getSynonym());
	结果如下：
	[久有存心, 化尽心血, 想方设法, 费尽心机]
	注意：如果没有同义词，则getSynonym()返回空集合：Collections.emptyList()
	
	间接同义词和直接同义词的区别如下：
	假设：
	A和B是同义词，A和C是同义词，B和D是同义词，C和E是同义词
	则：
	对于A来说，A B C是直接同义词
	对于B来说，A B D是直接同义词
	对于C来说，A C E是直接同义词
	对于A B C来说，A B C D E是间接同义词
	
####14、反义标注

    List<Word> words = WordSegmenter.segWithStopWords("5月初有哪些电影值得观看");
    System.out.println(words);
	结果如下：
	[5, 月初, 有, 哪些, 电影, 值得, 观看]
	做反义标注：
	AntonymTagging.process(words);
	System.out.println(words);
	结果如下：
	[5, 月初[月底, 月末, 月终], 有, 哪些, 电影, 值得, 观看]
    
    List<Word> words = WordSegmenter.segWithStopWords("由于工作不到位、服务不完善导致顾客在用餐时发生不愉快的事情,餐厅方面应该向顾客作出真诚的道歉,而不是敷衍了事。");
	System.out.println(words);
	结果如下：
	[由于, 工作, 不到位, 服务, 不完善, 导致, 顾客, 在, 用餐, 时, 发生, 不愉快, 的, 事情, 餐厅, 方面, 应该, 向, 顾客, 作出, 真诚, 的, 道歉, 而不是, 敷衍了事]
	做反义标注：
	AntonymTagging.process(words);
	System.out.println(words);
	结果如下：
	[由于, 工作, 不到位, 服务, 不完善, 导致, 顾客, 在, 用餐, 时, 发生, 不愉快, 的, 事情, 餐厅, 方面, 应该, 向, 顾客, 作出, 真诚[糊弄, 虚伪, 虚假, 险诈], 的, 道歉, 而不是, 敷衍了事[一丝不苟, 兢兢业业, 尽心竭力, 竭尽全力, 精益求精, 诚心诚意]]

	以词“月初”为例：
	可以通过Word的getAntonym()方法获取反义词如：
	System.out.println(word.getAntonym());
	结果如下：
	[月底, 月末, 月终]
	注意：如果没有反义词，getAntonym()返回空集合：Collections.emptyList()
	
####15、拼音标注

	List<Word> words = WordSegmenter.segWithStopWords("《速度与激情7》的中国内地票房自4月12日上映以来，在短短两周内突破20亿人民币");
	System.out.println(words);
	结果如下：
	[速度, 与, 激情, 7, 的, 中国, 内地, 票房, 自, 4月, 12日, 上映, 以来, 在, 短短, 两周, 内, 突破, 20亿, 人民币]
	执行拼音标注：
	PinyinTagging.process(words);
	System.out.println(words);
	结果如下：
    [速度 sd sudu, 与 y yu, 激情 jq jiqing, 7, 的 d de, 中国 zg zhongguo, 内地 nd neidi, 票房 pf piaofang, 自 z zi, 4月, 12日, 上映 sy shangying, 以来 yl yilai, 在 z zai, 短短 dd duanduan, 两周 lz liangzhou, 内 n nei, 突破 tp tupo, 20亿, 人民币 rmb renminbi]
	
	以词“速度”为例：
	可以通过Word的getFullPinYin()方法获取完整拼音如：sudu
	可以通过Word的getAcronymPinYin()方法获取首字母缩略拼音如：sd
	
####16、Lucene插件：

	1、构造一个word分析器ChineseWordAnalyzer
    Analyzer analyzer = new ChineseWordAnalyzer();
    如果需要使用特定的分词算法，可通过构造函数来指定：
    Analyzer analyzer = new ChineseWordAnalyzer(SegmentationAlgorithm.FullSegmentation);
	如不指定，默认使用双向最大匹配算法：SegmentationAlgorithm.BidirectionalMaximumMatching
	可用的分词算法参见枚举类：SegmentationAlgorithm
	
	2、利用word分析器切分文本
	TokenStream tokenStream = analyzer.tokenStream("text", "杨尚川是APDPlat应用级产品开发平台的作者");
	//准备消费
	tokenStream.reset();
	//开始消费
	while(tokenStream.incrementToken()){
		//词
		CharTermAttribute charTermAttribute = tokenStream.getAttribute(CharTermAttribute.class);
		//词在文本中的起始位置
		OffsetAttribute offsetAttribute = tokenStream.getAttribute(OffsetAttribute.class);
		//第几个词
		PositionIncrementAttribute positionIncrementAttribute = tokenStream.getAttribute(PositionIncrementAttribute.class);
		//词性
		PartOfSpeechAttribute partOfSpeechAttribute = tokenStream.getAttribute(PartOfSpeechAttribute.class);
		//首字母缩略拼音
		AcronymPinyinAttribute acronymPinyinAttribute = tokenStream.getAttribute(AcronymPinyinAttribute.class);
		//完整拼音
		FullPinyinAttribute fullPinyinAttribute = tokenStream.getAttribute(FullPinyinAttribute.class);
		//同义词
		SynonymAttribute synonymAttribute = tokenStream.getAttribute(SynonymAttribute.class);
		//反义词
		AntonymAttribute antonymAttribute = tokenStream.getAttribute(AntonymAttribute.class);

		LOGGER.info(charTermAttribute.toString()+" ("+offsetAttribute.startOffset()+" - "+offsetAttribute.endOffset()+") "+positionIncrementAttribute.getPositionIncrement());
		LOGGER.info("PartOfSpeech:"+partOfSpeechAttribute.toString());
		LOGGER.info("AcronymPinyin:"+acronymPinyinAttribute.toString());
		LOGGER.info("FullPinyin:"+fullPinyinAttribute.toString());
		LOGGER.info("Synonym:"+synonymAttribute.toString());
		LOGGER.info("Antonym:"+antonymAttribute.toString());
	}
	//消费完毕
	tokenStream.close();
	
	3、利用word分析器建立Lucene索引
	Directory directory = new RAMDirectory();
	IndexWriterConfig config = new IndexWriterConfig(analyzer);
	IndexWriter indexWriter = new IndexWriter(directory, config);
	
	4、利用word分析器查询Lucene索引
	QueryParser queryParser = new QueryParser("text", analyzer);
	Query query = queryParser.parse("text:杨尚川");
	TopDocs docs = indexSearcher.search(query, Integer.MAX_VALUE);

####17、Solr插件：
	
	1、下载word-1.3.jar
	下载地址：http://search.maven.org/remotecontent?filepath=org/apdplat/word/1.3/word-1.3.jar
	
	2、创建目录solr-5.2.0/example/solr/lib，将word-1.3.jar复制到lib目录
	
	3、配置schema指定分词器
	将solr-5.2.0/example/solr/collection1/conf/schema.xml文件中所有的
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
	最少词数算法：MinimalWordCount
	最大Ngram分值算法：MaxNgramScore
	如不指定，默认使用双向最大匹配算法：BidirectionalMaximumMatching
	
	5、如果需要指定特定的配置文件：
	<tokenizer class="org.apdplat.word.solr.ChineseWordTokenizerFactory" segAlgorithm="ReverseMinimumMatching"
			conf="solr-5.2.0/example/solr/nutch/conf/word.local.conf"/>
	word.local.conf文件中可配置的内容见 word-1.3.jar 中的word.conf文件
	如不指定，使用默认配置文件，位于 word-1.3.jar 中的word.conf文件
	
####18、ElasticSearch插件：

	1、打开命令行并切换到elasticsearch的bin目录
	cd elasticsearch-2.0.0-beta2/bin
	
	2、运行plugin脚本安装word分词插件：
	./plugin install http://apdplat.org/word/archive/v1.3.zip
	安装的时候注意：
		如果提示：
			ERROR: failed to download 
		或者 
			ERROR: incorrect hash (SHA1)
		则重新再次运行命令，如果还是不行，多试两次，
		
	3、修改文件elasticsearch-2.0.0-beta2/config/elasticsearch.yml，新增如下配置：	
	index.analysis.analyzer.default.type : "word"
	index.analysis.tokenizer.default.type : "word"
	
	4、启动ElasticSearch测试效果，在Chrome浏览器中访问：	
	http://localhost:9200/_analyze?analyzer=word&text=杨尚川是APDPlat应用级产品开发平台的作者
		
	5、自定义配置
	修改配置文件elasticsearch-2.0.0-beta2/plugins/word/word.local.conf
		
	6、指定分词算法
	修改文件elasticsearch-2.0.0-beta2/config/elasticsearch.yml，新增如下配置：
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
	最少词数算法：MinimalWordCount
	最大Ngram分值算法：MaxNgramScore
	如不指定，默认使用双向最大匹配算法：BidirectionalMaximumMatching
	
####19、Luke插件：

	1、下载http://luke.googlecode.com/files/lukeall-4.0.0-ALPHA.jar（国内不能访问）

	2、下载并解压Java中文分词组件word-1.0-bin.zip：http://pan.baidu.com/s/1dDziDFz

	3、将解压后的 Java中文分词组件word-1.0-bin/word-1.0 文件夹里面的4个jar包解压到当前文件夹
	用压缩解压工具如winrar打开lukeall-4.0.0-ALPHA.jar，将当前文件夹里面除了META-INF文件夹、.jar、
	.bat、.html、word.local.conf文件外的其他所有文件拖到lukeall-4.0.0-ALPHA.jar里面

	4、执行命令 java -jar lukeall-4.0.0-ALPHA.jar 启动luke，在Search选项卡的Analysis里面
	就可以选择 org.apdplat.word.lucene.ChineseWordAnalyzer 分词器了

 	5、在Plugins选项卡的Available analyzers found on the current classpath里面也可以选择 
	org.apdplat.word.lucene.ChineseWordAnalyzer 分词器
	
	注意：如果你要自己集成word分词器的其他版本，在项目根目录下运行mvn install编译项目，然后运行命令
	mvn dependency:copy-dependencies复制依赖的jar包，接着在target/dependency/目录下就会有所有
	的依赖jar包。其中target/dependency/slf4j-api-1.6.4.jar是word分词器使用的日志框架，
	target/dependency/logback-classic-0.9.28.jar和
	target/dependency/logback-core-0.9.28.jar是word分词器推荐使用的日志实现，日志实现的配置文件
	路径位于target/classes/logback.xml，target/word-1.3.jar是word分词器的主jar包，如果需要
	自定义词典，则需要修改分词器配置文件target/classes/word.conf

   已经集成好的Luke插件下载（适用于lucene4.0.0） ：[lukeall-4.0.0-ALPHA-with-word-1.0.jar](http://pan.baidu.com/s/1bn52ooR)
   
   已经集成好的Luke插件下载（适用于lucene4.10.3）：[lukeall-4.10.3-with-word-1.2.jar](http://pan.baidu.com/s/1mgFt7ZU)
	
####20、通过计算词的语境来获得相关词：

我们如何通过计算词的语境来获得相关词呢？

	语境的定义是：在一段文本中，任意一个词的语境由它的前N个词和后N个词组成。
	相关词的定义是：如果两个词的语境越相似，那么这两个词就越相似，也就越相关。



算法由两个步骤组成：

	1、从大规模语料库中计算每一个词的语境，并使用词向量来表示语境。
	2、把求两个词的相似度的问题转换为求这两个词的语境的相似度的问题。
	通过计算语境的相似度，就可得到词的相似度，越相似的词就越相关。



使用方法如下：

	1、使用word分词内置语料库：运行word分词项目根目录下的脚本 
	demo-word-vector-corpus.bat 或 demo-word-vector-corpus.sh
	2、使用自己的文本内容：运行word分词项目根目录下的脚本 
	demo-word-vector-file.bat 或 demo-word-vector-file.sh

	由于语料库很大，所以启动的时间会很长，请耐心等待，下面以例子来说明：
	比如我们想分析 兰州 这个词的相关词有哪些，我们运行脚本 
	demo-word-vector-corpus.sh ，启动成功之后命令行提示：
	
	开始初始化模型
    模型初始化完成
    可通过输入命令sa=cos来指定相似度算法，可用的算法有：
       1、sa=cos，余弦相似度
       2、sa=edi，编辑距离
       3、sa=euc，欧几里得距离
       4、sa=sim，简单共有词
       5、sa=jac，Jaccard相似性系数
       6、sa=man，曼哈顿距离
       7、sa=shh，SimHash + 汉明距离
       8、sa=ja，Jaro距离
       9、sa=jaw，Jaro–Winkler距离
       10、sa=sd，Sørensen–Dice系数
    可通过输入命令limit=15来指定显示结果条数
    可通过输入命令exit退出程序
    输入要查询的词或命令：
	
	我们输入 兰州 后回车，结果显示：
	
	兰州 的相关词（EditDistanceTextSimilarity）：
    ----------------------------------------------------------
    	1、兰州 1.0
    	2、北京 0.21
    	3、福州 0.2
    	4、太原 0.19
    	5、成都 0.17
    	6、西安 0.17
    	7、哈尔滨 0.17
    	8、南宁 0.17
    	9、贵阳 0.16
    	10、庆阳 0.15
    	11、沈阳 0.14
    	12、合肥 0.14
    	13、大同 0.14
    	14、拉萨 0.13
    	15、西宁 0.13
    ----------------------------------------------------------
	这里显示的结果就是 兰州 这个词的相关词，词后面跟的是相关度分值，
	兰州 和 兰州 是同一个词，相关度百分之百，自然是1分。
	
	从这个结果我们来分析，这些词凭什么相关呢？线索在哪里？
	
	首先这些词的词性都是名词；
	其次这些词都是地名而且是大城市名；
	从这里我们也可以看到一个有意思的现象，同一词性比如地名的用法往往保持一致。
	
	相关词是从语境推导得到的，语境中词后面跟的数字是权重，权重是1/N的累加值
	下面我们看看这些词的语境：

	兰州 : [军区 1.0, 甘肃 0.78205127, 新区 0.7692308, 大学 0.42307693, 甘肃兰州 0.41025642, 货车 0.3846154, 西安 0.32051283, 本报 0.2948718, 新华社 0.2820513, 兰州新区 0.26923078, 召开 0.23076923, 发往 0.21794872, 中国 0.20512821, 兰州 0.20512821, 火车站 0.20512821, 铁路 0.17948718, 参加 0.15384616, 西宁 0.15384616, 方向 0.15384616, 成都 0.14102565, 警察 0.14102565, 建设 0.12820514, 市委 0.12820514, 来到 0.12820514, 一家 0.12820514, 中心 0.115384616, 炼油厂 0.102564104, 进入 0.102564104, 来自 0.102564104, 举行 0.102564104]	
	北京 : [新华社 1.0, 本报 0.7119143, 举行 0.19384204, 上海 0.17831326, 时间 0.16385542, 铁路局 0.1394913, 西站 0.13226238, 青年报 0.12717536, 晨报 0.11700134, 市委 0.1145917, 地区 0.11218206, 召开 0.10200803, 城市 0.08299866, 目前 0.07951807, 来到 0.06961178, 军区 0.06827309, 国际 0.066398926, 中心 0.063453816, 北京时间 0.06184739, 人民 0.059973225, 工作 0.05863454, 地铁 0.057563588, 北京铁路局 0.056492638, 医院 0.055421688, 飞往 0.05381526, 首都 0.053547524, 中国 0.053547524, 其中 0.05274431, 今天 0.052208837, 卫视 0.05167336]
	福州 : [火车站 1.0, 新区 0.46666667, 福州火车站 0.45555556, 晚报 0.2962963, 记者 0.2777778, 打工 0.27407408, 来到 0.24814814, 市民 0.23333333, 本报 0.22222222, 大学 0.21851853, 市区 0.2074074, 市委 0.19259259, 举行 0.19259259, 鼓楼区 0.18518518, 网友 0.18148148, 到达 0.17037037, 开往 0.16296296, 目前 0.14074074, 分行 0.14074074, 一家 0.12962963, 全市 0.12962963, 东街口 0.12222222, 福州晚报 0.12222222, 新华社 0.11851852, 铁路 0.11851852, 召开 0.11481482, 前往 0.11481482, 发展 0.11481482, 推进 0.11111111, 福州 0.11111111]	 
    太原 : [山西 1.0, 山西太原 0.6136364, 本报 0.39772728, 新华社 0.3409091, 火车站 0.26136363, 济南 0.25, 铁路 0.23863636, 北京 0.22727273, 推出 0.1590909, 国际 0.1590909, 返回 0.14772727, 刚玉 0.13636364, 来自 0.13636364, 发布 0.13636364, 打工 0.125, 中心 0.125, 市委 0.11363637, 银行 0.11363637, 铁路局 0.10227273, 西安 0.09090909, 集团 0.09090909, 公安 0.09090909, 开往 0.09090909, 比如 0.07954545, 金融 0.07954545, 火车票 0.07954545, 大同 0.06818182, 山西省 0.06818182, 军分区 0.06818182, 离开 0.06818182]
    成都 : [商报 1.0, 成都商报 0.4117647, 军区 0.1875, 铁路局 0.17830883, 北京 0.17463236, 本报 0.17095588, 重庆 0.15441176, 告诉 0.15441176, 交警 0.14338236, 方向 0.1360294, 记者 0.13419117, 平原 0.121323526, 四川 0.1194853, 长沙 0.11764706, 理工大学 0.0992647, 来自 0.09375, 新华社 0.09191176, 开往 0.090073526, 成都铁路局 0.08455882, 铁路 0.080882356, 召开 0.07904412, 市民 0.075367644, 市委 0.073529415, 公司 0.07169118, 广州 0.07169118, 西安 0.0680147, 郫县 0.060661763, 打工 0.060661763, 市区 0.05882353, 晚报 0.05882353]
    西安 : [火车站 1.0, 事变 0.75, 交通 0.7058824, 建设 0.5882353, 地铁 0.5882353, >咸阳 0.5588235, 来到 0.5294118, 市民 0.50735295, 大学 0.5, 铁路 0.5, 代表团 0.5, 铁路局 0.49264705, 公司 0.4852941, 武汉 0.4632353, 曲江 0.44117647, 供电 0.42647058, 新华社 0.4117647, 西安火车站 0.4117647, 北京 0.3602941, 交大 0.3602941, 本报 0.34558824, 西安事变 0.3382353, 城市 0.31617647, 城区 0.31617647, 落户 0.30882353, 市委 0.29411766, 国际 0.2867647, 城东 0.2867647, 成都 0.2720588, 举行 0.25]	
    哈尔滨 : [理工大学 1.0, 火车站 0.41584158, 哈尔滨理工大学 0.36138615, 工业 0.25742576, 方向 0.23762377, 新华社 0.20792079, 开往 0.18811882, 哈尔滨火车站 0.18316832, 位于 0.17821783, 大学 0.17326732, 铁路局 0.15841584, 来自 0.15346535, 最低 0.14356436, 北京 0.12871288, 本报 0.12376238, 黑龙江省 0.12376238, 发布 0.11386139, 中国 0.10891089, 飞往 0.0990099, 黑龙>江 0.08415841, 沈阳 0.07920792, 工程 0.07920792, 附近 0.074257426, 市委 0.06930693, 飞机 0.06930693, 上海 0.06930693, 考生 0.06930693, 进入 0.06930693, 停止 0.06930693, 经济 0.06435644]
    南宁 : [广西 1.0, 铁路局 0.8, 广西南宁 0.62222224, 本报 0.54444444, 新华社 0.36666667, 南宁铁路局 0.31111112, 市委 0.26666668, 柳州 0.18888889, 桂林 0.17777778, 铁路 0.15555556, 兴>宁区 0.14444445, 来到 0.11111111, 开往 0.11111111, 前往 0.11111111, 公安 0.11111111, 工作 0.11111111, 运往 0.11111111, 城市 0.08888889, 美丽 0.08888889, 召开 0.08888889, 从事 0.08888889, 官塘 0.08888889, 楼市 0.08888889, 分局 0.07777778, 南宁市委 0.07777778, 动车 0.07777778, 发生 0.07777778, 举行 0.07777778, 西乡 0.06666667, 市长 0.06666667]
    贵阳 : [本报 1.0, 重庆 0.73333335, 新华社 0.46666667, 方向 0.43333334, 前往 0.4, 哥俩 0.4, 城区 0.4, 老家 0.33333334, 西安 0.26666668, 成都 0.26666668, 街头 0.26666668, 晚报 0.26666668, 无关 0.26666668, 杭州 0.23333333, 涉及 0.2, 以及 0.2, 市内 0.2, 网友 0.2, 郑州 0.16666667, 南宁 0.16666667, 长沙 0.16666667, 武汉 0.16666667, 摆摊 0.16666667, 市委 0.13333334, 昆明 0.13333334, 安顺 0.13333334, 来到 0.13333334, 争霸 0.13333334, 四强 0.13333334, 铁路 0.13333334]
    庆阳 : [甘肃 1.0, 甘肃庆阳 0.8, 甘肃省 0.4, 地区 0.4, 老区 0.3, 森林 0.2, 平凉 0.2, 镇远县 0.1, 革命 0.1, 韩凤廷 0.1, 交通处 0.1, 兰州森林大队 0.1, 大队 0.1, 兰州 0.1, 西峰 0.1, 发>送 0.1, 一辆 0.1, 牌照 0.1, 来自 0.1]
    沈阳 : [军区 1.0, 晚报 0.5123967, 方向 0.3181818, 本报 0.27272728, 沈阳晚报 0.23553719, 新华社 0.20661157, 沈阳军区 0.18595041, 军区队 0.15289256, 海狮队 0.14876033, 自动化所 0.14049587, 此次 0.14049587, 经济区 0.1322314, 中国 0.12809917, >大连 0.12809917, 大爷 0.12809917, 市委 0.12396694, 一家 0.11570248, 高速 0.11570248, 国际 0.11157025, 火车票 0.11157025, 法库 0.10743801, 大学 0.10330579, 长春 0.10330579, 直达 0.09917355, 深圳 0.09090909, 上海 0.08677686, 记者 0.08677686, 海狮 0.08264463, 大妈 0.08264463, 两位 0.08264463]	
	合肥 : [火车站 1.0, 市民 0.8181818, 市区 0.53333336, 楼市 0.4848485, 合肥火车站 0.4121212, 铁路 0.38787878, 安徽 0.36969697, 到达 0.36363637, 市场 0.34545454, 上周 0.3030303, 芜湖 0.2969697, 召开 0.28484848, 记者 0.27272728, 成为 0.27272728, 来到 0.26666668, 安徽合肥 0.24242425, 城市 0.24242425, 经济圈 0.24242425, 公交 0.24242425, 目前 0.23636363, 本报 0.21818182, 今年 0.21818182, 起飞 0.21818182, 汽车 0.21212122, 物质 0.2060606, 合肥楼市 0.2060606, 空港 0.2060606, 工业 0.19393939, 标题 0.18181819, 野生 0.16969697]
	大同 : [大学 1.0, 铁路 0.52380955, 山西 0.5, 证券 0.33333334, 大同大学 0.33333334, 山西省 0.23809524, 此次 0.23809524, 山西大同 0.1904762, 世界 0.1904762, 世界大同 0.1904762, 街道 0.16666667, 太原 0.14285715, 市委 0.14285715, 上海 0.14285715, 派出所 0.14285715, 公安处 0.14285715, 日方 0.14285715, 转发 0.14285715, 运城 0.11904762, 军分区 0.0952381, 矿务局 0.0952381, 小学 0.0952381, 参加 0.0952381, 项目 0.0952381, 中学 0.0952381, 水厂 0.0952381, 车辆段 0.0952381, 开往 0.0952381, 大同证券 0.0952381, 战役 0.071428575]
	拉萨 : [火车站 1.0, 新华社 0.91935486, 西藏 0.7580645, 市区 0.61290324, 本报 0.58064514, 召开 0.5645161, 海关 0.5483871, 城市 0.48387095, 拉萨火车站 0.4032258, 市委 0.38709676, 成都 0.37096775, 贡嘎 0.3548387, 开幕 0.32258064, 发布 0.30645162, 西藏拉萨 0.2580645, 会议 0.2580645, 机场 0.22580644, 闭幕 0.22580644, 隆重 0.22580644, 林芝 0.20967741, 举行 0.19354838, 开通 0.19354838, 营业部 0.19354838, 市民 0.17741935, 市场 0.17741935, 经济 0.17741935, 中心 0.17741935, 空气 0.17741935, 成为 0.17741935, 人民 0.16129032]
	西宁 : [新华社 1.0, 上海 0.8235294, 兰州 0.3529412, 辗转 0.3529412, 本报 0.29411766, 青海 0.29411766, 考察 0.23529412, 当街 0.23529412, 特钢 0.1764706, 方向 0.1764706, 分行 0.1764706, 索贿 0.1764706, 北京 0.14705883, 但是 0.14705883, 拉萨 0.11764706, 我们 0.11764706, 标题 0.11764706, 交警 0.11764706, 代表团 0.11764706, 处理 0.0882353, 银川 0.0882353, 车票 0.0882353, 筹建 0.0882353, 中转 0.0882353, 参加 0.0882353, 一月 0.05882353, 试验局 0.05882353, 二月 0.05882353, 地区 0.05882353, 严肃 0.05882353]	
	
	最后我们看一下分别使用7种相似度算法算出来的 兰州 的相关词：
	
	----------------------------------------------------------
    兰州 的相关词（CosineTextSimilarity）：
    	1、兰州 1.0
    	2、沈阳 0.5
    	3、北京军区 0.47
    	4、后勤部 0.46
    	5、沈阳军区 0.46
    	6、总医院 0.46
    	7、新疆军区 0.46
    	8、司令员 0.42
    	9、甘肃兰州 0.42
    	10、兰州新区 0.42
    	11、某师 0.39
    	12、郑蒲港 0.38
    	13、西咸 0.38
    	14、天水 0.37
    	15、郑东 0.37
    耗时：25秒,572毫秒
    ----------------------------------------------------------
    兰州 的相关词（EditDistanceTextSimilarity）：
    	1、兰州 1.0
    	2、北京 0.21
    	3、福州 0.2
    	4、太原 0.19
    	5、成都 0.17
    	6、南宁 0.17
    	7、西安 0.17
    	8、哈尔滨 0.17
    	9、贵阳 0.16
    	10、庆阳 0.15
    	11、合肥 0.14
    	12、大同 0.14
    	13、沈阳 0.14
    	14、珀斯 0.13
    	15、拉萨 0.13
    耗时：44秒,253毫秒
    ----------------------------------------------------------
    兰州 的相关词（EuclideanDistanceTextSimilarity）：
    	1、兰州 1.0
    	2、后勤部 0.37
    	3、北京军区 0.37
    	4、新疆军区 0.37
    	5、沈阳 0.37
    	6、沈阳军区 0.37
    	7、总医院 0.37
    	8、上海浦东新区 0.36
    	9、郑蒲港 0.36
    	10、浦东新区 0.36
    	11、甘肃兰州 0.36
    	12、西咸 0.36
    	13、西咸新区 0.36
    	14、正定新区 0.36
    	15、司令员 0.36
    耗时：24秒,710毫秒
    ----------------------------------------------------------
    兰州 的相关词（SimpleTextSimilarity）：
    	1、兰州 1.0
    	2、福州 0.36
    	3、西安 0.33
    	4、李红旗 0.33
    	5、中国金融信息中心 0.33
    	6、南特 0.32
    	7、卡塔赫纳 0.32
    	8、哈尔滨 0.3
    	9、武汉 0.3
    	10、戴克瑞 0.3
    	11、楚雄州 0.29
    	12、朱梦魁 0.29
    	13、岳菲菲 0.29
    	14、长沙 0.28
    	15、吕国庆 0.28
    耗时：21秒,918毫秒
    ----------------------------------------------------------
    兰州 的相关词（JaccardTextSimilarity）：
    	1、兰州 1.0
    	2、福州 0.22
    	3、西安 0.2
    	4、哈尔滨 0.18
    	5、北京 0.18
    	6、武汉 0.18
    	7、成都 0.18
    	8、长沙 0.15
    	9、太原 0.15
    	10、贵阳 0.15
    	11、沈阳 0.15
    	12、广州 0.15
    	13、拉萨 0.15
    	14、南昌 0.15
    	15、长春 0.13
    耗时：19秒,717毫秒
    ----------------------------------------------------------
    兰州 的相关词（ManhattanDistanceTextSimilarity）：
    	1、兰州 1.0
    	2、上海浦东新区 0.11
    	3、陕西西咸新区 0.11
    	4、甘肃兰州 0.11
    	5、北京军区 0.11
    	6、新疆军区 0.11
    	7、西咸 0.11
    	8、正定新区 0.11
    	9、天府新区 0.11
    	10、沈阳军区 0.11
    	11、国家级新区 0.11
    	12、兰州新区 0.11
    	13、侠客 0.1
    	14、威胁论 0.1
    	15、一两个月 0.1
    耗时：23秒,857毫秒
    ----------------------------------------------------------
    兰州 的相关词（SimHashPlusHammingDistanceTextSimilarity）：
    	1、兰州 1.0
    	2、鱼水 0.96
    	3、冯导 0.95
    	4、新闻稿 0.95
    	5、科学 0.95
    	6、物业公司 0.95
    	7、现役军人 0.95
    	8、何人 0.95
    	9、张轸 0.94
    	10、公告 0.94
    	11、信息发布 0.94
    	12、倡议 0.94
    	13、药液 0.94
    	14、考古发掘 0.94
    	15、公开发布 0.94
    耗时：5分钟,57秒,339毫秒
    ----------------------------------------------------------
    兰州 的相关词（JaroDistanceTextSimilarity）：
    	1、兰州 1.0
    	2、长沙 0.49
    	3、哈尔滨 0.49
    	4、福州 0.48
    	5、太原 0.47
    	6、庆阳 0.46
    	7、济南 0.46
    	8、北京 0.45
    	9、成都 0.45
    	10、张家明 0.45
    	11、西安 0.45
    	12、孙勇 0.45
    	13、楚雄州 0.44
    	14、福州站 0.44
    	15、南宁 0.44
    耗时：12秒,718毫秒
    ----------------------------------------------------------
    兰州 的相关词（JaroWinklerDistanceTextSimilarity）：
    	1、兰州 1.0
    	2、拉萨 0.56
    	3、南宁 0.55
    	4、朝廷 0.55
    	5、公判 0.54
    	6、萨蒙德 0.53
    	7、世界级 0.53
    	8、滨湖 0.53
    	9、大大小小 0.52
    	10、大选 0.52
    	11、七届 0.52
    	12、烘焙 0.51
    	13、武平县 0.51
    	14、莫斯科 0.51
    	15、复训 0.51
    耗时：16秒,723毫秒
    ----------------------------------------------------------
    兰州 的相关词（SørensenDiceCoefficientTextSimilarity）：
    	1、兰州 1.0
    	2、福州 0.37
    	3、西安 0.33
    	4、哈尔滨 0.3
    	5、北京 0.3
    	6、武汉 0.3
    	7、成都 0.3
    	8、长沙 0.27
    	9、太原 0.27
    	10、贵阳 0.27
    	11、沈阳 0.27
    	12、广州 0.27
    	13、拉萨 0.27
    	14、南昌 0.27
    	15、长春 0.23
    耗时：19秒,852毫秒
    ----------------------------------------------------------
  
####21、词频统计：

org.apdplat.word.WordFrequencyStatistics 提供了词频统计的功能
	
命令行脚本的调用方法如下：

	将需要统计词频的文本写入文件：text.txt
	chmod +x wfs.sh & wfs.sh -textFile=text.txt -statisticsResultFile=statistics-result.txt
	程序运行结束后打开文件statistics-result.txt查看词频统计结果

在程序中的调用方法如下：

	//词频统计设置
	WordFrequencyStatistics wordFrequencyStatistics = new WordFrequencyStatistics();
	wordFrequencyStatistics.setRemoveStopWord(false);
	wordFrequencyStatistics.setResultPath("word-frequency-statistics.txt");
	wordFrequencyStatistics.setSegmentationAlgorithm(SegmentationAlgorithm.MaxNgramScore);
	//开始分词
	wordFrequencyStatistics.seg("明天下雨，结合成分子，明天有关于分子和原子的课程，下雨了也要去听课");
	//输出词频统计结果
	wordFrequencyStatistics.dump();
	//准备文件
	Files.write(Paths.get("text-to-seg.txt"), Arrays.asList("word分词是一个Java实现的分布式中文分词组件，提供了多种基于词典的分词算法，并利用ngram模型来消除歧义。"));
	//清除之前的统计结果
	wordFrequencyStatistics.reset();
	//对文件进行分词
	wordFrequencyStatistics.seg(new File("text-to-seg.txt"), new File("text-seg-result.txt"));
	//输出词频统计结果
	wordFrequencyStatistics.dump("file-seg-statistics-result.txt");

第一句话的词频统计结果：

	1、下雨 2
	2、明天 2
	3、分子 2
	4、课程 1
	5、听课 1
	6、结合 1
	7、原子 1
	8、去 1
	9、成 1
	10、关于 1
	11、和 1
	12、也要 1
	13、有 1
	14、的 1
	15、了 1
	
第二句话的词频统计结果：

	1、分词 2
	2、的 2
	3、基于 1
	4、word 1
	5、组件 1
	6、词典 1
	7、ngram 1
	8、多种 1
	9、实现 1
	10、并 1
	11、利用 1
	12、消除歧义 1
	13、中文分词 1
	14、算法 1
	15、是 1
	16、分布式 1
	17、了 1
	18、提供 1
	19、模型 1
	20、来 1
	21、一个 1
	22、Java 1	
	
####22、文本相似度：

word分词提供了多种文本相似度计算方式：

方式一：余弦相似度，通过计算两个向量的夹角余弦值来评估他们的相似度

实现类：org.apdplat.word.analysis.CosineTextSimilarity
	
用法如下：
	
	String text1 = "我爱购物";
	String text2 = "我爱读书";
	String text3 = "他是黑客";
	TextSimilarity textSimilarity = new CosineTextSimilarity();
	double score1pk1 = textSimilarity.similarScore(text1, text1);
	double score1pk2 = textSimilarity.similarScore(text1, text2);
	double score1pk3 = textSimilarity.similarScore(text1, text3);
	double score2pk2 = textSimilarity.similarScore(text2, text2);
	double score2pk3 = textSimilarity.similarScore(text2, text3);
	double score3pk3 = textSimilarity.similarScore(text3, text3);
	System.out.println(text1+" 和 "+text1+" 的相似度分值："+score1pk1);
	System.out.println(text1+" 和 "+text2+" 的相似度分值："+score1pk2);
	System.out.println(text1+" 和 "+text3+" 的相似度分值："+score1pk3);
	System.out.println(text2+" 和 "+text2+" 的相似度分值："+score2pk2);
	System.out.println(text2+" 和 "+text3+" 的相似度分值："+score2pk3);
	System.out.println(text3+" 和 "+text3+" 的相似度分值："+score3pk3);
	
运行结果如下：
	
	我爱购物 和 我爱购物 的相似度分值：1.0
	我爱购物 和 我爱读书 的相似度分值：0.67
	我爱购物 和 他是黑客 的相似度分值：0.0
	我爱读书 和 我爱读书 的相似度分值：1.0
	我爱读书 和 他是黑客 的相似度分值：0.0
	他是黑客 和 他是黑客 的相似度分值：1.0

方式二：简单共有词，通过计算两篇文档共有的词的总字符数除以最长文档字符数来评估他们的相似度

实现类：org.apdplat.word.analysis.SimpleTextSimilarity
	
用法如下：

	String text1 = "我爱购物";
	String text2 = "我爱读书";
	String text3 = "他是黑客";
	TextSimilarity textSimilarity = new SimpleTextSimilarity();
	double score1pk1 = textSimilarity.similarScore(text1, text1);
	double score1pk2 = textSimilarity.similarScore(text1, text2);
	double score1pk3 = textSimilarity.similarScore(text1, text3);
	double score2pk2 = textSimilarity.similarScore(text2, text2);
	double score2pk3 = textSimilarity.similarScore(text2, text3);
	double score3pk3 = textSimilarity.similarScore(text3, text3);
	System.out.println(text1+" 和 "+text1+" 的相似度分值："+score1pk1);
	System.out.println(text1+" 和 "+text2+" 的相似度分值："+score1pk2);
	System.out.println(text1+" 和 "+text3+" 的相似度分值："+score1pk3);
	System.out.println(text2+" 和 "+text2+" 的相似度分值："+score2pk2);
	System.out.println(text2+" 和 "+text3+" 的相似度分值："+score2pk3);
	System.out.println(text3+" 和 "+text3+" 的相似度分值："+score3pk3);
	
运行结果如下：

	我爱购物 和 我爱购物 的相似度分值：1.0
	我爱购物 和 我爱读书 的相似度分值：0.5
	我爱购物 和 他是黑客 的相似度分值：0.0
	我爱读书 和 我爱读书 的相似度分值：1.0
	我爱读书 和 他是黑客 的相似度分值：0.0
	他是黑客 和 他是黑客 的相似度分值：1.0
	
方式三：编辑距离，通过计算两个字串之间由一个转成另一个所需的最少编辑操作次数来评估他们的相似度

实现类：org.apdplat.word.analysis.EditDistanceTextSimilarity
	
用法如下：

	String text1 = "我爱购物";
	String text2 = "我爱读书";
	String text3 = "他是黑客";
	TextSimilarity textSimilarity = new EditDistanceTextSimilarity();
	double score1pk1 = textSimilarity.similarScore(text1, text1);
	double score1pk2 = textSimilarity.similarScore(text1, text2);
	double score1pk3 = textSimilarity.similarScore(text1, text3);
	double score2pk2 = textSimilarity.similarScore(text2, text2);
	double score2pk3 = textSimilarity.similarScore(text2, text3);
	double score3pk3 = textSimilarity.similarScore(text3, text3);
	System.out.println(text1+" 和 "+text1+" 的相似度分值："+score1pk1);
	System.out.println(text1+" 和 "+text2+" 的相似度分值："+score1pk2);
	System.out.println(text1+" 和 "+text3+" 的相似度分值："+score1pk3);
	System.out.println(text2+" 和 "+text2+" 的相似度分值："+score2pk2);
	System.out.println(text2+" 和 "+text3+" 的相似度分值："+score2pk3);
	System.out.println(text3+" 和 "+text3+" 的相似度分值："+score3pk3);
	
运行结果如下：

	我爱购物 和 我爱购物 的相似度分值：1.0
	我爱购物 和 我爱读书 的相似度分值：0.5
	我爱购物 和 他是黑客 的相似度分值：0.0
	我爱读书 和 我爱读书 的相似度分值：1.0
	我爱读书 和 他是黑客 的相似度分值：0.0
	他是黑客 和 他是黑客 的相似度分值：1.0
	
方式四：SimHash + 汉明距离，先使用SimHash把不同长度的文本映射为等长文本，然后再计算等长文本的汉明距离

实现类：org.apdplat.word.analysis.SimHashPlusHammingDistanceTextSimilarity
	
用法如下：

	String text1 = "我爱购物";
	String text2 = "我爱读书";
	String text3 = "他是黑客";
	TextSimilarity textSimilarity = new SimHashPlusHammingDistanceTextSimilarity();
	double score1pk1 = textSimilarity.similarScore(text1, text1);
	double score1pk2 = textSimilarity.similarScore(text1, text2);
	double score1pk3 = textSimilarity.similarScore(text1, text3);
	double score2pk2 = textSimilarity.similarScore(text2, text2);
	double score2pk3 = textSimilarity.similarScore(text2, text3);
	double score3pk3 = textSimilarity.similarScore(text3, text3);
	System.out.println(text1+" 和 "+text1+" 的相似度分值："+score1pk1);
	System.out.println(text1+" 和 "+text2+" 的相似度分值："+score1pk2);
	System.out.println(text1+" 和 "+text3+" 的相似度分值："+score1pk3);
	System.out.println(text2+" 和 "+text2+" 的相似度分值："+score2pk2);
	System.out.println(text2+" 和 "+text3+" 的相似度分值："+score2pk3);
	System.out.println(text3+" 和 "+text3+" 的相似度分值："+score3pk3);
	
运行结果如下：

	我爱购物 和 我爱购物 的相似度分值：1.0
    我爱购物 和 我爱读书 的相似度分值：0.95
    我爱购物 和 他是黑客 的相似度分值：0.83
    我爱读书 和 我爱读书 的相似度分值：1.0
    我爱读书 和 他是黑客 的相似度分值：0.86
    他是黑客 和 他是黑客 的相似度分值：1.0
	
方式五：Jaccard相似性系数（Jaccard similarity coefficient），通过计算两个集合交集的大小除以并集的大小来评估他们的相似度

实现类：org.apdplat.word.analysis.JaccardTextSimilarity
	
用法如下：

	String text1 = "我爱购物";
	String text2 = "我爱读书";
	String text3 = "他是黑客";
	TextSimilarity textSimilarity = new JaccardTextSimilarity();
	double score1pk1 = textSimilarity.similarScore(text1, text1);
	double score1pk2 = textSimilarity.similarScore(text1, text2);
	double score1pk3 = textSimilarity.similarScore(text1, text3);
	double score2pk2 = textSimilarity.similarScore(text2, text2);
	double score2pk3 = textSimilarity.similarScore(text2, text3);
	double score3pk3 = textSimilarity.similarScore(text3, text3);
	System.out.println(text1+" 和 "+text1+" 的相似度分值："+score1pk1);
	System.out.println(text1+" 和 "+text2+" 的相似度分值："+score1pk2);
	System.out.println(text1+" 和 "+text3+" 的相似度分值："+score1pk3);
	System.out.println(text2+" 和 "+text2+" 的相似度分值："+score2pk2);
	System.out.println(text2+" 和 "+text3+" 的相似度分值："+score2pk3);
	System.out.println(text3+" 和 "+text3+" 的相似度分值："+score3pk3);
	
运行结果如下：

	我爱购物 和 我爱购物 的相似度分值：1.0
	我爱购物 和 我爱读书 的相似度分值：0.5
	我爱购物 和 他是黑客 的相似度分值：0.0
	我爱读书 和 我爱读书 的相似度分值：1.0
	我爱读书 和 他是黑客 的相似度分值：0.0
	他是黑客 和 他是黑客 的相似度分值：1.0
	
方式六：欧几里得距离（Euclidean Distance），通过计算两点间的距离来评估他们的相似度

实现类：org.apdplat.word.analysis.EuclideanDistanceTextSimilarity
	
用法如下：

	String text1 = "我爱购物";
	String text2 = "我爱读书";
	String text3 = "他是黑客";
	TextSimilarity textSimilarity = new EuclideanDistanceTextSimilarity();
	double score1pk1 = textSimilarity.similarScore(text1, text1);
	double score1pk2 = textSimilarity.similarScore(text1, text2);
	double score1pk3 = textSimilarity.similarScore(text1, text3);
	double score2pk2 = textSimilarity.similarScore(text2, text2);
	double score2pk3 = textSimilarity.similarScore(text2, text3);
	double score3pk3 = textSimilarity.similarScore(text3, text3);
	System.out.println(text1+" 和 "+text1+" 的相似度分值："+score1pk1);
	System.out.println(text1+" 和 "+text2+" 的相似度分值："+score1pk2);
	System.out.println(text1+" 和 "+text3+" 的相似度分值："+score1pk3);
	System.out.println(text2+" 和 "+text2+" 的相似度分值："+score2pk2);
	System.out.println(text2+" 和 "+text3+" 的相似度分值："+score2pk3);
	System.out.println(text3+" 和 "+text3+" 的相似度分值："+score3pk3);
	
运行结果如下：

	我爱购物 和 我爱购物 的相似度分值：1.0
	我爱购物 和 我爱读书 的相似度分值：0.41
	我爱购物 和 他是黑客 的相似度分值：0.29
	我爱读书 和 我爱读书 的相似度分值：1.0
	我爱读书 和 他是黑客 的相似度分值：0.29
	他是黑客 和 他是黑客 的相似度分值：1.0
	
方式七：曼哈顿距离（Manhattan Distance），通过计算两个点在标准坐标系上的绝对轴距总和来评估他们的相似度

实现类：org.apdplat.word.analysis.ManhattanDistanceTextSimilarity
	
用法如下：

	String text1 = "我爱购物";
	String text2 = "我爱读书";
	String text3 = "他是黑客";
	TextSimilarity textSimilarity = new ManhattanDistanceTextSimilarity();
	double score1pk1 = textSimilarity.similarScore(text1, text1);
	double score1pk2 = textSimilarity.similarScore(text1, text2);
	double score1pk3 = textSimilarity.similarScore(text1, text3);
	double score2pk2 = textSimilarity.similarScore(text2, text2);
	double score2pk3 = textSimilarity.similarScore(text2, text3);
	double score3pk3 = textSimilarity.similarScore(text3, text3);
	System.out.println(text1+" 和 "+text1+" 的相似度分值："+score1pk1);
	System.out.println(text1+" 和 "+text2+" 的相似度分值："+score1pk2);
	System.out.println(text1+" 和 "+text3+" 的相似度分值："+score1pk3);
	System.out.println(text2+" 和 "+text2+" 的相似度分值："+score2pk2);
	System.out.println(text2+" 和 "+text3+" 的相似度分值："+score2pk3);
	System.out.println(text3+" 和 "+text3+" 的相似度分值："+score3pk3);
	
运行结果如下：

	我爱购物 和 我爱购物 的相似度分值：1.0
	我爱购物 和 我爱读书 的相似度分值：0.33
	我爱购物 和 他是黑客 的相似度分值：0.14
	我爱读书 和 我爱读书 的相似度分值：1.0
	我爱读书 和 他是黑客 的相似度分值：0.14
	他是黑客 和 他是黑客 的相似度分值：1.0
	
方式八：Jaro距离（Jaro Distance），编辑距离的一种类型

实现类：org.apdplat.word.analysis.JaroDistanceTextSimilarity
	
用法如下：

	String text1 = "我爱购物";
	String text2 = "我爱读书";
	String text3 = "他是黑客";
	TextSimilarity textSimilarity = new JaroDistanceTextSimilarity();
	double score1pk1 = textSimilarity.similarScore(text1, text1);
	double score1pk2 = textSimilarity.similarScore(text1, text2);
	double score1pk3 = textSimilarity.similarScore(text1, text3);
	double score2pk2 = textSimilarity.similarScore(text2, text2);
	double score2pk3 = textSimilarity.similarScore(text2, text3);
	double score3pk3 = textSimilarity.similarScore(text3, text3);
	System.out.println(text1+" 和 "+text1+" 的相似度分值："+score1pk1);
	System.out.println(text1+" 和 "+text2+" 的相似度分值："+score1pk2);
	System.out.println(text1+" 和 "+text3+" 的相似度分值："+score1pk3);
	System.out.println(text2+" 和 "+text2+" 的相似度分值："+score2pk2);
	System.out.println(text2+" 和 "+text3+" 的相似度分值："+score2pk3);
	System.out.println(text3+" 和 "+text3+" 的相似度分值："+score3pk3);
	
运行结果如下：

	我爱购物 和 我爱购物 的相似度分值：1.0
	我爱购物 和 我爱读书 的相似度分值：0.67
	我爱购物 和 他是黑客 的相似度分值：0.0
	我爱读书 和 我爱读书 的相似度分值：1.0
	我爱读书 和 他是黑客 的相似度分值：0.0
	他是黑客 和 他是黑客 的相似度分值：1.0
		
方式九：Jaro–Winkler距离（Jaro–Winkler Distance），Jaro的扩展
	
实现类：org.apdplat.word.analysis.JaroWinklerDistanceTextSimilarity
	
用法如下：

	String text1 = "我爱购物";
	String text2 = "我爱读书";
	String text3 = "他是黑客";
	TextSimilarity textSimilarity = new JaroWinklerDistanceTextSimilarity();
	double score1pk1 = textSimilarity.similarScore(text1, text1);
	double score1pk2 = textSimilarity.similarScore(text1, text2);
	double score1pk3 = textSimilarity.similarScore(text1, text3);
	double score2pk2 = textSimilarity.similarScore(text2, text2);
	double score2pk3 = textSimilarity.similarScore(text2, text3);
	double score3pk3 = textSimilarity.similarScore(text3, text3);
	System.out.println(text1+" 和 "+text1+" 的相似度分值："+score1pk1);
	System.out.println(text1+" 和 "+text2+" 的相似度分值："+score1pk2);
	System.out.println(text1+" 和 "+text3+" 的相似度分值："+score1pk3);
	System.out.println(text2+" 和 "+text2+" 的相似度分值："+score2pk2);
	System.out.println(text2+" 和 "+text3+" 的相似度分值："+score2pk3);
	System.out.println(text3+" 和 "+text3+" 的相似度分值："+score3pk3);
	
运行结果如下：

	我爱购物 和 我爱购物 的相似度分值：1.0
	我爱购物 和 我爱读书 的相似度分值：0.73
	我爱购物 和 他是黑客 的相似度分值：0.0
	我爱读书 和 我爱读书 的相似度分值：1.0
	我爱读书 和 他是黑客 的相似度分值：0.0
	他是黑客 和 他是黑客 的相似度分值：1.0
		
方式十：Sørensen–Dice系数（Sørensen–Dice coefficient），通过计算两个集合交集的大小的2倍除以两个集合的大小之和来评估他们的相似度
	
实现类：org.apdplat.word.analysis.SørensenDiceCoefficientTextSimilarity
	
用法如下：

	String text1 = "我爱购物";
	String text2 = "我爱读书";
	String text3 = "他是黑客";
	TextSimilarity textSimilarity = new SørensenDiceCoefficientTextSimilarity();
	double score1pk1 = textSimilarity.similarScore(text1, text1);
	double score1pk2 = textSimilarity.similarScore(text1, text2);
	double score1pk3 = textSimilarity.similarScore(text1, text3);
	double score2pk2 = textSimilarity.similarScore(text2, text2);
	double score2pk3 = textSimilarity.similarScore(text2, text3);
	double score3pk3 = textSimilarity.similarScore(text3, text3);
	System.out.println(text1+" 和 "+text1+" 的相似度分值："+score1pk1);
	System.out.println(text1+" 和 "+text2+" 的相似度分值："+score1pk2);
	System.out.println(text1+" 和 "+text3+" 的相似度分值："+score1pk3);
	System.out.println(text2+" 和 "+text2+" 的相似度分值："+score2pk2);
	System.out.println(text2+" 和 "+text3+" 的相似度分值："+score2pk3);
	System.out.println(text3+" 和 "+text3+" 的相似度分值："+score3pk3);
	
运行结果如下：

	我爱购物 和 我爱购物 的相似度分值：1.0
	我爱购物 和 我爱读书 的相似度分值：0.67
	我爱购物 和 他是黑客 的相似度分值：0.0
	我爱读书 和 我爱读书 的相似度分值：1.0
	我爱读书 和 他是黑客 的相似度分值：0.0
	他是黑客 和 他是黑客 的相似度分值：1.0
	
###分词算法效果评估：

	1、word分词 最大Ngram分值算法：
	分词速度：370.9714 字符/毫秒
	行数完美率：66.55%  行数错误率：33.44%  总的行数：2533709  完美行数：1686210  错误行数：847499
	字数完美率：60.94% 字数错误率：39.05% 总的字数：28374490 完美字数：17293964 错误字数：11080526
	
	2、word分词 最少词数算法：
	分词速度：330.1586 字符/毫秒
	行数完美率：65.67%  行数错误率：34.32%  总的行数：2533709  完美行数：1663958  错误行数：869751
	字数完美率：60.12% 字数错误率：39.87% 总的字数：28374490 完美字数：17059641 错误字数：11314849
	
	3、word分词 全切分算法：
	分词速度：62.960262 字符/毫秒
	行数完美率：57.2%  行数错误率：42.79%  总的行数：2533709  完美行数：1449288  错误行数：1084421
	字数完美率：47.95% 字数错误率：52.04% 总的字数：28374490 完美字数：13605742 错误字数：14768748
	
	4、word分词 双向最大最小匹配算法：
	分词速度：462.87158 字符/毫秒
	行数完美率：53.06%  行数错误率：46.93%  总的行数：2533709  完美行数：1344624  错误行数：1189085
	字数完美率：43.07% 字数错误率：56.92% 总的字数：28374490 完美字数：12221610 错误字数：16152880
	
	5、word分词 双向最小匹配算法：
	分词速度：967.68604 字符/毫秒
	行数完美率：46.34%  行数错误率：53.65%  总的行数：2533709  完美行数：1174276  错误行数：1359433
	字数完美率：36.07% 字数错误率：63.92% 总的字数：28374490 完美字数：10236574 错误字数：18137916
	
	6、word分词 双向最大匹配算法：
	分词速度：661.148 字符/毫秒
	行数完美率：46.18%  行数错误率：53.81%  总的行数：2533709  完美行数：1170075  错误行数：1363634
	字数完美率：35.65% 字数错误率：64.34% 总的字数：28374490 完美字数：10117122 错误字数：18257368
	
	7、word分词 正向最大匹配算法：
	分词速度：1567.1318 字符/毫秒
	行数完美率：41.88%  行数错误率：58.11%  总的行数：2533709  完美行数：1061189  错误行数：1472520
	字数完美率：31.35% 字数错误率：68.64% 总的字数：28374490 完美字数：8896173 错误字数：19478317
	
	8、word分词 逆向最大匹配算法：
	分词速度：1232.6017 字符/毫秒
	行数完美率：41.69%  行数错误率：58.3%  总的行数：2533709  完美行数：1056515  错误行数：1477194
	字数完美率：30.98% 字数错误率：69.01% 总的字数：28374490 完美字数：8792532 错误字数：19581958
	
	9、word分词 逆向最小匹配算法：
	分词速度：1936.9575 字符/毫秒
	行数完美率：41.42%  行数错误率：58.57%  总的行数：2533709  完美行数：1049673  错误行数：1484036
	字数完美率：31.34% 字数错误率：68.65% 总的字数：28374490 完美字数：8893622 错误字数：19480868
	
	10、word分词 正向最小匹配算法：
	分词速度：2228.9465 字符/毫秒
	行数完美率：36.7%  行数错误率：63.29%  总的行数：2533709  完美行数：930069  错误行数：1603640
	字数完美率：26.72% 字数错误率：73.27% 总的字数：28374490 完美字数：7583741 错误字数：20790749
	
###相关文章：

   [1、中文分词算法 之 基于词典的正向最大匹配算法](http://yangshangchuan.iteye.com/blog/2031813)
    
   [2、中文分词算法 之 基于词典的逆向最大匹配算法](http://yangshangchuan.iteye.com/blog/2033843)
    
   [3、中文分词算法 之 词典机制性能优化与测试](http://yangshangchuan.iteye.com/blog/2035007)
   
   [4、中文分词算法 之 基于词典的正向最小匹配算法](http://yangshangchuan.iteye.com/blog/2040423)
   
   [5、中文分词算法 之 基于词典的逆向最小匹配算法](http://yangshangchuan.iteye.com/blog/2040431)
   
   [6、一种利用ngram模型来消除歧义的中文分词方法](http://my.oschina.net/apdplat/blog/411112)
   
   [7、一种基于词性序列的人名识别方法](http://my.oschina.net/apdplat/blog/411032)
   
   [8、中文分词算法 之 基于词典的全切分算法](http://my.oschina.net/apdplat/blog/412785)
   
   [9、9大Java开源中文分词器的使用方法和分词效果对比](http://my.oschina.net/apdplat/blog/412921)
   
   [10、中文分词之11946组同义词](http://my.oschina.net/apdplat/blog/408779)
   
   [11、中文分词之9271组反义词](http://my.oschina.net/apdplat/blog/411301)
   
   [12、如何利用多核提升分词速度](http://my.oschina.net/apdplat/blog/414076)
   
   [13、利用word分词来计算文本相似度](http://my.oschina.net/apdplat/blog/417047)
   
   [14、利用word分词来对文本进行词频统计](http://my.oschina.net/apdplat/blog/417641)

   [15、利用word分词通过计算词的语境来获得相关词](http://my.oschina.net/apdplat/blog/417922)
   
###相关项目：
   
[Java开源项目cws_evaluation：中文分词器分词效果评估对比](https://github.com/ysc/cws_evaluation/)

[Java开源项目QuestionAnsweringSystem：人机问答系统](https://github.com/ysc/QuestionAnsweringSystem/)
   
[Java开源项目word_web：通过web服务器对word分词的资源进行集中统一管理](https://github.com/ysc/word_web/)

###相关文献：

[An Implementation of Double-Array Trie](http://linux.thai.net/~thep/datrie/datrie.html)

[MMSEG: A Word Identification System for Mandarin Chinese Text Based on Two Variants of the Maximum Matching Algorithm](http://technology.chtsai.org/mmseg/)

[With Google’s new tool Ngram Viewer, you can visualise the rise and fall of concepts across 5 million books and 500 years!](https://books.google.com/ngrams)

[word2vec](https://code.google.com/p/word2vec/)

[魅力汉语](http://open.163.com/special/cuvocw/meilihanyu.html)
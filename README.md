Chinese Word Segmentation Component

分词使用方法：
<p>List<Word> words = SegmentationFactory.getSegmentation(SegmentationAlgorithm.MaximumMatching).seg(sentence));</p>
<p>List<Word> words = SegmentationFactory.getSegmentation(SegmentationAlgorithm.ReverseMaximumMatching).seg(sentence));</p>

分词算法文章：
<p><a href="http://yangshangchuan.iteye.com/blog/2031813" target="_blank">中文分词算法 之 基于词典的正向最大匹配算法</a></p>
<p><a href="http://yangshangchuan.iteye.com/blog/2033843" target="_blank">中文分词算法 之 基于词典的逆向最大匹配算法</a></p>
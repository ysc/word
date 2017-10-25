package org.apdplat.word.elasticsearch;

import org.apdplat.word.lucene.ChineseWordAnalyzer;
import org.apdplat.word.segmentation.Segmentation;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import org.apdplat.word.segmentation.SegmentationFactory;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ysc on 25/10/2017.
 */
public class ChineseWordAnalyzerProvider extends AbstractIndexAnalyzerProvider<ChineseWordAnalyzer> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChineseWordAnalyzerProvider.class);

    private final ChineseWordAnalyzer analyzer;
    private final Segmentation segmentation;

    public ChineseWordAnalyzerProvider(IndexSettings indexSettings, Environment environment, String name, Settings settings) {
        super(indexSettings, name, settings);
        String segAlgorithm = settings.get("segAlgorithm");
        if(segAlgorithm != null){
            LOGGER.info("tokenizer使用指定分词算法："+segAlgorithm);
            segmentation = SegmentationFactory.getSegmentation(SegmentationAlgorithm.valueOf(segAlgorithm));
        }else{
            LOGGER.info("没有为word tokenizer指定segAlgorithm参数");
            LOGGER.info("tokenizer使用默认分词算法："+SegmentationAlgorithm.MaxNgramScore);
            segmentation = SegmentationFactory.getSegmentation(SegmentationAlgorithm.MaxNgramScore);
        }
        analyzer = new ChineseWordAnalyzer(segmentation);
    }

    @Override
    public ChineseWordAnalyzer get() {
        return this.analyzer;
    }
}
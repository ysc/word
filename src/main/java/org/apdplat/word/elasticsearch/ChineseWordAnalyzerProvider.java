package org.apdplat.word.elasticsearch;

import org.apdplat.word.lucene.ChineseWordAnalyzer;
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

    private final ChineseWordAnalyzer analyzer = new ChineseWordAnalyzer(SegmentationFactory.getSegmentation(SegmentationAlgorithm.MaxNgramScore));

    public ChineseWordAnalyzerProvider(IndexSettings indexSettings, Environment environment, String name, Settings settings) {
        super(indexSettings, name, settings);
    }

    @Override
    public ChineseWordAnalyzer get() {
        return this.analyzer;
    }
}
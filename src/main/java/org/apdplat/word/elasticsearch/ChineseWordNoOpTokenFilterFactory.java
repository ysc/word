package org.apdplat.word.elasticsearch;

import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;

/**
 * Created by ysc on 25/10/2017.
 */
public class ChineseWordNoOpTokenFilterFactory extends AbstractTokenFilterFactory {

    public ChineseWordNoOpTokenFilterFactory(IndexSettings indexSettings, Environment environment, String name, Settings settings) {
        super(indexSettings, name, settings);
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return tokenStream;
    }
}

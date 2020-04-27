package org.picstories.parsers.configuration;

import com.google.common.collect.Sets;
import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author arman.shamenov
 */
@Component
public class ApplicationConfigFilter implements AutoConfigurationImportFilter {
    private static final Set<String> SHOULD_SKIP = Sets.newHashSet(
            "org.picstories.library.elasticsearch.ReactiveElasticsearchConfiguration",
                    "org.picstories.library.mongodb.ReactiveMongoDbConfiguration");

    @Override
    public boolean[] match(String[] classNames, AutoConfigurationMetadata autoConfigurationMetadata) {
        boolean[] matches = new boolean[classNames.length];

        for(int i = 0; i< classNames.length; i++) {
            matches[i] = !SHOULD_SKIP.contains(classNames[i]);
        }
        return matches;
    }
}

package org.picstories.parsers;

import org.picstories.library.elasticsearch.ReactiveElasticsearchConfiguration;
import org.picstories.library.mongodb.ReactiveMongoDbConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author arman.shamenov
 */
@SpringBootApplication(exclude = {ReactiveMongoDbConfiguration.class,
        ReactiveElasticsearchConfiguration.class})
public class ParsersApplication {
    public static void main(String[] args) {
        SpringApplication.run(ParsersApplication.class, args);
    }
}

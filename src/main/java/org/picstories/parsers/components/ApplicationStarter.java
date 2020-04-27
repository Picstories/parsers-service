package org.picstories.parsers.components;

import org.picstories.library.model.kafka.parsers.ParseTask;
import org.picstories.parsers.service.ParsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import reactor.kafka.receiver.KafkaReceiver;

/**
 * @author arman.shamenov
 */
@Component
public class ApplicationStarter implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationStarter.class);

    private final ParsersService parsersService;
    private final KafkaReceiver<String, ParseTask> kafkaReceiver;


    public ApplicationStarter(ParsersService parsersService,
                              KafkaReceiver<String, ParseTask> kafkaReceiver) {
        this.parsersService = parsersService;
        this.kafkaReceiver = kafkaReceiver;
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        kafkaReceiver.receive()
                .doOnNext(record -> logger.info("Record consume = {} ", record.toString()))
                .subscribe(parsersService::consume);
    }
}

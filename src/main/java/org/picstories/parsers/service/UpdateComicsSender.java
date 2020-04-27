package org.picstories.parsers.service;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.picstories.library.model.kafka.parsers.UpdateTask;
import org.picstories.library.utils.KafkaTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

/**
 * @author arman.shamenov
 */
@Service
public class UpdateComicsSender {
    private static final Logger logger = LoggerFactory.getLogger(UpdateComicsSender.class);

    private final KafkaSender<String, UpdateTask> kafkaSender;

    public UpdateComicsSender(KafkaSender<String, UpdateTask> kafkaSender) {
        this.kafkaSender = kafkaSender;
    }

    public void send(UpdateTask message) {
        ProducerRecord<String, UpdateTask> producerRecord = new ProducerRecord<>(KafkaTopics.KAFKA_UPDATE_COMIC_TOPIC, message);

        logger.info("produce record to kafka = {} in the topic = {}", producerRecord, producerRecord.topic());
        String metadata = String.format("task  id = %s with data = %s", message.getId(), message);

        kafkaSender.send(Mono.just(SenderRecord.create(producerRecord, metadata)))
                .doOnNext(senderResult -> logger.info("parse result after send kafka = {} in the topic = {} ",
                        senderResult.toString(),
                        senderResult.recordMetadata().topic()))
                .doOnError(err-> logger.error("error while send result to kafka", err))
                .subscribe();
    }
}

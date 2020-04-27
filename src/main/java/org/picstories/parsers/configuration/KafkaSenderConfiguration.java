package org.picstories.parsers.configuration;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.picstories.library.model.kafka.parsers.UpdateTask;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerializer;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadFactory;

/**
 * @author arman.shamenov
 */
@Configuration
public class KafkaSenderConfiguration {
    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaServer;

    @Value("${spring.kafka.producer.client-id}")
    private String kafkaProducerId;

    @Bean
    public KafkaSender<String, UpdateTask> kafkaSender() {
        Map<String, Object> res = new HashMap<>();
        res.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        res.put(ProducerConfig.CLIENT_ID_CONFIG, kafkaProducerId);
        res.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        res.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        ThreadFactory factory = new ThreadFactoryBuilder()
                .setNameFormat("kafka-producer-thread-%d")
                .setDaemon(false)
                .build();
        Scheduler scheduler =  Schedulers.newElastic(35, factory);
        SenderOptions<String, UpdateTask> senderOptions = SenderOptions
                .<String, UpdateTask>create(res)
                .maxInFlight(1024)
                .scheduler(scheduler);

        return KafkaSender.create(senderOptions);
    }
}

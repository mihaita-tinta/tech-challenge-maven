package com.tech.challenge.maven.kafka;


import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import reactor.core.publisher.Mono;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * https://docs.spring.io/spring-kafka/reference/html/
 */
@Configuration
@EnableKafka
public class KafkaConfig {
    private static final Logger log = getLogger(KafkaConfig.class);

    @Autowired
    private KafkaTemplate<Integer, String> template;

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name("topic1")
                .partitions(10)
                .replicas(1)
                .build();
    }

    @KafkaListener(id = "myId", topics = "topic1")
    public void listen(String input) {
       log.info("listen - input: {}", input);
    }

    @Bean
    public ApplicationRunner runner() {
        return args -> {
            ListenableFuture<SendResult<Integer, String>> send = template.send("topic1", "test");
            Mono.fromFuture(send.completable())
                    .subscribe();
        };
    }
}

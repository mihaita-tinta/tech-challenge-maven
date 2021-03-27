package com.tech.challenge.maven.kafka;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.challenge.maven.config.MavenConfigurationProperties;
import com.tech.challenge.maven.kafka.events.ShotFired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class KafkaClient {
    private static final Logger log = LoggerFactory.getLogger(KafkaClient.class);
    private final KafkaTemplate<Integer, String> kafka;

    @Autowired
    MavenConfigurationProperties properties;

    @Value("${maven.kafka.topicRoundShoot}")
    String topic;

    @Autowired
    ObjectMapper mapper;

    public KafkaClient(KafkaTemplate<Integer, String> kafka) {
        this.kafka = kafka;
    }

    public Mono<SendResult<Integer, String>> shoot(ShotFired shotFired, String jwt) {
        log.info("shoot - shotFired: {}", shotFired);
        Mono<SendResult<Integer, String>> sendResultMono = null;
        try {

            Message<String> message = MessageBuilder
                    .withPayload(mapper.writeValueAsString(shotFired))
                    .setHeader(KafkaHeaders.TOPIC, topic)
                    .setHeader("Authorization", "Bearer " + jwt)
                    .build();
            sendResultMono = Mono.fromFuture(kafka.send(message).completable());
            return sendResultMono;
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
    }
}

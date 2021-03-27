package com.tech.challenge.maven.kafka;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.challenge.maven.kafka.events.ShotFired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import reactor.core.publisher.Mono;

@Component
public class KafkaClient {
    private static final Logger log = LoggerFactory.getLogger(KafkaClient.class);
    private final KafkaTemplate<Integer, String> kafka;

    @Value("${maven.kafka.topicRoundShoot}")
    String topic;

    @Autowired
    ObjectMapper mapper;

    public KafkaClient(KafkaTemplate<Integer, String> kafka) {
        this.kafka = kafka;
    }

    public Mono<SendResult<Integer, String>> shoot(ShotFired shotFired) {
        log.debug("shoot - shotFired: {}", shotFired);
        Mono<SendResult<Integer, String>> sendResultMono = null;
        try {
            sendResultMono = Mono.fromFuture(kafka.send(topic, mapper.writeValueAsString(shotFired)).completable());
            return sendResultMono;
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
    }
}

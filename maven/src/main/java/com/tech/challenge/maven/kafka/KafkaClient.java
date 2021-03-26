package com.tech.challenge.maven.kafka;


import com.tech.challenge.maven.kafka.events.ShotFired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import reactor.core.publisher.Mono;

@Component
public class KafkaClient {

    private final KafkaTemplate<Integer, ShotFired> kafka;

    @Value("${maven.kafka.topicRoundShoot}")
    String topic;

    public KafkaClient(KafkaTemplate<Integer, ShotFired> kafka) {
        this.kafka = kafka;
    }

    public Mono<SendResult<Integer, ShotFired>> shoot(ShotFired shotFired) {
        Mono<SendResult<Integer, ShotFired>> sendResultMono = Mono.fromFuture(kafka.send(topic, shotFired).completable());
        return
                sendResultMono;
    }
}

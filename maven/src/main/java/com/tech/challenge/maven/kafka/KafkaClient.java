package com.tech.challenge.maven.kafka;


import com.tech.challenge.maven.kafka.events.ShotFired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

@Component
public class KafkaClient {

    private final KafkaTemplate<Integer, ShotFired> kafka;

    @Value("${maven.kafka.topicRoundShoot}")
    String topic;

    public KafkaClient(KafkaTemplate<Integer, ShotFired> kafka) {
        this.kafka = kafka;
    }

    public ListenableFuture<SendResult<Integer, ShotFired>> shoot(ShotFired shotFired) {
        return kafka.send(topic, shotFired);
    }
}

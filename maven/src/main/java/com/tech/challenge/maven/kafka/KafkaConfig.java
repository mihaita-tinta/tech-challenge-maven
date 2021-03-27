package com.tech.challenge.maven.kafka;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.challenge.maven.agent.MavenAgent;
import com.tech.challenge.maven.kafka.events.GameEnded;
import com.tech.challenge.maven.kafka.events.GameStarted;
import com.tech.challenge.maven.kafka.events.RoundEnded;
import com.tech.challenge.maven.kafka.events.RoundStarted;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * https://docs.spring.io/spring-kafka/reference/html/
 */
@Configuration
@EnableKafka
public class KafkaConfig {
    private static final Logger log = getLogger(KafkaConfig.class);

    @Autowired
    MavenAgent mavenAgent;

    @Autowired
    ObjectMapper mapper;

    @KafkaListener(groupId = "group-21bb9c95-a2c8-4ea9-bffa-a9ae28ed9aa3", topics = "${maven.kafka.topicGameStarted}")
    public void listenGameStarted(String input) throws JsonProcessingException {
        log.info("listenGameStarted - input: {}", input);
        GameStarted event = mapper.readValue(input, GameStarted.class);
        mavenAgent
                .onGameStarted(event)
                .subscribe();
    }

    @KafkaListener(groupId = "group-21bb9c95-a2c8-4ea9-bffa-a9ae28ed9aa3", topics = "${maven.kafka.topicGameEnded}")
    public void listenGameEnded(String input) throws JsonProcessingException {
        log.info("listenGameEnded - input: {}", input);
        GameEnded event = mapper.readValue(input, GameEnded.class);
        mavenAgent.onGameEnded(event);
    }

    @KafkaListener(groupId = "group-21bb9c95-a2c8-4ea9-bffa-a9ae28ed9aa3", topics = "${maven.kafka.topicRoundStarted}")
    public void listenRoundStarted(String input) throws JsonProcessingException {
        log.info("listenRoundStarted - input: {}", input);
        RoundStarted event = mapper.readValue(input, RoundStarted.class);
        mavenAgent
                .onRoundStarted(event)
                .subscribe();
    }

    @KafkaListener(groupId = "group-21bb9c95-a2c8-4ea9-bffa-a9ae28ed9aa3", topics = "${maven.kafka.topicRoundEnded}")
    public void listenRoundEnded(String input) throws JsonProcessingException {
        log.info("listenRoundEnded - input: {}", input);
        RoundEnded roundEnded = mapper.readValue(input, RoundEnded.class);
        mavenAgent.onRoundEnded(roundEnded);
    }

}

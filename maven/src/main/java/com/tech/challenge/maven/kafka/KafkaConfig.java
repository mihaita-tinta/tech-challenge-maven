package com.tech.challenge.maven.kafka;


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

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name("topic1")
                .partitions(10)
                .replicas(1)
                .build();
    }

    @KafkaListener(id = "maven-game-started", topics = "${maven.kafka.topicGameStarted}")
    public void listenGameStarted(GameStarted gameStarted) {
        log.info("listenGameStarted - input: {}", gameStarted);
        mavenAgent
                .onGameStarted(gameStarted)
                .subscribe();
    }

    @KafkaListener(id = "maven-game-ended", topics = "${maven.kafka.topicGameEnded}")
    public void listenGameEnded(GameEnded gameEnded) {
        log.info("listenGameEnded - input: {}", gameEnded);
        mavenAgent.onGameEnded(gameEnded);
    }

    @KafkaListener(id = "maven-round-started", topics = "${maven.kafka.topicRoundStarted}")
    public void listenRoundStarted(RoundStarted roundStarted) {
        log.info("listenRoundStarted - input: {}", roundStarted);
        mavenAgent.onRoundStarted(roundStarted);
    }

    @KafkaListener(id = "maven-round-ended", topics = "${maven.kafka.topicRoundEnded}")
    public void listenRoundEnded(RoundEnded roundEnded) {
        log.info("listenRoundEnded - input: {}", roundEnded);
        mavenAgent.onRoundEnded(roundEnded);
    }

}

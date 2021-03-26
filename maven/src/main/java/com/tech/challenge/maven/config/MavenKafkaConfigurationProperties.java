package com.tech.challenge.maven.config;

import lombok.Data;

@Data
public class MavenKafkaConfigurationProperties {

    private String topicGameStarted;
    private String topicGameEnded;
    private String topicRoundStarted;
    private String topicRoundEnded;
    private String topicRoundShoot;
}

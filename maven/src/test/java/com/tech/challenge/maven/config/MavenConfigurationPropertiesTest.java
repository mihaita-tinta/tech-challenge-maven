package com.tech.challenge.maven.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "maven.kafka.topicGameStarted=game-started")
@ExtendWith(SpringExtension.class)
class MavenConfigurationPropertiesTest {

    @Autowired
    MavenConfigurationProperties properties;

    @Test
    public void test() {

        assertEquals("game-started", properties.getKafka().getTopicGameStarted());

    }

}

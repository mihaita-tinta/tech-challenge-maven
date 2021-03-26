package com.tech.challenge.maven.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "maven")
public class MavenConfigurationProperties {

    private String username;
    private String password;
    private MavenHttpConfigurationProperties http;
    private MavenKafkaConfigurationProperties kafka;
}

package com.tech.challenge.maven.config;

import lombok.Data;

@Data
public class MavenHttpConfigurationProperties {

    private String scheme;
    private String host;
    private int port;

    public String getBaseUrl() {
        return String.format("%s://%s:%d/", scheme, host, port);
    }
}

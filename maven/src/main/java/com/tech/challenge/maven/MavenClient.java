package com.tech.challenge.maven;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * https://www.baeldung.com/spring-5-webclient
 * or
 * https://spring.io/guides/gs/spring-cloud-loadbalancer/
 */
@Component
public class MavenClient {

    @Autowired
    WebClient.Builder loadBalancedWebClientBuilder;

    public Mono<String> hi(String name) {
        return loadBalancedWebClientBuilder.build().get().uri("http://my-test/test/" + name)
                .retrieve().bodyToMono(String.class)
                .map(res -> String.format("%s", res));
    }
}

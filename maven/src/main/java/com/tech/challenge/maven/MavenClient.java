package com.tech.challenge.maven;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.WebSocketMessage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * https://www.baeldung.com/spring-5-webclient
 * or
 * https://spring.io/guides/gs/spring-cloud-loadbalancer/
 */
@Component
public class MavenClient {
    private static final Logger log = getLogger(MavenClient.class);

    InMemoryRepository inMemoryRepository = new InMemoryRepository();

    @Autowired
    WebClient.Builder loadBalancedWebClientBuilder;

    public Mono<String> hi(String name) {
        return loadBalancedWebClientBuilder.build().get().uri("http://my-test/test/" + name)
                .retrieve().bodyToMono(String.class)
                .map(res -> String.format("%s", res));
    }

    public Flux<String> connect(String req) {
        Flux<String> flux = Flux
                .create((Consumer<FluxSink<String>>) sink -> {
                    inMemoryRepository.onConnect(sink, req);
                })
                .onErrorResume(Exception.class, Flux::error)
                .doOnComplete(() -> log.info("connect - handle - doOnComplete for " + req))
                .doFinally(signalType -> {
                    log.info("connect - handle - finally for " + req);
                    inMemoryRepository.onClose(req);
                });

        return flux;
    }
}

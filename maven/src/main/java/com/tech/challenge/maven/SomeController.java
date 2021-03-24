package com.tech.challenge.maven;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class SomeController {

    @Value("${server.port}")
    String port;

    @Autowired
    MavenClient client;

    @GetMapping("/test")
    public Mono<String> get() {
        return client.hi("asda");
    }

    @GetMapping("/test/{name}")
    public Mono<String> a(@PathVariable String name) {
        return Mono.just("test" + port);
    }
}

package com.tech.challenge.maven;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

@Import(MavenClient.class)
@WebFluxTest(controllers = SomeController.class)
class MavenClientTest {
    private static final Logger log = LoggerFactory.getLogger(MavenClientTest.class);

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    MavenClient client;

    @Test
    public void testWebTestClient() {
        webTestClient
                .get()
                .uri("/test").header("username", "test")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .consumeWith(s -> System.out.println(s))
                .isEqualTo("test");
    }

    @Test
    public void test() throws InterruptedException {
        TcpClient tcpClient = TcpClient.create().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30_000)
                .doOnConnected(connection -> connection.addHandlerLast(new ReadTimeoutHandler(30))
                        .addHandlerLast(new WriteTimeoutHandler(30)));
        CountDownLatch latch = new CountDownLatch(1);

        WebClient client = WebClient.builder().baseUrl("http://localhost:8080")
                .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.ALL_VALUE)
                .defaultHeader(HttpHeaders.USER_AGENT, "Maven Client")
                .build();

        client
                .get()
                .uri("/test").header("username", "test").retrieve()
                .bodyToMono(String.class)
                .doOnError(e -> {
                    log.info("test - error", e);
                    latch.countDown();
                })
                .subscribe(res -> {
                    log.info("test - res {}", res);
                    latch.countDown();
                })
        ;

        latch.await();
    }

}

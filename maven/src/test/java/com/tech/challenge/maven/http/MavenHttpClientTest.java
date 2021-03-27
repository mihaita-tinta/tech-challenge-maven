package com.tech.challenge.maven.http;

import com.tech.challenge.maven.http.MavenHttpClient;
import com.tech.challenge.maven.http.model.BattleshipRequestBody;
import com.tech.challenge.maven.http.model.LoginResponse;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

//@Import({MavenClient.class, WebClientConfig.class})
//@WebFluxTest(controllers = SomeController.class)
@SpringBootTest
@AutoConfigureWireMock
class MavenHttpClientTest {
    private static final Logger log = LoggerFactory.getLogger(MavenHttpClientTest.class);

    @Autowired
    MavenHttpClient client;

    @Test
    public void testLogin() {

        stubFor(post(urlEqualTo("/api/authenticate"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(containing("{\"username\":\"mih\",\"password\":\"maven\"}"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"token\": \"adsasdsa\"}")
                        .withStatus(200)));

        Mono<LoginResponse> login = client.login();
        StepVerifier.create(login)
                .assertNext(res -> assertEquals("adsasdsa", res.getToken()))
                .expectComplete()
        .verify();

    }
    @Test
    public void testRegisterTournament() {

        client.jwt = "_JWT_";

        stubFor(post(urlEqualTo("/api/tournaments/BnIy16hjAt/teams"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withHeader("Authorization", equalTo("Bearer _JWT_"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{}")
                        .withStatus(201)));

        Mono<String> r = client.registerTournament("BnIy16hjAt");
        StepVerifier.create(r)
                .assertNext(res -> {})
                .expectComplete()
        .verify();

    }
    @Test
    public void testPlaceBattleship() {
        client.jwt = "_JWT_";

        stubFor(post(urlEqualTo("/api/tournaments/BnIy16hjAt/battleships"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withHeader("Authorization", equalTo("Bearer _JWT_"))
                .withRequestBody(containing("{\"gameId\":\"BnIy16hjAt-1\",\"x\":4,\"y\":6,\"direction\":\"NORTH\"}"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{}")
                        .withStatus(200)));

        Mono<String> r = client.placeBattleship("BnIy16hjAt",
                    "BnIy16hjAt-1",
                    4, 6,
                    BattleshipRequestBody.Direction.NORTH);
        log.info("test:{}", r.block());
        StepVerifier.create(r)
                .assertNext(res -> {})
                .expectComplete()
        .verify();

    }

//    @Test
//    public void test() throws InterruptedException {
//        TcpClient tcpClient = TcpClient.create().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30_000)
//                .doOnConnected(connection -> connection.addHandlerLast(new ReadTimeoutHandler(30))
//                        .addHandlerLast(new WriteTimeoutHandler(30)));
//        CountDownLatch latch = new CountDownLatch(1);
//
//        WebClient client = WebClient.builder().baseUrl("http://localhost:8080")
//                .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
//                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                .defaultHeader(HttpHeaders.ACCEPT, MediaType.ALL_VALUE)
//                .defaultHeader(HttpHeaders.USER_AGENT, "Maven Client")
//                .build();
//
//        client
//                .get()
//                .uri("/test").header("username", "test").retrieve()
//                .bodyToMono(String.class)
//                .doOnError(e -> {
//                    log.info("test - error", e);
//                    latch.countDown();
//                })
//                .subscribe(res -> {
//                    log.info("test - res {}", res);
//                    latch.countDown();
//                })
//        ;
//
//        latch.await();
//    }

}

package com.tech.challenge.maven.http;

import com.tech.challenge.maven.InMemoryRepository;
import com.tech.challenge.maven.config.MavenConfigurationProperties;
import com.tech.challenge.maven.http.model.BattleshipRequestBody;
import com.tech.challenge.maven.http.model.LoginRequest;
import com.tech.challenge.maven.http.model.LoginResponse;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
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
public class MavenHttpClient {
    private static final Logger log = getLogger(MavenHttpClient.class);

    private final MavenConfigurationProperties properties;
    String jwt;

    InMemoryRepository inMemoryRepository = new InMemoryRepository();

    @Autowired
//    WebClient.Builder loadBalancedWebClientBuilder;
    WebClient client;

    public MavenHttpClient(MavenConfigurationProperties properties) {
        this.properties = properties;
    }

    public Mono<LoginResponse> login() {
        log.info("login - using credentials: {}, xxx", properties.getUsername());
        return client
                .post()
                .uri("api/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new LoginRequest(properties.getUsername(), properties.getPassword()))
                .retrieve().bodyToMono(LoginResponse.class)
                .doOnNext(res -> this.jwt = res.getToken())
                .doOnNext(res -> log.info("connect - logged in successfully using credentials: {}", properties.getUsername()))
                .doOnError(e -> log.error("connect - failed to login using credentials: {}", properties.getUsername(), e))
                ;
    }

    public Mono<String> registerTournament(String tournamentId) {
        return client
                .post()
                .uri("api/tournaments/{tournamentId}/teams", tournamentId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.setBearerAuth(jwt))
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        log.info("connect - registered successfully for tournamentId: {}", tournamentId);
                        return response.bodyToMono(String.class);
                    }

                    return response.bodyToMono(String.class)
                            .doOnNext(res -> {
                                log.info("connect - failed to register for tournamentId: {}, res: {}", tournamentId, res);
                            });
                })
                ;
    }

    public Mono<String> placeBattleship(String tournamentId,
                                        String gameId,
                                        int x, int y, BattleshipRequestBody.Direction direction) {
        return client
                .post()
                .uri("tournaments/{tournamentId}/battleships", tournamentId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.setBearerAuth(jwt))
                .bodyValue(new BattleshipRequestBody(gameId, x, y, direction))
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> hi(String name) {
        return client.get().uri("http://backend/test/" + name)
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

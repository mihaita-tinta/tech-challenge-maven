package com.tech.challenge.maven.agent;

import com.tech.challenge.maven.http.MavenHttpClient;
import com.tech.challenge.maven.kafka.events.GameStarted;
import org.assertj.core.internal.bytebuddy.matcher.ElementMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class MavenAgentTest {

    @InjectMocks
    MavenAgent agent;
    @Mock
    MavenHttpClient http;
    @Mock
    GameStarted gameStarted;

    @Test
    public void test() {

        Mockito.when(http.placeBattleship(any(), any(), anyInt(), anyInt(), any()))
                .thenReturn(
                        Mono.error(new IllegalArgumentException("should retry 1")),
                        Mono.error(new IllegalArgumentException("should retry 2")),
                        Mono.just("asdaas"));

        Mono<String> mono = agent.onGameStarted(gameStarted);

        StepVerifier.create(mono)
                .expectNextCount(1)
                .expectComplete()
                .verify();

        Mockito.verify(http, times(3)).placeBattleship(any(), any(), anyInt(), anyInt(), any());

        assertEquals(3, agent.getBrain().getCurrentPosition().getX());
    }

}

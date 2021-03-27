package com.tech.challenge.maven.agent;

import com.tech.challenge.maven.agent.ai.RandomBattleshipPositionDecider;
import com.tech.challenge.maven.http.MavenHttpClient;
import com.tech.challenge.maven.kafka.KafkaClient;
import com.tech.challenge.maven.kafka.events.GameStarted;
import com.tech.challenge.maven.kafka.events.RoundStarted;
import com.tech.challenge.maven.kafka.events.ShotFired;
import com.tech.challenge.maven.model.BattleshipPosition;
import org.assertj.core.internal.bytebuddy.matcher.ElementMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.SendResult;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MavenAgentTest {

    @InjectMocks
    MavenAgent agent;
    @Mock
    MavenHttpClient http;
    @Mock
    KafkaClient kafka;
    @Mock
    GameStarted gameStarted;
    @Mock
    RoundStarted round;
    @Mock
    SendResult<Integer, ShotFired> shot;

    @Mock
    RandomBattleshipPositionDecider battleshipPositionDecider;

    @Mock
    BattleshipPosition pos1;
    @Mock
    BattleshipPosition pos2;
    @Mock
    BattleshipPosition pos3;

    @Test
    public void testOnGameStartedWithRetries() {

        when(battleshipPositionDecider.next(agent.getBrain().getMemory()))
                .thenReturn(pos1, pos2, pos3);

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

        assertEquals(pos3, agent.getBrain().getMemory().getCurrentPosition());
    }
    @Test
    public void testOnRoundStarted() {

        Mockito.when(kafka.shoot(any()))
                .thenReturn(Mono.just(shot));

        Mono<Void> mono = agent.onRoundStarted(round);

        StepVerifier.create(mono)
                .expectComplete()
                .verify();

        Mockito.verify(kafka).shoot(any());

    }

}

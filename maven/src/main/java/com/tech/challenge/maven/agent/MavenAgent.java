package com.tech.challenge.maven.agent;

import com.tech.challenge.maven.config.MavenConfigurationProperties;
import com.tech.challenge.maven.http.MavenHttpClient;
import com.tech.challenge.maven.http.model.BattleshipRequestBody;
import com.tech.challenge.maven.kafka.KafkaClient;
import com.tech.challenge.maven.kafka.events.*;
import com.tech.challenge.maven.model.BattleshipPosition;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;
import reactor.util.retry.Retry;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class MavenAgent {
    private static final Logger log = getLogger(MavenAgent.class);
    private final MavenHttpClient http;
    private final KafkaClient kafka;
    private final MavenBrain brain;
    private final int maxAttempts = 10;

    public MavenAgent(MavenHttpClient http, KafkaClient kafka, MavenConfigurationProperties properties) {
        this.http = http;
        this.kafka = kafka;

        brain = new MavenBrain();// newborn
    }

    public Mono<String> onGameStarted(GameStarted gameStarted) {
        log.info("onGameStarted - game: {}", gameStarted);
        brain.gameStarted(gameStarted);

        BattleshipPosition battlefieldPosition = brain.getNextBattlefieldPosition();
        return placeBattlefield(gameStarted, battlefieldPosition, 0)
                .doOnError(e -> {
                    log.error("onGameStarted - failed to place battleship: {}", battlefieldPosition, e);
                });
    }

    private Mono<String> placeBattlefield(GameStarted gameStarted, BattleshipPosition battlefieldPosition, int attempt) {
        log.warn("placeBattlefield - attempt: {}, game: {}, battlefieldPosition: {}", attempt, gameStarted, battlefieldPosition);
        return Mono
                .deferContextual(ctx -> {
                    Integer retriesLeft = ctx.getOrDefault("retriesLeft", maxAttempts - attempt);
                    if (retriesLeft > 0) {
                        return Mono.just(ctx);
                    }
                    return Mono.error(new IllegalStateException("no more retries"));
                })
                .flatMap(ctx -> http.placeBattleship(gameStarted.getTournamentId(),
                        gameStarted.getGameId(),
                        battlefieldPosition.getX(),
                        battlefieldPosition.getY(),
                        battlefieldPosition.getDirection())
                        .doOnNext(success -> {
                            brain.setCurrentPosition(battlefieldPosition);
                            log.info("placeBattlefield - successful attempt: {}, game: {}, battlefieldPosition: {}", attempt, gameStarted, battlefieldPosition);
                        }))
                .onErrorResume(e -> {
                    log.warn("placeBattlefield - failed attempt: {}, game: {}, battlefieldPosition: {}", attempt, gameStarted, battlefieldPosition);
                    BattleshipPosition newBattlefieldPosition = brain.getNextBattlefieldPosition();
                    return placeBattlefield(gameStarted, newBattlefieldPosition, attempt + 1);
                });
    }

    public void onGameEnded(GameEnded gameEnded) {
        log.info("onGameEnded - game: {}", gameEnded);
        brain.gameEnded(gameEnded);

    }

    public void onRoundStarted(RoundStarted roundStarted) {
        log.info("onRoundStarted - round: {}", roundStarted);

        shoot(roundStarted);

    }

    public void onRoundEnded(RoundEnded roundEnded) {
        log.info("onRoundEnded - round: {}", roundEnded);
        brain.observeReward(roundEnded);

    }


    void shoot(RoundStarted roundStarted) {

        ShotFired shotFired = new ShotFired();
        shotFired.setGameId(roundStarted.getGameId());
        shotFired.setTournamentId(roundStarted.getTournamentId());
        shotFired.setRoundNo(roundStarted.getRoundNo());

        Tuple2<Integer, Integer> xy = brain.getNextTargetPoint();
        shotFired.setX(xy.getT1());
        shotFired.setY(xy.getT2());

        kafka.shoot(shotFired)
                .addCallback(s -> log.info("ok"), e -> log.error("error", e));
    }

    MavenBrain getBrain() {
        return brain;
    }
}

package com.tech.challenge.maven.agent;

import com.tech.challenge.maven.agent.ai.RandomBattleshipPositionDecider;
import com.tech.challenge.maven.agent.ai.RandomBattleshipTargetDecider;
import com.tech.challenge.maven.config.MavenConfigurationProperties;
import com.tech.challenge.maven.http.MavenHttpClient;
import com.tech.challenge.maven.kafka.KafkaClient;
import com.tech.challenge.maven.kafka.events.*;
import com.tech.challenge.maven.model.BattleshipPosition;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class MavenAgent {
    private static final Logger log = getLogger(MavenAgent.class);
    private final MavenHttpClient http;
    private final KafkaClient kafka;
    private final MavenBrain brain;
    private final int maxAttempts = 10;
    private final RandomBattleshipPositionDecider battleshipPositionDecider;
    private final RandomBattleshipTargetDecider targetDecider;
    private final MavenConfigurationProperties properties;

    public MavenAgent(MavenHttpClient http, KafkaClient kafka, MavenConfigurationProperties properties,
                      RandomBattleshipPositionDecider battleshipPositionDecider, RandomBattleshipTargetDecider targetDecider) {
        this.http = http;
        this.kafka = kafka;
        this.battleshipPositionDecider = battleshipPositionDecider;
        this.properties = properties;
        this.targetDecider = targetDecider;

        brain = new MavenBrain();// newborn
    }

    public Mono<String> onGameStarted(GameStarted gameStarted) {
        log.info("onGameStarted - game: {}", gameStarted);
        if (properties.isIgnoreMessages() || !gameStarted.getTournamentId().equals(properties.getTournamentId()))
            return Mono.empty();
        brain.gameStarted(gameStarted);

        BattleshipPosition battlefieldPosition = battleshipPositionDecider.next(brain.getMemory());
        return placeBattlefield(gameStarted, battlefieldPosition, 0)
                .doOnError(e -> {
                    log.error("onGameStarted - failed to place battleship: {}", battlefieldPosition, e);
                });
    }

    private Mono<String> placeBattlefield(GameStarted gameStarted, BattleshipPosition battlefieldPosition, int attempt) {
        log.warn("placeBattlefield - attempt: {}, game: {}, battlefieldPosition: {}", attempt, gameStarted, battlefieldPosition);

        if (attempt > maxAttempts)
            throw new IllegalStateException("max attempts reached");

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
                            brain.rememberBattleshipPosition(battlefieldPosition);
                            log.info("placeBattlefield - successful attempt: {}, game: {}, battlefieldPosition: {}", attempt, gameStarted, battlefieldPosition);
                        }))
                .onErrorResume(e -> {
                    log.warn("placeBattlefield - failed attempt: {}, game: {}, battlefieldPosition: {}", attempt, gameStarted, battlefieldPosition);
                    brain.rememberBattleshipPositionFailedAttempt(battlefieldPosition);
                    // TODO verify this is caused by a valid position on the map, indicating there may be an opponent boat there.
                    BattleshipPosition newBattlefieldPosition = battleshipPositionDecider.next(brain.getMemory());
                    return placeBattlefield(gameStarted, newBattlefieldPosition, attempt + 1);
                });
    }

    public void onGameEnded(GameEnded gameEnded) {
        if (properties.isIgnoreMessages() || !gameEnded.getTournamentId().equals(properties.getTournamentId())
                || !gameEnded.getGameId().equals(brain.memory.currentGameId))
            return;
        log.info("onGameEnded - game: {}", gameEnded);
        brain.gameEnded(gameEnded);

    }

    public Mono<Void> onRoundStarted(RoundStarted roundStarted) {
        log.info("onRoundStarted - round: {}", roundStarted);
        if (properties.isIgnoreMessages() || !roundStarted.getTournamentId().equals(properties.getTournamentId())
                || !roundStarted.getGameId().equals(brain.memory.currentGameId))
            return Mono.empty();

        brain.roundStart(roundStarted);

        ShotFired shotFired = new ShotFired();
        shotFired.setGameId(roundStarted.getGameId());
        shotFired.setTournamentId(roundStarted.getTournamentId());
        shotFired.setRoundNo(roundStarted.getRoundNo());

        Shot xy = targetDecider.next(brain.getMemory());
        shotFired.setX(xy.getX());
        shotFired.setY(xy.getY());

        return kafka.shoot(shotFired)
                .doOnNext(r -> {
                    log.debug("shoot - round: {}, success: {}", roundStarted, shotFired);
                    brain.rememberShoot(xy.getX(), xy.getY());
                })
                .doOnError(e -> {
                    log.error("shoot - error, retry", e);
                    Shot retry = targetDecider.next(brain.getMemory());
                    shotFired.setX(retry.getX());
                    shotFired.setY(retry.getY());
                    kafka.shoot(shotFired)
                            .doOnNext(r -> {
                                log.debug("shoot - round: {}, success: {}", roundStarted, shotFired);
                                brain.rememberShoot(retry.getX(), retry.getY());
                            })
                            .doOnError(e2 -> {
                                log.error("shoot - error", e2);
                            });
                })
                .then();

    }

    public void onRoundEnded(RoundEnded roundEnded) {
        log.info("onRoundEnded - round: {}", roundEnded);
        if (properties.isIgnoreMessages() || !roundEnded.getTournamentId().equals(properties.getTournamentId())
                || !roundEnded.getGameId().equals(brain.memory.currentGameId))
            return;
        brain.roundEnded(roundEnded);
    }

    MavenBrain getBrain() {
        return brain;
    }
}

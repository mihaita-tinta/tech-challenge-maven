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

    public void onGameStarted(GameStarted gameStarted) {
        log.info("onGameStarted - game: {}", gameStarted);
        if (properties.isIgnoreMessages() || !gameStarted.getTournamentId().equals(properties.getTournamentId()))
            return;

        brain.gameStarted(gameStarted);

        placeBattlefield(gameStarted);
    }

    private void placeBattlefield(GameStarted gameStarted) {

        BattleshipPosition battlefieldPosition = battleshipPositionDecider.next(brain.getMemory());
        log.warn("placeBattlefield - attempt: {}, game: {}, battlefieldPosition: {}", gameStarted, battlefieldPosition);

        boolean done = false;
        int currentAttempt = 0;
        while (!done && currentAttempt < 10) { // quick and dirty
            try {
                placeInternalAttempt(gameStarted, battlefieldPosition, currentAttempt++);
                done = true;
            } catch (Exception e) {
                e.printStackTrace();
                done = false;
            }
        }
    }

    private void placeInternalAttempt(GameStarted gameStarted, BattleshipPosition battlefieldPosition, int attempt) {
        http.placeBattleship(gameStarted.getTournamentId(),
                gameStarted.getGameId(),
                battlefieldPosition.getX(),
                battlefieldPosition.getY(),
                battlefieldPosition.getDirection())
                .doOnNext(success -> {
                    brain.rememberBattleshipPosition(battlefieldPosition);
                    log.info("placeBattlefield - successful attempt: {}, game: {}, battlefieldPosition: {}", attempt, gameStarted, battlefieldPosition);
                }).block();

    }

    public void onGameEnded(GameEnded gameEnded) {
        if (properties.isIgnoreMessages() || !gameEnded.getTournamentId().equals(properties.getTournamentId())
                || !gameEnded.getGameId().equals(brain.memory.currentGameId))
            return;
        log.info("onGameEnded - game: {}", gameEnded);
        brain.gameEnded(gameEnded);

    }

    public Mono<Void> onRoundStarted(RoundStarted roundStarted) {
        if (properties.isIgnoreMessages() || !roundStarted.getTournamentId().equals(properties.getTournamentId())
                || !roundStarted.getGameId().equals(brain.memory.currentGameId))
            return Mono.empty();
        log.info("onRoundStarted - round: {}", roundStarted);

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
        if (properties.isIgnoreMessages() || !roundEnded.getTournamentId().equals(properties.getTournamentId())
                || !roundEnded.getGameId().equals(brain.memory.currentGameId))
            return;
        log.info("onRoundEnded - round: {}", roundEnded);
        brain.roundEnded(roundEnded);
    }

    MavenBrain getBrain() {
        return brain;
    }
}

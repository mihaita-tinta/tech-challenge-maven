package com.tech.challenge.maven.agent;

import com.tech.challenge.maven.config.MavenConfigurationProperties;
import com.tech.challenge.maven.http.MavenHttpClient;
import com.tech.challenge.maven.http.model.BattleshipRequestBody;
import com.tech.challenge.maven.kafka.KafkaClient;
import com.tech.challenge.maven.kafka.events.*;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import reactor.core.Exceptions;
import reactor.util.context.Context;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
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

    public void onGameStarted(GameStarted gameStarted) {
        log.info("onGameStarted - game: {}", gameStarted);
        brain.gameStarted(gameStarted);


        Retry customStrategy = Retry.from(companion -> companion.handle((retrySignal, sink) -> {
            Context ctx = sink.currentContext();
            int retriesLeft = ctx.getOrDefault("retriesLeft", maxAttempts);
            if (retriesLeft > 0) {
                sink.next(Context.of(
                        "retriesLeft", retriesLeft - 1,
                        "lastError", retrySignal.failure()
                ));
            } else {
                sink.error(Exceptions.retryExhausted("retries exhausted", retrySignal.failure()));
            }
        }));

        Tuple3<Integer, Integer, BattleshipRequestBody.Direction> battlefieldPosition = brain.getNextBattlefieldPosition();

        http.placeBattleship(gameStarted.getTournamentId(),
                gameStarted.getGameId(),
                battlefieldPosition.getT1(),
                battlefieldPosition.getT2(),
                battlefieldPosition.getT3())
                .doOnError(e -> {
                    log.error("onGameStarted - failed to place battleship: {}", battlefieldPosition, e);
                })
                .retryWhen(customStrategy)
                .subscribe();

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
}

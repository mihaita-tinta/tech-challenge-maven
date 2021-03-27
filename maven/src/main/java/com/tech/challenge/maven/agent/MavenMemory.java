package com.tech.challenge.maven.agent;

import com.tech.challenge.maven.http.model.BattleshipRequestBody;
import com.tech.challenge.maven.kafka.events.*;
import com.tech.challenge.maven.model.BattleshipPosition;
import lombok.Data;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class MavenMemory {

    int currentBattlegroundSize;
    BattleshipPosition currentPosition;
    BattleshipTemplate currentBattleshipTemplate;
    Cell core;
    Replay currentPlay;
    Shot currentShot;

    List<BattleshipPosition> failedPositionAttempts = new ArrayList<>();
    List<Replay> replays = new ArrayList<>();

    public void gameStarted(GameStarted gameStarted) {
        setCurrentBattlegroundSize(gameStarted.getBattlegroundSize());
        setCurrentBattleshipTemplate(gameStarted.getBattleshipTemplate());
        setCore(gameStarted.getCore());

        currentPlay = Replay.builder()
                        .battlegroundSize(gameStarted.getBattlegroundSize())
                        .template(gameStarted.getBattleshipTemplate())
                        .kills(new HashMap<>())
                        .miss(new HashMap<>())
                        .hits(new HashMap<>())
                        .build();
    }

    public void gameEnded(GameEnded gameEnded) {
        replays.add(currentPlay);
        failedPositionAttempts.clear();
    }

    public void roundEnded(RoundEnded roundEnded) {
        roundEnded.getShots()
                .forEach(shot -> {
                    if (shot.isMiss()) {
                        currentPlay.miss.putIfAbsent(shot, 1);
                    }
                    if (shot.isKill()) {
                        Integer existing = currentPlay.kills.getOrDefault(shot, 0);
                        currentPlay.kills.put(shot, existing + 1);
                    }
                    if (shot.isHit()) {
                        Integer existing = currentPlay.hits.getOrDefault(shot, 0);
                        currentPlay.hits.put(shot, existing + 1);
                    }

                    if (currentShot.getX() == shot.getX() && currentShot.getY() == shot.getY()) {
                        currentShot.setStatus(shot.getStatus());
                    }
                });
    }

    public void roundStart(RoundStarted roundStarted) {
        currentShot = Shot.builder()
                .build();
    }

    public void rememberBattleshipPositionFailedAttempt(BattleshipPosition battleshipPosition) {
        failedPositionAttempts.add(battleshipPosition);
    }

}

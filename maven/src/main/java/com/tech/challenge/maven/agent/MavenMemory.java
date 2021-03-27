package com.tech.challenge.maven.agent;

import com.tech.challenge.maven.http.model.BattleshipRequestBody;
import com.tech.challenge.maven.kafka.events.*;
import com.tech.challenge.maven.model.BattleshipPosition;
import lombok.Data;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class MavenMemory {

    int currentBattlegroundSize;
    BattleshipPosition currentPosition;
    BattleshipTemplate currentBattleshipTemplate;
    Replay currentPlay;
    Shot currentShot;

    List<Replay> replays = new ArrayList<>();

    public void gameStarted(GameStarted gameStarted) {
        setCurrentBattlegroundSize(gameStarted.getBattlegroundSize());
        setCurrentBattleshipTemplate(gameStarted.getBattleshipTemplate());

        currentPlay = Replay.builder()
                        .battlegroundSize(gameStarted.getBattlegroundSize())
                        .template(gameStarted.getBattleshipTemplate())
                        .myShots(new ArrayList<>())
                        .allShots(new ArrayList<>())
                        .build();
    }

    public void gameEnded(GameEnded gameEnded) {
        replays.add(currentPlay);
    }

    public void roundEnded(RoundEnded roundEnded) {
        currentPlay.allShots.addAll(
                roundEnded.getShots()
                .stream()
                .map(shot -> {
                    if (currentShot.getX() == shot.getX() && currentShot.getY() == shot.getY()) {
                        currentShot.setStatus(shot.getStatus());
                    }
                    return shot;
                })
                .collect(Collectors.toList())
        );
        currentPlay.myShots.add(currentShot);
    }

    public void roundStart(RoundStarted roundStarted) {

    }


}

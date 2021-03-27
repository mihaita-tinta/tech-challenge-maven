package com.tech.challenge.maven.agent;

import com.tech.challenge.maven.agent.ai.RandomBattleshipPositionDecider;
import com.tech.challenge.maven.http.model.BattleshipRequestBody;
import com.tech.challenge.maven.kafka.events.*;
import com.tech.challenge.maven.model.BattleshipPosition;
import lombok.Data;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.util.Random;

@Data
public class MavenBrain {

    final MavenMemory memory = new MavenMemory();

    public void roundEnded(RoundEnded roundEnded) {
        memory.roundEnded(roundEnded);
    }

    public void gameEnded(GameEnded gameEnded) {
        memory.gameEnded(gameEnded);
    }

    public void gameStarted(GameStarted gameStarted) {
        memory.gameStarted(gameStarted);
    }
    public void roundStart(RoundStarted roundStarted) {
        memory.roundStart(roundStarted);
    }

    public void rememberBattleshipPosition(BattleshipPosition battleshipPosition) {
        memory.setCurrentPosition(battleshipPosition);
    }
    public void rememberBattleshipPositionFailed(BattleshipPosition battleshipPosition) {
        memory.setCurrentPosition(battleshipPosition);
    }
    public void rememberBattleshipPositionFailedAttempt(BattleshipPosition battleshipPosition) {
        memory.rememberBattleshipPositionFailedAttempt(battleshipPosition);
    }

    public Tuple2<Integer, Integer> getNextTargetPoint() {

        int battlegroundSize = memory.getCurrentBattlegroundSize();
        BattleshipTemplate template = memory.getCurrentBattleshipTemplate();

        int x = 0;// TODO fun part here
        int y = 0;

        // TODO remember the action
        return Tuples.of(x, y);
    }
}

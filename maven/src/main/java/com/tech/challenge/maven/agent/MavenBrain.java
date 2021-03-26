package com.tech.challenge.maven.agent;

import com.tech.challenge.maven.http.model.BattleshipRequestBody;
import com.tech.challenge.maven.kafka.events.GameEnded;
import com.tech.challenge.maven.kafka.events.GameStarted;
import com.tech.challenge.maven.kafka.events.RoundEnded;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

public class MavenBrain {


    public void observeReward(RoundEnded roundEnded) {

    }

    public void gameEnded(GameEnded gameEnded) {

    }

    public void gameStarted(GameStarted gameStarted) {

    }


    public Tuple3<Integer, Integer, BattleshipRequestBody.Direction> getNextBattlefieldPosition() {

        int x = 0;// TODO fun part here
        int y = 0;
        BattleshipRequestBody.Direction direction = BattleshipRequestBody.Direction.EAST;

        return Tuples.of(x, y, direction);
    }
    public Tuple2<Integer, Integer> getNextTargetPoint() {

        int x = 0;// TODO fun part here
        int y = 0;

        // TODO remember the action
        return Tuples.of(x, y);
    }
}

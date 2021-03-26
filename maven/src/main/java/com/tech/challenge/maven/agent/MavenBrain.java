package com.tech.challenge.maven.agent;

import com.tech.challenge.maven.http.model.BattleshipRequestBody;
import com.tech.challenge.maven.kafka.events.BattleshipTemplate;
import com.tech.challenge.maven.kafka.events.GameEnded;
import com.tech.challenge.maven.kafka.events.GameStarted;
import com.tech.challenge.maven.kafka.events.RoundEnded;
import com.tech.challenge.maven.model.BattleshipPosition;
import lombok.Data;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

@Data
public class MavenBrain {

    int currentBattlegroundSize;
    BattleshipPosition currentPosition;


    public void observeReward(RoundEnded roundEnded) {

    }

    public void gameEnded(GameEnded gameEnded) {

    }

    public void gameStarted(GameStarted gameStarted) {
        currentBattlegroundSize = gameStarted.getBattlegroundSize();
        currentPosition = new BattleshipPosition();
        currentPosition.setCurrentBattleshipTemplate(gameStarted.getBattleshipTemplate());

    }


    public BattleshipPosition getNextBattlefieldPosition() {

        int x = currentPosition != null ? currentPosition.getX() + 1 : 0;// TODO fun part here
        int y = 0;
        BattleshipRequestBody.Direction direction = BattleshipRequestBody.Direction.EAST;

        currentPosition.setX(x);
        currentPosition.setY(y);
        currentPosition.setDirection(direction);

        return currentPosition;
    }
    public Tuple2<Integer, Integer> getNextTargetPoint() {

        int x = 0;// TODO fun part here
        int y = 0;

        // TODO remember the action
        return Tuples.of(x, y);
    }
}

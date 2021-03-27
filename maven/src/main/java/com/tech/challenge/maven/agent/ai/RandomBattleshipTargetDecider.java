package com.tech.challenge.maven.agent.ai;

import com.tech.challenge.maven.agent.MavenMemory;
import com.tech.challenge.maven.http.model.BattleshipRequestBody;
import com.tech.challenge.maven.kafka.events.BattleshipTemplate;
import com.tech.challenge.maven.kafka.events.Cell;
import com.tech.challenge.maven.kafka.events.Shot;
import com.tech.challenge.maven.model.BattleshipPosition;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.List;
import java.util.Random;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class RandomBattleshipTargetDecider {
    private static final Logger log = getLogger(RandomBattleshipTargetDecider.class);

    final Random random = new Random();
    final PositionCalculator positionCalculator = new PositionCalculator();

    public Shot next(MavenMemory memory) {
        long now = System.currentTimeMillis();
        int battlegroundSize = memory.getCurrentBattlegroundSize();
        BattleshipTemplate template = memory.getCurrentBattleshipTemplate();
        Cell core = memory.getCore();

        Tuple2<Integer, Integer> xy = getXY(memory, battlegroundSize);
        int x =  xy.getT1();
        int y =  xy.getT2();
        Shot newShot = Shot.builder()
                .x(x)
                .y(y)
                .build();
        while (memory.getCurrentPlay().getMiss().containsKey(newShot) ||
                memory.getCurrentPlay().getKills().containsKey(newShot)
                || memory.getCurrentPlay().getBattleship().containsKey(newShot)) {

            xy = getXY(memory, battlegroundSize);
            newShot.setX(xy.getT1());
            newShot.setY(xy.getT2());
        }



        // random
        // not miss
        // not our ship
        // if core.getHp() > 1 try also hits
        // if core.getHp() > 1 and there is a shot with hits < core.getHp()



        // TODO remember the action
        log.info("next took: {} ms", (now - System.currentTimeMillis()));
        return newShot;
    }

    private Tuple2<Integer, Integer> getXY(MavenMemory memory, int battlegroundSize) {


        Shot possibleHead = memory.getCurrentPlay().getPossibleHead(memory.getCore(), random);
        if (possibleHead != null) {
            return Tuples.of(
                    possibleHead.getX(),
                    possibleHead.getY());
        }

        Shot previousHit = memory.getCurrentPlay().getRandomShotFromHits(random);

        if (previousHit != null) {

            int xOffset = random.nextInt(memory.getCurrentBattleshipTemplate().getWidth());
            int yOffset = random.nextInt(memory.getCurrentBattleshipTemplate().getHeight());
            int signX = random.nextBoolean() ? 1 : -1;
            int signY = random.nextBoolean() ? 1 : -1;

//            List<Shot> shots = guessEnemy(battlegroundSize, memory, previousHit.getX(), previousHit.getY());
            // TODO
            return Tuples.of(
                    signX * xOffset + previousHit.getX() % battlegroundSize,
                    signY * yOffset + previousHit.getY() % battlegroundSize);

        }

        return Tuples.of(random.nextInt(battlegroundSize), random.nextInt(battlegroundSize));
    }

    public List<Shot> guessEnemy(int battlegroundSize, MavenMemory memory, int x, int y) {

        return positionCalculator.getPoints(battlegroundSize, memory.getCurrentBattleshipTemplate(),
                x,
                y,
                BattleshipRequestBody.Direction.values()[random.nextInt(BattleshipRequestBody.Direction.values().length)]);
    }
}

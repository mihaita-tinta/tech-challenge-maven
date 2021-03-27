package com.tech.challenge.maven.agent.ai;

import com.tech.challenge.maven.agent.MavenMemory;
import com.tech.challenge.maven.http.model.BattleshipRequestBody;
import com.tech.challenge.maven.kafka.events.BattleshipTemplate;
import com.tech.challenge.maven.kafka.events.Cell;
import com.tech.challenge.maven.kafka.events.Shot;
import com.tech.challenge.maven.model.BattleshipPosition;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Random;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class RandomBattleshipTargetDecider {
    private static final Logger log = getLogger(RandomBattleshipTargetDecider.class);

    final Random random = new Random();


    public Shot next(MavenMemory memory) {

        int battlegroundSize = memory.getCurrentBattlegroundSize();
        BattleshipTemplate template = memory.getCurrentBattleshipTemplate();
        Cell core = memory.getCore();

        int x = random.nextInt(battlegroundSize);
        int y = random.nextInt(battlegroundSize);
        Shot newShot = Shot.builder()
                .x(x)
                .y(y)
                .build();
        while (memory.getCurrentPlay().getMiss().containsKey(newShot)
                || memory.getCurrentPlay().getBattleship().containsKey(newShot)) {
            x = random.nextInt(battlegroundSize);
            y = random.nextInt(battlegroundSize);
            newShot.setX(x);
            newShot.setY(y);
        }

        // random
        // not miss
        // not our ship
        // if core.getHp() > 1 try also hits
        // if core.getHp() > 1 and there is a shot with hits < core.getHp()



        // TODO remember the action
        return newShot;
    }



    public BattleshipPosition internal(int battlegroundSize, BattleshipTemplate template) {
        BattleshipPosition newPosition = new BattleshipPosition();

        BattleshipRequestBody.Direction[] possibleDirections = BattleshipRequestBody.Direction.values();
        int directionIndex = random.nextInt(possibleDirections.length);

        BattleshipRequestBody.Direction possibleDirection = possibleDirections[directionIndex];
        newPosition.setDirection(possibleDirection);
        int boundX = 1 + battlegroundSize - possibleDirection.getXLimit(template.getWidth(), template.getHeight());
        newPosition.setX(random.nextInt(boundX));
        int boundY = 1 + battlegroundSize - possibleDirection.getYLimit(template.getWidth(), template.getHeight());
        newPosition.setY(random.nextInt(boundY));
        newPosition.setTemplate(template);

        log.debug("internal - new position: {}", newPosition);
        return newPosition;
    }
}

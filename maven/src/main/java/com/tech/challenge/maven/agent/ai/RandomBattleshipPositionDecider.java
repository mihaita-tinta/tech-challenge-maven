package com.tech.challenge.maven.agent.ai;

import com.tech.challenge.maven.agent.MavenMemory;
import com.tech.challenge.maven.http.model.BattleshipRequestBody;
import com.tech.challenge.maven.kafka.events.BattleshipTemplate;
import com.tech.challenge.maven.model.BattleshipPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Random;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class RandomBattleshipPositionDecider {
    private static final Logger log = getLogger(RandomBattleshipPositionDecider.class);

    final Random random = new Random();


    /**
     * Decision maker for the battleship position
     *
     * @return
     */
    public BattleshipPosition next(MavenMemory memory) {

        return internal(memory.getCurrentBattlegroundSize(), memory.getCurrentBattleshipTemplate());
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

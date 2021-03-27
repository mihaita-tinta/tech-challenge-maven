package com.tech.challenge.maven.agent;

import com.tech.challenge.maven.kafka.events.BattleshipTemplate;
import com.tech.challenge.maven.kafka.events.Cell;
import com.tech.challenge.maven.kafka.events.Shot;
import com.tech.challenge.maven.model.BattleshipPosition;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

@Data
@Builder
public class Replay {

    int battlegroundSize;
    BattleshipTemplate template;
    BattleshipPosition position;

    Map<Shot, Integer> hits;
    Map<Shot, Integer> kills;
    Map<Shot, Integer> miss;

    Map<Shot, Integer> battleship;


    public Shot getRandomShotFromHits(Random random) {
        if (hits.isEmpty())
            return null;

        int stop = random.nextInt(hits.size());
        int i = 0;
        for (Map.Entry<Shot, Integer> entry : hits.entrySet()) {
            if (i == stop) {
                return entry.getKey();
            }
            i++;
        }
        return null;
    }
    public Shot getPossibleHead(Cell core, Random random) {
        if (hits.isEmpty())
            return null;

        int stop = random.nextInt(hits.size());
        int i = 0;
        Shot possible = null;
        for (Map.Entry<Shot, Integer> entry : hits.entrySet()) {
            if (entry.getValue() > 1 && entry.getValue() < core.getHp()) {
                possible = entry.getKey();
            }

            if (stop == i)
                break;
            i++;
        }
        return possible;
    }

}

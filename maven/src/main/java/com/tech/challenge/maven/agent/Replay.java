package com.tech.challenge.maven.agent;

import com.tech.challenge.maven.kafka.events.BattleshipTemplate;
import com.tech.challenge.maven.kafka.events.Shot;
import com.tech.challenge.maven.model.BattleshipPosition;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
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

}

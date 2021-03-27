package com.tech.challenge.maven.agent;

import com.tech.challenge.maven.kafka.events.BattleshipTemplate;
import com.tech.challenge.maven.kafka.events.Shot;
import com.tech.challenge.maven.model.BattleshipPosition;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Replay {

    int battlegroundSize;
    BattleshipTemplate template;
    BattleshipPosition position;

    List<Shot> myShots;
    List<Shot> allShots;
}

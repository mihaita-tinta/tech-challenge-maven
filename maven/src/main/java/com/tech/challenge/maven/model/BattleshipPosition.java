package com.tech.challenge.maven.model;

import com.tech.challenge.maven.http.model.BattleshipRequestBody;
import com.tech.challenge.maven.kafka.events.BattleshipTemplate;
import lombok.Data;

@Data
public class BattleshipPosition {
    private BattleshipTemplate currentBattleshipTemplate;
    private int x;
    private int y;
    private BattleshipRequestBody.Direction direction;
}

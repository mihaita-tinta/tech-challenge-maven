package com.tech.challenge.maven.agent.ai;

import com.tech.challenge.maven.kafka.events.BattleshipTemplate;
import com.tech.challenge.maven.model.BattleshipPosition;

public class WorldHelper {

    public static void print(int battlegroundSize, BattleshipPosition position) {

        // TODO not finished...maybe we use the ui for testing this
        StringBuilder sb = new StringBuilder("\n-------- Map ---------\n");
        for (int x = 0; x < battlegroundSize; x++) {
            for (int y = 0; y < battlegroundSize; y++) {
                position.render(x, y);

            }
            sb.append("\n");
        }
        sb.append("\n--------------------");
        System.out.println(sb.toString());
    }

}

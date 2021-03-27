package com.tech.challenge.maven.agent.ai;

import com.tech.challenge.maven.http.model.BattleshipRequestBody;
import com.tech.challenge.maven.kafka.events.BattleshipTemplate;
import com.tech.challenge.maven.kafka.events.Cell;
import com.tech.challenge.maven.kafka.events.Shot;

import java.util.ArrayList;
import java.util.List;

public class PositionCalculator {

    public List<Shot> getPoints(int size, BattleshipTemplate template, int x, int y, BattleshipRequestBody.Direction direction) {

        List<Shot> points = new ArrayList<>();

        for (int row =0 ; row < size; row++ ) {
            for (int column =0 ; column < size; column++ ) {

                int templateWidth = template.getWidth();
                int templateHeight = template.getHeight();

                switch (direction) {
                    case SOUTH:
                    case NORTH: {
                        if (column >= x && column < x + templateWidth &&
                                row >= y && row < y + templateHeight) {
                            int offset = direction == BattleshipRequestBody.Direction.SOUTH ? templateHeight - 1 - row + y : row - y;
                            Cell cell = template.getCanvas().get(offset).get(column - x);
                            if (!cell.isEmpty()) {
                                points.add(Shot.builder()
                                        .x(column)
                                        .y(row)
                                        .build());
                            }
                        }

                        break;
                    }
                    case WEST:
                    case EAST:
                    default: {
                        if (column >= x && column < x + templateHeight &&
                                row >= y && row < y + templateWidth) {
                            int offset = direction == BattleshipRequestBody.Direction.EAST ? templateHeight - 1 - column + x : column - x;
                            Cell cell = template.getCanvas().get(offset).get(row - y);
                            if (!cell.isEmpty()) {
                                points.add(Shot.builder()
                                        .x(column)
                                        .y(row)
                                        .build());
                            }
                        }

                    }
                }
            }
        }
        return points;
    }
}

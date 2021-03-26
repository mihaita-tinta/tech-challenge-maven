package com.tech.challenge.maven.http.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * gameId:
 *  description: Id of the game. Format => {tournamentId}-{inc}
 *  type: string
 *  example: tournment1-0
 *  x:
 *  description: X coordinate
 *  type: integer
 *  example: 55
 *  y:
 *  description: Y coordinate
 *  type: integer
 *  example: 20
 *  direction:
 *  type: string
 *  enum:
 *  - EAST
 *  - WEST
 *  - SOUTH
 *  - NORTH
 */
@Data
@AllArgsConstructor
public class BattleshipRequestBody {
    private String gameId;
    private int x;
    private int y;
    private Direction direction;

    public enum Direction {
        EAST,
        WEST,
        SOUTH,
        NORTH
    }
}

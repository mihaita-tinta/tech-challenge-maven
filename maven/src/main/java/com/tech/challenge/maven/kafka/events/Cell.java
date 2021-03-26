package com.tech.challenge.maven.kafka.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cell {
    private String symbol;
    private int hp;

    public boolean isHead() {
        return "*".equals(symbol);
    }

    public boolean isBody() {
        return "+".equals(symbol);
    }

    public boolean isEmpty() {
        return " ".equals(symbol);
    }
}


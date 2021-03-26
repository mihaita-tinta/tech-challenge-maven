package com.tech.challenge.maven.kafka.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shot {
    private String status;
    private int x;
    private int y;

    public boolean isMiss() {
        return "MISS".equals(status);
    }

    public boolean isKill() {
        return "KILL".equals(status);
    }

    public boolean isEmpty() {
        return "HIT".equals(status);
    }
}


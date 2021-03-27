package com.tech.challenge.maven.kafka.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

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

    public boolean isHit() {
        return "HIT".equals(status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shot shot = (Shot) o;
        return x == shot.x &&
                y == shot.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}


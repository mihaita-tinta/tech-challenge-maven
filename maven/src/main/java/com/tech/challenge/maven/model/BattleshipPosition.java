package com.tech.challenge.maven.model;

import com.tech.challenge.maven.http.model.BattleshipRequestBody;
import com.tech.challenge.maven.kafka.events.BattleshipTemplate;
import lombok.Data;

import java.awt.*;

@Data
public class BattleshipPosition {
    private int x;
    private int y;
    private BattleshipRequestBody.Direction direction;
    private BattleshipTemplate template;


    public String render(int x, int y) {

        Rectangle rectangle = new Rectangle(template.getWidth(), template.getHeight());
        rectangle.setLocation(this.x, this.y);
        if (rectangle.contains(new Point(x, y))) {
            return template.getCanvas()
                    .get(y - this.y)
                    .get(x - this.x)
                    .getSymbol();
        }

        return "O";
    }
//
//    public boolean overlaps (Rectangle r) {
//
//        Rectangle rectangle = new Rectangle(0, 0);
//        rectangle.contains(new Point(0, 0));
//        return x < r.x + r.width && x + width > r.x && y < r.y + r.height && y + height > r.y;
//    }
}

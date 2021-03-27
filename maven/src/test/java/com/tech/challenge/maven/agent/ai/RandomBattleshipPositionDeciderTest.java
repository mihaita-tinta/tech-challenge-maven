package com.tech.challenge.maven.agent.ai;

import com.tech.challenge.maven.agent.MavenAgent;
import com.tech.challenge.maven.agent.MavenMemory;
import com.tech.challenge.maven.http.MavenHttpClient;
import com.tech.challenge.maven.http.model.BattleshipRequestBody;
import com.tech.challenge.maven.kafka.events.BattleshipTemplate;
import com.tech.challenge.maven.kafka.events.Cell;
import com.tech.challenge.maven.model.BattleshipPosition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.Arrays;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RandomBattleshipPositionDeciderTest {
    private static final Logger log = LoggerFactory.getLogger(RandomBattleshipPositionDeciderTest.class);

    @InjectMocks
    RandomBattleshipPositionDecider decider;
    @Mock
    MavenMemory memory;

    @Test
    public void test() {
        int size = 17;
        BattleshipTemplate template = new BattleshipTemplate();
        template.setHeight(2);
        template.setWidth(3);
        template.setCanvas(asList(
                asList(
                        Cell.builder().symbol(" ").hp(0).build(),
                        Cell.builder().symbol("*").hp(1).build(),
                        Cell.builder().symbol(" ").hp(0).build()
                ),
                asList(
                        Cell.builder().symbol("+").hp(1).build(),
                        Cell.builder().symbol(" ").hp(0).build(),
                        Cell.builder().symbol("+").hp(1).build()
                )
                )
        );
        Mockito.when(memory.getCurrentBattlegroundSize())
                .thenReturn(size);
        Mockito.when(memory.getCurrentBattleshipTemplate())
                .thenReturn(template);


        Rectangle screen = new Rectangle(size, size);

        IntStream.range(0, 100)
                .mapToObj(i -> decider.next(memory))
                .forEach(pos -> {
                    assertTrue(screen.contains(pos.getX(), pos.getY()));
                });

    }


    @Test
    public void testGetBattleshipPoints() {
        int size = 5;
        BattleshipTemplate template = new BattleshipTemplate();
        template.setHeight(2);
        template.setWidth(3);
        template.setCanvas(asList(
                asList(
                        Cell.builder().symbol(" ").hp(0).build(),
                        Cell.builder().symbol("*").hp(1).build(),
                        Cell.builder().symbol(" ").hp(0).build()
                ),
                asList(
                        Cell.builder().symbol("+").hp(1).build(),
                        Cell.builder().symbol(" ").hp(0).build(),
                        Cell.builder().symbol("+").hp(1).build()
                )
                )
        );
        Mockito.when(memory.getCurrentBattlegroundSize())
                .thenReturn(size);
        Mockito.when(memory.getCurrentBattleshipTemplate())
                .thenReturn(template);
        BattleshipPosition next = decider.next(memory);
        next.setX(0);
        next.setY(4);
        next.setDirection(BattleshipRequestBody.Direction.WEST);
        for (int row =0 ; row < size; row++ ) {

            for (int column =0 ; column < size; column++ ) {

                int templateWidth = next.getTemplate().getWidth();
                int templateHeight = next.getTemplate().getHeight();
                int x = next.getX();
                int y = next.getY();

                switch (next.getDirection()) {
                    case SOUTH:
                    case NORTH: {
                        if (column >= x && column < x + templateWidth &&
                                row >= y && row < y + templateHeight) {
                            int offset = next.getDirection() == BattleshipRequestBody.Direction.SOUTH ? templateHeight - 1 - row + y : row - y;
                            System.out.print(
                                    template.getCanvas().get(offset).get(column - x)
                                            .getSymbol());
                        }
                        else {
                            System.out.print("O");
                        }
                        break;
                    }
                    case WEST:
                    case EAST:
                    default: {
                        if (column >= x && column < x + templateHeight &&
                                row >= y && row < y + templateWidth) {
//                            int templateRowPos = column - x;

                            int offset = next.getDirection() == BattleshipRequestBody.Direction.EAST ? templateHeight - 1 - column + x : column - x;

//                            int offset =  templateHeight - 1;
//                            templateRowPos = offset - templateRowPos;
                            System.out.print(
                                    template.getCanvas().get(offset).get(row - y)
                                            .getSymbol());
                        }
                        else {
                            System.out.print("O");
                        }
                    }
                }
                System.out.print(" ");
            }
            System.out.println(" ");
        }

        new PositionCalculator()
                .getPoints(size, next.getTemplate(), next.getX(), next.getY(), next.getDirection())
                .forEach(System.out :: println);
    }
}

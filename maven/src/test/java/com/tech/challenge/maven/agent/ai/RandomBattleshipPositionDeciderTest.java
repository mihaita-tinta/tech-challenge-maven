package com.tech.challenge.maven.agent.ai;

import com.tech.challenge.maven.agent.MavenAgent;
import com.tech.challenge.maven.agent.MavenMemory;
import com.tech.challenge.maven.http.MavenHttpClient;
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
}

package com.tech.challenge.maven.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.challenge.maven.kafka.events.RoundEnded;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KafkaConfigTest {

    @Test
    public void test() throws JsonProcessingException {
        String input = "{\"gameId\":\"-9XJrm5hUl\",\"tournamentId\":\"-9XJrm5hUl-0\",\"roundNo\":3,\"shots\":[]}";
        ObjectMapper mapper = new ObjectMapper();
        RoundEnded roundEnded = mapper.readValue(input, RoundEnded.class);
        assertNotNull(roundEnded.getGameId());

    }

}

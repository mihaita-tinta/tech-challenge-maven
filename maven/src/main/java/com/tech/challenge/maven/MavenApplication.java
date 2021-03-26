package com.tech.challenge.maven;

import com.tech.challenge.maven.http.MavenHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class MavenApplication {

	@Autowired
	MavenHttpClient http;

	public static void main(String[] args) {
		SpringApplication.run(MavenApplication.class, args);
	}

	@Bean
	public ApplicationRunner runner() {
		return args -> {
			String tournamentId = args.getOptionValues("tournamentId").get(0);
			http.login()
					.flatMap(r -> http.registerTournament(tournamentId))
					.subscribe();
		};
	}
}

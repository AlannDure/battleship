package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;
import java.util.Arrays;


@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository, SalvoRepository salvoRepository, ScoreRepository scoreRepository ) {
		return (args) -> {

					/*
				Player PlayerOne = new Player("j.bauer@ctu.gov", passwordEncoder().encode("24"));
				playerRepository.save(PlayerOne);
				Player PlayerTwo = new Player("c.obrian@ctu.gov", passwordEncoder().encode("42"));
				playerRepository.save(PlayerTwo);
				Player PlayerThree = new Player("kim_bauer@gmail.com", passwordEncoder().encode("kb"));
				playerRepository.save(PlayerThree);
				Player PlayerFour = new Player("t.almeida@ctu.gov", passwordEncoder().encode("mole"));
				playerRepository.save(PlayerFour);
				Player PlayerFive = new Player("alan.admin@gmail.com", passwordEncoder().encode("admin"));
				playerRepository.save(PlayerFive);


				Game GameOne = new  Game();
				gameRepository.save(GameOne);
				Game GameTwo = new Game(LocalDateTime.now().plusHours(1));
				gameRepository.save(GameTwo);
				Game GameThree = new Game(LocalDateTime.now().plusHours(2));
				gameRepository.save(GameThree);

				GamePlayer GamePlayerOne = new GamePlayer (GameOne,PlayerOne);
				gamePlayerRepository.save(GamePlayerOne);
				GamePlayer GamePlayerTwo = new GamePlayer (GameOne,PlayerTwo);
				gamePlayerRepository.save(GamePlayerTwo);
				GamePlayer GamePlayerThree = new GamePlayer (GameTwo,PlayerThree);
				gamePlayerRepository.save(GamePlayerThree);
				GamePlayer GamePlayerFour = new GamePlayer (GameTwo, PlayerFour);
				gamePlayerRepository.save(GamePlayerFour);
				GamePlayer GamePlayerFive = new GamePlayer (GameThree,PlayerOne);
				gamePlayerRepository.save(GamePlayerFive);

				Ship shipOne = new Ship("Destroyer", Arrays.asList("A1","A2","A3"));
				GamePlayerOne.addShip(shipOne);
				Ship shipTwo = new Ship("Submarine", Arrays.asList("A4","A5","A6"));
				GamePlayerOne.addShip(shipTwo);
				gamePlayerRepository.save(GamePlayerOne);

				Ship shipThree = new Ship("Destroyer", Arrays.asList("B1","B2","B3"));
				GamePlayerTwo.addShip(shipThree);
				Ship shipFour = new Ship("Submarine", Arrays.asList("B4","B5","B6"));
				GamePlayerTwo.addShip(shipFour);
				gamePlayerRepository.save(GamePlayerTwo);

				Ship shipFive = new Ship("Destroyer", Arrays.asList("C1","C2","C3"));
				GamePlayerThree.addShip(shipFive);
				Ship shipSix = new Ship("Submarine", Arrays.asList("C4","C5","C6"));
				GamePlayerThree.addShip(shipSix);
				gamePlayerRepository.save(GamePlayerThree);

				Salvo SalvoOne = new Salvo (1,GamePlayerOne,Arrays.asList("B5","C5","F1"));
				salvoRepository.save(SalvoOne);
				Salvo SalvoTwo = new Salvo(1, GamePlayerTwo,Arrays.asList("B4","B5","B6"));
				salvoRepository.save(SalvoTwo);
				Salvo SalvoThree = new Salvo (2,GamePlayerOne,Arrays.asList("F2","D5"));
				salvoRepository.save(SalvoThree);
				Salvo SalvoFour = new Salvo(2,GamePlayerTwo, Arrays.asList("E1","H3","A2"));
				salvoRepository.save(SalvoFour);
				Salvo SalvoFive = new Salvo(1,GamePlayerThree,Arrays.asList("G6","H6","A4"));
				salvoRepository.save(SalvoFive);
				Salvo SalvoSix = new Salvo(1,GamePlayerFour,Arrays.asList("H1","H2","H3"));
				salvoRepository.save(SalvoSix);
				Salvo SalvoSeven = new Salvo(2,GamePlayerThree, Arrays.asList("E1", "F2", "G3"));
				salvoRepository.save(SalvoSeven);
				Salvo SalvoEight = new Salvo(2,GamePlayerFour,Arrays.asList("B5", "C6", "H1"));
				salvoRepository.save(SalvoEight);
				Salvo SalvoNine = new Salvo(1,GamePlayerFive,Arrays.asList("C6","C7"));
				salvoRepository.save(SalvoNine);

				Score ScoreOne = new Score(1,LocalDateTime.now().plusHours(1),GameOne,PlayerOne);
				Score ScoreTwo = new Score(0,LocalDateTime.now().plusHours(1),GameOne,PlayerTwo);
				scoreRepository.save(ScoreOne);
				scoreRepository.save(ScoreTwo);
				Score ScoreThree = new Score(0.5,LocalDateTime.now().plusHours(2),GameTwo,PlayerThree);
				Score ScoreFour = new Score(0.5,LocalDateTime.now().plusHours(2),GameTwo,PlayerFour);
				scoreRepository.save(ScoreThree);
				scoreRepository.save(ScoreFour);
				Score ScoreFive = new Score(1,LocalDateTime.now().plusHours(3),GameThree,PlayerOne);
				scoreRepository.save(ScoreFive);


				*/











		};
	}

}


package com.mindhubweb.salvo;

import com.mindhubweb.salvo.Models.*;
import com.mindhubweb.salvo.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Autowired
	private PasswordEncoder passwordEncoder;

	//@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository ,
									  GameRepository gameRepository ,
									  GamePlayerRepository gamePlayerRepository ,
									  ShipRepository shipRepository,
									  SalvoRepository salvoRepository,
									  ScoreRepository scoreRepository) {
		return (args) -> {
			Player player1 = playerRepository.save(new Player("j.bauer@ctu.gov", passwordEncoder.encode ("24")));
			Player player2 = playerRepository.save(new Player("c.obrian@ctu.gov",passwordEncoder.encode ("42")));
			Player player3 = playerRepository.save(new Player("kim_bauer@gmail.com ", passwordEncoder.encode ("kb")));
			Player player4 = playerRepository.save(new Player("t.almeida@ctu.gov", passwordEncoder.encode ("mole")));

			LocalDateTime newDate = LocalDateTime.now();
			Game game1 = gameRepository.save(new Game( newDate ));

			newDate = newDate.plusHours(1);
			Game game2 = gameRepository.save(new Game( newDate ));

			newDate = newDate.plusMinutes(60);
			Game game3 = gameRepository.save(new Game( newDate ));

			newDate = newDate.plusMinutes(60);
			Game game4 = gameRepository.save(new Game( newDate ));
			newDate = newDate.plusMinutes(60);
			Game game5 = gameRepository.save(new Game( newDate ));



			GamePlayer gamePlayer1 = gamePlayerRepository.save(new GamePlayer(game1,player1));
			GamePlayer gamePlayer2 = gamePlayerRepository.save(new GamePlayer(game1,player2));
			GamePlayer gamePlayer3 = gamePlayerRepository.save(new GamePlayer(game2,player3));
			GamePlayer gamePlayer4 = gamePlayerRepository.save(new GamePlayer(game3,player3));
			GamePlayer gamePlayer5 = gamePlayerRepository.save(new GamePlayer(game2,player4));
			GamePlayer gamePlayer6 = gamePlayerRepository.save(new GamePlayer(game3,player1));
			GamePlayer gamePlayer7 = gamePlayerRepository.save(new GamePlayer(game4,player4));
			GamePlayer gamePlayer8 = gamePlayerRepository.save(new GamePlayer(game4,player1));
			GamePlayer gamePlayer9 = gamePlayerRepository.save(new GamePlayer(game5,player2));
			GamePlayer gamePlayer10 = gamePlayerRepository.save(new GamePlayer(game5,player1));



			Ship ship1 = shipRepository.save(new Ship ("Carrier", Arrays.asList("B5","C5","D5"),gamePlayer1));
			Ship ship2 = shipRepository.save(new Ship ("Battleship",Arrays.asList("B6","C6","D6"),gamePlayer1));
			Ship ship3 = shipRepository.save(new Ship ("Submarine",Arrays.asList("C6","C7"),gamePlayer1));
			Ship ship4 = shipRepository.save(new Ship ("Destroyer",Arrays.asList("A2","A3","A4"),gamePlayer1));
			Ship ship5 = shipRepository.save(new Ship ("Patrol Boat",Arrays.asList("G6","H6"),gamePlayer1));
			Ship ship6 = shipRepository.save(new Ship ("Carrier", Arrays.asList("B1","C1","D1"),gamePlayer2));
			Ship ship7 = shipRepository.save(new Ship ("Battleship",Arrays.asList("B4","C4","D4"),gamePlayer2));
			Ship ship8 = shipRepository.save(new Ship ("Submarine",Arrays.asList("C2","C3"),gamePlayer2));
			Ship ship9 = shipRepository.save(new Ship ("Destroyer",Arrays.asList("A1","A2","A3"),gamePlayer2));
			Ship ship10 = shipRepository.save(new Ship ("Patrol Boat",Arrays.asList("G5","H5"),gamePlayer2));

			Salvo salvo1 = salvoRepository.save(new Salvo (1, Arrays.asList("C3","B6","G2"), gamePlayer1));
			Salvo salvo2 = salvoRepository.save(new Salvo (1, Arrays.asList("A1","F4","B1"), gamePlayer2));
			Salvo salvo3 = salvoRepository.save(new Salvo (2, Arrays.asList("E3","D2","C6"), gamePlayer1));
			Salvo salvo4 = salvoRepository.save(new Salvo (2, Arrays.asList("G5","H6","H5"), gamePlayer2));
            Salvo salvo5 = salvoRepository.save(new Salvo (3, Arrays.asList("B4","C4","D4"), gamePlayer1));
            Salvo salvo6 = salvoRepository.save(new Salvo (3, Arrays.asList("C6","C7","H6"), gamePlayer2));

			LocalDateTime finishGameDate = LocalDateTime.now();

			Score score1 = scoreRepository.save(new Score(1.0,(finishGameDate),player1,game1));
			Score score2 = scoreRepository.save(new Score(0.0,(finishGameDate),player2,game1));
			Score score3 = scoreRepository.save(new Score(0.5,(finishGameDate),player3,game2));
			Score score4 = scoreRepository.save(new Score(0.5,(finishGameDate),player4,game2));
			Score score5 = scoreRepository.save(new Score(0.5,(finishGameDate),player1,game4));
			Score score6 = scoreRepository.save(new Score(0.5,(finishGameDate),player4,game4));
			Score score7 = scoreRepository.save(new Score(0.0,(finishGameDate),player2,game5));
			Score score8 = scoreRepository.save(new Score(1.0,(finishGameDate),player1,game5));
			Score score9 = scoreRepository.save(new Score(1.0,(finishGameDate),player1,game3));
			Score score10 = scoreRepository.save(new Score(0.0,(finishGameDate),player3,game3));



		};
	}
}
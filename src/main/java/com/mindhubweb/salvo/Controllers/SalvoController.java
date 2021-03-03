package com.mindhubweb.salvo.Controllers;

import com.mindhubweb.salvo.Models.*;
import com.mindhubweb.salvo.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private ShipRepository shipRepository;

    @Autowired
    private SalvoRepository salvoRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ScoreRepository scoreRepository;

    @RequestMapping("/games")
    public Map<String, Object> getGames(Authentication authentication) {

        Map<String, Object> dto = new LinkedHashMap<String, Object>();


        if (isGuest(authentication)) {
            dto.put("player", null);
        } else {
            Player player = playerRepository.findByUserName(authentication.getName());
            dto.put("player", playerDTO(player));
        }

        dto.put("games", gameRepository
                .findAll()
                .stream()
                .map(this::makeGameDTO)
                .collect(toList()));


        return dto;

    }

    @PostMapping("players")
    public ResponseEntity<Object> register(@RequestParam String username, @RequestParam String password) {

        if (username.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }

        if (playerRepository.findByUserName(username) != null) {
            return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
        }

        playerRepository.save(new Player(username, passwordEncoder.encode(password)));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping("/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> getGameView(@PathVariable long gamePlayerId, Authentication authentication){
        Player player = playerRepository.findByUserName(authentication.getName());
        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).get();
        if(gamePlayer.getPlayer().getId() == player.getId()){
            return new ResponseEntity<>(makeGamePlayerView(gamePlayer), HttpStatus.ACCEPTED);
        }
        else{
            return new ResponseEntity<>(makeMap("error", "Access denied"),HttpStatus.UNAUTHORIZED);
        }
    }

    // Creacion de nuevo juego "createGame"
    @PostMapping("/games")
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication){
        ResponseEntity<Map<String, Object>> response;
        if(isGuest(authentication)){
            response = new ResponseEntity<>(makeMap("error", "Unauthenticated user"), HttpStatus.FORBIDDEN);
        } else {
            Player player = playerRepository.findByUserName(authentication.getName());
            Game newGame = gameRepository.save(new Game());
            GamePlayer newGamePlayer = gamePlayerRepository.save(new GamePlayer(newGame,player));

            response = new ResponseEntity<>(makeMap("gpId", newGamePlayer.getId()), HttpStatus.CREATED);
        }
        return response;
    }
    //Unirme a un juego "joinGame"
    @PostMapping("/game/{gameId}/players")
    public ResponseEntity<Map<String, Object>> joinGame(Authentication authentication, @PathVariable long gameId){
        ResponseEntity<Map<String, Object>> response;
        if(isGuest(authentication)) {
            response = new ResponseEntity<>(makeMap("error", "Unauthenticated user"), HttpStatus.UNAUTHORIZED);
        }else {
            Optional<Game> game = gameRepository.findById(gameId);
            if(game.isEmpty()){
                response = new ResponseEntity<>(makeMap("error", "Game don't exist"), HttpStatus.NOT_FOUND);
            }else if(game.get().getGamePlayers().size() == 2){
                return new ResponseEntity<>(makeMap("error", "Game full"), HttpStatus.FORBIDDEN);
            }else{
                Player player = playerRepository.findByUserName(authentication.getName());
                if (! game.get().getPlayers().contains(player)){
                //if(game.get().getGamePlayers().stream().findAny().get().getPlayer().getId() == player.getId())
                    GamePlayer newGamePlayer = gamePlayerRepository.save(new GamePlayer(game.get(), player));
                    response = new ResponseEntity<>(makeMap("gpId", newGamePlayer.getId()), HttpStatus.ACCEPTED);
                }else{
                    response = new ResponseEntity<>(makeMap("error", "can't play with yourself!"), HttpStatus.FORBIDDEN);
                }

            }
        }
        return response;
    }

    //Agrear barcos a la grilla
    @PostMapping("/games/players/{gamePlayerId}/ships")
    public ResponseEntity<Map<String, Object>> addShips(Authentication authentication,@PathVariable long gamePlayerId, @RequestBody List<Ship> ships){
        ResponseEntity<Map<String, Object>> response;
        if (isGuest(authentication)) {
            response = new ResponseEntity<>(makeMap("error", "Unauthenticated user"), HttpStatus.UNAUTHORIZED);
        }else{
            Optional<GamePlayer> gamePlayer = gamePlayerRepository.findById(gamePlayerId);
            Player player = playerRepository.findByUserName(authentication.getName());
            if(gamePlayer.isEmpty()){
                response = new ResponseEntity<>(makeMap("error", "GamePlayer don't exist"), HttpStatus.NOT_FOUND);
            }else if (gamePlayer.get().getPlayer().getId() != player.getId()){
                    response = new ResponseEntity<>(makeMap("error", "Not your game"), HttpStatus.FORBIDDEN);
            }else if(gamePlayer.get().getShips().size()> 0){
                    response = new ResponseEntity<>(makeMap("error" , "Yo already have ships"), HttpStatus.FORBIDDEN);
            }else if(ships.size() != 5){
                    response = new ResponseEntity<>(makeMap("error" , "you must have 5 ships"), HttpStatus.FORBIDDEN);
            }
            else{
                /*for(int i = 0 ; i < ships.size() ; i++){
                    ships.get(i).setGamePlayer(gamePlayer.get());
                }*/
                //Este mismo ciclo for se resume en esto:
                ships.forEach(ship -> {
                    ship.setGamePlayer(gamePlayer.get());
                });

                shipRepository.saveAll(ships);
                response = new ResponseEntity<>(makeMap("success" , "ships added"),HttpStatus.ACCEPTED);
            }

        }

        return response;
    }

    @PostMapping("/games/players/{gamePlayerId}/salvos")
    public ResponseEntity<Map<String, Object>> addSalvos(Authentication authentication, @PathVariable long gamePlayerId, @RequestBody Salvo salvo){
        ResponseEntity<Map<String, Object>> response;
        if (isGuest(authentication)) {
            response = new ResponseEntity<>(makeMap("error", "Unauthenticated user"), HttpStatus.UNAUTHORIZED);
        }else{
            Optional<GamePlayer> gamePlayer = gamePlayerRepository.findById(gamePlayerId);
            Player player = playerRepository.findByUserName(authentication.getName());
            Optional<GamePlayer> oponent = gamePlayer.get().getOponent();

            if(gamePlayer.isEmpty()){
                response = new ResponseEntity<>(makeMap("error", "GamePlayer don't exist"), HttpStatus.NOT_FOUND);
            }else if (gamePlayer.get().getPlayer().getId() != player.getId()) {
                response = new ResponseEntity<>(makeMap("error", "Not your game"), HttpStatus.FORBIDDEN);
            }else {
                int iSafeShips = 5 - gamePlayer.get().getSunkenShips(salvo.getTurn()).size();
                if (salvo.getTurn() - 1 != gamePlayer.get().getSalvos().size()) {
                    response = new ResponseEntity<>(makeMap("error", "It's not a valid turn"), HttpStatus.FORBIDDEN);
                } else if (oponent.isEmpty()) {
                    return new ResponseEntity<>(makeMap("error", "Wait for your oponent"), HttpStatus.FORBIDDEN);
                } else if (salvo.getSalvoLocations().size() != iSafeShips && oponent.get().getSalvos().size() == gamePlayer.get().getSalvos().size()) {
                    response = new ResponseEntity<>(makeMap("error", "you must fire " + iSafeShips + " shots"), HttpStatus.FORBIDDEN);
                } else if (gamePlayer.get().getStatus() != GameStatus.FIRE){
                    response = new ResponseEntity<>(makeMap("error", "Wait for your turn"), HttpStatus.FORBIDDEN);
                }
                else {
                    salvo.setGamePlayer(gamePlayer.get());
                    gamePlayer.get().getSalvos().add(salvo);
                    salvoRepository.save(salvo);


                    if(gamePlayer.get().getStatus() == GameStatus.WIN){
                        scoreRepository.save(new Score(1.0,LocalDateTime.now(),gamePlayer.get().getPlayer(),gamePlayer.get().getGame()));
                        scoreRepository.save(new Score(0.0,LocalDateTime.now(),oponent.get().getPlayer(),oponent.get().getGame()));
                    }else if(gamePlayer.get().getStatus()  == GameStatus.LOSE){
                        scoreRepository.save(new Score(0.0,LocalDateTime.now(),gamePlayer.get().getPlayer(),gamePlayer.get().getGame()));
                        scoreRepository.save(new Score(1.0,LocalDateTime.now(),oponent.get().getPlayer(),oponent.get().getGame()));
                    }else if(gamePlayer.get().getStatus()  == GameStatus.TIE){
                        scoreRepository.save(new Score(0.5,LocalDateTime.now(),gamePlayer.get().getPlayer(),gamePlayer.get().getGame()));
                        scoreRepository.save(new Score(0.5,LocalDateTime.now(),oponent.get().getPlayer(),oponent.get().getGame()));
                    }

                    response = new ResponseEntity<>(makeMap("success", "salvo added"), HttpStatus.ACCEPTED);
                }
            }

        }
        return response;
    }


    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    private Map<String, Object> makeGameDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", game.getId());
        dto.put("created", game.getLocalDate());
        dto.put("gamePlayers", game.getGamePlayers()
                .stream()
                .map(gamePlayer -> makeGamePlayerDTO(gamePlayer))
                .collect(toList()));
        return dto;
    }

    private Map<String, Object> makeGamePlayerDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("gpId", gamePlayer.getId());
        dto.put("playerId", gamePlayer.getPlayer().getId());
        dto.put("userName", gamePlayer.getPlayer().getUserName());
        dto.put("shipSinks", gamePlayer.getSunkenShips()
                .stream()
                .map(this::makeGamePlayerShipView)
                .collect(toList())
        );
        var oScore = gamePlayer.getScore();
        if (oScore != null)
            dto.put("score", oScore.getScore());
        else
            dto.put("score", 0);



        return dto;
    }

    private Map<String, Object> makeGamePlayerShipView(Ship ship) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("shipType", ship.getShipType());
        dto.put("shipLocation", ship.getShipLocations());
        return dto;
    }

    private Map<String, Object> makeGamePlayerView(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", gamePlayer.getGame().getId());
        dto.put("created", gamePlayer.getJoinDate());
        dto.put("gamePlayers", gamePlayer.getGame().getGamePlayers()
                .stream()
                .map(this::makeGamePlayerDTO)
                .collect(toList())
        );
        dto.put("ships", gamePlayer.getShips()
                .stream()
                .map(this::makeGamePlayerShipView)
                .collect(toList())
        );
        dto.put("salvos", gamePlayer.getGame().getGamePlayers()
                .stream()
                .flatMap(this::makeGamePlayerSalvos)
                .collect(toList())
        );
        dto.put("status", gamePlayer.getStatus());
        return dto;
    }

    private Stream<Map<String, Object>> makeGamePlayerSalvos(GamePlayer gamePlayer) {
        return gamePlayer.getSalvos().stream().map(x -> x.makeSalvoView());
    }


    public Map<String, Object> playerDTO(Player player) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", player.getId());
        dto.put("username", player.getUserName());

        return dto;
    }
}


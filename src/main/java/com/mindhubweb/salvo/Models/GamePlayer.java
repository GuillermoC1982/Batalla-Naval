package com.mindhubweb.salvo.Models;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
public class GamePlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    LocalDateTime joinDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private final Set<Ship> ships = new HashSet<>();

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private final Set<Salvo> salvos = new HashSet<>();

    public GamePlayer() {
    }

    public GamePlayer(Game game, Player player) {
        this.game = game;
        this.player = player;
        this.joinDate = LocalDateTime.now();

    }

    public Optional<GamePlayer> getOponent() {
        return this.game.getGamePlayers().stream().filter(gp -> !gp.equals(this)).findAny();
    }

    public Set<Ship> getSunkenShips() {
        Set<Ship> oRetval = new LinkedHashSet<>();
        ArrayList<String> oAllOpponentSalvoLocations = new ArrayList<String>();
        Optional<GamePlayer> oOponent = this.getOponent();
        if (oOponent.isPresent())
            oOponent.get().getSalvos().forEach(s -> oAllOpponentSalvoLocations.addAll(s.getSalvoLocations()));

        this.getShips().stream().forEach(oShip -> {
            if (oAllOpponentSalvoLocations.containsAll(oShip.getShipLocations()))
                oRetval.add(oShip);
        });

        return oRetval;
    }

    public Set<Ship> getSunkenShips(int turn) {
        Set<Ship> oRetval = new LinkedHashSet<>();
        ArrayList<String> oAllOpponentSalvoLocations = new ArrayList<String>();
        Optional<GamePlayer> oOponent = this.getOponent();
        if (oOponent.isPresent())
            oOponent.get().getSalvos().stream().filter(salvo -> salvo.getTurn() <= turn).forEach(s -> oAllOpponentSalvoLocations.addAll(s.getSalvoLocations()));

        this.getShips().stream().forEach(oShip -> {
            if (oAllOpponentSalvoLocations.containsAll(oShip.getShipLocations()))
                oRetval.add(oShip);
        });

        return oRetval;
    }


    public GameStatus getStatus (){
        Optional<GamePlayer> oOponent = this.getOponent();

        if (this.getShips().size() < 5)
            return GameStatus.PLACE_SHIPS;
        else if (oOponent.isEmpty()){
            return GameStatus.WAITING_OPONENT;
        }
        else {
             if ( oOponent.get().getShips().size() == 0)
                return GameStatus.WAIT_OPONENT_PLACING_SHIPS;
             else {
                 int gpTurn = this.salvos.size();
                 int opTurn = oOponent.get().getSalvos().size();

                 if(gpTurn < opTurn)
                     return GameStatus.FIRE;
                 if(gpTurn > opTurn)
                     return GameStatus.WAIT;

                 else {
                     int playerSunks = oOponent.get().getSunkenShips().size();
                     int enemySunks = this.getSunkenShips().size();

                     if (playerSunks == 5 && enemySunks < 5)
                         return GameStatus.WIN;
                     else if (playerSunks < 5 && enemySunks == 5)
                         return GameStatus.LOSE;
                     else if (playerSunks == 5 && enemySunks == 5)
                         return GameStatus.TIE;
                     else
                         return GameStatus.FIRE;
                 }
            }
        }

    }

    public Score getScore() {
        return player.getScore(game);
    }


    public Set<Salvo> getSalvos() {
        return salvos;
    }

    public Set<Ship> getShips() {
        return ships;
    }

    public long getId() {
        return id;
    }

    public Player getPlayer() {
        return player;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public LocalDateTime getJoinDate() {
        return joinDate;
    }
}

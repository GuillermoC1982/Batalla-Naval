package com.mindhubweb.salvo.Models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    LocalDateTime LocalDate;

    @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    Set<GamePlayer> gamePlayers = new HashSet<>();

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private final Set<Score> scores  = new HashSet<>();

    public void addGamePlayer(GamePlayer gamePlayer) {
        gamePlayer.setGame(this);
        gamePlayers.add(gamePlayer);
    }

    public Game (){
        this.LocalDate = LocalDateTime.now();
    }

    public Game(LocalDateTime paramDate) {
        this.LocalDate = paramDate;
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getLocalDate() {
        return LocalDate;
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public Set<Player> getPlayers () {
        return this.gamePlayers.stream().map(GamePlayer::getPlayer).collect(Collectors.toSet());
    }
}

package com.mindhubweb.salvo.Models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private double score;

    private LocalDateTime finishGameDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    public Score( double score, LocalDateTime finishGameDate, Player player, Game game) {
        this.score = score;
        this.finishGameDate = finishGameDate;
        this.player = player;
        this.game = game;
    }

    public Score (){

    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public LocalDateTime getFinishGameDate() {
        return finishGameDate;
    }

    public void setFinishGameDate(LocalDateTime finishGameDate) {
        this.finishGameDate = finishGameDate;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}

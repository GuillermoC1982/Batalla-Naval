package com.mindhubweb.salvo.Models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private String userName;
    private String password;

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    Set<GamePlayer> gamePlayers  = new HashSet<>();

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private final Set<Score> scores = new HashSet<>();

    public void addGamePlayer(GamePlayer gamePlayer) {
        gamePlayer.setPlayer(this);
        gamePlayers.add(gamePlayer);
    }

    public Player() { }

    public Player(String userName,String password) {

        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public long getId() {
        return id;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }


    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return id == player.id &&
                Objects.equals(userName, player.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userName);
    }

    public Score getScore(Game game) {
        return this.scores.stream().filter(score -> score.getGame().equals(game)).findFirst().orElse(null);
    }
}

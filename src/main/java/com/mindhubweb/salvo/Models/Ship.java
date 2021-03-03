package com.mindhubweb.salvo.Models;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private String shipType;

    @ElementCollection
    @Column(name="shipLocation")
    private List<String> shipLocations = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    public Ship() { }
    
    public Ship(String shipType, List<String> shipLocations, GamePlayer gamePlayer) {
        this.shipType = shipType;
        this.shipLocations = shipLocations;
        this.gamePlayer = gamePlayer;
    }

    public long getId() {
        return id;
    }

    public String getShipType() {
        return shipType;
    }

    public List<String> getShipLocations() {
        return shipLocations;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setShipType(String shipType) {
        this.shipType = shipType;
    }

    public void setShipLocations(List<String> shipLocations) {
        this.shipLocations = shipLocations;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ship ship = (Ship) o;
        return id == ship.id &&
                Objects.equals(shipType, ship.shipType) &&
                Objects.equals(shipLocations, ship.shipLocations) &&
                Objects.equals(gamePlayer, ship.gamePlayer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, shipType, shipLocations, gamePlayer);
    }
}

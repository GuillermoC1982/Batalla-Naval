package com.mindhubweb.salvo.Models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Entity
public class Salvo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private int turn;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    @ElementCollection
    @Column(name="salvoLocation")
    private List<String> salvoLocations = new ArrayList<>();

    public Salvo(){

    }

    public Salvo(int turn, List<String> salvoLocations, GamePlayer gamePlayer) {
        this.turn = turn;
        this.salvoLocations = salvoLocations;
        this.gamePlayer = gamePlayer;

    }

    public Map<String, Object> makeSalvoView() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("turn", this.getTurn());
        dto.put("idGamePlayer", this.getGamePlayer().getId());
        dto.put("salvoLocation", this.getSalvoLocations());
        dto.put("hits", getHits());
        dto.put("sinks", getSinks());



        return dto;
    }

    public ArrayList<String> getHits(){
        ArrayList<String> oRetVal = new ArrayList<String>();
        this.getSalvoLocations().stream().forEach(oSalvoLocation -> {
            this.getGamePlayer().getOponent().get().getShips().stream().forEach(oShip -> {
                if(oShip.getShipLocations().contains(oSalvoLocation))
                    oRetVal.add(oSalvoLocation);
            });
        });
        return oRetVal;
    }
    public List<Map<String, String>> getSinks(){

        List<Map<String, String>> oRetVal = new ArrayList<Map<String, String>>();
        ArrayList<String> oAllSalvoLocations = new ArrayList<String>();
        this.getGamePlayer().getSalvos().forEach(s -> oAllSalvoLocations.addAll(s.getSalvoLocations()));

        this.getGamePlayer().getOponent().get().getShips().stream().forEach(oShip -> {

            List<String> oSafeShipLocations = oShip.getShipLocations().stream().filter(oShipLoc -> !oAllSalvoLocations.contains(oShipLoc)).collect(toList());
            if(oSafeShipLocations.size() == 0)
            {
                List<String> oSalvoHitShipLocations = this.getSalvoLocations().stream().filter(oSalvoLoc -> oShip.getShipLocations().contains(oSalvoLoc)).collect(toList());

                oRetVal.addAll(oSalvoHitShipLocations.stream().map(x-> {
                    Map<String, String> oSink = new LinkedHashMap<String, String>();
                    oSink.put("location", x);
                    oSink.put("shipType", oShip.getShipType());
                    return oSink;
                }).collect(toList()));
            }
        });

        return oRetVal;
    }


    public long getId() {
        return id;
    }

    public int getTurn() {
        return turn;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public List<String> getSalvoLocations() {
        return salvoLocations;
    }

    public void setSalvoLocations(List<String> salvoLocations) {
        this.salvoLocations = salvoLocations;
    }
}

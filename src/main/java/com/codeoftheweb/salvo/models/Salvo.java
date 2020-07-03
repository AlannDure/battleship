package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Salvo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;

    @ElementCollection
    private List <String> locations = new ArrayList<>();

    private int turn;

    public Salvo () {}

    public Salvo (int turn, GamePlayer gamePlayer, List<String> locations  ) {
        this.turn = turn;
        this.gamePlayer = gamePlayer;
        this.locations = locations;
    }

    public Salvo (int turn, List <String> locations){
        this.turn = turn;
        this.locations = locations;
    }

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public GamePlayer getGamePlayer() { return gamePlayer; }

    public void setGamePlayer(GamePlayer gamePlayer) { this.gamePlayer = gamePlayer; }

    public List<String> getLocations() { return locations; }

    public void setLocations(List<String> locations) { this.locations = locations; }

    public int getTurn() { return turn; }

    public void setTurn(int turn) { this.turn = turn; }

    public List <String> getHits (){

        List <String> shots = getLocations();

        GamePlayer opp = this.getGamePlayer().getOpponent();

        Set<Ship> enemyShips = opp.getShips();

        List <String> enemyLocs = new ArrayList<>();

        enemyShips.forEach(ship -> enemyLocs.addAll(ship.getLocations()));

        return shots.stream().filter(shot -> enemyLocs.stream().anyMatch(loc -> loc.equals(shot))).collect(Collectors.toList());
    }

    public List<Ship> sunkShips (){

        GamePlayer opp = this.getGamePlayer().getOpponent();

        Set<Ship> enemyShips = opp.getShips();

        Set<Salvo> mySalvoes = getGamePlayer().getSalvoes().stream().filter(salvo -> salvo.getTurn() <= getTurn()).collect(Collectors.toSet());

        List <String> allShots = new ArrayList<>();

        mySalvoes.forEach(salvo -> allShots.addAll(salvo.getLocations()));

        return enemyShips.stream().filter(ship -> allShots.containsAll(ship.getLocations())).collect(Collectors.toList());
    }

    public Map<String, Object> salvoDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("turn", getTurn());
        dto.put("player",gamePlayer.getPlayer().getId());
        dto.put("locations", getLocations());

        return dto;
    }

    public Map<String, Object> hitsDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("turn", getTurn());
        dto.put("player", gamePlayer.getPlayer().getId());

        GamePlayer opp = this.getGamePlayer().getOpponent();

        if (opp != null) {

            dto.put("hits", getHits());

        }else{
            dto.put("hits",  new ArrayList<>());
        }
        return dto;
    }
    public Map<String, Object> sinkDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("turn", getTurn());
        dto.put("player",gamePlayer.getPlayer().getId());

        GamePlayer opp = this.getGamePlayer().getOpponent();

        if (opp != null){

            dto.put("sunks", sunkShips().stream().map(Ship::shipDTO));
        }else{
            dto.put("sunks", new ArrayList<>());
        }
        return dto;
    }


}

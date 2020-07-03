package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.*;

@Entity
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;

    @ElementCollection
    private List <String> locations = new ArrayList<>();

    private String type;

    public Ship() {}

    public Ship(String type, List <String> locations) {
        this.type = type;
        this.locations = locations;
    }

    public Ship(String type, List <String> locations, GamePlayer gamePlayer) {
        this.type = type;
        this.locations = locations;
        this.gamePlayer = gamePlayer;
    }

    public long getId() { return id; }

    public GamePlayer getGamePlayer() { return gamePlayer; }

    public void setGamePlayer(GamePlayer gamePlayer) { this.gamePlayer = gamePlayer; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public List<String> getLocations() { return locations; }

    public void setLocations(List<String> locations) { this.locations = locations; }

    public Map<String, Object> shipDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("type", getType());
        dto.put("locations", getLocations());
        return dto;
    }
}


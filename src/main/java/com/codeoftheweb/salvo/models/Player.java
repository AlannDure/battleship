package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.util.*;
import static java.util.stream.Collectors.toList;


@Entity
 public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    private Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy="player", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Score> scores = new HashSet<>();

    private String userName;
    private String password;

    public Player(){}

    public Player(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() { return this.userName; }

    public void setUserName(String userName) { this.userName = userName; }

    public long getId() { return id; }

    @JsonIgnore
    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    @JsonIgnore
    public List<Game> getGames() { return gamePlayers.stream().map(game -> game.getGame()).collect(toList()); }

    public void addGamePlayer(GamePlayer gamePlayer) { gamePlayer.setPlayer(this);gamePlayers.add(gamePlayer); }

    public Score getScoreByGame(Game game) {return scores.stream().filter(x -> x.getGame().equals(game)).findFirst().orElse(null);}


    public Map<String, Object> playerDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", id);
        dto.put("username", userName);
        return dto;
    }

}

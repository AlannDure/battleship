package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import static java.util.stream.Collectors.toList;


@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy ="game", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Score> scores = new HashSet<>();


    private LocalDateTime date;

    public Game() { this.date = LocalDateTime.now(); }

    public Game(LocalDateTime date) { this.date = date; }

    public LocalDateTime getDate() { return date; }

    public long getId() { return id; }

    public List<Player> getPlayers() {
        return gamePlayers.stream().map(sub -> sub.getPlayer()).collect(toList());
    }

    public Set<GamePlayer> getGamePlayers() { return gamePlayers; }

    public void addGamePlayer(GamePlayer gamePlayer) { gamePlayer.setGame(this); gamePlayers.add(gamePlayer); }

    public Set<Score> getScores() { return scores; }

    public void setScores(Set<Score> scores) { this.scores = scores; }

    public Map<String, Object> gameDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", id);
        dto.put("created", date);
        dto.put("gamePlayers", getGamePlayers().stream().map(GamePlayer :: gamePlayerDTO).collect(toList()));
        return dto;
    }

}


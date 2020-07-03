package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.toList;


@Entity
public class GamePlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;

    @OneToMany(mappedBy ="gamePlayer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Ship> ships = new HashSet<>();

    @OneToMany(mappedBy ="gamePlayer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Salvo> salvoes = new HashSet<>();


    private LocalDateTime joinDate;

    public GamePlayer(){}

    public GamePlayer(Game game, Player player) {
        this.game = game;
        this.player = player;
        this.joinDate = LocalDateTime.now();
    }

    public GameState getState (){

        GameState gameState = GameState.PLAYER_PLACES_SALVOES;

        if(this.ships.size() != 5){
            gameState = GameState.PLAYER_PLACES_SHIPS;

        }else if(getOpponent() == null){
            gameState = GameState.WAITING_OPPONENT;

        }else if(getOpponent().ships.size() != 5){
            gameState = GameState.OPPONENT_PLACES_SHIPS;

        }else if(this.salvoes.size() > getOpponent().salvoes.size()){
            gameState = GameState.OPPONENT_PLACES_SALVOES;

        }else if((this.salvoes.size() > 0 && getOpponent().getSalvoes().size() > 0) && salvoes.size() == getOpponent().salvoes.size()){

            int playerSunkTotal = salvoes.stream().filter(x -> x.getTurn() == this.salvoes.size()).findFirst().get().sunkShips().size();

            int oppSunkTotal =  getOpponent().getSalvoes().stream().filter(x -> x.getTurn() == this.salvoes.size()).findFirst().get().sunkShips().size();


                    if(oppSunkTotal > playerSunkTotal && oppSunkTotal == ships.size()){
                        gameState = GameState.PLAYER_LOSE;
                    }else if(playerSunkTotal > oppSunkTotal && playerSunkTotal == ships.size()){
                        gameState = GameState.PLAYER_WINS;
                    }else if(playerSunkTotal == 5 && oppSunkTotal == 5){
                        gameState = GameState.PLAYERS_TIE;
                    }
        }
        return gameState;
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

    public LocalDateTime getJoinDate() {
        return joinDate;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Set<Ship> getShips() { return ships; }

    public void addShip(Ship ship) { this.ships.add(ship); ship.setGamePlayer(this);}

    public Set<Salvo> getSalvoes() { return salvoes; }

    public void setSalvoes(Set<Salvo> salvoes) { this.salvoes = salvoes; }

    public Score getScore() {return this.player.getScoreByGame(this.game);}

    public void addSalvo(Salvo salvo) {this.salvoes.add(salvo); salvo.setGamePlayer(this);}

    public GamePlayer getOpponent() { return this.getGame().getGamePlayers().stream().filter(x -> x.getId() != this.getId()).findFirst().orElse(null); }

    public Map<String, Object> gamePlayerDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", id);
        dto.put("player", player.playerDTO());
        Score score = this.getScore();
        if(score != null) {
            dto.put("score", score.getScore());
        }else{
            dto.put("score", null);
        }
        return dto;
    }

    public Map<String, Object> gameViewDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", game.getId());
        dto.put("created", game.getDate());
        dto.put("gamePlayers", game.getGamePlayers().stream().map(GamePlayer :: gamePlayerDTO).collect(toList()));
        dto.put("ships", getShips().stream().map(Ship :: shipDTO).collect(toList()));
        dto.put("salvoes",getGame().getGamePlayers().stream().flatMap(gp -> gp.getSalvoes().stream().map(Salvo :: salvoDTO)).collect(toList()));
        dto.put("hits",salvoes.stream().map(Salvo::hitsDTO).collect(toList()));
        dto.put("sunks",salvoes.stream().map(Salvo::sinkDTO).collect(toList()));
        if(getOpponent() != null){
            dto.put("oppHits",getOpponent().getSalvoes().stream().map(Salvo::hitsDTO).collect(toList()));
            dto.put("oppSunk",getOpponent().getSalvoes().stream().map(Salvo::sinkDTO).collect(toList()));
        }else{
            dto.put("oppHits",new ArrayList<>());
            dto.put("oppSunk",new ArrayList<>());
        }
        dto.put("gameState",getState());
        return dto;
    }

}


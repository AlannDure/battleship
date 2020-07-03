package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.repositories.GameRepository;
import com.codeoftheweb.salvo.repositories.PlayerRepository;
import com.codeoftheweb.salvo.repositories.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.toList;


@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    PasswordEncoder passwordEncoder;


    @RequestMapping("/games")
    public Map<String, Object> getAll(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<>();
        if (!this.isGuest(authentication)) {
            dto.put("player", playerRepository.findByUserName(authentication.getName()).playerDTO());
        } else {
            dto.put("player",null);
        }
        dto.put("games", gameRepository.findAll().stream().map(Game::gameDTO).collect(toList()));

        return dto;
    }

    @RequestMapping("/game_view/{gamePlayerId}")
    public ResponseEntity <Map<String, Object>> gamePlayerId(@PathVariable long gamePlayerId, Authentication authentication) {
        ResponseEntity<Map<String, Object>> response;
        if (isGuest(authentication)) {
            response = new ResponseEntity<>(makeMap(AppMessage.KEY_ERROR, AppMessage.MSG_ERROR_LOGIN), HttpStatus.UNAUTHORIZED);
        } else {
            GamePlayer gp = gamePlayerRepository.findById(gamePlayerId).orElse(null);
            Player player = playerRepository.findByUserName(authentication.getName());
            if (gp == null) {
                response = new ResponseEntity<>(makeMap(AppMessage.KEY_ERROR, AppMessage.MSG_ERROR_GAME), HttpStatus.NOT_FOUND);
            } else if (gp.getPlayer().getId() != player.getId()) {
                response = new ResponseEntity<>(makeMap(AppMessage.KEY_ERROR, AppMessage.MSG_ERROR_GAME_MATCH), HttpStatus.FORBIDDEN);
            } else {
                response = new ResponseEntity<>(gp.gameViewDTO(), HttpStatus.CREATED);
            }
        }
        return response;
    }

    @RequestMapping(value = "/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> newPlayer(@RequestParam String username, @RequestParam String password) {
        ResponseEntity<Map<String, Object>> response;
        Player player = playerRepository.findByUserName(username);
        if (username.isEmpty() || password.isEmpty()) {
            response = new ResponseEntity<>(makeMap(AppMessage.KEY_ERROR, AppMessage.MSG_ERROR_FIELDS), HttpStatus.BAD_REQUEST);
        }else if(player != null ){
            response = new ResponseEntity<>(makeMap(AppMessage.KEY_ERROR, AppMessage.MSG_ERROR_FIELDS),HttpStatus.FORBIDDEN);
        }else {
           Player newPlayer = new Player(username,passwordEncoder.encode(password));
           playerRepository.save(newPlayer);
           response = new ResponseEntity<>(makeMap("ID",newPlayer.getId()),HttpStatus.CREATED);
        }
        return response;
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }
    private Map<String, Object> makeMap(String key, Object value){
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(key, value);
        return map;
    }

    @PostMapping("/games")
    public ResponseEntity<Map<String, Object>> newGame(Authentication authentication) {
        ResponseEntity<Map<String, Object>> response;
        if(this.isGuest(authentication)){
            response = new ResponseEntity<>(makeMap(AppMessage.KEY_ERROR, AppMessage.MSG_ERROR_LOGIN), HttpStatus.UNAUTHORIZED);
        }
        else{
            Game newGame = new Game ();
            Player player  = playerRepository.findByUserName(authentication.getName());
            GamePlayer newGamePlayer = new GamePlayer(newGame,player);
            gameRepository.save(newGame);
            gamePlayerRepository.save(newGamePlayer);
            response = new ResponseEntity<>(makeMap("gpid",newGamePlayer.getId()),HttpStatus.CREATED);
        }
        return response;
    }

    @PostMapping ("/games/{gameId}/players")
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable long gameId, Authentication authentication) {
        ResponseEntity<Map<String, Object>> response;
        if (this.isGuest(authentication)) {
            response = new ResponseEntity<>(makeMap(AppMessage.KEY_ERROR, AppMessage.MSG_ERROR_LOGIN), HttpStatus.UNAUTHORIZED);
        } else {
            Game game = gameRepository.findById(gameId).orElse(null);
            if(game == null){
                response = new ResponseEntity<>(makeMap(AppMessage.KEY_ERROR, AppMessage.MSG_ERROR_GAME),HttpStatus.NOT_FOUND);
            } else if (game.getGamePlayers().size() > 1){
                response = new ResponseEntity<>(makeMap(AppMessage.KEY_ERROR, AppMessage.MSG_ERROR_PLAYER),HttpStatus.FORBIDDEN);
            } else {
                Player player = playerRepository.findByUserName(authentication.getName());
                if(game.getGamePlayers().stream().allMatch(gp -> gp.getPlayer().getId() == player.getId())) {
                    response = new ResponseEntity<>(makeMap(AppMessage.KEY_ERROR, AppMessage.MSG_ERROR_PLAYER), HttpStatus.FORBIDDEN);
                } else{
                   GamePlayer newGamePlayer = new GamePlayer(game,player);
                   gamePlayerRepository.save(newGamePlayer);
                   response = new ResponseEntity<>(makeMap("gpid",newGamePlayer.getId()),HttpStatus.CREATED);
                }
            }
        }
        return response;
    }

    @PostMapping ("/games/players/{gamePlayerId}/ships")
    public ResponseEntity<Map<String,Object>> addShips(@PathVariable long gamePlayerId, @RequestBody List <Ship> ships , Authentication authentication){
        ResponseEntity<Map<String,Object>> response;
        if (this.isGuest(authentication)) {
            response = new ResponseEntity<>(makeMap(AppMessage.KEY_ERROR, AppMessage.MSG_ERROR_LOGIN), HttpStatus.UNAUTHORIZED);
        } else {
            GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).orElse(null);
            Player player = playerRepository.findByUserName(authentication.getName());
            if (gamePlayer == null){
                response = new ResponseEntity<>(makeMap(AppMessage.KEY_ERROR, AppMessage.MSG_ERROR_GAME),HttpStatus.BAD_REQUEST);
            }
            else if (gamePlayer.getPlayer().getId() != player.getId()) {
                response = new ResponseEntity<>(makeMap(AppMessage.KEY_ERROR, AppMessage.MSG_ERROR_GAME_MATCH), HttpStatus.FORBIDDEN);
            } else if (gamePlayer.getShips().size() >= 5){
                response = new ResponseEntity<>(makeMap(AppMessage.KEY_ERROR, AppMessage.MSG_ERROR_FIELDS), HttpStatus.FORBIDDEN);
            } else {
                ships.forEach(gamePlayer :: addShip);
                gamePlayerRepository.save(gamePlayer);
                response = new ResponseEntity<>(makeMap(AppMessage.KEY_SUCCESS, AppMessage.MSG_ADDED),HttpStatus.CREATED);
            }
        }
        return response;
    }

    @PostMapping("/games/players/{gamePlayerId}/salvoes")
    public ResponseEntity<Map<String,Object>> addSalvo(@PathVariable long gamePlayerId, @RequestBody List <String> shots , Authentication authentication){
        ResponseEntity<Map<String,Object>> response;
        if (this.isGuest(authentication)) {
            response = new ResponseEntity<>(makeMap(AppMessage.KEY_ERROR, AppMessage.MSG_ERROR_LOGIN), HttpStatus.UNAUTHORIZED);
    }else{
          GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).orElse(null);
          Player player = playerRepository.findByUserName(authentication.getName());
          if(gamePlayer == null){
              response = new ResponseEntity<>(makeMap(AppMessage.KEY_ERROR, AppMessage.MSG_ERROR_GAME),HttpStatus.NOT_FOUND);
          }else if(gamePlayer.getPlayer().getId() != player.getId()){
              response = new ResponseEntity<>(makeMap(AppMessage.KEY_ERROR, AppMessage.MSG_ERROR_GAME_MATCH),HttpStatus.UNAUTHORIZED);
          }else if(shots.size() != 5){
              response = new ResponseEntity<>(makeMap(AppMessage.KEY_ERROR, AppMessage.MSG_ERROR_FIELDS),HttpStatus.FORBIDDEN);
          }else{

              int turn = gamePlayer.getSalvoes().size() + 1;
              Salvo salvo = new Salvo(turn,shots);
              gamePlayer.addSalvo(salvo);
              gamePlayerRepository.save(gamePlayer);
              response = new ResponseEntity<>(makeMap(AppMessage.KEY_SUCCESS, AppMessage.MSG_ADDED),HttpStatus.CREATED);

              if(turn == gamePlayer.getOpponent().getSalvoes().size()){
                  if (gamePlayer.getState() == GameState.PLAYER_WINS){
                      Score scorePlayer = new Score(1,LocalDateTime.now(),gamePlayer.getGame(),gamePlayer.getPlayer());
                      scoreRepository.save(scorePlayer);
                      Score scoreOpp = new Score(0,LocalDateTime.now(),gamePlayer.getOpponent().getGame(),gamePlayer.getOpponent().getPlayer());
                      scoreRepository.save(scoreOpp);
                  }else if (gamePlayer.getState() == GameState.PLAYER_LOSE){
                      Score scorePlayer = new Score(0,LocalDateTime.now(),gamePlayer.getGame(),gamePlayer.getPlayer());
                      scoreRepository.save(scorePlayer);
                      Score scoreOpp = new Score(1,LocalDateTime.now(),gamePlayer.getOpponent().getGame(),gamePlayer.getOpponent().getPlayer());
                      scoreRepository.save(scoreOpp);
                  }else if (gamePlayer.getState() == GameState.PLAYERS_TIE){
                        Score scorePlayer = new Score(0.5,LocalDateTime.now(),gamePlayer.getGame(),gamePlayer.getPlayer());
                        scoreRepository.save(scorePlayer);
                        Score scoreOpp = new Score(0.5,LocalDateTime.now(),gamePlayer.getOpponent().getGame(),gamePlayer.getOpponent().getPlayer());
                        scoreRepository.save(scoreOpp);
                  }
              }
          }
        }
        return response;
    }
}


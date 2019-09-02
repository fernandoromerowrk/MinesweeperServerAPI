/**
 * 
 */
package com.deviget.minesweeperserver.api;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.deviget.minesweeperserver.api.Game.RevealResult;

/**
 * @author fernando
 * API entry point
 * Exposes methods for creating and playing games.
 * Games created are stored in memory until they are won or lost.
 * They can be persisted for further recovery (not available yet)
 */
@RestController
@RequestMapping(value={"/games"})
public class APIController {
	
	private static final Map<String, Game> GAMES_CREATED = new ConcurrentHashMap<>();
	private static final String GAME_DOESNT_EXIST_MSG = "Game doesn't exist";
	
	@PostMapping(value = "")
	public ResponseEntity<?> newGame(short rows, short columns, int mines) {
		try {
			Game game = Game.createGame(rows, columns, mines);
			GAMES_CREATED.put(game.getId(), game);
			return  ResponseEntity.created(null).body(game);
		} catch (WrongParametersException ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		}		 
	}
	
	@GetMapping(value = "/{gameId}")
	public ResponseEntity<?> getGame(@PathVariable String gameId) {
		try {
			Game game = GAMES_CREATED.get(gameId);
			if(game == null) {
				throw new WrongParametersException(APIController.GAME_DOESNT_EXIST_MSG);
			}
			return ResponseEntity.ok().body(game);
		} catch (WrongParametersException ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		}		 
	}
	 
	 
	@PutMapping(value = "/{gameId}/cells/reveal")
	public ResponseEntity<?> revealCell(@PathVariable String gameId, @RequestParam short row, @RequestParam short column) {
		try {
			Game game = GAMES_CREATED.get(gameId);
			if(game == null) {
				throw new WrongParametersException(APIController.GAME_DOESNT_EXIST_MSG);
			}
			RevealResult revealResult = game.revealCell(new Game.Cell.Coordinates(row, column));
			synchronized(APIController.class) {
				if(revealResult.getStatus() != Game.Status.STARTED) {
					GAMES_CREATED.remove(gameId);
				}
			}
			return  ResponseEntity.ok().body(revealResult);
		} catch (WrongParametersException ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		}		 
	 }
	 
	 @PutMapping(value = "/{gameId}/cells/flag")
	 public ResponseEntity<?> flagCell(@PathVariable String gameId, @RequestParam short row, @RequestParam short column) {
		 try {
			Game game = GAMES_CREATED.get(gameId);
			if(game == null) {
				throw new WrongParametersException(APIController.GAME_DOESNT_EXIST_MSG);
			}
			game.flagCell(new Game.Cell.Coordinates(row, column));
			return  ResponseEntity.ok().body("");
		} catch (WrongParametersException ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		}		 
	 } 
		

}

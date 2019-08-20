/**
 * 
 */
package com.deviget.minesweeperserver.api;

import static org.junit.Assert.assertTrue;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author fernando
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GameRepositoryTest {

	@Autowired
	GameRepository gameRepository;
	
	@Test
	public void testSave() throws WrongParametersException {
		short rows = 8;
		short columns = 8;
		int mines = 9;
	  	Game game = Game.createGame(rows, columns, mines);
	  	final Game gameNew = this.gameRepository.save(game);
	  	Optional<Game> savedGame = this.gameRepository.findAll().stream()
	  		.filter(aGame -> aGame.getId().equals(gameNew.getId())).findFirst();
	  	assertTrue(savedGame.isPresent());
	  	System.out.println(gameNew);
	}
	
	@Test
	public void testFindById() throws WrongParametersException {
		short rows = 8;
		short columns = 8;
		int mines = 9;
	  	Game game = Game.createGame(rows, columns, mines);
	  	game = this.gameRepository.save(game);
	  	Optional<Game> aGame = this.gameRepository.findById(game.getId());
	  	assertTrue(aGame.isPresent());
	  	System.out.println(aGame.get());
	}

}

package com.deviget.minesweeperserver.api;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.deviget.minesweeperserver.api.Game;
import com.deviget.minesweeperserver.api.Game.Cell;

public class GameFactoryTests {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void whenWrongNumberOfRowsThenThrowWrongParametersException() throws WrongParametersException {
		
		short rows = 2;
		short columns = 3;
		int mines = 4;
	    exception.expect(WrongParametersException.class);
		Game.createGame(rows, columns, mines);	    
	}
	
	@Test
	public void whenWrongNumberOfMinesThenThrowWrongParametersException() throws WrongParametersException {
		
		short rows = 3;
		short columns = 3;
		int mines = 5;
	    exception.expect(WrongParametersException.class);
		Game.createGame(rows, columns, mines);	    
	}

	@Test
	public void whenArgumentsOkCreateGameAndCheckCellsComposition() throws WrongParametersException {
		short rows = 3;
		short columns = 3;
		int mines = 4;
		Game game = Game.createGame(rows, columns, mines);
		List<Cell> gameCells = game.getCells();
		long numRowsCalc = gameCells.stream().map(c -> c.getCoordinates().getRow()).distinct().count();
		long numColsCalc = gameCells.stream().map(c -> c.getCoordinates().getColumn()).distinct().count();
		long numMinesCalc = gameCells.stream().filter(c -> c.isHasMine()).count();
		assertEquals((long)rows, numRowsCalc);
		assertEquals((long)columns, numColsCalc);
		assertEquals((long)mines, numMinesCalc);
		System.out.println(game);
	}

}

package com.deviget.minesweeperserver;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class GameFactoryTests {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void whenWrongNumberOfRowsThenThrowIllegalArgumentException() {
		
		short rows = 2;
		short columns = 3;
		int mines = 4;
	    exception.expect(IllegalArgumentException.class);
		Game.createGame(rows, columns, mines);	    
	}
	
	@Test
	public void whenWrongNumberOfMinesThenThrowIllegalArgumentException() {
		
		short rows = 3;
		short columns = 3;
		int mines = 5;
	    exception.expect(IllegalArgumentException.class);
		Game.createGame(rows, columns, mines);	    
	}

	@Test
	public void whenArgumentsOkCreateGameAndCheckCellsComposition() {
		short rows = 3;
		short columns = 3;
		int mines = 4;
		Game game = Game.createGame(rows, columns, mines);
		long numRowsCalc = game.getCells().stream().map(c -> c.getCoordinate().getRow()).distinct().count();
		long numColsCalc = game.getCells().stream().map(c -> c.getCoordinate().getColumn()).distinct().count();
		long numMinesCalc = game.getCells().stream().filter(c -> c.isHasMine()).count();
		assertEquals((long)rows, numRowsCalc);
		assertEquals((long)columns, numColsCalc);
		assertEquals((long)mines, numMinesCalc);
	}

}

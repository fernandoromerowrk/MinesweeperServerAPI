package com.deviget.minesweeperserver.api;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.deviget.minesweeperserver.api.Game;
import com.deviget.minesweeperserver.api.Game.Cell;
import com.deviget.minesweeperserver.api.Game.Cell.FlaggedStatus;
import com.deviget.minesweeperserver.api.Game.RevealResult;

public class GameTests {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void whenMineRevealedThenGameIsLost() throws WrongParametersException {
		short rows = 3;
		short columns = 3;
		int mines = 2;
		//initial game status
		Game game = Game.createGame(rows, columns, mines);
		System.out.println(game);
		//get a mine location
		Optional<Cell> mineCellOpt = game.getCells().stream().filter(cell -> cell.isHasMine()).findFirst();		
		System.out.println("About to reveal cell at " + mineCellOpt.get().getCoordinates());
		RevealResult result = game.revealCell(mineCellOpt.get().getCoordinates());
		assertEquals(Game.Status.LOST, result.getStatus());
		System.out.println(result.getAdjCellsRevealed());
	}
	
	@Test
	public void whenCellwithNoAdjMinesThenRevealAdjacentOnes() throws WrongParametersException {
		short rows = 4;
		short columns = 4;
		int mines = 2;
		//loop until a game with a cell with no adj mines is created
		Game game = null;
		Cell cellNoAdjMines = null;
		do {
			game = Game.createGame(rows, columns, mines);
			//get a mine location
			Optional<Cell> cellOpt = game.getCells().stream().filter(
				cell -> !cell.isHasMine() && cell.getAdjMinesNumber() == 0).findFirst();
			if(cellOpt.isPresent()) {
				cellNoAdjMines = cellOpt.get();
			}
		} while (cellNoAdjMines == null);
		
		//initial game status		
		System.out.println(game);
		
		System.out.println("About to reveal cell at " + cellNoAdjMines.getCoordinates());
		game.revealCell(cellNoAdjMines.getCoordinates());
		
		//game status with revealed cells		
		System.out.println(game);
		
		//TODO devise an assert condition
	}
	
	@Test()
	public void whenAllCellWithNoAdjMinesRevealedThenGameIsWon() throws WrongParametersException {
		short rows = 8;
		short columns = 8;
		int mines = 25;
		Game game = Game.createGame(rows, columns, mines);
		//initial game status		
		System.out.println(game);
		Cell cellNonRev = null;
		RevealResult result = null;
		do {
			//get a non-revealed cell location
			Optional<Cell> cellOpt = game.getCells().stream().filter(
				cell -> !cell.isHasMine() && !cell.isRevealed()).findFirst();
			if(cellOpt.isPresent()) {
				cellNonRev = cellOpt.get();
				System.out.println("About to reveal cell at " + cellNonRev.getCoordinates());
				result  = game.revealCell(cellNonRev.getCoordinates());
				//game status with revealed cells		
				System.out.println(game);
			} else {
				cellNonRev = null;
			}
			
		} while (cellNonRev != null);
		
		assertEquals(Game.Status.WON, result.getStatus());
		
	}
	
	@Test()
	public void whenNonRevealedCellIsFlaggedThenCellHasRedFlagStatus() throws WrongParametersException {
		short rows = 3;
		short columns = 3;
		int mines = 3;
		Game game = Game.createGame(rows, columns, mines);
		//initial game status		
		System.out.println(game);
		//get a non-revealed cell location
		Optional<Cell> cellOpt = game.getCells().stream().filter(
			cell -> !cell.isHasMine() && !cell.isRevealed()).findFirst();
		Cell nonRevCell = cellOpt.get();
		System.out.println("About to flag cell at " + nonRevCell.getCoordinates());
		game.flagCell(nonRevCell.getCoordinates());
		//new game status
		System.out.println(game);		
		assertEquals(FlaggedStatus.RED_FLAG, game.getCellByCoordinates(nonRevCell.getCoordinates()).getFlaggedStatus());
	}
	
	@Test()
	public void whenRedFlaggedCellIsFlaggedThenCellHasQuestionMarkFlagStatus() throws WrongParametersException {
		short rows = 3;
		short columns = 3;
		int mines = 3;
		Game game = Game.createGame(rows, columns, mines);
		//initial game status		
		System.out.println(game);
		//get a non-revealed cell location
		Optional<Cell> cellOpt = game.getCells().stream().filter(
			cell -> !cell.isHasMine() && !cell.isRevealed()).findFirst();
		Cell nonRevCell = cellOpt.get();
		System.out.println("About to flag cell at " + nonRevCell.getCoordinates());
		game.flagCell(nonRevCell.getCoordinates());
		System.out.println("About to flag again cell at " + nonRevCell.getCoordinates());
		game.flagCell(nonRevCell.getCoordinates());
		//new game status
		System.out.println(game);		
		assertEquals(FlaggedStatus.QUESTION_MARK, game.getCellByCoordinates(nonRevCell.getCoordinates()).getFlaggedStatus());
	}

}

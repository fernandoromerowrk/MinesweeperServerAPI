/**
 * 
 */
package com.deviget.minesweeperserver.api;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import com.deviget.minesweeperserver.api.Game.Cell.Coordinates;
import com.deviget.minesweeperserver.api.Game.Cell.FlaggedStatus;

/**
 * @author fernando
 *
 */
@Document
class Game implements Serializable {

	/* INTERNAL IMPLEMENTATION */
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Game.class);
	
	static final Duration MAX_DURATION = Duration.ofSeconds(999L);
	
	private static final Random rnd = new Random();
	
	/**
	 * Auto-generated
	 */
	@Id
	private ObjectId id;
	
	/**
	 * Number of rows
	 */
	private short rows;
	
	/**
	 * Number of columns
	 */
	private short columns;
	
	/**
	 * Number of mines
	 */
	private int mines;
	
	/**
	 * Map of contained cells keyed by coordinates
	 */
	private Map<Cell.Coordinates, Cell> cells;
	
	/**
	 * Created, Started, etc 
	 */
	private Status status;
	
	/**
	 * Game creator login
	 */
	private final String createdBy;
	
	/**
	 * Creation date, for tracking time-to-live
	 * if game was never started
	 */
	private LocalTime createdAt;
	
	/**
	 * Start date, for tracking play time elapse
	 * Tracked when first cell is revealed
	 */
	private LocalTime startedAt;
	
	/**
	 * Total time in seconds played
	 */
	private Duration timePlayed;
	
	//to only allow creation of instance through factory method
	private Game() {
		/*TODO when security infrastructure is enabled
		* use principal login to track games per accounts		*
		*/
		this.createdBy = "ANONYMOUS";
		this.status = Status.CREATED;
		this.id = ObjectId.get();
		this.cells = new HashMap<>();
	}
		
	/**
	 * @param id
	 * @param rows
	 * @param columns
	 * @param mines
	 * @param cells
	 * @param status
	 * @param createdBy
	 * @param createdAt
	 * @param startedAt
	 * @param timePlayed
	 */
	@PersistenceConstructor
	Game(ObjectId id, short rows, short columns, int mines, Map<Cell.Coordinates, Cell> cells, Status status,
			String createdBy) {
		super();
		this.id = id;
		this.rows = rows;
		this.columns = columns;
		this.mines = mines;
		this.cells = cells;
		this.status = status;
		this.createdBy = createdBy;
	}	
	
	
	enum RelativePosition {
		TOP_LEFT, TOP_MIDDLE, TOP_RIGHT, LEFT_MIDDLE, RIGHT_MIDDLE, BOTTOM_LEFT, BOTTOM_MIDDLE, BOTTOM_RIGHT
	}
	
	static class Cell  {

		private final Coordinates coordinates;

		private boolean isRevealed;
		
		private FlaggedStatus flaggedStatus;
		
		private boolean hasMine;
		
		private short adjMinesNumber;
		
		/**
		 * @author fernando
		 *
		 */
		enum FlaggedStatus {
			
			NON_FLAGGED(' '), RED_FLAG('R'), QUESTION_MARK('?');
			
			private char symbol;
			
			FlaggedStatus(char sym) {
				this.symbol = sym;
			}
			
			char getSymbol() {
				return this.symbol;
			}
			
		}

		static class Coordinates {

			private final short row;
			
			private final short column;

			/**
			 * @param row
			 * @param column
			 */
			public Coordinates(short row, short column) {
				super();
				this.row = row;
				this.column = column;
			}

			/**
			 * @return the row
			 */
			public short getRow() {
				return row;
			}

			/**
			 * @return the column
			 */
			public short getColumn() {
				return column;
			}

			/* (non-Javadoc)
			 * @see java.lang.Object#toString()
			 */
			@Override
			public String toString() {
				return "Coordinates [row=" + row + ", column=" + column + "]";
			}

			/* (non-Javadoc)
			 * @see java.lang.Object#hashCode()
			 */
			@Override
			public int hashCode() {
				return Objects.hash(column, row);
			}

			/* (non-Javadoc)
			 * @see java.lang.Object#equals(java.lang.Object)
			 */
			@Override
			public boolean equals(Object obj) {
				if (this == obj) {
					return true;
				}
				if (obj == null) {
					return false;
				}
				if (!(obj instanceof Coordinates)) {
					return false;
				}
				Coordinates other = (Coordinates) obj;
				return column == other.column && row == other.row;
			}			

		}		
		
		/**
		 * @param coordinates
		 * @param hasMine
		 */
		Cell(Coordinates coordinates, boolean hasMine) {
			super();
			this.coordinates = coordinates;
			this.hasMine = hasMine;
			this.isRevealed = false;
			this.flaggedStatus = FlaggedStatus.NON_FLAGGED;
		}		

		/**
		 * @return the isRevealed
		 */
		public boolean isRevealed() {
			return isRevealed;
		}

		/**
		 * @param isRevealed the isRevealed to set
		 */
		void setRevealed(boolean isRevealed) {
			this.isRevealed = isRevealed;
		}

		/**
		 * @return the flaggedStatus
		 */
		public FlaggedStatus getFlaggedStatus() {
			return flaggedStatus;
		}

		/**
		 * @param flaggedStatus the flaggedStatus to set
		 */
		void setFlaggedStatus(FlaggedStatus flaggedStatus) {
			this.flaggedStatus = flaggedStatus;
		}

		/**
		 * @return the coordinates
		 */
		public Coordinates getCoordinates() {
			return coordinates;
		}

		/**
		 * @return the hasMine
		 */
		boolean isHasMine() {
			return hasMine;
		}

		/**
		 * @return the adjMinesNumber
		 */
		short getAdjMinesNumber() {
			return adjMinesNumber;
		}
		
		/**
		 * @return the adjMinesNumber if cell is revealed
		 */
		public short getAdjMinesNumberRev() {
			return (isRevealed ? adjMinesNumber : 0);
		}
		
		Cell getClone() {
			Cell clone = new Cell(coordinates, hasMine);
			clone.isRevealed = isRevealed;
			clone.flaggedStatus = flaggedStatus;
			clone.adjMinesNumber = adjMinesNumber;
			return clone;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Cell [" + (coordinates != null ? "coordinates=" + coordinates + ", " : "") + "isRevealed=" + isRevealed
					+ ", " + (flaggedStatus != null ? "flaggedStatus=" + flaggedStatus + ", " : "") + "hasMine="
					+ hasMine + ", adjMinesNumber=" + adjMinesNumber + "]";
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return Objects.hash(coordinates);
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof Cell)) {
				return false;
			}
			Cell other = (Cell) obj;
			return Objects.equals(coordinates, other.coordinates);
		}	
		
	}
	
	/**
	 * @author fernando
	 *
	 */
	enum Status {
		CREATED, STARTED, EXPIRED, LOST, WON
	}
	
	/**
	 * @author fernando
	 * Used to model result of revealingCell. I.E
	 *  Game won, all revealed adj cells from last move, or
	 *  Game lost, no revealed adj cells
	 *  Game started(ongoing status until finished), revealed adj cells if applicable
	 */
	class RevealResult {
		private final Game.Status status;
		private final Set<Cell> adjCellsRevealed;
		/**
		 * @param status
		 * @param adjCellsRevealed
		 */
		RevealResult(Status status, Set<Cell> adjCellsRevealed) {
			super();
			this.status = status;
			this.adjCellsRevealed = adjCellsRevealed;
		}
		/**
		 * @return the status
		 */
		public Game.Status getStatus() {
			return status;
		}
		/**
		 * @return the adjCellsRevealed
		 */
		public Set<Cell> getAdjCellsRevealed() {
			return adjCellsRevealed;
		}		
		
	}
	
	private Optional<Cell> getRelativeCell(Cell cell, RelativePosition relPos) {
		
		Cell relCell = null;
		switch(relPos) {
			case TOP_LEFT:
				if(cell.coordinates.column > 1 && cell.coordinates.row > 1) {
					relCell = this.cells.get(new Coordinates(
						(short)(cell.coordinates.row - 1), (short)(cell.coordinates.column - 1)));
				}
				break;
			case TOP_MIDDLE:
				if(cell.coordinates.row > 1) {
					relCell = this.cells.get(new Coordinates(
						(short)(cell.coordinates.row - 1), cell.coordinates.column));
				}
				break;
			case TOP_RIGHT:
				if(cell.coordinates.row > 1 && cell.coordinates.column < this.columns) {
					relCell = this.cells.get(new Coordinates(
						(short)(cell.coordinates.row - 1), (short)(cell.coordinates.column + 1)));
				}
				break;
			case LEFT_MIDDLE:
				if(cell.coordinates.column > 1) {
					relCell = this.cells.get(new Coordinates(
						cell.coordinates.row, (short)(cell.coordinates.column - 1)));
				}
				break;
			case RIGHT_MIDDLE:
				if(cell.coordinates.column < this.columns) {
					relCell = this.cells.get(new Coordinates(
						cell.coordinates.row, (short)(cell.coordinates.column + 1)));
				}
				break;
			case BOTTOM_LEFT:
				if(cell.coordinates.column > 1 && cell.coordinates.row < this.rows) {
					relCell = this.cells.get(
						new Coordinates((short)(cell.coordinates.row + 1), (short)(cell.coordinates.column - 1)));
				}
				break;
			case BOTTOM_MIDDLE:
				if(cell.coordinates.row < this.rows) {
					relCell = this.cells.get(
						new Coordinates((short)(cell.coordinates.row + 1), cell.coordinates.column));
				}
				break;
			case BOTTOM_RIGHT:
				if(cell.coordinates.column < this.columns && cell.coordinates.row < this.rows) {
					relCell = this.cells.get(new Coordinates(
							(short)(cell.coordinates.row + 1), (short)(cell.coordinates.column + 1)));
				}
				break;				
			default:
				break;
			
		}
		
		return Optional.ofNullable(relCell);
		
	}
	
	private Map<RelativePosition, Cell> getAdjacentCells(Cell cell) {
		Map<RelativePosition, Cell> relCells = new HashMap<>();
		Stream.of(RelativePosition.values()).forEach(relPos -> {
			Optional<Cell> optRelCell = this.getRelativeCell(cell, relPos);
			if(optRelCell.isPresent()) {
				relCells.put(relPos, optRelCell.get());
			} else {
				relCells.put(relPos, null);
			}
		});		
		
		return relCells;

	}
	
	/* EXPOSED API */
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id.toHexString();
	}	
	
	/**
	 * @return the rows
	 */
	public short getRows() {
		return rows;
	}
	
	/**
	 * @return the columns
	 */
	public short getColumns() {
		return columns;
	}	

	/**
	 * @return the mines
	 */
	public int getMines() {
		return mines;
	}

	Cell getCellByCoordinates(Cell.Coordinates cellCoord) {
		return cells.get(cellCoord).getClone();
	}
	
	/**
	 * @return Cell list (cloned from originals, so their state cannot be affected)
	 */
	public List<Cell> getCells() {
		return cells.values().stream()
				.map(Cell::getClone)
				.collect(Collectors.toList());
	}	
	
	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @return the startedAt
	 */
	public LocalTime getStartedAt() {
		return startedAt;
	}

	/**
	 * @return the timePlayed
	 */
	public Duration getTimePlayed() {
		return timePlayed;
	}

	/**
	 * @param cellCoord
	 * @return
	 * @throws WrongParametersException 
	 * 
	 * Reveals cell and if isn't a mine and hasn't adjacent ones
	 * returns list of additional revealed cells (or mines if game's lost)
	 */
	RevealResult revealCell(Cell.Coordinates cellCoord) throws WrongParametersException {
		Cell cell = cells.get(cellCoord);
		if(cell == null) {
			throw new WrongParametersException("Wrong cell coordinates"); 
		}
		
		Set<Cell> adjCellsRev = new HashSet<>();
		
		//check-then-act semantics
		try {
			synchronized(this) {
				if(cell.hasMine) {
					this.status = Game.Status.LOST;
					//collect al mine cells now that everything is lost
					this.cells.values().stream()
						.filter(gCell -> gCell.isHasMine())
						.forEach(gCell -> adjCellsRev.add(gCell));
					return new RevealResult(Game.Status.LOST, adjCellsRev);
				}
				if(this.status != Game.Status.STARTED) {
					this.status = Game.Status.STARTED;
				}
				
				cell.isRevealed = true;
				//resets flagged status if appropiate
				if(cell.flaggedStatus != FlaggedStatus.NON_FLAGGED) {
					cell.flaggedStatus = FlaggedStatus.NON_FLAGGED;
				}
				
				//if all cells revealed then we have a winner
				Optional<Cell> nonRevCellOpt = this.cells.values().stream()
					.filter(gCell -> !gCell.isHasMine() && !gCell.isRevealed())
					.findFirst();
				if(!nonRevCellOpt.isPresent()) {
					throw new GameWonException();
				} else {
					LOGGER.debug("Found non-revealed cell at " + nonRevCellOpt.get().getCoordinates());
				}
			}	
			
			//reveals adjacent cells if no adjacent mines present
			if(cell.adjMinesNumber == 0) {
				LOGGER.debug("Cell " + cell.coordinates + " has no adj mines, revealing adj cells as well!");
				this.getAdjacentCells(cell).entrySet().stream().forEach(
					adjCellEntry -> {
						//check that it hasn't been revealed already
						if(adjCellEntry.getValue() != null && !adjCellEntry.getValue().isRevealed) {
							adjCellsRev.add(adjCellEntry.getValue());
							LOGGER.debug("Revealing adj Cell at " + cell.coordinates);							
							try {
								RevealResult adjRevRes = revealCell(adjCellEntry.getValue().getCoordinates());
								adjCellsRev.addAll(adjRevRes.getAdjCellsRevealed());
								//if game is won by revealing this adjacent cell stop processing
							    if(adjRevRes.getStatus() == Game.Status.WON) {
							    	throw new GameWonException(); 
							    }
							} catch (WrongParametersException e) {
								//this should never happen
								LOGGER.error("Game internal consistency error " + e.getMessage());
							}														
						}
					});				
			} else {
				LOGGER.debug("Cell " + cell.coordinates + " has adj mines, no revealing adjacent ones!");
			}
		} catch (GameWonException e) {
			this.status = Game.Status.WON;
			return new RevealResult(Game.Status.WON, adjCellsRev);			
		}
			
		return new RevealResult(Game.Status.STARTED, adjCellsRev);
		
	}
	
	void flagCell(Cell.Coordinates cellCoord) throws WrongParametersException {
		Cell cell = cells.get(cellCoord);
		if(cell == null) {
			throw new WrongParametersException("Wrong cell coordinates"); 
		}
		if(cell.isRevealed()) {
			throw new WrongParametersException("Can't flag a revealed cell"); 
		}
		
		synchronized(this) {			
			switch(cell.getFlaggedStatus()) {
				case NON_FLAGGED:
					cell.setFlaggedStatus(FlaggedStatus.RED_FLAG);
					break;
				case RED_FLAG:
					cell.setFlaggedStatus(FlaggedStatus.QUESTION_MARK);
					break;
				case QUESTION_MARK:
					cell.setFlaggedStatus(FlaggedStatus.NON_FLAGGED);
					break;				
				default:
					break;
			}
		}

	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 * Format may change in the future
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Game [id=");
		builder.append(id);
		builder.append(", ");
		if (cells != null) {
			builder.append(System.getProperty("line.separator")).append("cells=");
			for(short ix = 1; ix <= rows; ix++ ) {
				builder.append(System.getProperty("line.separator"));
				for(short ij = 1; ij <= columns; ij++ ) {
					builder.append(" [");
					Cell cell = this.cells.get(new Coordinates(ix, ij));
					if(cell.hasMine) {
						builder.append(" * ");
					} else if(cell.isRevealed) {
						builder.append(" ").append(cell.adjMinesNumber).append(" ");
					} else if(cell.flaggedStatus != FlaggedStatus.NON_FLAGGED) {
						builder.append(" ").append(cell.flaggedStatus.getSymbol()).append(" ");
					} else {
						builder.append("(").append(cell.adjMinesNumber).append(")");
					}
					builder.append("]");
				}				
			}
			builder.append(System.getProperty("line.separator"));
		}
		if (status != null) {
			builder.append("status=");
			builder.append(status);
			builder.append(", ");
		}
		if (createdBy != null) {
			builder.append("createdBy=");
			builder.append(createdBy);
			builder.append(", ");
		}
		if (createdAt != null) {
			builder.append("createdAt=");
			builder.append(createdAt);
			builder.append(", ");
		}
		if (startedAt != null) {
			builder.append("startedAt=");
			builder.append(startedAt);
			builder.append(", ");
		}
		if (timePlayed != null) {
			builder.append("timePlayed=");
			builder.append(timePlayed);
		}
		builder.append("]");
		return builder.toString();
	}



	/**
	 * @param rows - Must be greater than 2
	 * @param columns - Must be greater than 2
	 * @param mines - Positive value, number of mines must not exceed half of total cells.
	 * @return
	 * @throws WrongParametersException 
	 */
	static Game createGame(short rows, short columns, int mines) throws WrongParametersException {
		
		//check arguments
		if(rows < 2) {
			throw new WrongParametersException("Number of rows must be greater than 2");
		}
		if(columns < 2) {
			throw new WrongParametersException("Number of columns must be greater than 2");
		}
		int cellsNumber = rows * columns;
		int maxNumMinesAllowed = cellsNumber / 2;
		if(mines > maxNumMinesAllowed) {
			throw new WrongParametersException("Number of mines must not be greater than " + maxNumMinesAllowed);
		}		
		
		//initial game state
		Game game = new Game();
		game.columns = columns;
		game.rows = rows;
		game.mines = mines;

		//generate cells, one row at a time
		int minesCreated = 0;		
		int rndVal;
		for(short ix = 1; ix <= rows; ix++ ) {
			for(short ij = 1; ij <= columns; ij++ ) {
				//sets mine cell condition randomly
				//untill all mine cells have been created
				boolean hasMine = false;
				if(minesCreated < mines) {
					rndVal = rnd.nextInt(2);					
					if(rndVal > 0) {
						hasMine = true;
						minesCreated++;
					}
				}
				Coordinates coord = new Cell.Coordinates(ix, ij);
				Cell cell = new Cell(coord, hasMine);
				game.cells.put(coord, cell);
			}	
		}
		
		//once cells are created, set number of adjacent mines for each
		//non-mine one
		for(Cell cell : game.cells.values()) {
			if(cell.hasMine) {
				continue;
			}
			long adjMines = Stream.of(RelativePosition.values()).filter(relPos -> {
				Optional<Cell> relCellOpt = game.getRelativeCell(cell, relPos);
				return (relCellOpt.isPresent() && relCellOpt.get().hasMine);
			}).count();
			cell.adjMinesNumber = (short)adjMines;			
		}
		
		return game;
		
	}
	


}
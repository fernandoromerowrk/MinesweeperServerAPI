/**
 * 
 */
package com.deviget.minesweeperserver;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import com.deviget.minesweeperserver.Game.Cell.Coordinate;

/**
 * @author fernando
 *
 */
class Game implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	static final Duration MAX_DURATION = Duration.ofSeconds(999L);
	
	private static final AtomicLong ID_GENERATOR = new AtomicLong(0);
	
	private static final Random rnd = new Random();
	
	
	
	/**
	 * Auto-generated
	 */
	private long id;
	
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
	 * Map of contained cells keyed by coordinate
	 */
	private Map<Cell.Coordinate, Cell> cells;
	
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
		this.id = Game.ID_GENERATOR.incrementAndGet();
		this.cells = new HashMap<>();
	}	
	
	static class Cell {		
		
		private final Coordinate coordinate;

		private boolean isRevealed;
		
		private FlaggedStatus flaggedStatus;
		
		private boolean hasMine;
		
		private short adjMinesNumber;
		
		/**
		 * @author fernando
		 *
		 */
		enum FlaggedStatus {
			NON_FLAGGED, RED_FLAG, QUESTION_MARK
		}

		static class Coordinate {

			private final short row;
			
			private final short column;

			/**
			 * @param row
			 * @param column
			 */
			public Coordinate(short row, short column) {
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
				return "Coordinate [row=" + row + ", column=" + column + "]";
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
				if (!(obj instanceof Coordinate)) {
					return false;
				}
				Coordinate other = (Coordinate) obj;
				return column == other.column && row == other.row;
			}			

		}		
		
		/**
		 * @param coordinate
		 * @param hasMine
		 */
		Cell(Coordinate coordinate, boolean hasMine) {
			super();
			this.coordinate = coordinate;
			this.hasMine = hasMine;
			this.isRevealed = false;
			this.flaggedStatus = FlaggedStatus.NON_FLAGGED;
		}		

		/**
		 * @return the isRevealed
		 */
		boolean isRevealed() {
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
		FlaggedStatus getFlaggedStatus() {
			return flaggedStatus;
		}

		/**
		 * @param flaggedStatus the flaggedStatus to set
		 */
		void setFlaggedStatus(FlaggedStatus flaggedStatus) {
			this.flaggedStatus = flaggedStatus;
		}

		/**
		 * @return the coordinate
		 */
		Coordinate getCoordinate() {
			return coordinate;
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
		
		Cell getClone() {
			Cell clone = new Cell(coordinate, hasMine);
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
			return "Cell [" + (coordinate != null ? "coordinate=" + coordinate + ", " : "") + "isRevealed=" + isRevealed
					+ ", " + (flaggedStatus != null ? "flaggedStatus=" + flaggedStatus + ", " : "") + "hasMine="
					+ hasMine + ", adjMinesNumber=" + adjMinesNumber + "]";
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return Objects.hash(coordinate);
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
			return Objects.equals(coordinate, other.coordinate);
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
	 * @return the rows
	 */
	short getRows() {
		return rows;
	}
	
	/**
	 * @return the columns
	 */
	short getColumns() {
		return columns;
	}	

	/**
	 * @return the mines
	 */
	int getMines() {
		return mines;
	}

	/**
	 * @return Cell list (cloned from originals, so their state cannot be affected)
	 */
	List<Cell> getCells() {
		return cells.values().stream()
				.map(Cell::getClone)
				.collect(Collectors.toList());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Game [id=");
		builder.append(id);
		builder.append(", ");
		if (cells != null) {
			builder.append("cells=");
			builder.append(cells);
			builder.append(", ");
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
	 */
	static Game createGame(short rows, short columns, int mines) {
		
		//check arguments
		if(rows < 2) {
			throw new IllegalArgumentException("Number of rows must be greater than 2");
		}
		if(columns < 2) {
			throw new IllegalArgumentException("Number of columns must be greater than 2");
		}
		int cellsNumber = rows * columns;
		int maxNumMinesAllowed = cellsNumber / 2;
		if(mines > maxNumMinesAllowed) {
			throw new IllegalArgumentException("Number of mines must not be greater than " + maxNumMinesAllowed);
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
				Coordinate coord = new Cell.Coordinate(ix, ij);
				Cell cell = new Cell(coord, hasMine);
				game.cells.put(coord, cell);
			}	
		}
		
		return game;
		
	}	

}
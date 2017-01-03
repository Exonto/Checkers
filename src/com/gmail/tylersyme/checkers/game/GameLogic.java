package com.gmail.tylersyme.checkers.game;

import java.util.ArrayList;
import java.util.List;

public abstract class GameLogic
{
	protected List<CheckerPiece> userCheckerPieces = new ArrayList<>();
	protected List<CheckerPiece> opponentCheckerPieces = new ArrayList<>();

	// Grid Checker Pieces is a 2D Array which stores each checker piece at the
	// x and y coordinate.
	protected List<ArrayList<CheckerPiece>> gridCheckerPieces = new ArrayList<>();

	protected Checkerboard checkerboard;
	protected boolean hasGameStarted = false;
	
	// Changes when the player clicks the 'end turn' button
	protected boolean isUsersTurn = false;
	// Changes when the player moves a checker piece (allows for double jumps)
	protected boolean hasUserMoved = false;

	public GameLogic(Checkerboard checkerboard)
	{
		this.checkerboard = checkerboard;
	}

	/**
	 * Not to be confused with {@link GameLogic#setupCheckerboard()}, this
	 * method will start an entirely new checker game. It will first setup
	 * the checkerboard and then determine which player (whether AI or online
	 * opponent) will go first.
	 * 
	 * @param output
	 * @param input
	 */
	public abstract void startNewGame();

	/**
	 * <p>
	 * This is used to update a particular Checker Piece's location data. This
	 * method should not be used by itself to move a checker piece. Instead, it
	 * is designed to be used in tandem with {@link CheckerPiece#move(int, int)}
	 * where the method's four main purposes are to:
	 * <ul>
	 * <li>Determine if the movement is a valid one</li>
	 * <li>To capture any pieces which may have been jumped over</li>
	 * <li>To "king" the piece if it reached the opposite side of the board</li>
	 * <li>To declare a winner if only one player's pieces remains</li>
	 * </ul></p>
	 * <p>
	 * If the movement was invalid, this method will return 'false' and the
	 * movement action of {@link CheckerPiece#move(int, int)} should be
	 * cancelled.
	 * </p>
	 * 
	 * @param movedPiece
	 * @param newX
	 * @param newY
	 * @param isUserPieceMovement
	 * @return Whether the movement was valid
	 */
	public abstract boolean handleCheckerMovement(
			CheckerPiece movedPiece,
			int newX,
			int newY);

	/**
	 * The player's turn ends and the opponent is now able to move his/her
	 * checkerpiece.
	 */
	public abstract void endTurn();

	/**
	 * The game of checkers ends and the player is shortly returned to the
	 * main menu.
	 * 
	 * @param didUserWin
	 */
	public abstract void endGame(boolean didUserWin);

	/**
	 * The game of checkers ends and the player is shortly returned to the
	 * main menu. Neither player wins or loses.
	 * 
	 * @param didUserWin
	 */
	public abstract void drawGame();
	
	/**
	 * Sends a draw request to the opponent. If the opponent agrees then the
	 * game resolves as a draw. Otherwise the sender of the request is alerted
	 * to their opponent's rejection on the proposal.
	 * 
	 * @param didUserWin
	 */
	public abstract void requestDraw();
	
	/**
	 * This will open a dialog window if your opponent requests a draw. The user
	 * can either accept or reject the proposal.
	 */
	public abstract void opponentRequestedDraw();
	
	/**
	 * This will send a chat message to the opponent.
	 * 
	 * @param chatMsg The message to be sent
	 */
	public abstract void sendChatMessage(String chatMsg);

	/**
	 * Returns whether the given new X and Y coordinates represent a valid
	 * location for the given checker piece to be moved to.<br>
	 * This does not take into account whether it is the player's turn.
	 * 
	 * @param movedPiece The checker piece to be moved
	 * @param newX
	 * @param newY
	 * @return Whether newX and newY are valid movement coordinates
	 */
	public boolean isValidMove(CheckerPiece movedPiece, int newX, int newY)
	{
		int xDif = movedPiece.getX() - newX;
		int yDif = movedPiece.getY() - newY;

		// Every valid move requires that a change in both x and y take
		// place and that the square does not already have a piece on it.
		if (xDif == 0 || yDif == 0
				|| this.getCheckerPieceAt(newX, newY) != null)
			return false;

		// The checker piece moves forward one space (checks if the piece is
		// a king to allow for backward movement)
		if (((movedPiece.isKing()) ? (Math.abs(yDif)) : (yDif)) == 1
				&& Math.abs(xDif) == 1)
		{
			return true;

		// The checker piece is attempting to capture an opponent's
		// piece (checks if the piece is a king to allow for backward
		// movement)
		} else if (((movedPiece.isKing()) ? (Math.abs(yDif)) : (yDif)) == 2
				&& Math.abs(xDif) == 2)
		{
			// Get the piece that may be jumped over
			CheckerPiece toBeCaptured = this.getCheckerPieceAt(newX
					+ (xDif / 2), newY + (yDif / 2));

			if (toBeCaptured != null && !toBeCaptured.isUserPiece())
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns which captured piece (if any) was jumped over.
	 * 
	 * @param movedPiece
	 * @param newX
	 * @param newY
	 * @return The Captured Checker Piece<br>
	 *         Will return <b>null</b> if no checker piece was captured
	 */
	protected CheckerPiece getCapturedPiece(
			CheckerPiece movedPiece,
			int newX,
			int newY)
	{
		int xDif = movedPiece.getX() - newX;
		int yDif = movedPiece.getY() - newY;

		CheckerPiece toBeCaptured = null;
		if (Math.abs(xDif) == 2 && Math.abs(yDif) == 2)
		{
			toBeCaptured = this.getCheckerPieceAt(newX + (xDif / 2), newY
					+ (yDif / 2));
		}

		return toBeCaptured;
	}
	
	/**
	 * Returns whether the checker's <b>valid</b> movement was a jump in 
	 * either direction.
	 * 
	 * @param movedPiece
	 * @param newX
	 * @param newY
	 * @return
	 */
	protected boolean isJump(
			CheckerPiece movedPiece,
			int newX,
			int newY)
	{
		int xDif = movedPiece.getX() - newX;
		int yDif = movedPiece.getY() - newY;
		
		// Return true if both the x and y difference is equal is two
		return (Math.abs(xDif) == 2 && Math.abs(yDif) == 2);
	}

	/**
	 * Internally deletes the given checker piece and erases it from the game.
	 * 
	 * @param captured
	 */
	public void captureCheckerPiece(CheckerPiece captured)
	{
		if (captured.isUserPiece() == false)
		{
			this.opponentCheckerPieces.remove(captured);
		} else
		{
			this.userCheckerPieces.remove(captured);
		}

		this.gridCheckerPieces.get(captured.getX()).set(captured.getY(), null);
	}

	/**
	 * Returns whether either player has lost all their pieces.
	 * 
	 * @return
	 */
	protected boolean gameEnded()
	{
		return (this.userCheckerPieces.size() == 0 || this.opponentCheckerPieces
				.size() == 0);
	}
	
	/**
	 * Returns whether this player's pieces have all been captures.
	 * 
	 * @return
	 */
	protected boolean didUserWin()
	{
		return (this.userCheckerPieces.size() > 0);
	}

	// -------------------------------------------------------------------------
	// Game Setup
	// -------------------------------------------------------------------------

	/**
	 * This will clear the Checkerboard's pieces and start a brand new game by
	 * placing both the user's and opponent's Checkers into the correct setup
	 * position.
	 */
	public void setupCheckerboard()
	{
		this.userCheckerPieces.clear();
		this.opponentCheckerPieces.clear();
		this.gridCheckerPieces.clear();
		this.ensureBoardCapacity();

		// Setup both the user's and opponent's checker pieces
		// Note - The top left square of the checkerboard is position (0, 0)
		for (int y = 5; y < 8; y++)
		{
			for (int x = 0 + ((y + 1) % 2); x < 8; x += 2)
			{
				CheckerPiece newUserPiece = new CheckerPiece(this, true, x, y);

				// Transform the current x and y position to create a mirrored
				// version of the opponent's pieces on the opposite side of the
				// board
				CheckerPiece newOpponentPiece = new CheckerPiece(this, false,
						(7 - x), (7 - y));

				this.userCheckerPieces.add(newUserPiece);
				this.opponentCheckerPieces.add(newOpponentPiece);

				this.addCheckerPiece(newUserPiece);
				this.addCheckerPiece(newOpponentPiece);
			}
		}
	}

	/**
	 * This is used to appropriately set the given Checker Piece to the 2D
	 * array gridCheckerPieces. For example, if the piece's position is
	 * (3,5) then the element at column 3 and row 5 will be set to the given
	 * Checker Piece.<br>
	 * <b>Note:</b> Does not protect against out of bounds values
	 * 
	 * @param piece
	 */
	private void addCheckerPiece(CheckerPiece piece)
	{
		this.gridCheckerPieces.get(piece.getX()).set(piece.getY(), piece);
	}

	/**
	 * Returns the checker piece (if any) at the given x and y location.
	 * 
	 * @param x
	 * @param y
	 * @return The Checker Piece<br>
	 *         Will return <b>null</b> if no checker piece exists at (x, y)
	 */
	public CheckerPiece getCheckerPieceAt(int x, int y)
	{
		return this.gridCheckerPieces.get(x).get(y);
	}

	/**
	 * Fills the 2D grid with 8x8 elements of empty space.
	 */
	private void ensureBoardCapacity()
	{
		for (int xPos = 0; xPos < 8; xPos++)
		{
			this.gridCheckerPieces.add(new ArrayList<CheckerPiece>()); // New
																		// Column
			for (int yPos = 0; yPos < 8; yPos++)
			{
				this.gridCheckerPieces.get(xPos).add(null); // Modify Row
			}
		}
	}

	// -------------------------------------------------------------------------
	// Getters and Setters
	// -------------------------------------------------------------------------

	/**
	 * @return Both the user's and opponent's checker pieces.
	 */
	public List<CheckerPiece> getAllCheckerPieces()
	{
		List<CheckerPiece> allPieces = new ArrayList<CheckerPiece>();
		allPieces.addAll(this.userCheckerPieces);
		allPieces.addAll(this.opponentCheckerPieces);

		return allPieces;
	}

	public List<CheckerPiece> getUserCheckerPieces()
	{
		return this.userCheckerPieces;
	}

	public List<CheckerPiece> getOpponentCheckerPieces()
	{
		return this.opponentCheckerPieces;
	}

	public List<ArrayList<CheckerPiece>> getGridCheckerPieces()
	{
		return this.gridCheckerPieces;
	}

	public boolean isUsersTurn()
	{
		return isUsersTurn;
	}

	public void setUsersTurn(boolean isUsersTurn)
	{
		this.isUsersTurn = isUsersTurn;
	}

	public boolean hasGameStarted()
	{
		return hasGameStarted;
	}

	public void setHasGameStarted(boolean hasGameStarted)
	{
		this.hasGameStarted = hasGameStarted;
	}

	public Checkerboard getCheckerboard()
	{
		return checkerboard;
	}

	public void setCheckerboard(Checkerboard checkerboard)
	{
		this.checkerboard = checkerboard;
	}
}

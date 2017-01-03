package com.gmail.tylersyme.checkers.game;


/**
 * This represents a single checker piece on a checkerboard.
 */
public class CheckerPiece
{

	private GameLogic gameLogic;
	private int x;
	private int y;
	private boolean isUserPiece;
	private boolean isKing = false;

	public CheckerPiece(GameLogic gameLogic, boolean isUserPiece, int x, int y)
	{
		this.gameLogic = gameLogic;
		this.x = x;
		this.y = y;
		this.setUserPiece(isUserPiece);
	}
	
	/**
	 * <p>
	 * This will move the Checker Piece to the new location.<br>
	 * If either newX or newY is out of bounds (0<=x<=7), the closest possible 
	 * square location will be used instead.
	 * </p>
	 * <p>
	 * This movement action will cancel itself if the new location is determined
	 * to be invalid.
	 * </p>
	 * 
	 * @param newX
	 * @param newY
	 */
	public void move(int newX, int newY)
	{
		if (newX > 7)
		{
			newX = 7;
		} else if (newX < 0)
		{
			newX = 0;
		}
		if (newY > 7)
		{
			newY = 7;
		} else if (newY < 0)
		{
			newY = 0;
		}
		
		//Will cancel the movement if it is invalid
		if (this.gameLogic.handleCheckerMovement(this, newX, newY))
		{
			this.x = newX;
			this.y = newY;
		}
	}

	// -------------------------------------------------------------------------
	// Getters and Setters
	// -------------------------------------------------------------------------

	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public boolean isUserPiece()
	{
		return isUserPiece;
	}

	public void setUserPiece(boolean isUserPiece)
	{
		this.isUserPiece = isUserPiece;
	}

	public boolean isKing()
	{
		return isKing;
	}

	public void setKing(boolean isKing)
	{
		this.isKing = isKing;
	}

}

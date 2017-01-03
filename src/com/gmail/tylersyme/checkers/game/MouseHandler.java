package com.gmail.tylersyme.checkers.game;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseHandler implements MouseListener, MouseMotionListener
{
	private Checkerboard sender;

	public Point mouseDragLocation;
	public Point squareClicked;
	public CheckerPiece pieceClicked;
	public boolean isDraggingPiece = false;

	public MouseHandler(Checkerboard sender)
	{
		this.setSender(sender);
	}

	/**
	 * Will return the square that is beneath the two given coordinates.<br>
	 * 
	 * @param board
	 * @param rawX
	 * @param rawY
	 * @return The square which is beneath rawX and rawY.<br>
	 *         Returns the closest possible square if either rawX or rawY are
	 *         out of bounds.
	 */
	public static Point getSquareAt(Checkerboard board, int rawX, int rawY)
	{
		Point rawPoint = new Point(rawX, rawY);
		Point squarePoint = new Point(rawPoint.x / board.getSquareSize(),
				rawPoint.y / board.getSquareSize());

		// Adjusts for out of bounds rawX and/or rawY coordinates
		if (squarePoint.x > 7)
		{
			squarePoint.x = 7;
		} else if (squarePoint.x < 0)
		{
			squarePoint.x = 0;
		}
		if (squarePoint.y > 7)
		{
			squarePoint.y = 7;
		} else if (squarePoint.y < 0)
		{
			squarePoint.y = 0;
		}
		
		return squarePoint;
	}

	/**
	 * <p>
	 * Returns the checker piece under the given mouse location.<br>
	 * This is simply determined by checking if there is a checker piece at
	 * the square where the user clicked.
	 * </p>
	 * This will not detect any of the opponent's pieces. Only the user's own
	 * pieces may be returned.
	 * 
	 * @param clickLoc
	 * @return The checker piece beneath the user's mouse.<br>
	 *         Will return <b>null</b> if there is no checker piece beneath
	 *         the user's mouse.
	 */
	private CheckerPiece getClickedCheckerPiece(Point clickLoc)
	{
		Point squareClicked = getSquareAt(this.sender, clickLoc.x, clickLoc.y);

		CheckerPiece clickedCheckerPiece = sender.getGameLogic()
				.getCheckerPieceAt(squareClicked.x, squareClicked.y);
		if (clickedCheckerPiece != null && clickedCheckerPiece.isUserPiece())
		{
			return clickedCheckerPiece;
		}

		return null;
	}
	
	private boolean isMouseForceDisabled()
	{
		return (this.sender.getGameLogic().hasGameStarted() == false ||
				this.sender.getGameLogic().isUsersTurn() == false);
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{

	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * Occurs when the user presses their mouse.
	 */
	@Override
	public void mousePressed(MouseEvent e)
	{
		if (this.isMouseForceDisabled() == false)
		{
			// Left Click Only
			if (e.getButton() == MouseEvent.BUTTON1)
			{
				Point mouseClick = e.getPoint();
				Point squareClicked = getSquareAt(this.sender, mouseClick.x,
						mouseClick.y);
	
				// Stores the square that was clicked and the checker at that square
				// (if any)
				this.squareClicked = squareClicked;
				this.pieceClicked = this.getClickedCheckerPiece(mouseClick);
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		if (this.pieceClicked != null)
		{
			//The square the mouse is currently hovering over
			Point squareHover = getSquareAt(this.sender,
					this.mouseDragLocation.x, this.mouseDragLocation.y);
			
			//Check to see if the square is a valid movement location
			if (this.sender.getGameLogic().isValidMove(this.pieceClicked,
					squareHover.x, squareHover.y))
			{
				//Move the piece to its new location
				this.pieceClicked.move(squareHover.x, squareHover.y);
			}
		}

		// If a piece is being dragged, this will end upon releasing the mouse
		this.isDraggingPiece = false;

		this.squareClicked = null;
		this.pieceClicked = null;

		this.sender.repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		if (this.pieceClicked != null)
		{
			this.isDraggingPiece = true;
			this.mouseDragLocation = e.getPoint();

			this.sender.repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	// -------------------------------------------------------------------------
	// Getters and Setters
	// -------------------------------------------------------------------------

	public Checkerboard getSender()
	{
		return sender;
	}

	public void setSender(Checkerboard sender)
	{
		this.sender = sender;
	}

}

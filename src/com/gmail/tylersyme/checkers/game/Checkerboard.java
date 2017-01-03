package com.gmail.tylersyme.checkers.game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JPanel;

/**
 * A Checkerboard is responsible <b>only</b> for drawing the visible
 * checkerboard and its pieces to the screen from within its JFrame.
 */
public class Checkerboard extends JPanel
{
	// These represent the color of the squares which compose the visual
	// checkerboard.
	private static Color TILE_COLOR_1 = new Color(140, 102, 78);
	private static Color TILE_COLOR_2 = new Color(79, 45, 24);

	// These represent the color of the checker pieces.
	private static Color USER_PIECE_COLOR = Color.WHITE;
	private static Color OPPONENT_PIECE_COLOR = Color.RED;

	// The width and height of the checkerboard
	public static int BOARD_SIZE = 720;

	private static final long serialVersionUID = -7745329248389094195L;

	// -------------------------------------------------------------------------

	private CheckerGamePanel checkerGamePanel;
	private int boardSize;
	private GameLogic gameLogic;
	private MouseHandler mouseListener; // Handles checkerpiece movement w/Mouse

	private boolean gameEnded = false;
	private boolean didUserWin = false;
	private boolean wasDraw = false;

	/**
	 * The checkerboard's size will be set to the default value of
	 * {@link BOARD_SIZE}.
	 */
	public Checkerboard(CheckerGamePanel checkerGamePanel)
	{
		this.checkerGamePanel = checkerGamePanel;

		this.boardSize = BOARD_SIZE;
		this.setPreferredSize(new Dimension(BOARD_SIZE, BOARD_SIZE));
		this.mouseListener = new MouseHandler(this);

		this.addMouseListener(mouseListener);
		this.addMouseMotionListener(mouseListener);
	}

	public Checkerboard(CheckerGamePanel checkerGamePanel, int boardSize)
	{
		this.checkerGamePanel = checkerGamePanel;

		this.boardSize = boardSize;
		this.setPreferredSize(new Dimension(this.boardSize, this.boardSize));

		this.mouseListener = new MouseHandler(this);

		this.addMouseListener(mouseListener);
		this.addMouseMotionListener(mouseListener);
	}

	/**
	 * Starts a new multiplayer game between two players.
	 * 
	 * @param output
	 * @param input
	 */
	public void startNewMultiplayerGame(
			ObjectOutputStream output,
			ObjectInputStream input)
	{
		this.gameEnded = false;
		this.didUserWin = false;
		this.wasDraw = false;
		
		this.gameLogic = new GameLogicMP(this, output, input);
		this.gameLogic.startNewGame();
	}

	public void startNewSingleplayerGame()
	{
		// NOT YET IMPLEMENTED //
	}

	// -------------------------------------------------------------------------
	// Drawing Methods
	// -------------------------------------------------------------------------

	/**
	 * This is a universal function which allows the user to edit the color
	 * scheme of all checkerboards.
	 * 
	 * @param color1
	 * @param color2
	 */
	public static void changeBoardTheme(Color color1, Color color2)
	{
		TILE_COLOR_1 = color1;
		TILE_COLOR_2 = color2;
	}

	@Override
	public void paintComponent(Graphics g)
	{
		this.drawGame(g);
	}

	public void showGameEnded(boolean didUserWin)
	{
		this.gameEnded = true;
		this.didUserWin = didUserWin;
	}
	
	public void showGameDraw()
	{
		this.gameEnded = true;
		this.wasDraw = true;
	}

	/**
	 * Used to draw a square of the checkboard.
	 * 
	 * @param g
	 * @param fillColor The color of the square
	 * @param rawLoc The exact top-left position of the square
	 */
	private void drawSquare(Graphics g, Color fillColor, Point rawLoc)
	{
		int squareSize = this.getSquareSize();

		g.setColor(fillColor);
		g.fillRect(rawLoc.x, rawLoc.y, squareSize, squareSize);
	}

	/**
	 * Used to draw a checker piece. This piece's scale will be determined
	 * by the square size of the checkerboard.
	 * 
	 * @param g
	 * @param fillColor The color of the square
	 * @param rawLoc The exact top-left position of the square
	 * @param isKing Determines if the piece has been "kinged"
	 */
	private void drawCheckerPiece(
			Graphics g,
			Color fillColor,
			Point rawLoc,
			boolean isKing)
	{
		int pieceSize = this.getPieceSize();

		g.setColor(fillColor);
		g.fillOval(rawLoc.x, rawLoc.y, pieceSize, pieceSize);
		g.setColor(Color.BLACK);
		g.drawOval(rawLoc.x, rawLoc.y, pieceSize, pieceSize);

		g.drawOval(
				(int) (rawLoc.x += pieceSize * .15),
				(int) (rawLoc.y += pieceSize * .15),
				(int) (pieceSize * .70),
				(int) (pieceSize * .70));

		if (isKing)
		{
			g.setFont(new Font("TimesRoman", Font.PLAIN, 16));
			g.drawString(
					"K",
					(int) (rawLoc.x + (pieceSize * .28)),
					(int) (rawLoc.y + (pieceSize * .43)));
		}
	}

	/**
	 * Draws every checker piece to the screen in the corresponding square.<br>
	 * This uses the Checkerboard's GameLogic to determine which pieces to draw
	 * and at what location.
	 * 
	 * @param g
	 */
	private void drawCheckerPieces(Graphics g)
	{
		for (CheckerPiece piece : this.gameLogic.getAllCheckerPieces())
		{
			// Calculates the raw point based upon the piece's grid location
			Point rawLoc = this.calculateRawPieceLocation(
					piece.getX(),
					piece.getY());

			this.drawCheckerPiece(g, (piece.isUserPiece()) ? (USER_PIECE_COLOR)
					: (OPPONENT_PIECE_COLOR), rawLoc, piece.isKing());
		}
	}

	/**
	 * Draws the full checkerboard to the screen.
	 * 
	 * @param g
	 * @param rawLoc The exact top-left position of the square
	 */
	private void drawCheckerboard(Graphics g, Point rawLoc)
	{
		int squareSize = this.getSquareSize();

		// Start from the top left corner and start drawing each square up and
		// down
		for (int xRow = 0; xRow < 8; xRow++)
		{
			if (xRow > 0)
			{
				rawLoc.y -= (squareSize * 8);
				rawLoc.x += squareSize;
			}
			for (int yColumn = 0; yColumn < 8; yColumn++)
			{
				// Calculates which of the two alternating square colors will
				// be used
				Color squareColor = ((2 + xRow + yColumn) % 2 == 0) ? (TILE_COLOR_1)
						: (TILE_COLOR_2);

				this.drawSquare(g, squareColor, rawLoc);

				rawLoc.y += squareSize;
			}
		}
	}

	/**
	 * Draws a "ghost" checker piece that is used when the user clicks and
	 * drags his/her piece across the screen. It is used to mark where the
	 * selected piece would be placed if the user were to release the mouse
	 * and will follow the mouse as it is dragged.
	 * 
	 * @param g
	 */
	private void drawDraggedPiece(Graphics g)
	{
		if (this.mouseListener.isDraggingPiece)
		{
			Point mouseDragLoc = this.mouseListener.mouseDragLocation;
			Point squareHover = MouseHandler.getSquareAt(
					this,
					mouseDragLoc.x,
					mouseDragLoc.y);
			Point rawLoc = this.calculateRawPieceLocation(
					squareHover.x,
					squareHover.y);
			CheckerPiece piece = this.mouseListener.pieceClicked;

			// Copies the color scheme and applies a 50% alpha (transparency)
			Color transparentCheckerPiece = new Color(
					USER_PIECE_COLOR.getRed(), USER_PIECE_COLOR.getGreen(),
					USER_PIECE_COLOR.getBlue(), 127);

			// Draw the "ghost" checker piece marker
			this.drawCheckerPiece(
					g,
					transparentCheckerPiece,
					rawLoc,
					piece.isKing());
		}
	}

	/**
	 * Draws the entire checkerboard game. This includes checker pieces.<br>
	 * If the game has ended this will also display an end of game message.
	 * 
	 * @param g
	 */
	private void drawGame(Graphics g)
	{
		this.drawCheckerboard(g, new Point(0, 0));
		this.drawCheckerPieces(g);

		// Dragged pieces are used as markers when the user clicks and drags on
		// one of his pieces
		this.drawDraggedPiece(g);

		if (this.gameEnded)
		{
			String gameEndMsg;
			Color msgColor;
			Color msgBoxColor;
			if (this.wasDraw == false)
			{
				if (this.didUserWin)
				{
					gameEndMsg = "You Are the Winner!";
					msgColor = USER_PIECE_COLOR.darker();
					msgBoxColor = OPPONENT_PIECE_COLOR.darker();
				} else
				{
					gameEndMsg = "Your Opponent is the Winner";
					msgColor = OPPONENT_PIECE_COLOR.darker();
					msgBoxColor = USER_PIECE_COLOR.darker();
				}
			} else
			{
				gameEndMsg = "The Game Was a Draw";
				msgColor = USER_PIECE_COLOR.darker();
				msgBoxColor = OPPONENT_PIECE_COLOR.darker();
			}

			// Center the text on the checkerboard
			g.setFont(new Font("TimesRoman", Font.PLAIN, 43));
			FontRenderContext context = ((Graphics2D) g).getFontRenderContext();
			TextLayout txt = new TextLayout(gameEndMsg, g.getFont(), context);
			Rectangle2D bounds = txt.getBounds();

			int x = (int) ((getWidth() - (int) bounds.getWidth()) / 2);
			int y = (int) ((getHeight() - (bounds.getHeight() - txt
					.getDescent())) / 2);
			y += txt.getAscent() - txt.getDescent();

			// Draw the centered message to the screen

			g.setColor(msgBoxColor); // Set drawing color
			g.fillRect(
					(int) x - 5,
					(int) (y - (txt.getAscent() - txt.getDescent())) - 5,
					(int) bounds.getWidth() + 10,
					(int) bounds.getHeight() + 10 + ((int) txt.getDescent()));
			g.setColor(msgColor); // Set drawing color
			g.drawString(gameEndMsg, x, y);
			
		}
	}

	// -------------------------------------------------------------------------
	// Drawing Assistance Methods
	// -------------------------------------------------------------------------

	/**
	 * A checkerboard is made up of 8x8 number of "tiles".<br>
	 * This method will return the width a square must be in order to fit
	 * exactly 8 of them across a checkerboard. It simply returns the size
	 * of the checkerboard divided by 8 to achieve this.
	 * 
	 * @return The appropriate width/height this square must be
	 */
	public int getSquareSize()
	{
		return (boardSize / 8);
	}

	/**
	 * Will always be 83% the size of a checkerboard square.
	 * 
	 * @return The appropriate width/height this checker piece must be
	 */
	public int getPieceSize()
	{
		// Will be 75% the size of the square
		return (int) (this.getSquareSize() * .83);
	}

	/**
	 * Calculates the raw x and y pixel draw location of a checkerpiece at the
	 * given x and y square on the checkerboard.
	 * 
	 * @param x The x position of the square
	 * @param y The y position of the square
	 * @return The Top-Left hand Point of the checkerpiece
	 */
	private Point calculateRawPieceLocation(int x, int y)
	{
		int sizeDifference = this.getSquareSize() - this.getPieceSize();
		Point rawLoc = new Point(x * this.getPieceSize()
				+ ((sizeDifference * x) + sizeDifference / 2), y
				* this.getPieceSize()
				+ ((sizeDifference * y) + sizeDifference / 2));

		return rawLoc;
	}

	// -------------------------------------------------------------------------
	// Getters and Setters
	// -------------------------------------------------------------------------

	public CheckerGamePanel getCheckerGamePanel()
	{
		return checkerGamePanel;
	}

	public int getBoardSize()
	{
		return boardSize;
	}

	public void setBoardSize(int pixelSize)
	{
		this.boardSize = pixelSize;
	}

	public GameLogic getGameLogic()
	{
		return gameLogic;
	}

	public void setGameLogic(GameLogic gameLogic)
	{
		this.gameLogic = gameLogic;
	}

}

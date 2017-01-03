package com.gmail.tylersyme.checkers.game;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.gmail.tylersyme.checkers.CheckersWindow;
import com.gmail.tylersyme.checkers.connection.ConnectionHandler;
import com.gmail.tylersyme.checkers.connection.PacketHandler;

public class GameLogicMP extends GameLogic
{
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private PacketHandler packetHandler;

	public GameLogicMP(Checkerboard checkerboard, ObjectOutputStream output,
			ObjectInputStream input)
	{
		super(checkerboard);

		this.output = output;
		this.input = input;
	}

	@Override
	public void startNewGame()
	{
		this.packetHandler = new PacketHandler(this, this.output, this.input);
		
		// Listens for incoming packets from opponent
		this.packetHandler.startListening();

		this.setupCheckerboard();

		if (ConnectionHandler.isHost)
		{
			this.checkerboard.getCheckerGamePanel().getMainWindow().setTitle("Server");
			
			Random r = new Random();
			int firstTurn = r.nextInt(2);

			if (firstTurn == 0)
			{
				this.isUsersTurn = true;
			} else
			{
				this.isUsersTurn = false;
			}
			this.getCheckerboard().getCheckerGamePanel()
					.updateUsersTurnLabel(this.isUsersTurn);

			// Will set the turn of the opponent to the opposite of the server's
			// turn
			this.packetHandler.sendPlayerTurnPacket(!this.isUsersTurn);
			this.packetHandler.sendStartGamePacket();

			this.setHasGameStarted(true);
		} else {
			this.checkerboard.getCheckerGamePanel().getMainWindow().setTitle("Client");
		}
		
		//this.packetHandler.sendEndGamePacket(false);
	}

	@Override
	public boolean handleCheckerMovement(
			CheckerPiece movedPiece,
			int newX,
			int newY)
	{
		boolean isValid = false;

		// 1. The game must be ongoing
		// 2. The movement must be valid (unless it is the opponent's piece)
		// 3. The user cannot have already moved (unless that move was a jump)
		// 4. If the user's last move was a jump, next move must also be a jump
		if (this.hasGameStarted
				&& (isValidMove(movedPiece, newX, newY) == true && (this.hasUserMoved == false || this
						.isJump(movedPiece, newX, newY)))

				|| movedPiece.isUserPiece() == false)
		{
			// Will send a packet only if this user's piece was moved
			if (movedPiece.isUserPiece())
			{
				this.packetHandler.sendCheckerMovePacket(
						movedPiece.getX(),
						movedPiece.getY(),
						newX,
						newY);

				// Will allow the user to possibly jump multiple times if their
				// first move was a jump. Otherwise their turn ends.
				if (this.isJump(movedPiece, newX, newY))
				{
					this.hasUserMoved = true;
				} else
				{
					// User must move before turn can end
					this.hasUserMoved = true;
					this.endTurn();
				}

				// King this piece if it reaches the row at the back
				if (newY == 0)
				{
					movedPiece.setKing(true);
				}
			} else
			{
				// King this piece if it reaches the row at the front
				if (newY == 7)
				{
					movedPiece.setKing(true);
				}
			}

			// Internally update the 2D grid list which is used to store pieces
			// at their x and y location
			this.gridCheckerPieces.get(movedPiece.getX()).set(
					movedPiece.getY(),
					null);
			this.gridCheckerPieces.get(newX).set(newY, movedPiece);

			// Checks to see if a piece was captured
			CheckerPiece capturedPiece = this.getCapturedPiece(
					movedPiece,
					newX,
					newY);
			if (capturedPiece != null)
			{
				this.captureCheckerPiece(capturedPiece);
			}

			// Determines if a player has won the game
			if (this.gameEnded())
			{
				this.endGame(this.didUserWin());
			}

			this.checkerboard.repaint();

			isValid = true;
		}

		return isValid;
	}

	@Override
	public void endTurn()
	{
		if (this.isUsersTurn && this.hasUserMoved)
		{
			this.isUsersTurn = false;
			this.hasUserMoved = false;
			this.getCheckerboard().getCheckerGamePanel()
					.updateUsersTurnLabel(false);

			// Let the other player know it's their turn
			this.packetHandler.sendPlayerTurnPacket(true);
		}
	}

	@Override
	public void endGame(boolean didUserWin)
	{
		if (this.hasGameStarted)
		{
			this.setHasGameStarted(false);

			this.packetHandler.sendEndGamePacket(!didUserWin);
			this.getCheckerboard().getCheckerGamePanel().disableButtons();

			this.getCheckerboard().showGameEnded(didUserWin);

			this.getCheckerboard().repaint();

			Thread returnToMenu = new Thread(new Runnable() {

				@Override
				public void run()
				{
					try
					{
						Thread.sleep(2000);
						
						packetHandler.setListening(false);
						try
						{
							input.close();
							output.close();
						} catch (IOException e)
						{
							e.printStackTrace();
						}

						CheckersWindow checkerWindow = (CheckersWindow) SwingUtilities
								.getWindowAncestor(getCheckerboard());

						checkerWindow.openMainMenu();
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			});

			returnToMenu.start();
		}
	}

	@Override
	public void drawGame()
	{
		if (this.hasGameStarted)
		{
			this.setHasGameStarted(false);

			this.packetHandler.sendDrawResponsePacket(true);
			this.getCheckerboard().getCheckerGamePanel().disableButtons();

			this.getCheckerboard().showGameDraw();

			this.getCheckerboard().repaint();

			Thread returnToMenu = new Thread(new Runnable() {

				@Override
				public void run()
				{
					try
					{
						Thread.sleep(2000);
						packetHandler.setListening(false);

						CheckersWindow checkerWindow = 
								(CheckersWindow) SwingUtilities
								.getWindowAncestor(getCheckerboard());

						checkerWindow.openMainMenu();
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			});

			returnToMenu.start();
		}
	}

	@Override
	public void requestDraw()
	{
		if (this.hasGameStarted)
		{
			this.packetHandler.sendRequestDrawPacket();
		}
	}

	@Override
	public void opponentRequestedDraw()
	{
		Object[] options = { "Accept", "Reject" };

		// Open a custom dialog window with a request and reject option
		int selection = JOptionPane.showOptionDialog(
				this.getCheckerboard(),
				"Your opponent has requested a draw.",
				"End With a Draw?",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				options[0]);

		if (selection == 0) // The 'Accept' Button
		{
			this.drawGame(); // The game is a draw
		}
	}

	@Override
	public void sendChatMessage(String chatMsg)
	{
		this.packetHandler.sendChatMessagePacket(chatMsg);
	}
}

package com.gmail.tylersyme.checkers.connection;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;

import com.gmail.tylersyme.checkers.CheckersWindow;
import com.gmail.tylersyme.checkers.game.CheckerPiece;
import com.gmail.tylersyme.checkers.game.GameLogic;

public class PacketHandler
{
	private GameLogic gameLogic;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private boolean isListening = false;

	public PacketHandler(GameLogic gameLogic, ObjectOutputStream output,
			ObjectInputStream input)
	{
		this.gameLogic = gameLogic;
		this.output = output;
		this.input = input;
	}

	/**
	 * This method will constantly listen for incoming packets on a separate
	 * thread. When a packet is found and read, it is then handled according
	 * to its packet ID.<br>
	 * If the connection is lost, this method stop itself from listening for
	 * more packets.
	 * 
	 * @see PacketHandler#handlePacket(String)
	 */
	public void startListening()
	{
		Thread listenThread = new Thread(new Runnable() {

			@Override
			public void run()
			{
				isListening = true;
				
				// Will continue to listen until told otherwise
				while (isListening)
				{
					try
					{
						String packet = (String) input.readObject();
						processPacket(packet);
					} catch (SocketException e) // Lost Connection
					{
						System.out.println("Lost Connection...");
						
						// Loss of connection stops listening for packets
						isListening = false;
					} catch (ClassNotFoundException e)
					{
						e.printStackTrace();
					} catch (EOFException e) // IO Stream closed
					{ 
						isListening = false;
					} catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		});
		listenThread.start();
	}

	/**
	 * This will handle any received packets based upon the packet's ID number.
	 * 
	 * @param packet The unparsed packet data
	 * @throws IOException If an invalid packet ID number was received
	 */
	private void processPacket(String packet) throws IOException
	{
		String[] parsedPacket = packet.split(":");
		int ID = Integer.parseInt(parsedPacket[0]);

		switch (ID)
		{
			case (0): // Game Started Packet
				this.gameLogic.setHasGameStarted(true);
				System.out.println("Client Game Has Started");

				break;
			case (1): // Player Change Turn Packet
				boolean usersTurn = Boolean.parseBoolean(parsedPacket[1]);

				this.gameLogic.setUsersTurn(usersTurn);
				this.gameLogic.getCheckerboard().getCheckerGamePanel()
						.updateUsersTurnLabel(usersTurn);

				break;
			case (2): // Checker Moved Packet
				int oldX = Integer.parseInt(parsedPacket[1]);
				int oldY = Integer.parseInt(parsedPacket[2]);
				int newX = Integer.parseInt(parsedPacket[3]);
				int newY = Integer.parseInt(parsedPacket[4]);

				oldX = (7 - oldX);
				oldY = (7 - oldY);
				newX = (7 - newX);
				newY = (7 - newY);

				CheckerPiece piece = this.gameLogic.getCheckerPieceAt(
						oldX,
						oldY);
				piece.move(newX, newY);

				break;
			case (3): // Game Ended Packet
				this.gameLogic.setHasGameStarted(false);

				this.gameLogic.getCheckerboard().showGameEnded(
						Boolean.parseBoolean(parsedPacket[1]));
				this.gameLogic.getCheckerboard().getCheckerGamePanel()
						.disableButtons();

				// Returns the player to the menu screen after two seconds
				Thread returnToMenu = new Thread(new Runnable() {

					@Override
					public void run()
					{
						try
						{
							isListening = false;
							
							try
							{
								input.close();
								output.close();
							} catch (IOException e)
							{
								e.printStackTrace();
							}
							
							Thread.sleep(2000);

							CheckersWindow checkerWindow = 
									(CheckersWindow) gameLogic
											.getCheckerboard()
											.getCheckerGamePanel()
											.getTopLevelAncestor();

							checkerWindow.openMainMenu();
						} catch (InterruptedException e)
						{
							e.printStackTrace();
						}
					}
				});

				returnToMenu.start();

				this.gameLogic.getCheckerboard().repaint();

				break;
			case (4): // Request Draw Packet
				this.gameLogic.opponentRequestedDraw();

				break;
			case (5): // Request Draw Response Packet
				boolean didAccept = Boolean.parseBoolean(parsedPacket[1]);
				if (didAccept)
				{
					this.gameLogic.drawGame();
				} else
				{
					// SEND ALERT MESSAGE HERE //
				}

				break;

			case (6): // Sent Chat Message Packet
				String chatMsg = parsedPacket[1];

				this.gameLogic.getCheckerboard().getCheckerGamePanel()
						.showMessage(chatMsg, false);

				break;
			default: // An unrecognized packet ID was sent
				throw new IOException("Invalid Packet ID was Received: ");
		}
	}

	// -------------------------------------------------------------------------
	// Packet Types
	// -------------------------------------------------------------------------

	/**
	 * This packet will cause the receiver's game to start.
	 */
	public void sendStartGamePacket()
	{
		int ID = 0;

		try
		{
			output.writeObject(new String("" + ID + ":"));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * This packet will cause the receiver's turn to change to the given
	 * boolean value.
	 * 
	 * @param isOpponentTurn
	 */
	public void sendPlayerTurnPacket(boolean isOpponentTurn)
	{
		int ID = 1;

		try
		{
			output.writeObject(new String("" + ID + ":" + isOpponentTurn));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * This packet will cause the receiver's checkerboard to process a checker
	 * movement from its perspective.
	 * 
	 * @param oldX
	 * @param oldY
	 * @param newX
	 * @param newY
	 */
	public void sendCheckerMovePacket(int oldX, int oldY, int newX, int newY)
	{
		int ID = 2;

		try
		{
			output.writeObject(new String("" + ID + ":" + oldX + ":" + oldY
					+ ":" + newX + ":" + newY));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * This packet will cause the receiver's game declare them either the
	 * winner or loser of the checker game.
	 */
	public void sendEndGamePacket(boolean didUserWin)
	{
		int ID = 3;

		try
		{
			output.writeObject(new String("" + ID + ":" + didUserWin));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * This packet will cause the receiver's game to open a dialog window which
	 * will allow him/her to either accept or reject the proposal.
	 */
	public void sendRequestDrawPacket()
	{
		int ID = 4;

		try
		{
			output.writeObject(new String("" + ID + ":"));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * This packet will cause the receiver to be alerted of their opponent's
	 * acceptance or rejection of a draw request.
	 */
	public void sendDrawResponsePacket(boolean didAccept)
	{
		int ID = 5;

		try
		{
			output.writeObject(new String("" + ID + ":" + didAccept));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * This packet will cause the receiver to be alerted of their opponent's
	 * acceptance or rejection of a draw request.
	 */
	public void sendChatMessagePacket(String chatMsg)
	{
		int ID = 6;

		try
		{
			output.writeObject(new String("" + ID + ":" + chatMsg));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	// -------------------------------------------------------------------------
	// Getters and Setters
	// -------------------------------------------------------------------------
	
	public boolean isListening()
	{
		return this.isListening;
	}
	
	public void setListening(boolean isListening)
	{
		this.isListening = isListening;
	}

}

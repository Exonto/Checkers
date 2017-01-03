package com.gmail.tylersyme.checkers.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import com.gmail.tylersyme.checkers.CheckersWindow;

/**
 * This class is responsible for handling
 */
public class ConnectionHandler
{

	public static boolean isHost = false;
	
	public static boolean isWaiting = false;
	public static boolean isConnecting = false;
	
	public static ServerSocket server;
	public static Socket connection;
	public static final int PORT = 38977;

	private static ObjectOutputStream output;
	private static ObjectInputStream input;

	/**
	 * This method will create a server which will then wait for an opponent
	 * to connect to the server's ip address at port 38977.<br>
	 * During this time, the user cannot attempt to connect to another checker 
	 * game.
	 * 
	 * @param mainWindow
	 */
	public static void hostGame(final CheckersWindow mainWindow)
	{
		Thread hostThread = new Thread(new Runnable() {

			@Override
			public void run()
			{
				// Cannot host if already hosting a game or connecting to
				// another game
				if (isConnecting == false && isWaiting == false)
				{
					isWaiting = true;
					try
					{
						// Start Server
						server = new ServerSocket(PORT, 1);

						// Waits for an opponent to join the game
						waitForOpponent();
						// When an opponent is found, input and output streams
						// are then established
						establishStreams();
						
						// Tells the main window that a multiplayer game has 
						// begun
						mainWindow.startNewMultiplayerGame(output, input);

					} catch (IOException ex)
					{
						ex.printStackTrace();
					}
				}
			}

		});
		hostThread.start();
	}

	/**
	 * Should occur when this computer connects to a game being hosted by 
	 * another computer.<br>
	 * This will establish input and output streams to the host.
	 * 
	 * @param mainWindow
	 * @param ipAddress
	 */
	public static void connectToGame(final CheckersWindow mainWindow, 
									 final String ipAddress)
	{
		Thread findThread = new Thread(new Runnable() {

			@Override
			public void run()
			{
				// Cannot connect if already hosting a game or connecting to
				// another game
				if (isConnecting == false && isWaiting == false)
				{
					isConnecting = true;
					
					boolean wasSuccessful = connectToServer(ipAddress);
					if (wasSuccessful)
					{
						establishStreams();

						mainWindow.startNewMultiplayerGame(output, input);
					}
					isConnecting = false;
				}
			}

		});
		findThread.start();
	}
	
	private static boolean connectToServer(String ipAddress)
	{
		try
		{
			connection = new Socket(InetAddress.getByName(ipAddress), PORT);
			return true;
		} catch (ConnectException connectionFailed)
		{
			System.out.println("Server Could Not Be Found...");
		} catch (UnknownHostException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	public static void stopSearch()
	{
		System.out.println("Shutting Down Server");
		isWaiting = false;
		isHost = false;
		try
		{
			output.close();
			input.close();
			server.close();
			connection.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Continually waits for an opponent to arrive. When an opponent arrives,
	 * this will cause the server to accept the incoming connections, thus
	 * beginning a new game.
	 */
	private static void waitForOpponent()
	{
		try
		{
			System.out.println("Waiting for Opponent...");
			connection = server.accept();
			System.out.println("Opponent Found!");

			isHost = true;
		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	public static void disconnect()
	{
		System.out.println("Disconnecting");
		isConnecting = false;
		try
		{
			output.close();
			input.close();
			connection.close();
			if (server != null)
			{
				server.close();
			}
		} catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}

	private static void establishStreams()
	{
		System.out.println("Establishing Stream Connections");
		try
		{
			output = new ObjectOutputStream(connection.getOutputStream());
			output.flush(); // Precautionary Measure
			input = new ObjectInputStream(connection.getInputStream());
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}

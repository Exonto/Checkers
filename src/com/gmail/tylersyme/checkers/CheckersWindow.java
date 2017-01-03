package com.gmail.tylersyme.checkers;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import com.gmail.tylersyme.checkers.connection.ConnectionHandler;
import com.gmail.tylersyme.checkers.game.CheckerGamePanel;

public class CheckersWindow extends JFrame
{
	private static final long serialVersionUID = 2398964421487122511L;

	// -------------------------------------------------------------------------

	private MainMenuPanel mainMenuPanel;
	private MultiplayerPanel multiplayerPanel;
	private CheckerGamePanel checkerGamePanel;

	/**
	 * Initializes the window and all of its attributes.
	 */
	public CheckersWindow()
	{
		this.initializeFrame();
	}

	/**
	 * This will cause the window to launch a new multiplayer checkers game.
	 * 
	 * @param output
	 * @param input
	 */
	public void startNewMultiplayerGame(
			ObjectOutputStream output,
			ObjectInputStream input)
	{
		this.setContentPane(this.checkerGamePanel);

		// Tells the internal Checkerboard (which contains the game logic)
		// to start the multiplayer game
		this.checkerGamePanel.getCheckerboard().startNewMultiplayerGame(
				output,
				input);

		this.revalidate();
		this.pack();
		this.centerWindow();
		this.repaint(); // Repaints the entire window
	}
	
	public void openMainMenu()
	{
		ConnectionHandler.disconnect();
		
		this.setContentPane(this.mainMenuPanel);
		this.pack();
	}
	
	public void openMultiplayerMenu()
	{
		this.setContentPane(this.multiplayerPanel);
		this.pack();
	}

	// -------------------------------------------------------------------------
	// Initialization Assistance
	// -------------------------------------------------------------------------

	/**
	 * Initializes all attributes of this JFrame.<br>
	 * Also initializes and adds the main menu panel to the window.
	 */
	private void initializeFrame()
	{
		// -- Initialize the Frame -- //

		this.setTitle("Checkers");
		this.setResizable(false);
		ImageIcon icon = new ImageIcon("checkerbackground.png");
		this.setIconImage(icon.getImage());

		// Closing this window causes the program to exit
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// -- Initialize Content Panes -- //

		// Note - The constructor will setup each content pane's components
		this.mainMenuPanel = new MainMenuPanel(this);
		this.multiplayerPanel = new MultiplayerPanel(this);
		this.checkerGamePanel = new CheckerGamePanel(this);

		this.setContentPane(this.mainMenuPanel);
		this.pack(); // Will pack to the size of mainMenu's preferred size
		this.centerWindow();
	}

	/**
	 * Centers this window to the screen based upon the monitor's screen
	 * resolution and this frame's pre-defined width and height.
	 */
	private void centerWindow()
	{
		Dimension resolution = this.getScreenResolution();

		// Centers this window's location
		this.setLocation((resolution.width / 2) - (this.getWidth() / 2),
				(resolution.height / 2) - (this.getHeight() / 2));
	}

	/**
	 * @return The screen resolution in terms of width and height
	 */
	private Dimension getScreenResolution()
	{
		return Toolkit.getDefaultToolkit().getScreenSize();
	}

	// -------------------------------------------------------------------------
	// Getters
	// -------------------------------------------------------------------------

	public MainMenuPanel getMainMenuPanel()
	{
		return mainMenuPanel;
	}

	public CheckerGamePanel getCheckerGamePanel()
	{
		return checkerGamePanel;
	}

	public MultiplayerPanel getMultiplayerPanel()
	{
		return multiplayerPanel;
	}

}

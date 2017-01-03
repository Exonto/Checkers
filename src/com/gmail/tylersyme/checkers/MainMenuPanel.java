package com.gmail.tylersyme.checkers;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.gmail.tylersyme.checkers.connection.ConnectionHandler;

public class MainMenuPanel extends JPanel implements ActionListener,
													 FocusListener
{

	private static final long serialVersionUID = -6081609502338751213L;

	// -------------------------------------------------------------------------

	private JButton singleplayer = new JButton("Singleplayer");
	private JButton hostGame = new JButton("Host Game");
	private JButton findGame = new JButton("Find Game");
	private JButton settings = new JButton("Game Settings");
	private JButton exit = new JButton("Quit");
	
	private JTextField enterIP = new JTextField("Enter Server IP");
	private boolean enterIPFocusGiven = false; // Used to remove description

	private CheckersWindow mainWindow;

	/**
	 * Initializes the panel and all of its components.
	 */
	public MainMenuPanel(CheckersWindow mainWindow)
	{
		this.initializePanel();

		this.mainWindow = mainWindow;
	}

	/**
	 * Initializes all components of this JPanel.
	 */
	public void initializePanel()
	{
		this.setPreferredSize(new Dimension(550, 350));

		// -- Define the Layout -- //

		this.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		// -- Default Inset of a 3 Pixel Top/Bottom Margin -- //
		constraints.insets = new Insets(3, 0, 3, 0);

		// -- Initialize Child Components -- //
		this.singleplayer.setFocusPainted(false);
		this.singleplayer.addActionListener(this);
		this.singleplayer.setEnabled(false);

		this.hostGame.setFocusPainted(false);
		this.hostGame.addActionListener(this);
		
		this.findGame.setFocusPainted(false);
		this.findGame.addActionListener(this);

		this.settings.setFocusPainted(false);

		this.exit.setFocusPainted(false);
		this.exit.addActionListener(this);;
		
		this.enterIP.addFocusListener(this);
		this.enterIP.setPreferredSize(new Dimension(130, 25));
		
		// -- Add Child Components w/Constraints -- //

		constraints.gridx = 0;

		constraints.gridy = 1;
		this.add(singleplayer, constraints);
		
		constraints.gridy = 2;
		this.add(hostGame, constraints);
		
		constraints.gridy = 3;
		this.add(findGame, constraints);
		
		constraints.gridx = 1;
		this.add(enterIP, constraints);
		
		constraints.gridx = 0;
		constraints.gridy = 4;
		this.add(settings, constraints);
		
		constraints.gridy = 5;
		this.add(exit, constraints);
	}

	// -------------------------------------------------------------------------
	// Event Handlers
	// -------------------------------------------------------------------------

	/**
	 * Handles button presses.
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == hostGame)
		{
			ConnectionHandler.hostGame(this.mainWindow);
		} else if (e.getSource() == findGame)
		{
			ConnectionHandler.connectToGame(this.mainWindow, 
											this.enterIP.getText());
		} else if (e.getSource() == exit)
		{
			// Closes the program
			((JFrame) this.getTopLevelAncestor()).dispose();
		} else if (e.getSource() == enterIP)
		{
			if (this.enterIPFocusGiven == false)
			{
				this.enterIPFocusGiven = true;
				
				this.enterIP.setText("");
			}
		}
	}
	
	@Override
	public void focusGained(FocusEvent e)
	{
		if (e.getSource() == enterIP)
		{
			if (this.enterIPFocusGiven == false)
			{
				this.enterIPFocusGiven = true;
				
				this.enterIP.setText("");
			}
		}
	}

	@Override
	public void focusLost(FocusEvent e)
	{ }

	// -------------------------------------------------------------------------
	// Getters and Setters
	// -------------------------------------------------------------------------

	public CheckersWindow getMainWindow()
	{
		return mainWindow;
	}

}

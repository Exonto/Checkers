package com.gmail.tylersyme.checkers;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class MultiplayerPanel extends JPanel implements ActionListener
{

	private static final long serialVersionUID = -6081609502338751213L;

	// -------------------------------------------------------------------------

	private JButton hostGame = new JButton("Host Game");
	private JButton joinGame = new JButton("Join Game");
	private JButton returnToMenu = new JButton("Back");

	private CheckersWindow mainWindow;

	/**
	 * Initializes the panel and all of its components.
	 */
	public MultiplayerPanel(CheckersWindow mainWindow)
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

		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup().addComponent(this.hostGame).addComponent(this.joinGame));
		
		
		GridBagConstraints constraints = new GridBagConstraints();

		// -- Default Inset of a 3 Pixel Top/Bottom Margin -- //
		constraints.insets = new Insets(3, 0, 3, 0);

		// -- Initialize Child Components -- //
		this.hostGame.setFocusPainted(false);
		this.hostGame.addActionListener(this);

		this.joinGame.setFocusPainted(false);
		this.joinGame.addActionListener(this);

		this.returnToMenu.setFocusPainted(false);
		this.returnToMenu.addActionListener(this);
		
		// -- Add Child Components w/Constraints -- //

		constraints.gridx = 1;
		constraints.gridy = 1;

		this.add(hostGame);
		constraints.gridx = 3;
		this.add(joinGame);
		/*constraints.gridx = 2;
		constraints.gridy = 2;
		this.add(returnToMenu, constraints);*/
	}

	// -------------------------------------------------------------------------
	// Event Handlers
	// -------------------------------------------------------------------------

	@Override
	public void actionPerformed(ActionEvent e)
	{
		/*if (e.getSource() == multiplayer)
		{
			ConnectionHandler.hostGame(this.mainWindow);
		} else if (e.getSource() == singleplayer)
		{
			ConnectionHandler.connectToGame(this.mainWindow);
		}*/
	}

	// -------------------------------------------------------------------------
	// Getters and Setters
	// -------------------------------------------------------------------------

	public CheckersWindow getMainWindow()
	{
		return mainWindow;
	}
	
}

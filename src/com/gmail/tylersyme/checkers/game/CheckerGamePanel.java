package com.gmail.tylersyme.checkers.game;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import com.gmail.tylersyme.checkers.CheckersWindow;

/**
 * <p>
 * This panel is designed to be the parent container of a full checker game.<br>
 * It will contain:
 * <ul>
 * <li>The Checkerboard</li>
 * <li>The Chat Window</li>
 * <li>The Resign, Draw, and End Turn Buttons</li>
 * </ul>
 * </p>
 */
public class CheckerGamePanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 3619833909126801343L;

	// -------------------------------------------------------------------------

	private CheckersWindow mainWindow;
	private Checkerboard checkerboard; // The checkerboard which will be drawn
										// to the screen

	// Non-Panel components are kept as variables for event handling purposes
	private JLabel isUsersTurnLabel;
	
	private JTextArea chatWindow;
	private JTextField typeChatMessageField;

	private JButton resignButton;
	private JButton drawButton;
	private JButton endTurnButton;

	/**
	 * Initializes the panel and all of its components.
	 */
	public CheckerGamePanel(CheckersWindow mainWindow)
	{
		this.mainWindow = mainWindow;
		this.initialize();
	}

	/**
	 * Disables the resign, draw, and end turn buttons.
	 */
	public void disableButtons()
	{
		this.resignButton.setEnabled(false);
		this.drawButton.setEnabled(false);
		this.endTurnButton.setEnabled(false);

		this.repaint();
	}

	/**
	 * Initializes this panel and all of its components.
	 */
	private void initialize()
	{
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		// A three pixel space on all sides of each grid space
		c.insets = new Insets(3, 3, 3, 3);

		// -- Panel Initialization -- //

		// This is the parent container of all components which are not the
		// checkerboard. It should appear on the right-hand side of the window.
		// All components and child panels are laid out from top to bottom.
		JPanel otherOperationsPanel = new JPanel();
		otherOperationsPanel.setLayout(new BoxLayout(otherOperationsPanel,
				BoxLayout.Y_AXIS));

		// The sole purpose of this panel is to apply FlowLayout behavior to
		// to the "Is My Turn?" indicator.
		JPanel userTurnPanel = new JPanel();

		// This is the parent container of the three buttons:
		// - Resign, Draw, and End Turn
		// Uses FlowLayout to organize buttons
		JPanel buttonPanel = new JPanel();

		// This is the checkerboard (panel) which will draw the visual
		// checkerboard to the screen.
		this.checkerboard = new Checkerboard(this);

		// -- Component Initialization and Adding -- //

		// Chat text display
		// Note - This does not need to be contained within a JPanel
		this.chatWindow = new JTextArea();
		this.chatWindow.setEditable(false);
		this.chatWindow.setFocusable(false);
		this.chatWindow.setWrapStyleWord(true);

		// @formatter:off
		
		// Chat text display's scroll pane (Vertical/Horizontal as needed)
		JScrollPane chatWindowScroll = new JScrollPane(chatWindow);
		chatWindowScroll.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		chatWindowScroll.setHorizontalScrollBarPolicy(
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		chatWindowScroll.setPreferredSize(new Dimension(300, 350));
		
		// @formatter:on

		// The field which allows the user to type a message.
		// Pressing 'Enter' will send the message.
		this.typeChatMessageField = new JTextField();
		typeChatMessageField.addActionListener(this);

		this.resignButton = new JButton("Resign");
		this.drawButton = new JButton("Draw");
		this.endTurnButton = new JButton("End Turn");

		// Removes the box highlight which appears when focus is brought to a
		// button.
		this.resignButton.setFocusPainted(false);
		this.resignButton.addActionListener(this);
		this.drawButton.setFocusPainted(false);
		this.drawButton.addActionListener(this);
		this.endTurnButton.setFocusPainted(false);
		this.endTurnButton.addActionListener(this);

		// Ordered left to right
		buttonPanel.add(resignButton);  // Far Left
		buttonPanel.add(drawButton);    // Middle
		buttonPanel.add(endTurnButton); // Far Right

		this.isUsersTurnLabel = new JLabel("Your Turn");
		this.isUsersTurnLabel.setBorder(BorderFactory.createEmptyBorder(
				3,
				0,
				3,
				0));

		userTurnPanel.add(this.isUsersTurnLabel);

		// Finally, add all the panels in the correct order
		otherOperationsPanel.add(userTurnPanel);   // Top
		otherOperationsPanel.add(chatWindowScroll);      // Middle-Top
		otherOperationsPanel.add(typeChatMessageField); // Middle-Bottom
		otherOperationsPanel.add(buttonPanel);	   // Bottom

		// -- Adding Child Panels w/Constraints -- //

		c.gridx = 0;
		c.gridy = 1;
		this.add(this.checkerboard, c);

		c.gridx = 1;
		c.gridy = 1;
		this.add(otherOperationsPanel, c);

	}

	/**
	 * This will update the JLabel indicator which shows the player whether it
	 * is their turn or their opponent's turn.
	 * 
	 * @param isUsersTurn Is it the user's turn or the opponents turn
	 */
	public void updateUsersTurnLabel(boolean isUsersTurn)
	{
		if (isUsersTurn)
		{
			this.isUsersTurnLabel.setText("Your Turn");
		} else
		{
			this.isUsersTurnLabel.setText("Opponent's Turn");
		}

		this.repaint();
	}

	/**
	 * Opens a confirmation window for requesting a draw.
	 */
	public void requestDrawConfirmation()
	{
		int selection = JOptionPane.showConfirmDialog(
				this,
				"Are you sure you want to request a draw?");

		if (selection == 0) // The 'Yes' Button
		{
			this.getCheckerboard().getGameLogic().requestDraw();
		}
	}

	/**
	 * Opens a confirmation window for resigning over the game.
	 */
	public void resignConfirmation()
	{
		int selection = JOptionPane.showConfirmDialog(
				this,
				"Are you sure you want to resign?");

		if (selection == 0) // The 'Yes' Button
		{
			this.getCheckerboard().getGameLogic().endGame(false);
		}
	}
	
	public void showMessage(String chatMsg, boolean isUser)
	{
		String sender = (isUser) ? ("You - ") : ("Opponent - ");
		this.chatWindow.append(sender + chatMsg + "\n");
	}

	// -------------------------------------------------------------------------
	// Event Handlers
	// -------------------------------------------------------------------------

	/**
	 * Called upon any JButton mouse click.<br>
	 * These will be ignored if the game has ended.
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		GameLogic gameLogic = this.getCheckerboard().getGameLogic();
		if (gameLogic.hasGameStarted)
		{
			if (source instanceof JButton)
			{
				JButton buttonPressed = (JButton) source;

				// Find the source button and handle accordingly
				if (this.resignButton == buttonPressed)
				{
					this.resignConfirmation();
				} else if (this.drawButton == buttonPressed)
				{
					this.requestDrawConfirmation();
				} else if (this.endTurnButton == buttonPressed)
				{
					gameLogic.endTurn();
				}
			} else if (source instanceof JTextField) // Send Chat Message
			{
				JTextField textField = (JTextField) source;
				
				if (textField == this.typeChatMessageField)
				{
					if (textField.getText().length() > 0)
					{
						this.showMessage(textField.getText(), true);
						gameLogic.sendChatMessage(textField.getText());
						
						textField.setText("");
					}
				}
			}
		}
	}

	// -------------------------------------------------------------------------
	// Getters and Setters
	// -------------------------------------------------------------------------

	public Checkerboard getCheckerboard()
	{
		return this.checkerboard;
	}

	public JLabel getIsUsersTurnLabel()
	{
		return isUsersTurnLabel;
	}

	public CheckersWindow getMainWindow()
	{
		return mainWindow;
	}

	public void setMainWindow(CheckersWindow mainWindow)
	{
		this.mainWindow = mainWindow;
	}

}

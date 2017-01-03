package com.gmail.tylersyme.checkers;


public class Checkers
{
	
	public static void main(String[] args)
	{
		Checkers checkersApp = new Checkers();
		checkersApp.launchApplication();
	}
	
	public CheckersWindow mainWindow;
	
	/**
	 * This is the launch point for the application.
	 */
	private void launchApplication()
	{
		this.mainWindow = new CheckersWindow();
		this.mainWindow.setVisible(true);
	}
	
}

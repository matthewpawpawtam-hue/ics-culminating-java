/* Matthew Tam
 * 12/17/2025
 * P4 ICS3U1
 * Connect Four Assignment
 * This program simulates the game connect four. There are three sound effects (background music, a sound when a piece drops down, and a sound when
 * the game is over). Additionally, player icons are customizable and the cursor icon has been changed.
 */

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import javax.swing.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class ConnectFour extends JPanel implements ActionListener, MouseListener, KeyListener
{
	static JFrame frame;
	final int BANANA = -1;
	final int STRAWBERRY = 1;
	final int EMPTY = 0;
	final int TIE = 2;
	final int SQUARE_SIZE = 60;
	final int TOP_OFFSET = 42;
	final int BORDER_SIZE = 4;
	Clip background;
	Clip plop;
	Clip yippee;
	String msg1 = "Banana";
	String msg2 = "Strawberry";
	Image cursorImage;

	int[] [] board;
	int currentPlayer;
	int currentColumn;
	Image firstImage, secondImage;

	Timer timer;

	// For drawing images offScreen (prevents Flicker)
	// These variables keep track of an off screen image object and
	// its corresponding graphics object
	Image offScreenImage;
	Graphics offScreenBuffer;

	boolean gameOver;

	// Constructor
	public ConnectFour ()
	{
		// Setting the defaults for the panel
		setPreferredSize (new Dimension (7 * SQUARE_SIZE + 2 * BORDER_SIZE + 1, (6 + 1) * SQUARE_SIZE + TOP_OFFSET + BORDER_SIZE + 1));
		setLocation (100, 10);
		setBackground (new Color (200, 200, 200));
		setLayout (new BoxLayout (this, BoxLayout.PAGE_AXIS));

		board = new int [8] [9];

		// SET UP THE MENU
		
		// Set up the Game Menu
		JMenu gameMenu = new JMenu ("Game");
		// Set up the Game MenuItems and add to gameMenu (with a separator)
		JMenuItem newOption = new JMenuItem ("New");
		JMenuItem exitOption = new JMenuItem ("Exit");
		gameMenu.add (newOption);
		gameMenu.addSeparator ();
		gameMenu.add (exitOption);
		
		// Set up the playersMenu
		JMenu playersMenu = new JMenu ("Player");
		// Create and put subMenu_p1Icons and subMenu_p2Icons in playersMenu
		JMenu subMenu_p1Icons = new JMenu ("Player 1 Icons");
		JMenu subMenu_p2Icons = new JMenu ("Player 2 Icons");
		playersMenu.add(subMenu_p1Icons);
		playersMenu.addSeparator();
		playersMenu.add(subMenu_p2Icons);
		// Set up menu items in subMenu_p1Icons and subMenu_p2Icons 
		// p1: banana, orange, apple
		// p2: strawberry, blueberry, raspberry
		JMenuItem p1_banana = new JMenuItem("Banana");
		JMenuItem p1_orange = new JMenuItem("Orange");
		JMenuItem p1_apple = new JMenuItem("Apple");
		JMenuItem p2_strawberry = new JMenuItem ("Strawberry");
		JMenuItem p2_blueberry = new JMenuItem ("Blueberry");
		JMenuItem p2_raspberry = new JMenuItem ("Raspberry");
		subMenu_p1Icons.add(p1_banana);
		subMenu_p1Icons.add(p1_orange);
		subMenu_p1Icons.add(p1_apple);
		subMenu_p2Icons.add(p2_strawberry);
		subMenu_p2Icons.add(p2_blueberry);
		subMenu_p2Icons.add(p2_raspberry);
		
		// Add final menus to JMenuBar mainMenu
		JMenuBar mainMenu = new JMenuBar ();
		mainMenu.add (gameMenu);
		mainMenu.add(playersMenu);
		// Set the menu bar for this frame to mainMenu
		frame.setJMenuBar (mainMenu);

		// Use a media tracker to make sure all of the images are
		// loaded before we continue with the program
		MediaTracker tracker = new MediaTracker (this);
		firstImage = Toolkit.getDefaultToolkit ().getImage ("banana.gif");
		tracker.addImage (firstImage, 0);
		secondImage = Toolkit.getDefaultToolkit ().getImage ("strawberry.gif");
		tracker.addImage (secondImage, 1);

		//  Wait until all of the images are loaded
		try
		{
			tracker.waitForAll ();
		}
		catch (InterruptedException e)
		{
		}

		// Set up the icon image (Tracker not needed for the icon image)
		Image iconImage = Toolkit.getDefaultToolkit ().getImage ("banana.gif");
		frame.setIconImage (iconImage);

		// Start a new game and then make the window visible
		newGame ();

		newOption.setActionCommand ("New");
		newOption.addActionListener (this);
		exitOption.setActionCommand ("Exit");
		exitOption.addActionListener (this);

		setFocusable (true); // Need this to set the focus to the panel in order to add the keyListener
		addKeyListener (this);

		addMouseListener (this);

		// set ActionCommands and add ActionListeners to p1 and p2 icon options
		p1_banana.setActionCommand("Banana");
		p1_banana.addActionListener(this);
		p1_orange.setActionCommand("Orange");
		p1_orange.addActionListener(this);
		p1_apple.setActionCommand("Apple");
		p1_apple.addActionListener(this);
		p2_strawberry.setActionCommand("Strawberry");
		p2_strawberry.addActionListener(this);
		p2_blueberry.setActionCommand("Blueberry");
		p2_blueberry.addActionListener(this);
		p2_raspberry.setActionCommand("Raspberry");
		p2_raspberry.addActionListener(this);
		
		// open wav files
		try {
			AudioInputStream sound = AudioSystem.getAudioInputStream(new File ("background2.wav"));
			background = AudioSystem.getClip();
			background.open(sound);
			sound = AudioSystem.getAudioInputStream(new File ("plop2.1.wav")); 
			plop = AudioSystem.getClip();
			plop.open(sound);
			sound = AudioSystem.getAudioInputStream(new File ("yippee2.wav"));
			yippee = AudioSystem.getClip();
			yippee.open(sound);

		}
		catch (Exception e){
		}
		
		// Loop background music when program starts
		background.setFramePosition(0);
		background.loop(Clip.LOOP_CONTINUOUSLY);
		
		// Stop background music when program closes
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
		    public void windowClosing (java.awt.event.WindowEvent windowEvent) {
		    	background.stop();
				try
				{
					Thread.sleep (1200);
				}
				catch (InterruptedException e)
				{
				}
		    	System.exit(0);
		    }   
		});
		
		// Change cursor of mouse icon
		cursorImage = Toolkit.getDefaultToolkit().getImage("amongUsCursor.png");
		Point hotspot = new Point (0,0);
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Cursor cursor = toolkit.createCustomCursor(cursorImage,  hotspot, "cursor");
		frame.setCursor(cursor);
		
	} // Close constructor

	// To handle normal menu items
	public void actionPerformed (ActionEvent event)
	{
		String eventName = event.getActionCommand ();
		if (eventName.equals ("New"))
		{
			newGame ();
		}
		else if (eventName.equals("Exit"))
		{
			System.exit (0);
		}
		else if (eventName.equals("Banana")) {
			// Change all current p1 icons to banana
			firstImage = Toolkit.getDefaultToolkit ().getImage ("banana.gif");
			msg1 = "Banana";
		}
		else if (eventName.equals("Orange")) {
			// Change all current p1 icons to orange
			firstImage = Toolkit.getDefaultToolkit ().getImage ("ORANGE.png");
			msg1 = "Orange";
		}
		else if (eventName.equals("Apple")) {
			// Change all current p1 icons to apple
			firstImage = Toolkit.getDefaultToolkit ().getImage ("APPLE.png");
			msg1 = "Apple";
		}
		else if (eventName.equals("Strawberry")) {
			// Change all current p2 icons to strawberry
			secondImage = Toolkit.getDefaultToolkit ().getImage ("strawberry.gif");
			msg2 = "Strawberry";
		}
		else if (eventName.equals("Blueberry")) {
			// Change all current p1 icons to blueberry
			secondImage = Toolkit.getDefaultToolkit ().getImage ("BLUEBERRY.PNG");
			msg2 = "Blueberry";
		}
		else if (eventName.equals("Raspberry")) {
			// Change all current p1 icons to raspberry
			secondImage = Toolkit.getDefaultToolkit ().getImage ("RASPBERRY.PNG");
			msg2 = "Raspberry";
		}
	}


	public void newGame ()
	{
		currentPlayer = BANANA;
		clearBoard (board);
		gameOver = false;
		currentColumn = 3;
		repaint ();
	}

	//------------YOUR CODE GOES HERE  ------------------//

	// clearBoard 
	// Description: This method goes through every row of board, then ever column within that row and makes every value 0
	// Parameter: 2d array board
	// Return: none
	public void clearBoard (int[] [] board) {
		for (int row = 1; row < board.length; row = row+1) { // every row of board
			for (int col = 1; col < board[row].length; col = col+1){ // every column within
				board[row][col] = 0;
			}
		}
	}

	// findNextRow 
	// Description: This method checks if the bottom most row is full. If it is, it will check if the row above is empty 
	// Parameter: 2d array board and int column
	// Return: Integer - the available empty row for the piece to go in
	public int findNextRow (int[] [] board, int column) { 
		int temp = 0;
		for (int i = 6; i > 0; i--){
			if (board [i][column] != 0 && board[i-1][column] == 0) { // if row i is full, return row -1 (if empty) 
				temp = i-1;
				break;
			}
			else if (board[i][column] == 0){ // if row is empty
				temp = i;
				break;
			}
		} // for
		return temp;
	} 

	// checkForWinner
	// Description: This method checks to see if there is a winning combination on the board (without going through every square)
	// Parameter: 2d array board, int lastRow, int lastColumn
	// Return: integer - return the winner -1 (BANANNA) or 1 (STRAWBERRY)  OR return tie 2 (TIE)
	public int checkForWinner (int[] [] board, int lastRow, int lastColumn) {
		// BANANA = -1
		// STRAWBERRY = 1
		int winner = 0;

		// LOOK VERTICALLY
		for (int row = 6; row >= 4; row = row-1) {// for every row (6-4) // no possible winner if row 3 or less, therefore no need to look down after
			for (int col = 1; col < board[row].length; col = col+1) {	// for every column within current row, look 3 rows up
				if (board[row][col] != 0) { // make sure it isn't checking for 0s
					if (board[row][col] == board[row-1][col] &&
							board[row][col] == board[row-2][col] &&
							board[row][col] == board[row-3][col]) { // check if values 3 rows up are equal 
						winner = board[row][col];
						gameOver = true;
						yippee.setFramePosition(0);
						yippee.start();
						break;
					}
				}
			}
		}

		// LOOK HORIZONTALLY
		for (int row = 6; row > 0; row = row-1) {// for every row 
			for (int col = 1; col <= 4; col = col+1) {// for columns 1-4 within current row, look 3 columns right // no possible winner if column 5 or more, therefore no need to look left after
				if (board[row][col] != 0) { // make sure it isn't checking for 0s
					if (board[row][col] == board[row][col+1] &&
							board[row][col] == board[row][col+2] &&
							board[row][col] == board[row][col+3]) { // check if values 3 columns right are equal 
						winner = board[row][col];
						gameOver = true;
						yippee.setFramePosition(0);
						yippee.start();
						break;
					}
				}
			}
		}
		
		// LOOK DIAGONALLY (South-east)
		boolean diagonal_1 = true;
		while (diagonal_1 == true) {
			if(board[lastRow][lastColumn] == board[lastRow+1][lastColumn+1] &&
					board[lastRow][lastColumn] == board[lastRow+2][lastColumn+2] &&
					board[lastRow][lastColumn] == board[lastRow+3][lastColumn+3]) { 
				winner = board[lastRow][lastColumn];
				diagonal_1 = false;
				gameOver = true;
				yippee.setFramePosition(0);
				yippee.start();
				break;
			}
			else {
				diagonal_1 = false;
				break;
			}
		}
		
		// LOOK DIAGONALLY (South-west)
		boolean diagonal_2 = true;
		while (diagonal_2 == true) {
			if(board[lastRow][lastColumn] == board[lastRow+1][lastColumn-1] &&
					board[lastRow][lastColumn] == board[lastRow+2][lastColumn-2] &&
					board[lastRow][lastColumn] == board[lastRow+3][lastColumn-3]) {
				winner = board[lastRow][lastColumn];
				diagonal_2 = false;
				gameOver = true;
				yippee.setFramePosition(0);
				yippee.start();
				break;
			}
			else {
				diagonal_2 = false;
				break;
			}
		}
		
		// LOOK DIAGONALLY (North-east)
		boolean diagonal_3 = true;
		while (diagonal_3 == true) {
			if(board[lastRow][lastColumn] == board[lastRow-1][lastColumn+1] &&
					board[lastRow][lastColumn] == board[lastRow-2][lastColumn+2] &&
					board[lastRow][lastColumn] == board[lastRow-3][lastColumn+3]) {
				winner = board[lastRow][lastColumn];
				diagonal_3 = false;
				gameOver = true;
				yippee.setFramePosition(0);
				yippee.start();
				break;
			}
			else {
				diagonal_3 = false;
				break;
			}
		}
		
		// LOOK DIAGONALLY (North-west)
		boolean diagonal_4 = true;
		while (diagonal_4 == true) {
			if(board[lastRow][lastColumn] == board[lastRow-1][lastColumn-1] &&
					board[lastRow][lastColumn] == board[lastRow-2][lastColumn-2] &&
					board[lastRow][lastColumn] == board[lastRow-3][lastColumn-3]) {
				winner = board[lastRow][lastColumn];
				diagonal_4 = false;
				gameOver = true;
				yippee.setFramePosition(0);
				yippee.start();
				break;
			}
			else {
				diagonal_4 = false;
				break;
			}
		}

		
		// TIE
		int sum = 0;
		for (int row = 1; row < board.length; row = row+1) { // every row of board
			for (int col = 1; col < board[row].length; col = col+1){ // every column within
				if (board[row][col] != 0) { // if position is full
					sum = sum + 1;
				}
			}
		}
		if (sum == 42) { // if every position is full
			winner = 2;
		}
				
		return winner; // winner = -1 (BANANNA) or 1 (STRAWBERRY)  // tie = 2
	}

	//----------------------------------------------------//

	
	public void handleAction (int x, int y)
	{
		if (gameOver)
		{
			JOptionPane.showMessageDialog (this, "Please Select Game...New to start a new game",
					"Game Over", JOptionPane.WARNING_MESSAGE);
			return;
		}

		int column = (x - BORDER_SIZE) / SQUARE_SIZE + 1;
		int row = findNextRow (board, column);
		if (row <= 0)
		{
			JOptionPane.showMessageDialog (this, "Please Select another Column",
					"Column is Full", JOptionPane.WARNING_MESSAGE);
			return;
		}

		animatePiece (currentPlayer, column, row);
		board [row] [column] = currentPlayer;

		int winner = checkForWinner (board, row, column);

		if (winner == BANANA)
		{
			gameOver = true;
			repaint ();
			JOptionPane.showMessageDialog (this, msg1 + " Wins!!!",
					"GAME OVER", JOptionPane.INFORMATION_MESSAGE);

		}
		else if (winner == STRAWBERRY)
		{
			gameOver = true;
			repaint ();
			JOptionPane.showMessageDialog (this, msg2 + " Wins!!!",
					"GAME OVER", JOptionPane.INFORMATION_MESSAGE);
		}
		else if (winner == TIE) // 2
		{
			gameOver = true;
			repaint ();
			JOptionPane.showMessageDialog (this, "Tie!!!",
					"GAME OVER", JOptionPane.INFORMATION_MESSAGE);
		}
		else
			// Switch to the other player
			currentPlayer *= -1;
		currentColumn = 3;

		repaint ();
	}


	// MouseListener methods
	public void mouseClicked (MouseEvent e)
	{
		int x, y;
		x = e.getX ();
		y = e.getY ();
		plop.setFramePosition(0);
		plop.start();

		handleAction (x, y);
	}


	public void mouseReleased (MouseEvent e)
	{
	}


	public void mouseEntered (MouseEvent e)
	{
	}


	public void mouseExited (MouseEvent e)
	{
	}


	public void mousePressed (MouseEvent e)
	{
	}


	//KeyListener methods
	public void keyPressed (KeyEvent kp)
	{
		if (kp.getKeyCode () == KeyEvent.VK_RIGHT)
		{
			if (currentColumn < 6)
				currentColumn++;
		}
		else if (kp.getKeyCode () == KeyEvent.VK_DOWN)
		{
			plop.setFramePosition(0);
			plop.start();
			handleAction ((currentColumn) * SQUARE_SIZE + BORDER_SIZE, 0);
		}
		else if (kp.getKeyCode () == KeyEvent.VK_LEFT)
		{
			if (currentColumn > 0)
				currentColumn--;
		}
		else
			return;
		repaint ();
	}


	public void keyReleased (KeyEvent e)
	{
	}


	public void keyTyped (KeyEvent e)
	{
	}


	public void animatePiece (int player, int column, int finalRow)
	{
		Graphics g = getGraphics ();

		// Find the x and y positions for each row and column
		int xPos = (4 - 1) * SQUARE_SIZE + BORDER_SIZE;
		int yPos = TOP_OFFSET + 0 * SQUARE_SIZE;
		offScreenBuffer.clearRect (xPos, yPos, SQUARE_SIZE, SQUARE_SIZE);
		for (double row = 0 ; row < finalRow ; row += 0.10)
		{
			// Find the x and y positions for each row and column
			xPos = (column - 1) * SQUARE_SIZE + BORDER_SIZE;
			yPos = (int) (TOP_OFFSET + row * SQUARE_SIZE);
			// Redraw the grid for this column
			for (int gridRow = 1 ; gridRow <= 6 ; gridRow++)
			{
				// Draw the squares
				offScreenBuffer.setColor (Color.black);
				offScreenBuffer.drawRect ((column - 1) * SQUARE_SIZE + BORDER_SIZE,
						TOP_OFFSET + gridRow * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
			}

			// Draw each piece, depending on the value in board
			if (player == BANANA)
				offScreenBuffer.drawImage (firstImage, xPos, yPos, SQUARE_SIZE, SQUARE_SIZE, this);
			else if (player == STRAWBERRY)
				offScreenBuffer.drawImage (secondImage, xPos, yPos, SQUARE_SIZE, SQUARE_SIZE, this);

			// Transfer the offScreenBuffer to the screen
			g.drawImage (offScreenImage, 0, 0, this);
			delay (3);
			offScreenBuffer.clearRect (xPos + 1, yPos + 1, SQUARE_SIZE - 2, SQUARE_SIZE - 2);
		}
	}


	// Avoid flickering -- smoother graphics
	public void update (Graphics g)
	{
		paint (g);
	}


	public void paintComponent (Graphics g)
	{

		// Set up the offscreen buffer the first time paint() is called
		if (offScreenBuffer == null)
		{
			offScreenImage = createImage (this.getWidth (), this.getHeight ());
			offScreenBuffer = offScreenImage.getGraphics ();
		}

		// All of the drawing is done to an off screen buffer which is
		// then copied to the screen.  This will prevent flickering
		// Clear the offScreenBuffer first
		offScreenBuffer.clearRect (0, 0, this.getWidth (), this.getHeight ());

		// Redraw the board with current pieces
		for (int row = 1 ; row <= 6 ; row++)
			for (int column = 1 ; column <= 7 ; column++)
			{
				// Find the x and y positions for each row and column
				int xPos = (column - 1) * SQUARE_SIZE + BORDER_SIZE;
				int yPos = TOP_OFFSET + row * SQUARE_SIZE;

				// Draw the squares
				offScreenBuffer.setColor (Color.black);
				offScreenBuffer.drawRect (xPos, yPos, SQUARE_SIZE, SQUARE_SIZE);

				// Draw each piece, depending on the value in board
				if (board [row] [column] == BANANA)
					offScreenBuffer.drawImage (firstImage, xPos, yPos, SQUARE_SIZE, SQUARE_SIZE, this);
				else if (board [row] [column] == STRAWBERRY)
					offScreenBuffer.drawImage (secondImage, xPos, yPos, SQUARE_SIZE, SQUARE_SIZE, this);
			}

		// Draw next player
		if (!gameOver)
			if (currentPlayer == BANANA)
				offScreenBuffer.drawImage (firstImage, currentColumn * SQUARE_SIZE + BORDER_SIZE, TOP_OFFSET, SQUARE_SIZE, SQUARE_SIZE, this);
			else
				offScreenBuffer.drawImage (secondImage, currentColumn * SQUARE_SIZE + BORDER_SIZE, TOP_OFFSET, SQUARE_SIZE, SQUARE_SIZE, this);

		// Transfer the offScreenBuffer to the screen
		g.drawImage (offScreenImage, 0, 0, this);
	}


	/** Purpose: To delay the given number of milliseconds
	 * @param milliSec The number of milliseconds to delay
	 */
	private void delay (int milliSec)
	{
		try
		{
			Thread.sleep (milliSec);
		}
		catch (InterruptedException e)
		{
		}
	}


	public static void main (String[] args)
	{
		frame = new JFrame ("Connect Four");
		ConnectFour myPanel = new ConnectFour ();

		frame.add (myPanel);
		frame.pack ();
		frame.setVisible (true);

	} // main method
	
} // class

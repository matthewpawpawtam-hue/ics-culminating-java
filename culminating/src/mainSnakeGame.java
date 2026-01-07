import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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

@SuppressWarnings("serial")
public class mainSnakeGame extends JPanel implements Runnable, KeyListener, ActionListener {
	
	Rectangle[] walls = new Rectangle[5];
	boolean up, down, left, right;
	int speed = 6;
	int screenWidth = 680;
	int screenHeight = 680;
	Thread thread;
	int FPS = 60;
    Graphics offScreenBuffer;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
    Image offScreenImage;
	int SQUARE_SIZE = 60;
	int board[][];
	Image apple;
	int xPos, yPos;
	int randX, randY;
	int [][] snake = new int [100][2];
	int snakeLength = 3;
	int points = 0;
	long startTime, timeElapsed;
	int frameCount = 0;
	
	public mainSnakeGame() {
		setFocusable(true);
		requestFocusInWindow();
		addKeyListener(this);
		setPreferredSize(new Dimension(screenWidth, screenHeight));
		setVisible(true);
        board = new int [10][10];
		thread = new Thread(this);
		thread.start();
		snake[0][0] = 0;
		snake[0][1] = 4;
		snake[1][0] = 1;
		snake[1][1] = 4;
		snake[2][0] = 2;
		snake[2][1] = 4;

	}
	
	@Override
	public void run() {
		initialize();
		while(true) {
			//main game loop
			update();
			this.repaint();
			try {
				Thread.sleep(1000/FPS);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void initialize() {
		startTime = System.currentTimeMillis();
		timeElapsed = 0;
		FPS = 60;
		for(int i = 0; i < 100000; i++) {
			// this is just to delay time
			String s = "set up stuff blah blah blah";
			s.toUpperCase();
		}

		// Set apple image in random location
		apple = new ImageIcon(getClass().getResource("APPLE.png")).getImage();
		randX = 465;
		randY = 285;

		System.out.println("Thread: Done initializing game");
	}
	
	public void update() {
		//update stuff
		timeElapsed = System.currentTimeMillis() - startTime;

		frameCount++;
		move();
		keepInBound();
		checkCollision();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	// offScreenBuffer
	if (offScreenImage == null ||
	    offScreenImage.getWidth(null) != getWidth() ||
    	offScreenImage.getHeight(null) != getHeight()) 
	{
    	offScreenImage = createImage(getWidth(), getHeight());
    	offScreenBuffer = offScreenImage.getGraphics();
	}

		
		offScreenBuffer.setColor(getBackground());
		offScreenBuffer.fillRect(0, 0, getWidth(), getHeight());
		Graphics2D g2 = (Graphics2D) g;

		for (int row = 1; row < 11; row++) { // row
            for (int column = 1; column < 11; column++) { // column
				
				// Find the x and y positions for each row and column                
				xPos = (column - 1) * SQUARE_SIZE + 40;
				yPos = (row - 1) * SQUARE_SIZE + 40;

				// Draw the squares
                Color myColor = new Color(0x2E7D32);
                offScreenBuffer.setColor (myColor);
                if (column % 2 == 0) {
                    if (row % 2 == 0) {
                        myColor = new Color(0xA5D6A7);
                        offScreenBuffer.setColor (myColor);
				    	offScreenBuffer.fillRect (xPos, yPos, SQUARE_SIZE, SQUARE_SIZE);
                    } else {
                        myColor = new Color (0x2A914E);
                        offScreenBuffer.setColor (myColor);
				    	offScreenBuffer.fillRect (xPos, yPos, SQUARE_SIZE, SQUARE_SIZE);
                    }
                } else {
                    if (row % 2 == 0) {
                        myColor = new Color(0x2A914E);
                        offScreenBuffer.setColor (myColor);
				    	offScreenBuffer.fillRect (xPos, yPos, SQUARE_SIZE, SQUARE_SIZE);
                    } else {
                        myColor = new Color (0xA5D6A7);
                        offScreenBuffer.setColor (myColor);
				    	offScreenBuffer.fillRect (xPos, yPos, SQUARE_SIZE, SQUARE_SIZE);
                    }
                }
			}
		}
		// Draw apple
		offScreenBuffer.drawImage(apple, randX, randY, this);

        // Draw border
		Color myColor = new Color (0x2E7D32);
        offScreenBuffer.setColor (myColor);
        offScreenBuffer.fillRect (0, 0, 680, 40);
        offScreenBuffer.fillRect (0, 0, 40, 680);
        offScreenBuffer.fillRect (0, 640, 680, 40);
        offScreenBuffer.fillRect (640, 0, 44, 680);

		// Draw snake
		offScreenBuffer.setColor(Color.BLUE);
		for (int i = 0; i < snakeLength; i++) {
			offScreenBuffer.fillRect(40 + snake[i][0] * 60, 40 + snake[i][1] * 60, 60, 60);
		}

		// Display information stuff
		offScreenBuffer.setColor(Color.WHITE);
		offScreenBuffer.drawString("" + timeElapsed + " ms since start of program", 40, 18);
		offScreenBuffer.drawString("" + timeElapsed/1000 + " seconds since the start of program", 40, 31);
		offScreenBuffer.drawString(frameCount + " frames ran since the start of program", 280, 18);
		offScreenBuffer.drawString("FPS: " + String.format("%.2f", (double)frameCount / ((double)timeElapsed / 1000)), 280, 31);
		offScreenBuffer.drawString("Points: " + points, 580, 18);

        // Transfer the offScreenBuffer to the screen
		g.drawImage (offScreenImage, 0, 0, this);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if(key == KeyEvent.VK_LEFT) {
			left = true;
			right = false;
			up = false;
			down = false;
		}else if(key == KeyEvent.VK_RIGHT) {
			left = false;
			right = true;
			up = false;
			down = false;
		}else if(key == KeyEvent.VK_UP) {
			left = false;
			right = false;
			up = true;
			down = false;
		}else if(key == KeyEvent.VK_DOWN) {
			left = false;
			right = false;
			down = true;
			up = false;
		}
	}

	// public void snakeCoordinate {
		
	// }
	
	// void move() {
	// 	if(left)
	// 		rect.x -= speed;
	// 	else if(right)
	// 		rect.x += speed;
	// 	if(up)
	// 		rect.y += -speed;
	// 	else if(down)
	// 		rect.y += speed;
	// }
	
	// void keepInBound() {
	// 	if (rect.x < 40)
	// 		rect.x = 40;
	// 	else if (rect.x > 600)
	// 		rect.x = 600;

	// 	if (rect.y < 40)
	// 		rect.y = 40;
	// 	else if (rect.y > 600)
	// 		rect.y = 600;
	// }
	
	void spawnNewApple() {
    	randX = ((int)(Math.random()*10)) * SQUARE_SIZE + 40;
    	randY = ((int)(Math.random()*10)) * SQUARE_SIZE + 40;
	}

	// void checkCollision() {
	// 	Rectangle appleRect = new Rectangle (randX, randY, apple.getWidth(null), apple.getHeight(null));
	// 	if (rect.intersects(appleRect)){
	// 		points ++;
	// 		spawnNewApple();	
	// 	}	
	// }

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'keyReleased'");
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String eventName = event.getActionCommand ();
		if (eventName.equals("Exit")){
			System.exit(0);
		}
	}
	
	public static void main(String[] args) {
		// Set up the Game Menu
		JMenu gameMenu = new JMenu ("Game");
		
		// Set up the Game MenuItems and add to gameMenu (with a separator)
		JMenuItem newOption = new JMenuItem ("New");
		JMenuItem exitOption = new JMenuItem ("Exit");

		gameMenu.add (newOption);
		gameMenu.addSeparator ();
		gameMenu.add (exitOption);

		JFrame frame = new JFrame ("P4 ICS3U Semester 1 - Snake Game Culminating (2025-2026) by Eric Lu & Matthew Tam");
		mainSnakeGame myPanel = new mainSnakeGame();

		newOption.setActionCommand ("New");
		newOption.addActionListener (myPanel);
		exitOption.setActionCommand ("Exit");
		exitOption.addActionListener (myPanel);

		// Add final menus to JMenuBar mainMenu
		JMenuBar mainMenu = new JMenuBar ();
		mainMenu.add (gameMenu);
		
		// Set the menu bar for this frame to mainMenu
		frame.setJMenuBar (mainMenu);
		frame.add(myPanel);
		frame.setVisible(true);
		myPanel.requestFocusInWindow();
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
	}
} // class

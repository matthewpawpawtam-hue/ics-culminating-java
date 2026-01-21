// INTRODUCTORY COMMENTS
// This is our version of a popular game called Snake.
// Made by Matthew Tam and Eric Lu on 01/21/2025 for final ICS game.
// Created on VS Code.

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;

@SuppressWarnings("serial")
public class mainSnakeGame extends JPanel implements Runnable, KeyListener, ActionListener {
    // Initialize global variables
    int gameState = 0; 
    int x = 160;
    int y = 280;
    boolean up = false;
    boolean down = false;
    boolean right = false;
    boolean left = false;
    boolean start = false;
    int screenWidth = 680;
    int screenHeight = 680;
    Thread thread;
    int FPS = 10;
    int SQUARE_SIZE = 60;
    int OFFSET = 40; 
    Image apple;
    Image logo;
    int [] appleX = new int [3];
    int [] appleY = new int [3];
    int [][] snake = new int [100][2];
    int snakeLength = 3;
    int points = 0;
    int highscore = 0;
    int numApples = 1;
    boolean run = true;
    String message = "";
    String popup = "Your Score";
    String popup2 = "About";
    String popup3 = "Instructions";
    boolean showMsg = true;
    Clip collect;
    Clip lose;
    Clip victory;

    static Image cursorImage; 
    
    // Definition: Constructor
    // Parameters: None
    // Return: None
    public mainSnakeGame() {
        System.out.println("Constructor");
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(this);
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setVisible(true);
        // Initialize Snake Positions
        snake[0][0] = 2; snake[0][1] = 4; 
        snake[1][0] = 1; snake[1][1] = 4;
        snake[2][0] = 0; snake[2][1] = 4;    
        loadResources();
    }
    
    // Description: Open images + sound files
    // Parameters: None
    // Return: Void
    public void loadResources() {
        try {
            // Sfx stuff
            try {
                AudioInputStream sound = AudioSystem.getAudioInputStream(getClass().getResource("/collect.wav"));
                collect = AudioSystem.getClip();
                collect.open(sound);
                
                sound = AudioSystem.getAudioInputStream(getClass().getResource("/lose.wav"));
                lose = AudioSystem.getClip();
                lose.open(sound);

                sound = AudioSystem.getAudioInputStream(getClass().getResource("/victory.wav"));
                victory = AudioSystem.getClip();
                victory.open(sound);

            } catch (Exception e) {
                
            }

            // Images
            if (getClass().getResource("/snake logo.png") != null) {
                logo = new ImageIcon(getClass().getResource("/snake logo.png")).getImage();
            }
            if (getClass().getResource("/APPLE.png") != null) {
                apple = new ImageIcon(getClass().getResource("/APPLE.png")).getImage();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Description: Code to runs the game
    // Parameters: None
    // Return: Void
    @Override
    public void run() {
        initialize();
        while(run) {
            update();
            this.repaint();
            try {
                Thread.sleep(3500/FPS); 
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    // Description: Setting up the apples
    // Parameters: None
    // Return: V
    public void initialize() {
		appleX[0] = 7;
		appleY[0] = 4;
		
		// If the user selected 2 or 3, spawn the rest randomly
		for (int i = 1; i < numApples; i++) {
			respawnSpecificApple(i);
		}
	}
    
	// Description: Update the game
    // Parameters: None
    // Return: Void
    public void update() {
        this.requestFocusInWindow();
        if (gameState == 0) return;
        move();
        keepInBound();
        checkApples(); // Checks all apples
        selfCollision();
        checkWin();
    } 
    
    // Description: paintComponent
    // Parameters: graphics to paint board and everything
    // Return: Void
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Title screen with paintComponent
        if (gameState == 0) {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.BLACK);
            Font bigFont = new Font("Calibri", Font.PLAIN, 30);
            g.setFont (bigFont);
            g.drawString("Press [SPACE] To Start", 205, 475);
            if(logo != null) g.drawImage(logo, 10, 35, 700, 700, this);
            return; 
        }

        // Draw Background
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        // Draw Checkerboard
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                // Calculate pixel positions 
                int xPos = col * SQUARE_SIZE + OFFSET;
                int yPos = row * SQUARE_SIZE + OFFSET;
                
                if ((col + row) % 2 == 0) g.setColor(new Color(0xA5D6A7));
                else g.setColor(new Color(0x2A914E));
                
                g.fillRect(xPos, yPos, SQUARE_SIZE, SQUARE_SIZE);
            }
        }

        // Draw apples
        for (int i = 0; i < numApples; i++){
            int drawX = appleX[i] * SQUARE_SIZE + OFFSET;
            int drawY = appleY[i] * SQUARE_SIZE + OFFSET;
            
            if (apple != null) {
                g.drawImage(apple, drawX, drawY, SQUARE_SIZE, SQUARE_SIZE, this);
            } else {
                g.setColor(Color.RED);
                g.fillOval(drawX + 10, drawY + 10, 40, 40); 
            }
        }

        // Draw borders
        g.setColor(new Color(0x2E7D32));
        g.fillRect(0, 0, 680, 40); //top
        g.fillRect(0, 0, 40, 680); //left
        g.fillRect(0, 640, 680, 40); //right
        g.fillRect(640, 0, 44, 680); //bottom

        // Draw snake
        g.setColor(Color.BLUE);
        for (int i = 0; i < snakeLength; i++) {
            g.fillRect(OFFSET + (snake[i][0] * SQUARE_SIZE), OFFSET + (snake[i][1] * SQUARE_SIZE), SQUARE_SIZE, SQUARE_SIZE);
        }

        // Display information stuff
        g.setColor(Color.WHITE);
        g.drawString("Points: " + points, 40, 17);
        g.drawString("Highscore: " + highscore, 40, 31);
    }

    // Description: Does stuff when certain keys are pressed
    // Parameters: None
    // Return: Void
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (gameState == 0) {
            if (key == KeyEvent.VK_SPACE) {
                gameState = 1; 
            } 
            return;
        }
        if(key == KeyEvent.VK_LEFT && !right) {
            left = true; right = false; up = false; down = false;
            start = true;
        } else if(key == KeyEvent.VK_RIGHT && !left) {
            left = false; right = true; up = false; down = false;
            start = true;
        } else if(key == KeyEvent.VK_UP && !down) {
            left = false; right = false; up = true; down = false;
            start = true;
        } else if(key == KeyEvent.VK_DOWN && !up) {
            left = false; right = false; down = true; up = false;
            start = true;
        }
    } 

    // Description: Adjusts the snakes position in the array
    // Parameters: None
    // Return: None
    void move() {
        if (!start) return;

        // Move Body
        for (int i = snakeLength - 1; i > 0; i--) {
            snake[i][0] = snake[i - 1][0];
            snake[i][1] = snake[i - 1][1];
        }

        // Move Head
        if (left) snake[0][0] -= 1;
        else if (right) snake[0][0] += 1;
        else if (up) snake[0][1] -= 1;
        else if (down) snake[0][1] += 1;
        
        x = snake[0][0] * SQUARE_SIZE + OFFSET;
        y = snake[0][1] * SQUARE_SIZE + OFFSET;
    } 
    
    // Description: Checks if the snake touches the border
    // Paramaters: None
    // Return: None
    void keepInBound() {
        int headX = snake[0][0];
        int headY = snake[0][1];
        
        if (headX < 0 || headX > 9 || headY < 0 || headY > 9){
            triggerLose();
        }
    } 
    
    // Description: Spawns new apples and makes sure it doesn't spawn on other apples or below the snake
    // Parameters: None
    // Return: None
    void spawnNewApples() {
        for(int i = 0; i < 3; i++) {
            boolean valid = false;
            while(!valid) {
                int newX = (int)(Math.random() * 10);
                int newY = (int)(Math.random() * 10);
                
                // Check collision with snake
                boolean onSnake = false;
                for(int s = 0; s < snakeLength; s++) {
                    if(snake[s][0] == newX && snake[s][1] == newY) {
                        onSnake = true;
                        break;
                    }
                }
                
                // Check collision with other apples
                boolean onOtherApple = false;
                for(int j=0; j<i; j++) { // Check apples already spawned
                    if(appleX[j] == newX && appleY[j] == newY) {
                        onOtherApple = true;
                        break;
                    }
                }

                if(!onSnake && !onOtherApple) {
                    appleX[i] = newX;
                    appleY[i] = newY;
                    valid = true;
                }
            }
        }
    }
    
    // Description: spawns a new apple at a random position
    // Parameters: int index. this is to respawn only the specific apple that was eaten
    // return: none 
    void respawnSpecificApple(int index) {
        boolean valid = false;
        while(!valid) {
            int newX = (int)(Math.random() * 10);
            int newY = (int)(Math.random() * 10);
            
            boolean onSnake = false;
            for(int s = 0; s < snakeLength; s++) {
                if(snake[s][0] == newX && snake[s][1] == newY) {
                    onSnake = true;
                    break;
                }
            }
            if(!onSnake) {
                appleX[index] = newX;
                appleY[index] = newY;
                valid = true;
            }
        }
    }

    void checkWin() {
        if (snakeLength == 100) {
            victory.setFramePosition(0); // starts at beginning of audio clip
            victory.start();
            restart();
        }
    }

    // description: checks if an apple is eaten
    // return: none
    // parameters: none
    void checkApples() {
        int headX = snake[0][0];
        int headY = snake[0][1];

        // Loop through all active apples
        for (int i = 0; i < numApples; i++) {
            if (headX == appleX[i] && headY == appleY[i]) {
                // Ate apple at index i
				growSnake();
                snakeLength++;
                points++;
                if (points > highscore) {
                    highscore = points;
                } 
                
                if (collect != null) {
                    collect.setFramePosition(0);
                    collect.start();
                }
                
                growSnake();
                respawnSpecificApple(i); // Only respawn the one that was eaten
            }
        }
    }

    
    // checks if self collides
    // return: none
    // parameters: none
    void selfCollision() {
        for (int i = 1; i < snakeLength; i++) {
            if (snake[0][0] == snake[i][0] && snake[0][1] == snake[i][1]) { // if head is on the same block as a body block
                triggerLose();
            }
        }
    }
    
    // description: triggers the lose sound effect and restarts the game.
    void triggerLose() {
        if(lose != null) {
            lose.setFramePosition(0);
            lose.start();
        }
        restart();
    }

    // descrioption: adds a block to the end of the snake
    // return: none
    // parameter: none
    void growSnake() { 
        snake[snakeLength][0] = snake[snakeLength - 1][0];
        snake[snakeLength][1] = snake[snakeLength - 1][1];
    }


    public static void scorePopup(String message, String popup){
        JOptionPane.showMessageDialog(null, message, popup, JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {} 


    // description: resets the snake to the start and makes it length 3. also shows the user their high score
    public void restart(){
        // resets the snake to the starting size
        snakeLength = 3;
        snake[0][0] = 2; 
		snake[0][1] = 4; 
        snake[1][0] = 1; 
		snake[1][1] = 4;
        snake[2][0] = 0; 
		snake[2][1] = 4;   
        // makes the snake stop moving 
        up = false; 
		down = false; 
		left = false; 
		right = false;
        
        if (showMsg){
            message = "Score: " + points + "\nHighscore: " + highscore;
            scorePopup(message, popup);
        }
        points = 0;
        start = false;
        initialize();
    }

    // this is the action listerner for the menu
    @Override
    public void actionPerformed(ActionEvent event) {
        String eventName = event.getActionCommand ();
        if (eventName.equals("Exit")) {
            System.exit(0);
        }
        if (eventName.equals("New")){
            if (gameState == 0) {
                showMsg = false;
                spawnNewApples();
                gameState = 1;
            } else if (gameState == 1) {
                showMsg = true;
            }
            restart();
            highscore = 0;
        }
        if (eventName.equals ("About")){ // pops up the creators the game
            message = "Snake Game\nCreated by Eric L. & Matthew T.\nGr. 11 Semester 1, 2025-2026";
            scorePopup(message, popup2);
        }
        if (eventName.equals("Instructions")){ // pops up the intructions menu
            message = "Eat apples. Don't hit walls. Don't eat yourself.";
            scorePopup(message, popup3);
        }
        if (eventName.equals("Apple1")) { // 1 apple
			if (gameState == 0){
                numApples = 1;
                spawnNewApples();
                gameState = 1;
            }
            else if (gameState == 1){
                numApples = 1;
                restart();
            }
		}
        if (eventName.equals("Apple2")) { // 2 apple
			if (gameState == 0){
                numApples = 2;
                spawnNewApples();
                gameState = 1;
            }
            else if (gameState == 1){
                numApples = 2; 
                restart();
            }
		}
        if (eventName.equals("Apple3")) { // 3 applle
            if (gameState == 0){
                numApples = 3;
                spawnNewApples();
                gameState = 1;
            }
            else if (gameState == 1){
                numApples = 3; 
                restart();
            }
		}
        if (eventName.equals("Speed1")){ // Slow
            if (gameState == 0){
                FPS = 5;
                gameState = 1;
            }
            else if (gameState == 1){
                FPS = 5;
                restart();
            }
        }
        if (eventName.equals("Speed2")){ // Medium // default
            if (gameState == 0){
                FPS = 10;
                gameState = 1;
            }
            else if (gameState == 1){
                FPS = 10;
                restart();
            }
        }
        if (eventName.equals("Speed3")){ // Fast
            if (gameState == 0){
                FPS = 20;
                gameState = 1;
            }
            else if (gameState == 1){
                FPS = 20;
                restart();
            }
        }
        this.requestFocusInWindow();
    }

    // main method
    public static void main(String[] args) {
        
        // Menu Setup
        JMenu gameMenu = new JMenu ("Game");
        JMenu aboutMenu = new JMenu ("Info");
        JMenu applesMenu = new JMenu ("Game Modes");
        JMenu speedMenu = new JMenu ("Snake Speed");

        JMenuItem newOption = new JMenuItem ("New");
        JMenuItem exitOption = new JMenuItem ("Exit");
        JMenuItem aboutOption = new JMenuItem ("About");
        JMenuItem instructionsOption = new JMenuItem ("How to play");
        
        JRadioButtonMenuItem apple1 = new JRadioButtonMenuItem ("1 Apple");
        JRadioButtonMenuItem apple2 = new JRadioButtonMenuItem ("2 Apples");
        JRadioButtonMenuItem apple3 = new JRadioButtonMenuItem ("3 Apples");
        JRadioButtonMenuItem speed1 = new JRadioButtonMenuItem ("Slow");
        JRadioButtonMenuItem speed2 = new JRadioButtonMenuItem ("Medium");
        JRadioButtonMenuItem speed3 = new JRadioButtonMenuItem ("Fast");
        
        apple1.setSelected(true); // Default selection
        ButtonGroup group = new ButtonGroup();
        group.add(apple1);
        group.add(apple2);
        group.add(apple3);

        speed2.setSelected(true); // Default selection
        ButtonGroup group2 = new ButtonGroup();
        group2.add(speed1);
        group2.add(speed2);
        group2.add(speed3);

        gameMenu.add(newOption);
        gameMenu.add(exitOption);
        aboutMenu.add(aboutOption);
        aboutMenu.add(instructionsOption);
        applesMenu.add(apple1);
        applesMenu.add(apple2);
        applesMenu.add(apple3);
        speedMenu.add(speed1);
        speedMenu.add(speed2);
        speedMenu.add(speed3);

        JFrame frame = new JFrame ("Snake Game");
        mainSnakeGame myPanel = new mainSnakeGame();

        // Mouse cursor
		cursorImage = Toolkit.getDefaultToolkit().getImage(mainSnakeGame.class.getResource("/snake cursor.png"));
		Point hotspot = new Point (0,0);
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Cursor cursor = toolkit.createCustomCursor(cursorImage,  hotspot, "cursor");
		frame.setCursor(cursor);

        // Listeners
        newOption.setActionCommand ("New");
        newOption.addActionListener (myPanel);
        exitOption.setActionCommand ("Exit");
        exitOption.addActionListener (myPanel);
        aboutOption.setActionCommand("About");
        aboutOption.addActionListener (myPanel);
        instructionsOption.setActionCommand("Instructions");
        instructionsOption.addActionListener (myPanel);
        apple1.setActionCommand("Apple1");
        apple1.addActionListener (myPanel);
        apple2.setActionCommand("Apple2");
        apple2.addActionListener (myPanel);
        apple3.setActionCommand("Apple3");
        apple3.addActionListener (myPanel);
        speed1.setActionCommand("Speed1");
        speed1.addActionListener (myPanel);
        speed2.setActionCommand("Speed2");
        speed2.addActionListener (myPanel);
        speed3.setActionCommand("Speed3");
        speed3.addActionListener (myPanel);

        JMenuBar mainMenu = new JMenuBar ();
        mainMenu.add (gameMenu);
        mainMenu.add (aboutMenu);
        mainMenu.add (applesMenu);
        mainMenu.add (speedMenu);
        
        frame.setJMenuBar (mainMenu);
        frame.add(myPanel);
        frame.pack(); // 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        myPanel.thread = new Thread(myPanel);
        myPanel.thread.start();
    }
}
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

@SuppressWarnings("serial")
public class mainSnakeGame extends JPanel implements Runnable, KeyListener {
	
	Rectangle rect = new Rectangle(0, 0, 30, 30);
	Rectangle[] walls = new Rectangle[5];
	boolean up, down, left, right;
	int speed = 4;
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
	
	public mainSnakeGame() {
		setPreferredSize(new Dimension(screenWidth, screenHeight));
		setVisible(true);
        board = new int [10][10];
		thread = new Thread(this);
		thread.start();
		snake[0][0] = 2;
		snake[0][1] = 4;
		snake[1][0] = 1;
		snake[1][1] = 4;
		snake[2][0] = 0;
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

		// Set apple image
		apple = new ImageIcon(getClass().getResource("APPLE.png")).getImage();
		randX = ((int)(Math.random()*10)) * SQUARE_SIZE + 40;
		randY = ((int)(Math.random()*10)) * SQUARE_SIZE + 40;

		// //setups before the game starts running
		// walls[0] = new Rectangle(200, 200, 60, 60);
		// walls[1] = new Rectangle(300, 40, 40, 100);
		// walls[2] = new Rectangle(450, 100, 80, 35);
		// walls[3] = new Rectangle(60, 60, 15, 15);
		// walls[4] = new Rectangle(250, 350, 150, 200);
	}
	
	public void update() {
		move();
		keepInBound();
		for(int i = 0; i < walls.length; i++){
			checkCollision(walls[i]);
		}

		// check for collision with apple
		// add to score + toggle growing snake
	}
	
	public void paintComponent(Graphics g) {
		
        // Set up the offscreen buffer the first time paint() is called
        if (offScreenBuffer == null)
		{
			offScreenImage = createImage (this.getWidth (), this.getHeight ());
			offScreenBuffer = offScreenImage.getGraphics ();
		}
		super.paintComponent(g);
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
				
				// Draw snake
				offScreenBuffer.setColor(Color.BLUE);
				offScreenBuffer.fillRect(40, 280, 180, 60);

            	//g.drawImage(offScreenImage, 0, 0, null);
			}
		}
		// draw apple
		offScreenBuffer.drawImage(apple, randX, randY, this);

        // Draw border
		Color myColor = new Color (0x2E7D32);
        offScreenBuffer.setColor (myColor);
        offScreenBuffer.fillRect (0, 0, 680, 40);
        offScreenBuffer.fillRect (0, 0, 40, 680);
        offScreenBuffer.fillRect (0, 640, 680, 40);
        offScreenBuffer.fillRect (640, 0, 44, 680);

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
		}else if(key == KeyEvent.VK_RIGHT) {
			right = true;
			left = false;
		}else if(key == KeyEvent.VK_UP) {
			up = true;
			down = false;
		}else if(key == KeyEvent.VK_DOWN) {
			down = true;
			up = false;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		if(key == KeyEvent.VK_LEFT) {
			left = false;
		}else if(key == KeyEvent.VK_RIGHT) {
			right = false;
		}else if(key == KeyEvent.VK_UP) {
			up = false;
		}else if(key == KeyEvent.VK_DOWN) {
			down = false;
		}
	}
	
	void move() {

	}
	
	void keepInBound() {
		if(rect.x < 0)
			rect.x = 0;
		else if(rect.x > screenWidth - rect.width)
			rect.x = screenWidth - rect.width;
		
		if(rect.y < 0)
			rect.y = 0;
		else if(rect.y > screenHeight - rect.height)
			rect.y = screenHeight - rect.height;
	}
	
	void checkCollision(Rectangle wall) {
		//check if rect touches wall
		if(rect.intersects(wall)) {
			System.out.println("collision");
			//stop the rect from moving
			double left1 = rect.getX();
			double right1 = rect.getX() + rect.getWidth();
			double top1 = rect.getY();
			double bottom1 = rect.getY() + rect.getHeight();
			double left2 = wall.getX();
			double right2 = wall.getX() + wall.getWidth();
			double top2 = wall.getY();
			double bottom2 = wall.getY() + wall.getHeight();
			
			if(right1 > left2 && 
			   left1 < left2 && 
			   right1 - left2 < bottom1 - top2 && 
			   right1 - left2 < bottom2 - top1)
	        {
	            //rect collides from left side of the wall
				rect.x = wall.x - rect.width;
	        }
	        else if(left1 < right2 &&
	        		right1 > right2 && 
	        		right2 - left1 < bottom1 - top2 && 
	        		right2 - left1 < bottom2 - top1)
	        {
	            //rect collides from right side of the wall
	        	rect.x = wall.x + wall.width;
	        }
	        else if(bottom1 > top2 && top1 < top2)
	        {
	            //rect collides from top side of the wall
	        	rect.y = wall.y - rect.height;
	        }
	        else if(top1 < bottom2 && bottom1 > bottom2)
	        {
	            //rect collides from bottom side of the wall
	        	rect.y = wall.y + wall.height;
	        }
		}
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame ("Example");
		mainSnakeGame myPanel = new mainSnakeGame();
		frame.add(myPanel);
		frame.addKeyListener(myPanel);
		frame.setVisible(true);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
	}
}

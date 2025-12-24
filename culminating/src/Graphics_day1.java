
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Graphics_day1 extends JPanel {
	static Image pic; //make global??
	
	// Constructor - no static, no return type, has to have the same name as class name
	public Graphics_day1 () {
		setPreferredSize (new Dimension (800, 600));
		setBackground (new Color (179, 217, 217));
		//setBackground (Color.yellow);
	
		pic = Toolkit.getDefaultToolkit().getImage("C:\\Users\\matth\\OneDrive\\Desktop\\OneDrive\\Documents\\vscode java for ics culminating\\culminating\\APPLE.png");
	}
	
public void paintComponent (Graphics g) {
	super.paintComponent(g);
	g.setColor(Color.red);
	g.fillRect (100, 200, 50, 150);
	//g.drawRect (100, 200, 50, 150); //top left corner x, top left corner y, width, height
	g.setColor (Color.blue);
	g.drawString("Hello World!", 370, 400);
	g.drawImage(pic, 300, 175, 200, 200, this);
}
	
	public static void main(String[] args) {
		JFrame myFrame = new JFrame ("Period 4 Graphics");
		Graphics_day1 myPanel = new Graphics_day1(); // creating a JPanel
		myFrame.add(myPanel);
		myFrame.pack();
		myFrame.setVisible(true);
	}

} //class

/* Week 6 Assignment.
 * Author: Yevheniia Tychynska.
 * Last update: 27 Apr 2024.
 * Version 3: Collision Detection Enhancement.
 * Read in a number from the user and create this many balls, each moving in its own direction.
 * Separate thread is created for each ball.
 * Balls bounce when strike each other or walls
 */

import java.awt.*; // Graphics; Graphics2D; Image; Color; Dimension;
import java.util.*; // ArrayList; Random;
import javax.swing.*; // ImageIcon; JFrame; JOptionPane; JPanel;
import java.util.concurrent.CopyOnWriteArrayList;

public class CollisionDetection extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private BallPanel panel;
	
	// frame constructor
	public CollisionDetection (int numberOfBalls) {
		panel = new BallPanel(numberOfBalls);
		this.setTitle("Wall Ball Wonder");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.add(panel);
		this.pack();
		this.setLocationRelativeTo(null); // a window will be allocated in the center of a screen
		this.setVisible(true);
	} // end constructor
	
	public static void main(String[] args) {
		
		int userNumber = 0;
		boolean validInput = false;
		
		do {
			try {
				String userInput = JOptionPane.showInputDialog("Enter number of balls you want to create");
				userNumber = Integer.parseInt(userInput);
				if (userNumber >= 0)
					validInput = true;
				else 
					JOptionPane.showMessageDialog(null, "Number of balls cannot be negative. Please enter only positive number");
			}
			catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "This is not a number. Please enter positive number");
			}
		}
		while (!validInput);
			
		new CollisionDetection(userNumber);
	} // end main
	
	// create panel for balls
	private class BallPanel extends JPanel {
		
		private static final long serialVersionUID = 1L;
		final int PANEL_WIDTH = 750;
		final int PANEL_HEIGHT = 500;
		Image background;
		// tread safe version of ArrayList
		CopyOnWriteArrayList <Ball> balls = new CopyOnWriteArrayList<>(); // create list of ball objects
			
		// panel constructor
		public BallPanel(int numberOfBalls) {
			this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
			this.setBackground(Color.black); // not necessary if you have image for background
			background = new ImageIcon("C:\\your_file_location\\sky.jpg").getImage();
			createBalls(numberOfBalls);
		}
		
		// create as many balls as user wishes, create separate thread for each ball 
		public void createBalls (int numberOfBalls) {
			Random random = new Random();
			for (int i = 1; i <= numberOfBalls; i++) {
				// initialize random diameter (between 30 and 99 pixels)
				int diameter = random.nextInt(70)+30;
				// initialize random position
				int x = random.nextInt(PANEL_WIDTH - diameter);
				int y = random.nextInt(PANEL_HEIGHT - diameter); 
				// initialize random direction and speed
				int xVelocity = random.nextBoolean() ? random.nextInt(5)+1 : -(random.nextInt(5)+1);
				int yVelocity = random.nextBoolean() ? random.nextInt(5)+1 : -(random.nextInt(5)+1);
				// initialize 
				Color color = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
				// instantiate runnable object ball with randomly chosen parameters
				Ball ball = new Ball(x, y, diameter, xVelocity, yVelocity, color, this);
				// add created object to ArrayList for further use
				balls.add(ball);
				// create thread, pass task from class Ball to it, start the thread
				new Thread(ball).start();
			} // end for
		} // end create balls
			
		@Override // paint panel with given background and all the balls from array list
		public void paintComponent (Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			g2d.drawImage(background, 0, 0, null);
			// draw as many balls as it is in the array list using their parameters
			for (Ball ball : balls) {
				g2d.setColor(ball.color);
				g2d.fillOval(ball.x, ball.y, ball.diameter, ball.diameter);
			} // end for
		} // end painComponent
		
		// swap directions and speeds if balls collide so, they bounce off 
		public void resolveCollision (Ball ball) {
			for (Ball another : balls) { // loop through array list
				if (another != ball && ball.isCollision(another)) { // check whether there is a collision
					// only one thread can modify object (Array List 'balls') at a time 
					synchronized (balls) {
						int temporaryXVelocity = ball.xVelocity;
						int temporaryYVelocity = ball.yVelocity;
						ball.xVelocity = another.xVelocity;
						ball.yVelocity = another.yVelocity;
						another.xVelocity = temporaryXVelocity;
						another.yVelocity = temporaryYVelocity;
					} // end critical section
				} // end if
			} // end for
		} // end resolveCollision
	} // end BallPanel
	
	// create runnable ball class to use it in threads
	private class Ball implements Runnable {
			
		private int diameter;
		private int xVelocity; // how far to move
		private int yVelocity; // how far to move
		private int x; // initial position
		private int y; // initial position
		private Color color;
		private BallPanel panel;
		
		// ball constructor
		public Ball (int x, int y, int diameter, int xVelocity, int yVelocity, Color color, BallPanel panel) {
			this.x = x;
			this.y = y;
			this.diameter = diameter;
			this.xVelocity = xVelocity;
			this.yVelocity = yVelocity;
			this.color = color;
			this.panel = panel;
		} // end ball constructor

		@Override // move a circle till it reach frame border, turn it back then
		public void run() {
			while (true) { // infinite loop
				// only one thread can modify coordinates, speed, and direction of balls in an array list at any given time
				synchronized (panel.balls) {
					x += xVelocity;
					y += yVelocity;
					if (x >= (panel.getWidth()- diameter) || x < 0) {
						xVelocity *= -1;
					}
					if (y >= (panel.getHeight() - diameter) || y < 0) {
						yVelocity *= -1;
					}
					panel.resolveCollision(this);
				} // end critical section
				panel.repaint();
				try {
					Thread.sleep(10); // update every 10 milliseconds 
				}
				catch (InterruptedException e) {
					return;
				}
			} // end loop
		} // end run
		
		// check whether there is a collision using Pythagorean theorem
		public boolean isCollision (Ball anotherBall) {
			double differenceX = (x + diameter / 2) - (anotherBall.x + anotherBall.diameter / 2);
			double differenceY = (y + diameter / 2) - (anotherBall.y + anotherBall.diameter / 2);
			double distance = Math.sqrt(differenceX * differenceX + differenceY * differenceY);
			return distance <= (diameter + anotherBall.diameter) / 2;
		} // end isCollision
	} // end Ball
} // end CollisionDetection

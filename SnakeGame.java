package SnakeGame;

import javax.swing.*; // JPanel
import java.awt.*;
import java.awt.event.*; // ActionListener, KeyListener. 
import java.util.Arrays;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {

    // Game configuration
    private final int WIDTH = 600;
    private final int HEIGHT = 600;
    private final int UNIT_SIZE = 20; // Also food and snake width. 
    private final int GAME_UNITS = (WIDTH * HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    private Timer timer; // Timer for the game loop. 
    private int INITIAL_DELAY = 150; // Starting delay for the timer. 
    private boolean paused;
    private int score;
    private Font scoreFont = new Font("Monospaced", Font.BOLD, 25);
    
    // Snake body coordinates and state. 
    private final int x[] = new int[GAME_UNITS];
    private final int y[] = new int[GAME_UNITS];
    private int bodyParts;
    private int INITIAL_LENGTH = 30;
    private char direction;
    private boolean running;
    
    // Food placement
    private int foodX;
    private int foodY;
    private Random random = new Random();
    
    public SnakeGame() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.lightGray);
        this.setFocusable(true);
        this.addKeyListener(this);
        timer = new Timer(200, this); // Timer delay determines game speed in milliseconds. 
        startGame();
    }
    
    private void startGame() {
    	placeFood();
    	Arrays.fill(x, 0);
    	Arrays.fill(y, 0);
    	bodyParts = INITIAL_LENGTH;
    	direction = 'R';
    	running = true;
    	score = 0;
    	paused = false;
        timer.setDelay(INITIAL_DELAY);
        timer.start();
    }
    
    // Place food randomly. 
    private void placeFood() {
        foodX = random.nextInt(WIDTH / UNIT_SIZE) * UNIT_SIZE;
        foodY = random.nextInt(HEIGHT / UNIT_SIZE) * UNIT_SIZE;
    }
    
    // Capture keyboard input. 
    
    @Override
    public void keyPressed(KeyEvent e) {
    	if (running) { // Game is running. 
    		if (e.getKeyCode() == KeyEvent.VK_P) { // If 'P' is pressed pause/resume the game. 
                paused = !paused;
                if (paused) {
                    timer.stop();  // Halt game loop updates. 
                } else {
                    timer.start(); // Resume game loop updates. 
                }
            } else {
	    		if (!paused) { // Game is not paused: Update snake direction based on arrow keys. 
	    			switch (e.getKeyCode()) {
			            case KeyEvent.VK_LEFT:
			                if (direction != 'R') direction = 'L';
			                break;
			            case KeyEvent.VK_RIGHT:
			                if (direction != 'L') direction = 'R';
			                break;
			            case KeyEvent.VK_UP:
			                if (direction != 'D') direction = 'U';
			                break;
			            case KeyEvent.VK_DOWN:
			                if (direction != 'U') direction = 'D';
			                break;
			        }
				}
            }
		} else { // When the game is over, allow restarting by pressing 'R'. 
			if (e.getKeyCode() == KeyEvent.VK_R) {
                startGame();
            }
		}
    }
    
    @Override
    public void keyReleased(KeyEvent e) { }
    
    @Override
    public void keyTyped(KeyEvent e) { }
    
    // Move the snake by updating body segment positions. 
    private void move() {
    	
        // Shift body coordinates by one step. 
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        
        // Update the head based on the current direction. 
        switch (direction) {
            case 'U': y[0] = y[0] - UNIT_SIZE; break;
            case 'D': y[0] = y[0] + UNIT_SIZE; break;
            case 'L': x[0] = x[0] - UNIT_SIZE; break;
            case 'R': x[0] = x[0] + UNIT_SIZE; break;
        }
        
    }
    
    // Check if the food has been eaten. 
    private void checkFood() {
        if ((x[0] == foodX) && (y[0] == foodY)) {
        	placeFood();
        	bodyParts++;
        	score++;
            timer.setDelay(Math.max(50, timer.getDelay() - 10)); // Increase speed by reducing delay (not below 50ms). 
        }
    }
    
    // Check for collisions. 
    private void checkCollisions() {
    	
        // Check self-collision. 
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
                break;
            }
        }
        
        // Check collision with wall. 
        if ((x[0] < 0) || (x[0] >= WIDTH) || (y[0] < 0) || (y[0] >= HEIGHT)) {
            running = false;
        }
        
        if (!running) {
            timer.stop();
        }
        
    }
    
    // Called by the timer every tick (game loop). 
    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && !paused) { // Update game if running and not paused. 
            move();
            checkFood();
            checkCollisions();
        }
        repaint();
    }
    
    // Paint the components. 
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }
    
    private void draw(Graphics g) {
    	
        if (running) {
        	
            // Draw food as an oval. 
            g.setColor(Color.white);
            g.fillOval(foodX, foodY, UNIT_SIZE, UNIT_SIZE);
            
            // Draw the snake. 
            for (int i = 0; i < bodyParts; i++) {
            	if (i == 0) {
            		g.setColor(Color.black);
            	} else if ((i % 2) == 1) {
            		g.setColor(Color.darkGray);
            	} else {
            		g.setColor(Color.gray);
            	}
            	g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }
            
            // Draw the score on the top-left corner. 
            g.setColor(Color.white);
            g.setFont(scoreFont);
            g.drawString("Score: " + score, 10, 25);
            
        } else {
            gameOver(g);
        }
        
    }
    
    // Display the game over screen with score and restart instruction. 
    private void gameOver(Graphics g) {
    	
    	g.setColor(Color.white);
    	
    	// "Game Over"
        g.setFont(new Font("Monospaced", Font.BOLD, 50));
        g.drawString("Game Over", WIDTH / 6, HEIGHT / 5);
        
        // Final score
        g.setFont(new Font("Monospaced", Font.BOLD, 30));
        g.drawString(" Score: " + score, WIDTH / 6, HEIGHT / 3);
        
        // Restart instruction
        g.setFont(new Font("Monospaced", Font.BOLD, 20));
        String restartMsg = "Press (R) to Restart";
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString(restartMsg, (WIDTH - metrics.stringWidth(restartMsg)) / 2, HEIGHT / 2);
        
    }
    
    // Main entry point: Creates the window and starts the game. 
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        SnakeGame gamePanel = new SnakeGame();
        frame.add(gamePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("   ---SNAKE GAME---   [Press (P) to Pause/Resume]");
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null); // The window is placed in the center of the screen. 
    }
    
}


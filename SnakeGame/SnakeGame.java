package SnakeGame;

import javax.swing.*; // JPanel
import java.awt.*;
import java.awt.event.*; // ActionListener, KeyListener. 
import java.util.Random;

class SnakeGame extends JPanel implements ActionListener, KeyListener {
	
    // Game configuration
    private final int WIDTH = 600;
    private final int HEIGHT = 600;
    private final int UNIT_SIZE = 30; // UNIT_SIZE = 20
    private int subUnit = UNIT_SIZE / 5;
    private Timer timer;
    private boolean running;
    private boolean paused;
    private Font scoreFont = new Font("Monospaced", Font.BOLD, 25);
    private int score;
    
	// Snake coordinates and state. 
    private int x[];
    private int y[];
    private int bodyParts;
    private char direction;
    private int eyeSize = subUnit * 2;
    private int crownSize = subUnit * 3;
    
    // Food placement
    private Random random = new Random();
    private int foodX;
    private int foodY;
    
    private SnakeGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.lightGray);
        setFocusable(true);
        addKeyListener(this);
        startGame();
    }
    
    private void startGame() {
    	snake(45, 'R'); // Snake's starting conditions. (90, 'R')
    	placeFood();
    	running = true;
    	paused = false;
    	score = 0;
    	timer = new Timer(150, this); // Timer delay determines game speed in milliseconds. 
        timer.start();
    }
    
    // Initialize snake. 
    private void snake(int headAndTailParts, char directionOfHead) {
		x = new int[(WIDTH * HEIGHT) / (UNIT_SIZE * UNIT_SIZE)];
		y = new int[(WIDTH * HEIGHT) / (UNIT_SIZE * UNIT_SIZE)];
		bodyParts = headAndTailParts;
		direction = directionOfHead;
	}
    
    // Place food randomly. 
    private void placeFood() {
    	
    	foodX = random.nextInt(WIDTH / UNIT_SIZE) * UNIT_SIZE;
        foodY = random.nextInt(HEIGHT / UNIT_SIZE) * UNIT_SIZE;
        
        // If snake's body and food overlap, then place food again. 
        for (int i = 1; i < bodyParts; i++) {
        	if (foodX == x[i] && foodY == y[i]) {
            	//System.out.println("body and food overlap");
                placeFood();
                //System.out.println("food placed again");
                break;
            }
        }
        
    }
    
    // Capture keyboard input. 
    
    @Override
    public void keyPressed(KeyEvent e) {
    	
    	if (running) {
    		
    		// If 'P' is pressed pause/resume the game. 
    		if (e.getKeyCode() == KeyEvent.VK_P) {
                paused = !paused;
                if (paused) {
                    timer.stop(); // Halt game loop updates. 
                } else {
                    timer.start(); // Resume game loop updates. 
                }
            }
    		
    		// If the game is not paused, then update the snake's direction based on the arrow keys. 
    		if (!paused) {
    			switch (e.getKeyCode()) {
		            case KeyEvent.VK_LEFT: changeDiretion('L'); break;
		            case KeyEvent.VK_RIGHT: changeDiretion('R'); break;
		            case KeyEvent.VK_UP: changeDiretion('U'); break;
		            case KeyEvent.VK_DOWN: changeDiretion('D'); break;
		        }
			}
    		
		}
    	
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
    	
    	// If the game is over, allow restarting with 'R'. 
    	if (!running) {
    		if (e.getKeyCode() == KeyEvent.VK_R) {
                startGame();
            }
		}
    	
    }
    
    @Override
    public void keyTyped(KeyEvent e) { }
    
	// Change snake direction according to key input. 
	private void changeDiretion(char keyInput) {
		
		if ((direction != 'R' && keyInput == 'L') || (direction != 'L' && keyInput == 'R') || 
				(direction != 'D' && keyInput == 'U') || (direction != 'U' && keyInput == 'D')) {
			
			direction = keyInput;
			
			move();
	        checkFood();
	        checkSelfCollision();
	        
	        repaint();
	        
		}
		
	}
	
	// Move the snake by updating body segment positions. 
    private void move() {
    	
    	// Shift body coordinates by one step. 
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
    	
    	// Update the head based on the current direction. 
        switch (direction) {
        	case 'R': x[0] = x[0] + UNIT_SIZE; break;
        	case 'D': y[0] = y[0] + UNIT_SIZE; break;
        	case 'L': x[0] = x[0] - UNIT_SIZE; break;
            case 'U': y[0] = y[0] - UNIT_SIZE; break;
        }
    	
    }
    
    // Check if the food has been eaten. 
    private void checkFood() {
        if (x[0] == foodX && y[0] == foodY) {
            timer.setDelay(Math.max(50, timer.getDelay() - 10)); // Increase speed by reducing delay (not below 50ms). 
        	score++;
        	bodyParts++;
        	//System.out.println("place food after eating");
        	placeFood();
        }
    }
    
    // Check self-collision. 
    private void checkSelfCollision() {
    	
        for (int i = 1; i < bodyParts; i++) {
            if (x[0] == x[i] && y[0] == y[i]) {
            	//System.out.println("self-collision");
            	running = false;
            	timer.stop();
            	break;
            }
        }
        
    }
    
    // Called by the timer every tick (game loop). 
    @Override
    public void actionPerformed(ActionEvent e) {
    	
        if (running && !paused) {
        	
            move();
            checkFood();
            checkSelfCollision();
            
            // Check collision with wall. 
            if (x[0] < 0 || x[0] >= WIDTH || y[0] < 0 || y[0] >= HEIGHT) {
            	running = false;
            	timer.stop();
            }
            
        }
        
        repaint();
        
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (running) {
            draw(g);
		} else {
            gameOver(g);
		}
    }
    
    private void draw(Graphics g) {
    	
        // Draw food as a circle. 
        g.setColor(Color.white);
        g.fillOval(foodX, foodY, UNIT_SIZE, UNIT_SIZE);
        
        // Draw the snake's head. 
    	g.setColor(Color.black);
        drawHead(g);
        
        // Draw the snake's tail. 
        for (int i = 1; i < bodyParts; i++) {
        	if (i % 2 == 1) {
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
        
    }
    
    // Snake's head depending on its direction. 
    private void drawHead(Graphics g) {
        switch (direction) {
			case 'R':
				g.fillRect(x[0], y[0], UNIT_SIZE + subUnit, UNIT_SIZE);
				g.setColor(Color.white);
				g.fillOval(x[0] + crownSize, y[0], eyeSize, eyeSize);
				g.fillOval(x[0] + crownSize, y[0] + crownSize, eyeSize, eyeSize);
				break;
			case 'D':
				g.fillRect(x[0], y[0], UNIT_SIZE, UNIT_SIZE + subUnit);
				g.setColor(Color.white);
				g.fillOval(x[0], y[0] + crownSize, eyeSize, eyeSize);
				g.fillOval(x[0] + crownSize, y[0] + crownSize, eyeSize, eyeSize);
				break;
			case 'L':
				g.fillRect(x[0] - subUnit, y[0], subUnit + UNIT_SIZE, UNIT_SIZE);
				g.setColor(Color.white);
				g.fillOval(x[0], y[0], eyeSize, eyeSize);
				g.fillOval(x[0], y[0] + crownSize, eyeSize, eyeSize);
				break;
			case 'U':
				g.fillRect(x[0], y[0] - subUnit, UNIT_SIZE, subUnit + UNIT_SIZE);
				g.setColor(Color.white);
				g.fillOval(x[0], y[0], eyeSize, eyeSize);
				g.fillOval(x[0] + crownSize, y[0], eyeSize, eyeSize);
				break;
		}
    }
    
    // Game over screen with score and restart instruction. 
    private void gameOver(Graphics g) {
    	
    	g.setColor(Color.white);
    	
    	// "Game Over"
        g.setFont(new Font("Monospaced", Font.BOLD, 50));
        g.drawString("Game Over", WIDTH / 6, HEIGHT / 5);
        
        // Final score
        g.setFont(new Font("Monospaced", Font.BOLD, 30));
        g.drawString("Score: " + score, WIDTH / 4, HEIGHT / 3);
        
        // Restart instruction
        g.setFont(new Font("Monospaced", Font.BOLD, 20));
        g.drawString("Press (R) to Restart", WIDTH / 3, HEIGHT / 2);
        
    }
    
    // Main entry point: Create the window and start the game. 
    public static void main(String[] args) {
        JFrame frame = new JFrame("-----SNAKE GAME-----   [Press (P) to Pause/Resume]");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new SnakeGame());
        frame.pack();
        frame.setLocationRelativeTo(null); // The window is placed in the center of the screen. 
        frame.setVisible(true);
    }
    
}


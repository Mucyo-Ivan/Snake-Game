import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;

public class SnakeGame extends JPanel implements ActionListener {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int UNIT_SIZE = 25;
    private static final int GAME_UNITS = (WIDTH * HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    private final LinkedList<Point> snake = new LinkedList<>();
    private Point food;
    private char direction = 'R';
    private boolean gameOver = false;
    private boolean paused = false;
    private Timer timer;
    private int score = 0;
    private int delay = 150; // Initial delay (speed of the game)

    public SnakeGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.black);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (gameOver) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) { // Restart on Enter key
                        resetGame();
                    }
                    return;
                }
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
                    case KeyEvent.VK_P:
                        paused = !paused;
                        break;
                }
            }
        });

        initializeGame();
    }

    public void initializeGame() {
        snake.clear();
        snake.add(new Point(5, 5)); // Initial position of the snake
        spawnFood();
        score = 0; // Reset score
        direction = 'R'; // Reset direction
        delay = 150; // Reset speed
        if (timer != null) timer.stop();
        timer = new Timer(delay, this); // Create a new timer with the initial delay
        timer.start();
    }

    public void resetGame() {
        gameOver = false;
        initializeGame();
    }

    public void spawnFood() {
        food = new Point((int) (Math.random() * (WIDTH / UNIT_SIZE)),
                (int) (Math.random() * (HEIGHT / UNIT_SIZE)));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver || paused) return;

        moveSnake();
        checkCollision();
        repaint();
    }

    public void moveSnake() {
        Point head = snake.getFirst();
        Point newHead = null;

        switch (direction) {
            case 'U':
                newHead = new Point(head.x, head.y - 1);
                break;
            case 'D':
                newHead = new Point(head.x, head.y + 1);
                break;
            case 'L':
                newHead = new Point(head.x - 1, head.y);
                break;
            case 'R':
                newHead = new Point(head.x + 1, head.y);
                break;
        }

        if (newHead.equals(food)) {
            snake.addFirst(newHead);
            spawnFood();
            score += 10; // Increment score
            Toolkit.getDefaultToolkit().beep(); // Beep sound effect

            // Speed up the game every 50 points
            if (score % 50 == 0 && delay > 50) {
                delay -= 10;
                timer.setDelay(delay);
            }
        } else {
            snake.addFirst(newHead);
            snake.removeLast();
        }
    }

    public void checkCollision() {
        Point head = snake.getFirst();

        // Check for wall collisions
        if (head.x < 0 || head.x >= WIDTH / UNIT_SIZE || head.y < 0 || head.y >= HEIGHT / UNIT_SIZE) {
            gameOver = true;
        }

        // Check for collisions with itself
        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                gameOver = true;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (gameOver) {
            String message = "Game Over! Press Enter to Restart";
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString(message, WIDTH / 2 - g.getFontMetrics().stringWidth(message) / 2, HEIGHT / 2);
            return;
        }

        // Draw the snake
        g.setColor(Color.green);
        for (Point p : snake) {
            g.fillRect(p.x * UNIT_SIZE, p.y * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
        }

        // Draw the food
        g.setColor(Color.red);
        g.fillRect(food.x * UNIT_SIZE, food.y * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);

        // Draw the score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Score: " + score, 10, 20);

        // Draw pause message
        if (paused) {
            String message = "Paused! Press 'P' to Resume";
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString(message, WIDTH / 2 - g.getFontMetrics().stringWidth(message) / 2, HEIGHT / 2);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        SnakeGame gamePanel = new SnakeGame();
        frame.add(gamePanel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

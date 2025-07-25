package com.snakegame;

import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import javax.swing.*;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private static final int PANEL_WIDTH = 600;
    private static final int PANEL_HEIGHT = 600;
    private static final int UNIT_SIZE = 20;

    private final LinkedList<Point> snake = new LinkedList<>();
    private Point food;
    private Direction direction;
    private Timer timer;
    private boolean running = false;
    private boolean paused = false;
    private int score = 0;

    public GamePanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        initGame();
    }

    private void initGame() {
        snake.clear();
        snake.add(new Point(PANEL_WIDTH / 2, PANEL_HEIGHT / 2));
        direction = Direction.RIGHT;
        score = 0;
        spawnFood();
        running = true;
        paused = false;
        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(100, this);
        timer.start();
    }

    private void spawnFood() {
        int maxX = (PANEL_WIDTH / UNIT_SIZE) - 1;
        int maxY = (PANEL_HEIGHT / UNIT_SIZE) - 1;
        Point p;
        do {
            int x = (int) (Math.random() * (maxX + 1)) * UNIT_SIZE;
            int y = (int) (Math.random() * (maxY + 1)) * UNIT_SIZE;
            p = new Point(x, y);
        } while (snake.contains(p));
        food = p;
    }

    private void moveSnake() {
        Point head = new Point(snake.getFirst());
        switch (direction) {
            case UP -> head.y -= UNIT_SIZE;
            case DOWN -> head.y += UNIT_SIZE;
            case LEFT -> head.x -= UNIT_SIZE;
            case RIGHT -> head.x += UNIT_SIZE;
        }
        snake.addFirst(head);
    }

    private void checkFood() {
        Point head = snake.getFirst();
        if (head.equals(food)) {
            score++;
            spawnFood();
        } else {
            snake.removeLast();
        }
    }

    private void checkCollision() {
        Point head = snake.getFirst();
        if (head.x < 0 || head.x >= PANEL_WIDTH || head.y < 0 || head.y >= PANEL_HEIGHT) {
            running = false;
        }
        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                running = false;
                break;
            }
        }
        if (!running) {
            timer.stop();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (running) {
            g.setColor(Color.RED);
            g.fillOval(food.x, food.y, UNIT_SIZE, UNIT_SIZE);
            g.setColor(Color.GREEN);
            for (Point p : snake) {
                g.fillRect(p.x, p.y, UNIT_SIZE, UNIT_SIZE);
            }
            g.setColor(Color.WHITE);
            g.drawString("Score: " + score, 10, 20);
            if (paused) {
                drawCenteredString(g, "Paused", getWidth(), getHeight());
            }
        } else {
            drawGameOver(g);
        }
    }

    private void drawGameOver(Graphics g) {
        g.setColor(Color.WHITE);
        drawCenteredString(g, "Game Over. Score: " + score, getWidth(), getHeight() / 2);
        drawCenteredString(g, "Press Enter to Restart", getWidth(), getHeight() / 2 + 40);
    }

    private void drawCenteredString(Graphics g, String text, int width, int y) {
        FontMetrics metrics = g.getFontMetrics(g.getFont());
        int x = (width - metrics.stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && !paused) {
            moveSnake();
            checkCollision();
            checkFood();
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (running) {
            switch (key) {
                case KeyEvent.VK_LEFT -> {
                    if (direction != Direction.RIGHT) direction = Direction.LEFT;
                }
                case KeyEvent.VK_RIGHT -> {
                    if (direction != Direction.LEFT) direction = Direction.RIGHT;
                }
                case KeyEvent.VK_UP -> {
                    if (direction != Direction.DOWN) direction = Direction.UP;
                }
                case KeyEvent.VK_DOWN -> {
                    if (direction != Direction.UP) direction = Direction.DOWN;
                }
                case KeyEvent.VK_SPACE -> {
                    paused = !paused;
                }
            }
        }
        if (!running && key == KeyEvent.VK_ENTER) {
            initGame();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // not needed
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // not needed
    }
}

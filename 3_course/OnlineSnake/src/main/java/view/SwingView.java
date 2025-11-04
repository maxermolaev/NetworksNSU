package view;

import model.*;
import controller.GameController;

import javax.swing.*;
import java.awt.event.*;

public class SwingView implements GameView {
    private final JFrame frame;
    private final GamePanel panel;
    private Direction currentDirection = Direction.RIGHT;
    private Timer timer;

    public SwingView() {
        frame = new JFrame("Snake Game");
        panel = new GamePanel();

        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        setupKeyBindings();
    }

    private void setupKeyBindings() {
        InputMap im = panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = panel.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "moveUp");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "moveDown");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "moveLeft");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "moveRight");

        am.put("moveUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentDirection != Direction.DOWN) {
                    currentDirection = Direction.UP;
                    System.out.println("⬆ UP pressed");
                }
            }
        });
        am.put("moveDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentDirection != Direction.UP) {
                    currentDirection = Direction.DOWN;
                    System.out.println("⬇ DOWN pressed");
                }
            }
        });
        am.put("moveLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentDirection != Direction.RIGHT) {
                    currentDirection = Direction.LEFT;
                    System.out.println("⬅ LEFT pressed");
                }
            }
        });
        am.put("moveRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentDirection != Direction.LEFT) {
                    currentDirection = Direction.RIGHT;
                    System.out.println("➡ RIGHT pressed");
                }
            }
        });
    }

    @Override
    public void render(GameField field) {
        panel.setGameField(field);
        panel.repaint();
    }

    @Override
    public Direction getUserInput() {
        return currentDirection;
    }

    @Override
    public void showGameOver(int score) {
        if (timer != null) timer.stop();
        JOptionPane.showMessageDialog(frame, "Game Over! Score: " + score);
    }

    @Override
    public void start(GameController controller) {
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        SwingUtilities.invokeLater(() -> {
            panel.setFocusable(true);
            panel.requestFocusInWindow();
        });

        timer = new Timer(150, e -> controller.updateGame());
        timer.start();
    }
}

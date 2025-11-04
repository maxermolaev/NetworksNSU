package view;

import model.*;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private GameField field;
    private static final int CELL_SIZE = 20;

    public void setGameField(GameField field) {
        this.field = field;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (field == null) return;

        Snake snake = field.getSnake();

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.RED);
        g.fillOval(field.getFood().getX() * CELL_SIZE, field.getFood().getY() * CELL_SIZE,
                CELL_SIZE, CELL_SIZE);

        g.setColor(Color.GREEN);
        for (Coords c : snake.getSnake()) {
            g.fillRect(c.getX() * CELL_SIZE, c.getY() * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        if (field != null)
            return new Dimension(field.getRow() * CELL_SIZE, field.getColumn() * CELL_SIZE);
        else
            return new Dimension(400, 400);
    }
}

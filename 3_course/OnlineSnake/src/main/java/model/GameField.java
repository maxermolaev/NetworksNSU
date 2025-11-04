package model;

import java.util.Random;

public class GameField {
    private int row;
    private int column;
    private Snake snake;
    private Food food;
    private int score;
    private boolean gameOver;

    public GameField(int row, int column) {
        this.row = row;
        this.column = column;
        startGame();
    }

    public void startGame() {
        snake = new Snake(new Coords(row / 2, column / 2));
        spawnFood();
        score = 0;
        gameOver = false;
    }

    public void spawnFood() {
        Random random = new Random();
        Coords pos;
        do {
            pos = new Coords(random.nextInt(row), random.nextInt(column));
        } while (snake.isSnake(pos));
        food = new Food(pos);
    }

    public void update() {
        Coords nextPos = snake.nextPosition(row, column);
        if (snake.isSnake(nextPos)) {
            gameOver = true;
        }

        boolean grow = nextPos.equals(food.getPosition());
        snake.move(nextPos, grow);
        if (grow) {
            score++;
            spawnFood();
        }
    }

    public int getScore() {
        return score;
    }

    public  Food getFood() {
        return food;
    }

    public Snake getSnake() {
        return snake;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}
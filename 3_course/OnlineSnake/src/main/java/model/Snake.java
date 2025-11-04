package model;

import java.util.Deque;
import java.util.LinkedList;

public class Snake {
    private Deque<Coords> snake = new LinkedList<>();
    private Direction direction;

    public Snake(Coords start) {
        snake.addFirst(start);
        direction = Direction.RIGHT;
    }

    public Coords getHead() {
        return snake.getFirst();
    }

    public Deque<Coords> getSnake() {
        return snake;
    }

    public void setDirection(Direction dir) {
        if ((direction == Direction.UP && dir == Direction.DOWN) ||
                (direction == Direction.DOWN && dir == Direction.UP) ||
                (direction == Direction.LEFT && dir == Direction.RIGHT) ||
                (direction == Direction.RIGHT && dir == Direction.LEFT)) {
            return;
        }
        this.direction = dir;
    }

    public Coords nextPosition(int row, int column) {
        Coords head = getHead();
        int x = head.getX();
        int y = head.getY();
        switch (direction) {
            case UP: y--; break;
            case DOWN: y++; break;
            case LEFT: x--; break;
            case RIGHT: x++; break;
        }
        return new Coords(x, y).wrap(row, column);
    }

    public void move(Coords newHead, boolean grow) {
        snake.addFirst(newHead);
        if (!grow) {
            snake.removeLast();
        }
    }

    public boolean isSnake(Coords coords) {
        return snake.contains(coords);
    }
}
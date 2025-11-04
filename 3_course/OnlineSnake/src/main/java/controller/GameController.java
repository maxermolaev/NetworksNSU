package controller;

import model.*;
import view.GameView;

public class GameController {
    private final GameField field;
    private final GameView view;

    public GameController(GameField field, GameView view) {
        this.field = field;
        this.view = view;
    }

    public void updateGame() {
        Direction dir = view.getUserInput();
        if (dir != null) field.getSnake().setDirection(dir);
        field.update();
        view.render(field);

        if (field.isGameOver()) {
            view.showGameOver(field.getScore());
        }
    }

    public void start() {
        view.start(this);
    }
}

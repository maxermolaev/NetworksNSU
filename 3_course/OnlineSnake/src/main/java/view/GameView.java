package view;

import model.Direction;
import model.GameField;
import controller.GameController;

public interface GameView {
    void render(GameField field);
    Direction getUserInput();
    void showGameOver(int score);
    void start(GameController presenter);
}


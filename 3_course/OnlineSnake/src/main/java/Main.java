import model.*;
import controller.GameController;
import view.SwingView;

public class Main {
    public static void main(String[] args) {
        GameField field = new GameField(20, 20);
        SwingView view = new SwingView();
        GameController controller = new GameController(field, view);
        controller.start();
    }
}

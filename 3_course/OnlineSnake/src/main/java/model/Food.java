package model;

import java.util.List;

public class Food {
    private Coords position;

    public Food(Coords position) {
        this.position = position;
    }

    public Coords getPosition() {
        return position;
    }

    public int getX() {
        return position.getX();
    }

    public int getY() {
        return position.getY();
    }

}

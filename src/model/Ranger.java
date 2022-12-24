package model;

public class Ranger {
    enum Axis { HORIZONTAL, VERTICAL }
    private Position position = new Position(0, 0);
    private Axis axis;
    public boolean isGoingLEFTorUP = true;

    public Ranger (Axis ax, Position pos) {
        position = new Position(pos);
        axis = ax;
    }

    public void setPosition(Position pos) {
        position = new Position(pos);
    }

    public Axis getDirection() {
        return axis;
    }

    public Position getPosition() {
        return new Position(position);
    }
}

package org.jbomberman.utils;

public class Triad {
    private Coordinate coordinate;
    private Direction direction;
    private boolean isLast;

    public Triad(Coordinate coordinate, Direction direction, boolean isLast) {
        this.coordinate = coordinate;
        this.direction = direction;
        this.isLast = isLast;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isLast() {
        return isLast;
    }

    @Override
    public String toString() {
        /*
        return "Triad{" +
                "coordinate=" + coordinate +
                ", direction=" + direction +
                ", isLast=" + isLast +
                '}';

         */
        return coordinate + " " + direction + " " + isLast;
    }
}

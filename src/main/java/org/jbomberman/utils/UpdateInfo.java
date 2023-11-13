package org.jbomberman.utils;

import java.util.ArrayList;

public class UpdateInfo {
    private final UpdateType updateType;
    private Coordinate oldc;
    private Coordinate newc;
    private Coordinate coordinate;
    private ArrayList<Coordinate> array;

    public UpdateInfo(UpdateType updateType, Coordinate oldc, Coordinate newc) {
        this.updateType = updateType;
        this.oldc = oldc;
        this.newc = newc;
    }
    public UpdateInfo(UpdateType updateType, Coordinate coordinate) {
        this.updateType = updateType;
        this.coordinate = coordinate;
    }
    public UpdateInfo(UpdateType updateType) {
        this.updateType = updateType;
    }

    public UpdateInfo(UpdateType updateType, ArrayList<Coordinate> array){
        this.updateType = updateType;
        this.array = array;
    }

    public UpdateType getUpdateType() {
        return updateType;
    }

    public Coordinate getOldCoord() {
        return oldc;
    }

    public Coordinate getNewCoord() {
        return newc;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public ArrayList<Coordinate> getArray() {
        return array;
    }
}

package org.jbomberman.utils;

import java.util.ArrayList;

public class UpdateInfo {
    private final UpdateType updateType;
    private Coordinate oldc;
    private Coordinate newc;
    private Coordinate coordinate;
    private ArrayList<Coordinate> array;
    private int index;

    public UpdateInfo(UpdateType updateType, Coordinate oldc, Coordinate newc, int index) {
        this.updateType = updateType;
        this.oldc = oldc;
        this.newc = newc;
        this.index = index;
    }
    public UpdateInfo(UpdateType updateType, Coordinate coordinate, int index) {
        this.updateType = updateType;
        this.coordinate = coordinate;
        this.index = index;
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

    public UpdateInfo(UpdateType updateType, int index) {
        this.updateType = updateType;
        this.index = index;
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

    public int getIndex() {
        return index;
    }
}

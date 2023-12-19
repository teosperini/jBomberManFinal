package org.jbomberman.utils;

import java.util.ArrayList;

public class UpdateInfo {
    private final UpdateType updateType;
    private boolean b;
    private String name;
    private Coordinate oldc;
    private Coordinate newc;
    private Coordinate c;
    private ArrayList<Coordinate> array;
    private int index;
    private int index2;

    public UpdateInfo(UpdateType updateType, Coordinate oldc, Coordinate newc, int index) {
        this.updateType = updateType;
        this.oldc = oldc;
        this.newc = newc;
        this.index = index;
    }
    public UpdateInfo(UpdateType updateType, Coordinate coordinate, int index) {
        this.updateType = updateType;
        this.c = coordinate;
        this.index = index;
    }

    public UpdateInfo(UpdateType updateType, Coordinate c) {
        this.updateType = updateType;
        this.c = c;
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

    public UpdateInfo(UpdateType updateType, ArrayList<Coordinate> array, int index) {
        this.updateType = updateType;
        this.array = array;
        this.index = index;
    }

    public UpdateInfo(UpdateType updateType, Coordinate oldc, Coordinate newc, String name, int index) {
        this.updateType = updateType;
        this.oldc = oldc;
        this.newc = newc;
        this.name = name;
        this.index = index;
    }

    public UpdateInfo(UpdateType updateType, boolean b) {
        this.updateType = updateType;
        this.b = b;
    }

    public UpdateInfo(UpdateType updateType, Coordinate coordinate, int index, int index2) {
        this.updateType = updateType;
        this.c = coordinate;
        this.index = index;
        this.index2 = index2;
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
        return c;
    }

    public ArrayList<Coordinate> getArray() {
        return array;
    }

    public int getIndex() {
        return index;
    }

    public int getIndex2(){return index2;}

    public boolean getBoo(){
        return b;
    }
}

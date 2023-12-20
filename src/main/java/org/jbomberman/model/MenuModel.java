package org.jbomberman.model;

import org.jbomberman.utils.UpdateInfo;
import org.jbomberman.utils.UpdateType;

import java.util.Observable;

public class MenuModel extends Observable {
    private int points;

    /*
    public void loadProfile() {
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.PAUSE));
    }
    public void leaveOptions() {
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.END_PAUSE));
    }

    public void exit() {
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.GAME_EXIT));
    }

     */

    public void setPoints(int points){
        this.points = points;
    }

    public void loadProfileMenu() {
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.PROFILE_LOADER));
    }
}

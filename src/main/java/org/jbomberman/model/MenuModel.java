package org.jbomberman.model;

import java.util.Observable;

public class MenuModel extends Observable {

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

    public void loadProfileMenu() {
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.PROFILE_LOADER));
    }
}

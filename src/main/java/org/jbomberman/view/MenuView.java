package org.jbomberman.view;


import org.jbomberman.controller.MainController;
import org.jbomberman.utils.SceneManager;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.Observable;
import java.util.Observer;

public class MenuView implements Observer{

    private final AnchorPane menu = new AnchorPane(SceneManager.getTitlePane("JBomberMan", 55));

    private Pane options;
    private Pane profile;
    private Profile profileGetter;

    //per fare il label toggle, devo creare un label e impostare che sul click deve eseguire l'azione

    private MainController controller;


    /**
     * Initializes the menu
     */
    public void initialize() {
        controller = MainController.getInstance();
        profileGetter = new Profile();
        buttons();
        options();
        profile();
    }


    private void buttons() {
        Label playButton = SceneManager.getButton("play", 0, Color.WHITE);
        Label optionsButton = SceneManager.getButton("options", 1, Color.WHITE);
        Label exitButton = SceneManager.getButton("quit game", 3, Color.WHITE);
        Label profileButton = SceneManager.getButton("profile", 2, Color.WHITE);

        playButton.setOnMouseClicked(event -> controller.gameButtonPressed());

        optionsButton.setOnMouseClicked(event -> options.setVisible(true));

        profileButton.setOnMouseClicked(event -> profile.setVisible(true));

        exitButton.setOnMouseClicked(event -> controller.gameExit());

        menu.getChildren().addAll(exitButton ,profileButton, playButton, optionsButton);
    }

    /**
     * Sets the options of the menu
     */
    private void options() {
        options = SceneManager.getPane("OPTIONS", 40);
        options.setVisible(false);
        options.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                options.setVisible(false);
            }
        });
        options.getChildren().add(SceneManager.backButton(options));
        menu.getChildren().add(options);
    }

    /**
     * Sets the profile of the menu
     */
    private void profile() {
        profile = profileGetter.getProfile();
        profile.setVisible(false);
        menu.getChildren().add(profile);
    }


    /**
     * Updates the menu
     * @param o     the observable object.
     * @param arg   an argument passed to the {@code notifyObservers}
     *                 method.
     */
    @Override
    public void update(Observable o, Object arg) {
        /*
        if (arg instanceof UpdateInfo updateInfo) {
            UpdateType updateType = updateInfo.getUpdateType();

            switch (updateType) {
                case PAUSE -> {
                    options.setVisible(true);
                    options.requestFocus();
                }
                case END_PAUSE -> {
                    profile.setVisible(false);
                    options.setVisible(false);
                    menu.requestFocus();
                }
                case GAME_EXIT -> {
                    controller.gameExit();
                }
                case PROFILE_LOADER -> {
                    profile.setVisible(true);
                    profile.requestFocus();
                }
            }
        }

         */
    }

    /**
     * Returns the menu
     * @return
     */
    public AnchorPane getMenu() {
        return menu;
    }
}
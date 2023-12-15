package org.jbomberman.view;


import org.jbomberman.controller.MainController;
import org.jbomberman.utils.Difficulty;
import org.jbomberman.utils.SceneManager;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.Observable;
import java.util.Observer;

public class MenuView implements Observer{

    //private final AnchorPane menu = new AnchorPane(SceneManager.getTitlePane("JBomberMan", 55));
    private final AnchorPane menu = new AnchorPane();
    private Pane mainMenu;
    private Pane options;
    private Pane profile;
    private Profile profileGetter;
    Pane difficulty;


    //per fare il label toggle, devo creare un label e impostare che sul click deve eseguire l'azione

    private MainController controller;


    /**
     * Initializes the menu
     */
    public void initialize() {
        controller = MainController.getInstance();
        profileGetter = new Profile();
        buttons();
        profile();
    }


    private void buttons() {
        Color color = Color.WHITE;
        mainMenu = SceneManager.getP("JBomberMan", false, true);
        mainMenu.setVisible(true);

        Label mainMenuPlayButton = SceneManager.getButton("play", 0, color);
        Label mainMenuOptionsButton = SceneManager.getButton("options", 1, color);
        Label mainMenuProfileButton = SceneManager.getButton("profile", 2, color);
        Label mainMeniExitButton = SceneManager.getButton("quit", 3, color);

        mainMenuPlayButton.setOnMouseClicked(mouseEvent -> controller.gameButtonPressed());
        mainMenuOptionsButton.setOnMouseClicked(mouseEvent -> SceneManager.changePane(mainMenu, options));
        mainMenuProfileButton.setOnMouseClicked(mouseEvent -> {});
        mainMeniExitButton.setOnMouseClicked(mouseEvent -> controller.gameExit());

        mainMenu.getChildren().addAll(mainMenuPlayButton, mainMenuOptionsButton, mainMenuProfileButton, mainMeniExitButton);

        //################# OPTIONS #################//
        options = SceneManager.getP("Options", false, false);
        options.setVisible(false);

        Label optionsDifficultyButton = SceneManager.getButton("difficulty",1, color);

        optionsDifficultyButton.setOnMouseClicked(mouseEvent -> SceneManager.changePane(options,difficulty));

        options.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
                SceneManager.changePane(options, mainMenu);
            }
        });

        options.getChildren().addAll(optionsDifficultyButton);

        //############# DIFFICULTY ################//
        difficulty = SceneManager.getP("Difficulty", false, false);

        difficulty.setVisible(false);

        Label difficultyEasy = SceneManager.getButton("easy", 0, Color.WHITE);
        Label difficultyNormal = SceneManager.getButton("normal", 1, Color.WHITE);
        Label difficultyHard = SceneManager.getButton("hard", 2, Color.WHITE);
        Label difficultyBackButton = SceneManager.getButton("back", 3, Color.WHITE);

        difficultyEasy.setOnMouseClicked(mouseEvent -> {
            controller.setDifficulty(Difficulty.EASY);
            SceneManager.changePane(difficulty, options);
        });
        difficultyNormal.setOnMouseClicked(mouseEvent -> {
            controller.setDifficulty(Difficulty.NORMAL);
            SceneManager.changePane(difficulty, options);
        });
        difficultyHard.setOnMouseClicked(mouseEvent -> {
            controller.setDifficulty(Difficulty.HARD);
            SceneManager.changePane(difficulty, options);
        });

        difficultyBackButton.setOnMouseClicked(mouseEvent -> SceneManager.changePane(difficulty, options));

        difficulty.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ESCAPE)){
                SceneManager.changePane(difficulty,options);
                keyEvent.consume();
            }
        });

        difficulty.getChildren().addAll(difficultyEasy,difficultyNormal,difficultyHard,difficultyBackButton);



        menu.getChildren().addAll(mainMenu, options, difficulty);


        /*
        Label playButton = SceneManager.getButton("play", 0, Color.WHITE);
        Label optionsButton = SceneManager.getButton("options", 1, Color.WHITE);
        Label profileButton = SceneManager.getButton("profile", 2, Color.WHITE);
        Label exitButton = SceneManager.getButton("quit game", 3, Color.WHITE);
        System.out.println(playButton.getAlignment());

        playButton.setOnMouseClicked(event -> controller.gameButtonPressed());

        optionsButton.setOnMouseClicked(event -> SceneManager.changePane(menu, options));

        profileButton.setOnMouseClicked(event -> SceneManager.changePane(menu, profile));

        exitButton.setOnMouseClicked(event -> controller.gameExit());

        //##### options
        options = SceneManager.getP("Options", false, false);


        difficulty = SceneManager.getP("Difficulty", false, false);
        difficulty.setVisible(false);

        Label difficultyEasy = SceneManager.getButton("easy", 0, Color.WHITE);
        Label difficultyNormal = SceneManager.getButton("normal", 1, Color.WHITE);
        Label difficultyHard = SceneManager.getButton("hard", 2, Color.WHITE);
        Label difficultyBackButton = SceneManager.getButton("back", 3, Color.WHITE);

        difficultyEasy.setOnMouseClicked(mouseEvent -> {
            controller.setDifficulty(Difficulty.EASY);
            SceneManager.changePane(difficulty, options);
        });
        difficultyNormal.setOnMouseClicked(mouseEvent -> {
            controller.setDifficulty(Difficulty.NORMAL);
            SceneManager.changePane(difficulty, options);
        });
        difficultyHard.setOnMouseClicked(mouseEvent -> {
            controller.setDifficulty(Difficulty.HARD);
            SceneManager.changePane(difficulty, options);
        });

        difficultyBackButton.setOnMouseClicked(mouseEvent -> SceneManager.changePane(difficulty, options));

        difficulty.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ESCAPE)){
                SceneManager.changePane(difficulty,options);
                keyEvent.consume();
            }
        });

        difficulty.getChildren().addAll(difficultyEasy,difficultyNormal,difficultyHard,difficultyBackButton);


        options.setVisible(false);

        options.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
                SceneManager.changePane(options, menu);
            }
        });

        options.getChildren().add(difficulty);

        menu.getChildren().addAll(exitButton ,profileButton, playButton, optionsButton, options);


         */
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
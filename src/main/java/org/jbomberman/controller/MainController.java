package org.jbomberman.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import org.jbomberman.model.GameModel;
import org.jbomberman.model.MenuModel;
import org.jbomberman.utils.BackgroundMusic;
import org.jbomberman.utils.Difficulty;
import org.jbomberman.utils.SceneManager;
import org.jbomberman.view.GameView;
import org.jbomberman.view.MenuView;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;


public class MainController {
    MenuView menuView;
    MenuModel model;
    GameModel gameModel;
    GameView gameView;


    Stage stage;
    Scene scene;

    private Timeline mobMovement = new Timeline();

    private boolean moving = false;
    private boolean pause = false;

    private static MainController instance;
    private Difficulty difficulty;

    private MainController() {
    }

    public static MainController getInstance() {
        if (instance == null) {
            instance = new MainController();
        }
        return instance;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }


    public void initialize(){
        menuView = new MenuView();
        model = new MenuModel();
        model.addObserver(menuView);
        menuView.initialize();

        difficulty = Difficulty.NORMAL;

        Parent root = menuView.getMenu();
        scene = new Scene(root, SceneManager.WIDTH, SceneManager.HEIGHT);
        stage.setScene(scene);
        stage.show();
    }

    //################ NEW GAME ################//
    public void gameButtonPressed() {
        gameView = new GameView();
        gameModel = new GameModel();
        gameModel.setDifficulty(difficulty);
        gameModel.addObserver(gameView);
        gameModel.notifyModelReady();

        scene.setRoot(gameView.getGame());
        gameView.getFocus();

        pause = false;
        moving = false;
        setTimeline();
        BackgroundMusic.playMusic();
        BackgroundMusic.setVolume();
    }


    public void loadProfile() {
        //TODO caricherà i dati dal model quando ci sarà il file json
    }

    //HANDLING OF THE KEY-EVENTS IN GAME

    public void handleGameKeyEvent(KeyEvent keyEvent) {
        KeyCode keyCode = keyEvent.getCode();
        //public void handleGameKeyEvent(KeyCode keyCode){
        if (keyCode == KeyCode.ESCAPE){
            pauseController();
        } else if (!pause && !moving){
            if (keyCode == KeyCode.SPACE) {
                // if space is pressed we try to release a bomb
                gameModel.releaseBomb();
            } else {
                // se il gioco è in pausa o voglio uscire non devo poter ricevere input tranne il tasto
                // per uscire ne si possono muovere i mob
                // se il player si sta muovendo o sta respawnando non devo permettere di ricevere input
                //ma i mob devono continuare a muoversi
                gameModel.movePlayer(keyCode);
            }
        }

    }
    public void pauseController() {
        pause = true;
        gameView.pauseView();
        mobMovement.pause();

    }

    public void resumeController() {
        pause = false;
        gameView.resumeView();
        mobMovement.play();
    }

    public void moving(boolean bool) {
        moving = bool;
    }

    public void setDifficulty(Difficulty difficulty){
        this.difficulty = difficulty;
    }

    public void endMatch(){
        mobMovement.stop();
        pause = true;
    }

    private void setTimeline(){
        mobMovement = new Timeline(
                new KeyFrame(Duration.millis(1000), event ->{
                        if (!pause){
                            gameModel.moveEnemies();
                        }
                })
        );
        mobMovement.setCycleCount(Animation.INDEFINITE);
        mobMovement.play();
    }

    public void bombExploded() {
        gameModel.explosion();
    }

    public void quitMatch() {
        //BackgroundMusic.stopMusic();
        scene.setRoot(menuView.getMenu());
        gameModel = null;
        gameView = null;
        //resettare il gioco alle impostaizoni di partenza, pronto per una nuova partita
    }

    //############## CLOSE THE WINDOW #############//

    public void gameExit() {
        stage.close();
    }
}


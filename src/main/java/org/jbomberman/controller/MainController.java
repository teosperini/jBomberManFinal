package org.jbomberman.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.util.Duration;
import org.jbomberman.model.MainModel;
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
    public static final int DX=17;
    public static final int DY=12;
    MenuView menuView;
    MainModel model;
    GameView gameView;


    Stage stage;
    Scene scene;

    private Timeline mobMovement = new Timeline();

    private boolean moving = false;
    private boolean pause = false;

    private static MainController instance;
    private Difficulty difficulty = Difficulty.NORMAL;

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
        //creazione menu
        menuView = new MenuView();
        menuView.initialize();

        //creazione model
        model = new MainModel(DX, DY);
        model.addObserver(menuView);
        model.setLevel(1);
        //difficulty = Difficulty.NORMAL;
        //model.setDifficulty(difficulty);


        Parent root = menuView.getMenu();
        scene = new Scene(root, SceneManager.WIDTH, SceneManager.HEIGHT);
        stage.setScene(scene);
        stage.show();
    }

    //################ NEW GAME ################//

    public void loadLeaderboard() {
        // TODO caricherà i dati dal model quando ci sarà il file json
        //  alla fine di ogni partita sarà richiesto di inserire il nome (altrimenti "guest")
        //  e verrà salvato insieme al punteggio raggiunto e al livello raggiunto (completato)
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
                if (model.releaseBomb()) {
                    // start a timer that at the end explode the bomb
                    PauseTransition timer = new PauseTransition(Duration.millis(1750));
                    timer.setOnFinished(actionEvent -> model.explodeBomb());
                    timer.play();
                }
            } else {
                model.movePlayer(keyCode);
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

    private void setTimeline(){
        mobMovement = new Timeline(
                new KeyFrame(Duration.seconds(1), event ->{
                    if (!pause){
                        model.moveEnemies();
                    }
                })
        );
        mobMovement.setCycleCount(Animation.INDEFINITE);
        mobMovement.play();
    }


    //irreversibly stops the game
    public void endMatch(){
        BackgroundMusic.stopMusic();
        mobMovement.stop();
        pause = true;
    }

    //returns to the main menu
    public void quitMatch() {
        scene.setRoot(menuView.getMenu());

        model.deleteObservers();
        model.addObserver(menuView);


        model.reset();
        model.setLevel(1);
        model.resetGame();
    }

    public void playButtonPressed() {
        // preparing the model
        model.initialize();

        //deleting all the previous observers
        model.deleteObservers();

        //preparing the view
        gameView = new GameView();
        model.addObserver(gameView);
        model.notifyModelReady();
        gameView.initialize();

        if (!BackgroundMusic.isPlaying()) {
            BackgroundMusic.playMusic();
        } else {
            BackgroundMusic.stopMusic();
            BackgroundMusic.playMusic();
        }

        //setting the scene
        scene.setRoot(gameView.getGame());
        gameView.getFocus();

        //setting the controller
        pause = false;
        moving = false;
        setTimeline();
    }

    public void restart(){
        model.reset();
        model.setLevel(1);
        model.resetGame();
        playButtonPressed();
    }

    public void nextLevel(){
        model.reset();
        model.setLevel(2);
        playButtonPressed();
    }

    //############## CLOSE THE WINDOW #############//

    public void gameExit() {
        stage.close();
    }

    //##################### TEST ####################//
    //TODO remove after test

    public void removeBlocks() {
        model.removeRandom();
    }

    public void setNick(String nickname) {
        model.setShownNickname(nickname);
    }

    //###############################################//
}
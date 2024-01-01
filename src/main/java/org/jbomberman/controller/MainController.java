package org.jbomberman.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
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
    MenuView menuView;
    MainModel model;
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
        model = new MainModel();
        model.addObserver(menuView);

        menuView.initialize();
        model.setLevel(1);

        difficulty = Difficulty.NORMAL;

        model.setDifficulty(difficulty);


        Parent root = menuView.getMenu();
        scene = new Scene(root, SceneManager.WIDTH, SceneManager.HEIGHT);
        stage.setScene(scene);
        stage.show();
    }

    //################ NEW GAME ################//

    public void playButtonPressed(){
        model.deleteObservers();

        gameView = new GameView();
        model.addObserver(gameView);

        model.notifyModelReady();
        gameView.initialize();

        scene.setRoot(gameView.getGame());
        gameView.getFocus();

        pause = false;
        moving = false;
        setTimeline();

        BackgroundMusic.playMusic();
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
                model.releaseBomb();
            } else {
                // se il gioco è in pausa o voglio uscire non devo poter ricevere input tranne il tasto
                // per uscire ne si possono muovere i mob
                // se il player si sta muovendo o sta respawnando non devo permettere di ricevere input
                //ma i mob devono continuare a muoversi
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

    public void bombExploded() {
        model.explosion();
    }

    //irreversibly stops the game
    public void endMatch(){
        BackgroundMusic.stopMusic();
        mobMovement.stop();
        pause = true;
    }

    public void quitMatch() {
        scene.setRoot(menuView.getMenu());

        model.reset();
        model.addObserver(gameView);

        model.setLevel(1);
        model.initialize();
        //resettare il gioco alle impostaizoni di partenza, pronto per una nuova partita
    }

    public void restart() {
        BackgroundMusic.stopMusic();
        model.reset();
        model.initialize();
        model.setLevel(model.getLevel());

        gameView = new GameView();

        model.addObserver(gameView);
        model.notifyModelReady();

        gameView.initialize();

        pause = false;
        moving = false;

        scene.setRoot(gameView.getGame());
        gameView.getFocus();

        setTimeline();


        BackgroundMusic.playMusic();

    }

    public void nextLevel(){
        BackgroundMusic.stopMusic();
        model.reset();
        model.setLevel(2);
        model.initialize();

        gameView = new GameView();

        model.addObserver(gameView);
        model.notifyModelReady();

        gameView.initialize();

        pause = false;
        moving = false;

        scene.setRoot(gameView.getGame());
        gameView.getFocus();

        setTimeline();
        BackgroundMusic.playMusic();
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
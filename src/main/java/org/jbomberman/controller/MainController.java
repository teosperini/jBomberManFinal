package org.jbomberman.controller;

import org.jbomberman.model.GameModel;
import org.jbomberman.model.MenuModel;
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
    GameView gameView;
    GameModel gameModel;


    Stage stage;
    Scene scene;

    private boolean isPlayerMoving;
    public boolean isRespawning = false;

    private boolean isGamePaused = false;

    private static MainController instance;

    private MainController() {
        menuView = new MenuView();
        model = new MenuModel();
        model.addObserver(menuView);
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

        gameView = new GameView();
        gameModel = new GameModel();
        gameModel.addObserver(gameView);
        gameModel.ready();

        Parent root = menuView.getMenu();
        scene = new Scene(root, SceneManager.WIDTH, SceneManager.HEIGHT);
        stage.setScene(scene);
        stage.show();
    }

    //HANDLING OF BUTTONS OF THE MAIN MENU

    public void gameButtonPressed() {
        scene.setRoot(gameView.getGame());
    }

    public void loadProfile() {
        //TODO caricherà i dati dal model quando ci sarà il file json
    }

    public void gameExit() {
        stage.close();
    }

    //HANDLING OF THE KEY-EVENTS IN GAME
    public void handleGameKeyEvent(KeyEvent keyEvent) {
        KeyCode keyCode = keyEvent.getCode();
        if (keyCode == KeyCode.ESCAPE){
            pause();
            // if space is pressed we try to release a bomb
            // evitare che nel respawn venga data la possibilità di droppare una bomba



        } else if (isGamePaused || isRespawning || isPlayerMoving){
            if (keyCode == KeyCode.SPACE) {
            } else {
                // else if an arrow key is pressed we move the player
            /*
            model.movePlayer(keyCode);
            isRespawning = false;
             */
            }
        }
    }

    public void stop() {
        //timeline.stop();
    }

    //HANDLING OF BUTTONS IN GAME
    public void pause() {
        isGamePaused = !isGamePaused;
        if (isGamePaused){

        }
        //timeline.pause();
        gameModel.gamePause();


    }

    public void resume() {
        //timeline.play();
        gameModel.gameResume();
        //isGamePaused = false;
    }
    public void quitMatch() {
        scene.setRoot(menuView.getMenu());
    }

    public void newGame() {
        gameView = new GameView();
        //gameModel.reset()
        gameModel.addObserver(gameView);
    }

    public void moved(){
        isPlayerMoving = !isPlayerMoving;
    }

}

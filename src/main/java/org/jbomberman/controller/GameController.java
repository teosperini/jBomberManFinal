package org.jbomberman.controller;


public class GameController {
    // model
    /*

    private static GameController instance;
    private Scene scene;

    private GameController() {
        // Costruttore privato per impedire la creazione di istanze esterne
    }

    public static GameController getInstance() {
        if (instance == null) {
            instance = new GameController();
        }
        return instance;
    }

    public void setScene(Scene scene){
        this.scene = scene;
    }

    public void initialize() {
        // SOUNDTRACK
        BackgroundMusic.playMusic();

        System.out.println("initializing new timeline");
        timeline = new Timeline(
                new KeyFrame(Duration.millis(1200), event -> {
                    System.out.println("moving: " + System.currentTimeMillis());
                    model.moveEnemies();
                })
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public MainModel getModel() {
        return model;
    }

    public void handleKeyEvent(KeyEvent keyEvent) {
        KeyCode keyCode = keyEvent.getCode();
        if(isGamePaused){
            if(keyCode == KeyCode.ESCAPE){
                resume();
            }
        }else{
            if (keyCode == KeyCode.SPACE) {
                // if space is pressed we try to release a bomb
                // evitare che nel respawn venga data la possibilità di droppare una bomba
                if(!isRespawning) {
                    model.releaseBomb();
                }
            } else if (keyCode == KeyCode.ESCAPE) {
                pause();

            } else {
                // else if an arrow key is moved we move the player
                model.movePlayer(keyCode);
                isRespawning = false;
            }



     */
}
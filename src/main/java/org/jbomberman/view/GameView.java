package org.jbomberman.view;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.jbomberman.controller.MainController;
import org.jbomberman.utils.*;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.io.InputStream;
import java.util.*;

public class GameView implements Observer {

    public final AnchorPane gameBoard;

    public static final int SCALE_FACTOR = 35;


    Font labelFont = Font.loadFont(getClass().getResourceAsStream("/org/jbomberman/SfComicScriptBold-YXD2.ttf"), 20.0);
    //END GAME PANELS
    Pane gameOver;
    Pane victory;

    //PAUSE PANELS
    Pane pause;
    Pane options;


    //IMAGE VIEWS
    ImageView player;
    ImageView pu_bomb;
    ImageView pu_life;
    ImageView pu_invincible;
    ImageView exit;

    private final MainController controller;

    private final List<ImageView> randomBlocks;
    private final List<ImageView> enemies;

    private final HBox bottomBar = new HBox();



    Label livesLabel;
    Label pointsLabel;
    Label timerLabel;


    private enum BlockImage {
        //bomb is the real bomb, fire is the power_up
        BEDROCK(BlockImage.class.getResourceAsStream("definitive/static_block.png")),
        STONE(BlockImage.class.getResourceAsStream("definitive/random_block.png")),
        GRASS(BlockImage.class.getResourceAsStream("definitive/background_green.png")),
        STEVE(BlockImage.class.getResourceAsStream("definitive/steve.png")),
        DOOR(BlockImage.class.getResourceAsStream("definitive/exit.png")),
        BOMB(BlockImage.class.getResourceAsStream("bomb/bomb_2.png")),
        ENEMY(BlockImage.class.getResourceAsStream("definitive/enemy.png")),
        FIRE(BlockImage.class.getResourceAsStream("power_up/bomb.png")),
        LIFE(BlockImage.class.getResourceAsStream("power_up/oneup.png")),
        INVINCIBLE(BlockImage.class.getResourceAsStream("power_up/resistance.png")),
        COIN(BlockImage.class.getResourceAsStream("power_up/coin.gif"))
        ;

        private final Image image;

        BlockImage(InputStream path) {
            this.image = new Image(path);
        }

        public Image getImage() {
            return image;
        }


    }


    public GameView() {
        System.out.println("View Initialization");
        controller = MainController.getInstance();
        gameBoard = new AnchorPane();
        randomBlocks = new ArrayList<>();
        enemies = new ArrayList<>();
        initialize();
    }

    public void initialize() {
        genInGamePanels();
        addBottomBar();
        //gameBoard.toFront();
        //gameBoard.requestFocus();
        gameBoard.setOnKeyPressed(controller::handleGameKeyEvent);
/*
        gameBoard.setOnSwipeUp(swipeEvent -> {
            controller.handleGameKeyEvent(KeyCode.UP);
        });
        gameBoard.setOnSwipeDown(swipeEvent -> {
            controller.handleGameKeyEvent(KeyCode.DOWN);
        });
        gameBoard.setOnSwipeLeft(swipeEvent -> {
            controller.handleGameKeyEvent(KeyCode.LEFT);
        });
        gameBoard.setOnSwipeRight(swipeEvent -> {
            controller.handleGameKeyEvent(KeyCode.RIGHT);
        });
        gameBoard.setOnZoom(touchEvent -> {
            controller.handleGameKeyEvent(KeyCode.SPACE);
        });

 */

    }

    public void getFocus() {
        gameBoard.setVisible(true);
        gameBoard.toFront();
        gameBoard.requestFocus();
    }

    public AnchorPane getGame() {
        return gameBoard;
    }

    private void drawBomb(Coordinate coordinate) {
        BackgroundMusic.playBomb();
        ImageView tntImage = drawImage(coordinate, BlockImage.BOMB.getImage());
        PauseTransition spawnTNT = new PauseTransition(Duration.millis(50));
        PauseTransition pauseTNT = new PauseTransition(Duration.millis(400));
        PauseTransition respawnTNT = new PauseTransition(Duration.millis(400));
        PauseTransition removeTNT = new PauseTransition(Duration.millis(650));
        spawnTNT.setOnFinished(event -> {
            gameBoard.getChildren().add(tntImage);
            player.toFront();
            pauseTNT.play();
        });

        pauseTNT.setOnFinished(event1 -> {
            gameBoard.getChildren().remove(tntImage);
            respawnTNT.play();
        });

        respawnTNT.setOnFinished(event3 -> {
            gameBoard.getChildren().add(tntImage);
            player.toFront();
            removeTNT.play();
        });

        removeTNT.setOnFinished(event4 -> {
            // remove the image from the board and tell the model that the bomb is exploded
            gameBoard.getChildren().remove(tntImage);
            controller.bombExploded();
        });

        spawnTNT.play();
    }

    private ImageView drawImage(Coordinate c, Image image) {
        ImageView imageView = new ImageView(image);
        imageView.setLayoutX((double)c.x() * SCALE_FACTOR);
        imageView.setLayoutY((double)c.y() * SCALE_FACTOR);
        imageView.setFitHeight(SCALE_FACTOR);
        imageView.setFitWidth(SCALE_FACTOR);
        return imageView;
    }

    private void addBottomBar() {

        // build the bottomBar
        bottomBar.setLayoutX(0);
        bottomBar.setLayoutY((double)SCALE_FACTOR * 11);
        bottomBar.setPrefHeight(SCALE_FACTOR);
        bottomBar.setPrefWidth((double)SCALE_FACTOR * 17);
        bottomBar.setStyle("-fx-background-color: grey");

        // build the label
        Font customFontSmall = Font.loadFont(GameView.class.getResourceAsStream("/org/jbomberman/SfComicScriptBold-YXD2.ttf"), 25.0);
        livesLabel = new Label("Lives: " + 3);
        pointsLabel = new Label("Points: " + 0);
        timerLabel = new Label("Tempo: 0");

        livesLabel.setFont(customFontSmall);
        livesLabel.setTextFill(Color.BLACK);

        pointsLabel.setFont(customFontSmall);
        pointsLabel.setTextFill(Color.BLACK);
        pointsLabel.setLayoutX(200);

        //##################### TEST ####################//
        //TODO remove after test

        Button buttonBlocks = new Button();
        buttonBlocks.setOnMouseClicked(mouseEvent -> {
            controller.removeBlocks();
            mouseEvent.consume();
            gameBoard.toFront();
            gameBoard.requestFocus();
        });
        buttonBlocks.setLayoutX(40);
        buttonBlocks.setLayoutY(20);

        bottomBar.getChildren().addAll(livesLabel, buttonBlocks, pointsLabel);
        //###############################################//

        //bottomBar.getChildren().addAll(livesLabel);
        gameBoard.getChildren().add(bottomBar);
    }

    @Override
    public void update(Observable ignored, Object arg) {

        if (arg instanceof UpdateInfo updateInfo) {
            UpdateType updateType = updateInfo.getUpdateType();

            switch (updateType) {

                case L_MAP -> {
                    switch (updateInfo.getIndex()) {
                        case 0 -> loader(updateInfo.getArray(), BlockImage.GRASS.getImage());

                        case 1 -> loader(updateInfo.getArray(), BlockImage.BEDROCK.getImage());
                        case 2 -> updateInfo.getArray().forEach(coordinate -> {
                            ImageView image = drawImage(coordinate, BlockImage.STONE.getImage());
                            randomBlocks.add(image);
                            gameBoard.getChildren().add(image);
                        });
                        default -> throw new IllegalStateException("Unexpected value: " + updateInfo.getIndex());
                    }
                }

                case L_PLAYER -> {
                    player = drawImage(updateInfo.getCoordinate(), BlockImage.STEVE.getImage());
                    gameBoard.getChildren().add(player);
                }

                case L_EXIT -> {
                    exit = drawImage(updateInfo.getCoordinate(), BlockImage.DOOR.getImage());
                    gameBoard.getChildren().add(exit);
                }

                case L_ENEMIES -> updateInfo.getArray().forEach(coordinate ->  {
                        ImageView enemy = drawImage(coordinate, BlockImage.ENEMY.getImage());
                        enemies.add(enemy);
                        gameBoard.getChildren().add(enemy);
                    });

                case U_BLOCK_DESTROYED -> destroyEntity(randomBlocks, updateInfo.getIndex());

                case U_ENEMY_DEAD -> destroyEntity(enemies, updateInfo.getIndex());

                case U_POSITION -> {
                    Coordinate newCoord = updateInfo.getNewCoord();
                    Coordinate oldCoord = updateInfo.getOldCoord();
                    int oldX = oldCoord.x() * SCALE_FACTOR;
                    int oldY = oldCoord.y() * SCALE_FACTOR;
                    int newX = newCoord.x() * SCALE_FACTOR;
                    int newY = newCoord.y() * SCALE_FACTOR;

                    int index = updateInfo.getIndex();
                    TranslateTransition transition = new TranslateTransition();
                    if (oldX != newX){
                        transition.setByX((double)newX-oldX);
                    }else{
                        transition.setByY((double)newY-oldY);
                    }

                    if (index < 0) {
                        controller.moving(true);
                        transition.setDuration(Duration.millis(200));
                        transition.setOnFinished(event ->
                                controller.moving(false)
                        );
                        transition.setNode(player);
                    } else {
                        transition.setDuration(Duration.millis(600));
                        transition.setNode(enemies.get(index));
                    }
                    transition.play();
                }
                //TODO problema con la gestione del movimento e del respawn
                // per capire il problema, provare a muoversi nel mentre si viene colpiti da un mostro o una bomba
                // la posizione del giocatore risulterà sfalsata

                case U_RESPAWN -> {
                    controller.moving(true);
                    PauseTransition pauseRespawn = getPauseTransition();
                    updateLife(updateInfo.getIndex());
                    pauseRespawn.play();
                }

                case U_POINTS -> updatePoints(updateInfo.getIndex(), updateInfo.getIndex2(), updateInfo.getCoordinate());

                case L_PU_LIFE -> {
                    pu_life = drawImage(updateInfo.getCoordinate(),BlockImage.LIFE.getImage());
                    gameBoard.getChildren().add(pu_life);
                }
                case U_PU_LIFE -> doLifePowerUp(updateInfo.getIndex());

                case L_PU_BOMB ->{
                    pu_bomb = drawImage(updateInfo.getCoordinate(), BlockImage.FIRE.getImage());
                    gameBoard.getChildren().add(pu_bomb);
                }
                case U_PU_BOMB -> doBombPowerUp();

                case L_PU_INVINCIBLE -> {
                    pu_invincible = drawImage(updateInfo.getCoordinate(), BlockImage.INVINCIBLE.getImage());
                    gameBoard.getChildren().add(pu_invincible);
                }
                case U_PU_INVINCIBLE -> doInvinciblePowerUp(updateInfo.getBoo());

                case BOMB_RELEASED -> drawBomb(updateInfo.getCoordinate());

                case U_GAME_WIN -> {
                    BackgroundMusic.stopMusic();
                    BackgroundMusic.playSuccess();
                    controller.endMatch();
                    PauseTransition pauseGameWin = new PauseTransition(Duration.millis(400));
                    pauseGameWin.setOnFinished(event -> {
                        victory.toFront();
                        victory.setVisible(true);
                        victory.requestFocus();
                    });
                    pauseGameWin.play();
                }

                case U_GAME_OVER -> {
                    controller.endMatch();
                    PauseTransition pauseGameOver = new PauseTransition(Duration.millis(400));
                    gameOverAnimation(updateInfo.getCoordinate());
                    pauseGameOver.setOnFinished(event -> {
                        gameOver.toFront();
                        gameOver.setVisible(true);
                        gameOver.requestFocus();
                    });
                    pauseGameOver.play();
                }

                default -> throw new IllegalStateException("Unexpected value: " + updateType);
            }
        }
    }

    private PauseTransition getPauseTransition() {
        PauseTransition pauseRespawn = new PauseTransition(Duration.millis(200));
        pauseRespawn.setOnFinished(event -> {
            player.setTranslateX(0);
            player.setTranslateY(0);
            //player.setLayoutX(35);
            //player.setLayoutY(35);
            controller.moving(false);
        });
        return pauseRespawn;
    }

/*
    private AnimationTimer animationTimer(){
        return new AnimationTimer() {
            public void handle(long now) {
                // Avvia l'animazione parallela quando la transizione di traslazione inizia
                if (translateTransition.getStatus() == Animation.Status.RUNNING) {
                    parallelTransition.play();
                    stop(); // Arresta l'AnimationTimer dopo l'avvio dell'animazione parallela

                }
            }
        };
    }

 */

    private void gameOverAnimation(Coordinate coordinate) {
        //TODO
    }
    public void pauseView(){
        pause.toFront();
        pause.setVisible(true);
        pause.requestFocus();
    }

    public void resumeView() {
        SceneManager.changePane(pause,gameBoard);
    }

    private void updateLife(int index){
        livesLabel.setText("Lives: " + index);
    }

    private void updatePoints(int totalPoints, int currentPoints, Coordinate coordinate){
        pointsLabel.setText("Points: " + totalPoints);
        Label text = SceneManager.getText(Integer.toString(currentPoints), coordinate, SCALE_FACTOR);

        gameBoard.getChildren().add(text);
        text.setVisible(true);
        text.toFront();

        TranslateTransition transition = new TranslateTransition(Duration.millis(700), text);
        transition.setByY(-30);

        FadeTransition fadeOutTransition = new FadeTransition(Duration.millis(500), text);
        fadeOutTransition.setFromValue(1);
        fadeOutTransition.setToValue(0);


        transition.setOnFinished(actionEvent -> fadeOutTransition.play());
        fadeOutTransition.setOnFinished(actionEvent -> gameBoard.getChildren().remove(text));

        transition.play();

        System.out.println("puntiiiiiiiii");
    }

    private void doLifePowerUp(int index) {
        updateLife(index);
        powerUPs(pu_life);
    }

    private void doBombPowerUp(){
        powerUPs(pu_bomb);
    }

    private void doInvinciblePowerUp(boolean boo) {
        if (boo) {
            player.setOpacity(0.5);
            powerUPs(pu_invincible);
        }else {
            player.setOpacity(1);
        }
    }

    private void powerUPs(ImageView imageView) {
        PauseTransition removePU = new PauseTransition(Duration.millis(200));
        removePU.setOnFinished(event -> gameBoard.getChildren().remove(imageView));
        removePU.play();

        imageView.setFitHeight(25);
        imageView.setFitWidth(25);
        //bottomBar.setAlignment(Pos.BOTTOM_RIGHT);
        //questo pezzo qui dovrebbe posizionare il power up nella bottom bar in base a che
        //power up è
        HBox.setMargin(imageView, new Insets(5, 0, 0, 10));
        bottomBar.getChildren().add(imageView);
    }

    private void destroyEntity(List<ImageView> array, int index) {
        gameBoard.getChildren().remove(array.get(index));
        array.remove(index);
    }

    private void loader(ArrayList<Coordinate> array, Image image) {
        array.forEach(coordinate -> {
            gameBoard.getChildren().add(drawImage(coordinate, image));
        });
    }

    private void genInGamePanels() {
        //################# PAUSE ################//
        pause = SceneManager.getP("Pause", true, false);
        pause.setVisible(false);

        Label pauseResumeButton = SceneManager.getButton("resume", 0, Color.WHITE);
        Label pauseOptionsButton = SceneManager.getButton("options", 1, Color.WHITE);
        Label pauseRestartButton = SceneManager.getButton("restart", 2, Color.WHITE);
        Label pauseExitButton = SceneManager.getButton("menu", 3, Color.WHITE);

        pauseResumeButton.setOnMouseClicked(mouseEvent -> controller.resumeController());
        pauseOptionsButton.setOnMouseClicked(mouseEvent -> SceneManager.changePane(pause,options));
        pauseRestartButton.setOnMouseClicked(mouseEvent -> controller.gameButtonPressed());
        pauseExitButton.setOnMouseClicked(mouseEvent -> controller.quitMatch());

        pause.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ESCAPE)){
                controller.resumeController();
                keyEvent.consume();
            }
        });

        pause.getChildren().addAll(pauseResumeButton, pauseOptionsButton, pauseRestartButton, pauseExitButton);

        //################# OPTIONS ################//
        options = SceneManager.getP("Options", true, false);
        options.setVisible(false);

        Label optionsBackButton = SceneManager.getButton("back", 2, Color.WHITE);

        optionsBackButton.setOnMouseClicked(mouseEvent -> SceneManager.changePane(options, pause));

        options.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ESCAPE)){
                SceneManager.changePane(options,pause);
                keyEvent.consume();
            }
        });

        options.getChildren().addAll(optionsBackButton);

        //################## GAME OVER #################//
        gameOver = SceneManager.getP("Game Over", true,false);
        gameOver.setVisible(false);

        Label gameOverRestartButton = SceneManager.getButton("restart", 1, Color.WHITE);
        Label gameOverExitButton = SceneManager.getButton("menu", 2, Color.WHITE);

        gameOverExitButton.setOnMouseClicked(mouseEvent -> controller.quitMatch());
        gameOverRestartButton.setOnMouseClicked(mouseEvent -> controller.gameButtonPressed());

        gameOver.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ESCAPE)){
                controller.quitMatch();
            }
        });

        gameOver.getChildren().addAll(gameOverRestartButton, gameOverExitButton);

        //################## VICTORY ###################//
        victory = SceneManager.getP("Victory", true, false);
        victory.setVisible(false);

        Label victoryNextLevelButton = SceneManager.getButton("nextLevel", 1, Color.WHITE);
        Label victoryExitButton = SceneManager.getButton("menu", 3, Color.WHITE);
        victoryNextLevelButton.setOnMouseClicked(mouseEvent -> {
            //TODO next level
        });
        victoryExitButton.setOnMouseClicked(mouseEvent -> controller.quitMatch());

        victory.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ESCAPE)){
                controller.quitMatch();
            }
        });

        victory.getChildren().addAll(victoryNextLevelButton, victoryExitButton);

        //################## GAMEBOARD ################//
        gameBoard.getChildren().addAll(pause, options , gameOver, victory);
    }

}
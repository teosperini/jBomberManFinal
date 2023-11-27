package org.jbomberman.view;

import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import org.jbomberman.controller.MainController;
import org.jbomberman.utils.*;
import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class GameView implements Observer {

    public final AnchorPane gameBoard;

    public static final int SCALE_FACTOR = 35;


    Font labelFont = Font.loadFont(getClass().getResourceAsStream("/org/jbomberman/SfComicScriptBold-YXD2.ttf"), 20.0);
    Pane gameOver;
    Pane gameWin;
    Pane pause;
    Pane options;

    ImageView player;
    ImageView pu_bomb;
    ImageView pu_life;
    ImageView exit;

    private boolean playerTransitionStarted= false;
    private TranslateTransition playerTransition = new TranslateTransition(Duration.millis(400), player);

    private final MainController controller;

    private final List<ImageView> randomBlocks;
    private final List<ImageView> enemies;

    private final HBox bottomBar = new HBox();

    Label restartLabel;
    Label nextLevelLabel;
    Label escLabel;
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
        BOMB(BlockImage.class.getResourceAsStream("bomb/bomb_1.png")),
        ENEMY(BlockImage.class.getResourceAsStream("definitive/enemy.png")),
        FIRE(BlockImage.class.getResourceAsStream("power_up/fire.png")),
        LIFE(BlockImage.class.getResourceAsStream("power_up/fire.png"))
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
        System.out.println("pezzo di merd");
        controller = MainController.getInstance();
        gameBoard = new AnchorPane();
        randomBlocks = new ArrayList<>();
        enemies = new ArrayList<>();
        initialize();
    }

    public void initialize() {
        genPause();
        genWinLose();
        addBottomBar();
        gameBoard.setOnKeyPressed(controller::handleGameKeyEvent);
    }

    public AnchorPane getGame() {
        return gameBoard;
    }

    private void drawBomb(Coordinate coordinate) {
        ImageView tntImage = drawImage(coordinate, BlockImage.BOMB.getImage());
        PauseTransition spawnTNT = new PauseTransition(Duration.millis(50));
        PauseTransition pauseTNT = new PauseTransition(Duration.millis(500));
        PauseTransition respawnTNT = new PauseTransition(Duration.millis(500));
        PauseTransition removeTNT = new PauseTransition(Duration.millis(650));
        spawnTNT.setOnFinished(event -> {
            player.toFront();
            BackgroundMusic.playBomb();
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
            //model.bombExploded();
        });

        spawnTNT.play();
    }

    private ImageView drawImage(Coordinate c, Image image) {
        ImageView imageView = new ImageView(image);
        imageView.setLayoutX((double)c.x() * GameView.SCALE_FACTOR);
        imageView.setLayoutY((double)c.y() * GameView.SCALE_FACTOR);
        imageView.setFitHeight(GameView.SCALE_FACTOR);
        imageView.setFitWidth(GameView.SCALE_FACTOR);
        return imageView;
    }

    private void addBottomBar() {

        // build the bottomBar
        bottomBar.setLayoutX(0);
        bottomBar.setLayoutY((double)GameView.SCALE_FACTOR * 11);
        bottomBar.setPrefHeight(GameView.SCALE_FACTOR);
        bottomBar.setPrefWidth((double)GameView.SCALE_FACTOR * 17);
        bottomBar.setStyle("-fx-background-color: grey");

        // build the label
        Font customFontSmall = Font.loadFont(GameView.class.getResourceAsStream("/org/jbomberman/SfComicScriptBold-YXD2.ttf"), 25.0);
        livesLabel = new Label("Vite: " + 3);
        pointsLabel = new Label("Punti: 0");
        timerLabel = new Label("Tempo: 0");

        livesLabel.setAlignment(Pos.CENTER_LEFT);
        livesLabel.setFont(customFontSmall);
        livesLabel.setTextFill(Color.BLACK);

        bottomBar.getChildren().addAll(livesLabel);
        // add the bottomBar to the game board
        gameBoard.getChildren().add(bottomBar);
    }

    @Override
    public void update(Observable ignored, Object arg) {

        if (arg instanceof UpdateInfo updateInfo) {
            UpdateType updateType = updateInfo.getUpdateType();

            switch (updateType) {

                case L_MAP -> {
                    switch (updateInfo.getIndex()) {
                        case 0 -> {loader(updateInfo.getArray(), BlockImage.GRASS.getImage());
                            System.out.println("AAAAAAAAAAAAAAAA");
                        }
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

                    if (index < 0) {
                        player.setLayoutX(newX);
                        player.setLayoutY(newY);
                    } else {
                        enemies.get(index).setLayoutX(newX);
                        enemies.get(index).setLayoutY(newY);
                    }
                }

                /*
                case U_POSITION -> {
                    Coordinate newCoord = updateInfo.getNewCoord();
                    int newX = newCoord.x() * SCALE_FACTOR;
                    int newY = newCoord.y() * SCALE_FACTOR;

                    int index = updateInfo.getIndex();
                    System.out.println("old " + oldX +" "+ oldY + " current " + newX + " " + newY);
                    if (index < 0) {
                        // Imposta il punto di partenza solo una volta
                        if (!playerTransitionStarted) {
                            playerTransition.setFromX(35);
                            playerTransition.setFromY(35);
                            playerTransitionStarted = true;
                        }

                        // Imposta il punto di arrivo
                        playerTransition.setToX(newX);
                        playerTransition.setToY(newY);
                        playerTransition.play();
                    } else {
                        enemies.get(index).setLayoutX(newX);
                        enemies.get(index).setLayoutY(newY);
                    }
                }
                */

                case U_RESPAWN -> {
                    controller.moving(true);
                    Coordinate coordinate = updateInfo.getCoordinate();
                    System.out.println("coordinate respawn" +coordinate);
                    PauseTransition pauseRespawn = new PauseTransition(Duration.millis(400));
                    pauseRespawn.setOnFinished(event -> {
                        player.setLayoutX(coordinate.x() * SCALE_FACTOR);
                        player.setLayoutY(coordinate.y() * SCALE_FACTOR);
                        updateLife(updateInfo.getIndex());
                        controller.moving(false);
                    });
                    pauseRespawn.play();
                }

                case L_PU_LIFE -> {
                    Coordinate coordinate = updateInfo.getCoordinate();
                    pu_life = new ImageView(BlockImage.LIFE.getImage());
                    pu_life.setLayoutX(coordinate.x());
                    pu_life.setLayoutY(coordinate.y());
                    gameBoard.getChildren().add(pu_life);
                }
                case U_PU_LIFE -> doLifePowerUp(updateInfo.getIndex());

                case L_PU_BOMB ->{
                    Coordinate coordinate = updateInfo.getCoordinate();
                    pu_bomb = new ImageView(BlockImage.BOMB.getImage());
                    pu_bomb.setLayoutY(coordinate.x());
                    pu_bomb.setLayoutY(coordinate.y());
                    gameBoard.getChildren().add(pu_bomb);
                }
                case U_PU_BOMB -> doBombPowerUp();

                case BOMB_RELEASED -> drawBomb(updateInfo.getCoordinate());

                case U_GAME_WIN -> {
                    controller.pauseController();
                    PauseTransition pauseGameWin = new PauseTransition(Duration.millis(400));
                    pauseGameWin.setOnFinished(event -> {
                        gameWin.toFront();
                        gameWin.setVisible(true);
                        gameWin.requestFocus();
                    });
                    pauseGameWin.play();
                }

                case U_GAME_OVER -> {
                    PauseTransition pauseGameOver = new PauseTransition(Duration.millis(400));
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

    public void pauseView(){
        pause.toFront();
        pause.setVisible(true);
        pause.requestFocus();
    }

    public void resumeView() {
        pause.setVisible(false);
        gameBoard.toFront();
        gameBoard.requestFocus();
    }

    private void updateLife(int index){
        livesLabel.setText("Vite: " + index);
    }

    private void doLifePowerUp(int index) {
        updateLife(index);
        powerUPs(pu_life, 1);
    }

    private void doBombPowerUp(){
        powerUPs(pu_bomb, 2);
    }

    private void powerUPs(ImageView imageView, double i) {
        PauseTransition removePU = new PauseTransition(Duration.millis(200));
        removePU.setOnFinished(event -> gameBoard.getChildren().remove(imageView));
        removePU.play();

        imageView.setLayoutY(SCALE_FACTOR);
        imageView.setLayoutX(SCALE_FACTOR);
        bottomBar.setAlignment(Pos.BOTTOM_LEFT);
        //questo pezzo qui dovrebbe posizionare il power up nella bottom bar in base a che
        //power up è
        HBox.setMargin(imageView, new Insets(0, 0, i * 10, 0));
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

    private void genPause() {
        Label resumeButton = SceneManager.getButton("resume", 0, Color.BLACK);
        Label optionsButton = SceneManager.getButton("options", 1, Color.BLACK);
        Label exitButton = SceneManager.getButton("main menu", 2, Color.BLACK);

        Label backButton = SceneManager.getButton("back", 2, Color.BLACK);

        pause = SceneManager.getSTPane("PAUSE", 40);
        pause.getChildren().addAll(resumeButton, optionsButton, exitButton);
        pause.setVisible(false);

        options = SceneManager.getSTPane("OPTIONS", 40);
        options.getChildren().addAll(backButton);
        options.setVisible(false);

        gameBoard.getChildren().addAll(pause, options);


        resumeButton.setOnMouseClicked(event -> controller.resumeController());

        optionsButton.setOnMouseClicked(event -> {
            pause.setVisible(false);
            options.setVisible(true);
            options.requestFocus();

            backButton.setOnMouseClicked(event2 -> {
                options.setVisible(false);
                pause.setVisible(true);
            });
        });

        exitButton.setOnMouseClicked(event -> controller.quitMatch());

    }

    private void genWinLose() {
        gameOver = SceneManager.getSTPane("GAME OVER", 40);
        gameWin = SceneManager.getSTPane("YOU WON", 40);

        //perchè li sto facendo così e non a pulsanti?
        //tipo "next level", "main menu"
        //che sarebbe molto più semplice?
        //TODO cambiare in quello che scritto sopra
        restartLabel = new Label("Press ENTER to restart the game");
        nextLevelLabel = new Label("Press SPACE to go to the next level");
        escLabel = new Label("Press ESC to go back to the main menu");

        restartLabel.setFont(labelFont);
        restartLabel.setTextFill(Color.BLACK);
        restartLabel.setAlignment(Pos.CENTER);
        restartLabel.setLayoutY((double) SCALE_FACTOR * 9 - 5); // Posiziona in basso
        restartLabel.setLayoutX((double) SCALE_FACTOR * 4);


        nextLevelLabel.setFont(labelFont);
        nextLevelLabel.setTextFill(Color.BLACK);
        nextLevelLabel.setAlignment(Pos.CENTER);
        nextLevelLabel.setLayoutY((double) SCALE_FACTOR * 9 - 5); // Posiziona in basso
        nextLevelLabel.setLayoutX((double) SCALE_FACTOR * 4 - 5);

        escLabel.setFont(labelFont);
        escLabel.setTextFill(Color.BLACK);
        escLabel.setAlignment(Pos.CENTER);
        escLabel.setLayoutY((double) SCALE_FACTOR * 9 + 12); // Posiziona in basso
        escLabel.setLayoutX((double) SCALE_FACTOR * 3);

        gameWin.setVisible(false);
        gameOver.setVisible(false);
        gameOver.getChildren().addAll(restartLabel, escLabel);
        gameWin.getChildren().addAll(nextLevelLabel, escLabel);

        gameOver.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                controller.newGame();
            } else if (keyEvent.getCode() == KeyCode.ESCAPE) {
                controller.quitMatch();
            }
        });
        gameWin.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.SPACE) {
                System.out.println("mettere nuovo livello");
            } else if (keyEvent.getCode() == KeyCode.ESCAPE) {
                controller.quitMatch();
            }
        });

        gameBoard.getChildren().addAll(gameOver, gameWin);

    }
}
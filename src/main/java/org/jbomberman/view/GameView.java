package org.jbomberman.view;

import javafx.geometry.Insets;
import org.jbomberman.controller.MainController;
import org.jbomberman.utils.*;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
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

    ImageView fireImageView;
    ImageView oneUp;
    Font labelFont = Font.loadFont(getClass().getResourceAsStream("/org/jbomberman/SfComicScriptBold-YXD2.ttf"), 20.0);
    Pane gameOver;
    Pane gameWin;
    Pane pause;
    Pane options;

    ImageView player;

    private final MainController controller;

    private final List<ImageView> randomBlocks;
    private final List<ImageView> enemies;
    private boolean isBombExploding = false;
    private boolean allowKeyPress = true;

    private int player_HP = 3;
    private static final HBox BOTTOM_BAR = new HBox();

    Label restartLabel;
    Label nextLevelLabel;
    Label escLabel;
    Label livesLabel;
    Label pointsLabel;
    Label timerLabel;

    private final AudioClip boom = new AudioClip(getClass().getResource("/org/jbomberman/utils/tnt_exp.mp3").toExternalForm());

    private enum BlockImage {
        BEDROCK(BlockImage.class.getResourceAsStream("definitive/static_block.png")),
        STONE(BlockImage.class.getResourceAsStream("definitive/random_block.png")),
        GRASS(BlockImage.class.getResourceAsStream("definitive/background_green.png")),
        STEVE(BlockImage.class.getResourceAsStream("down/image_2.png")),
        DOOR(BlockImage.class.getResourceAsStream("definitive/door.png")),
        BOMB(BlockImage.class.getResourceAsStream("bomb/bomb_1.png")),
        ENEMY(BlockImage.class.getResourceAsStream("enemy.png")),
        FIRE(BlockImage.class.getResourceAsStream("power_up/fire.png")),
        LIFE(BlockImage.class.getResourceAsStream("power_up/life.png"))
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
        controller = MainController.getInstance();
        gameBoard = new AnchorPane();
        randomBlocks = new ArrayList<>();
        enemies = new ArrayList<>();
        initialize();
    }

    public void initialize() {

        allowKeyPress = true;
        //genPause();
        //genWinLose();
        addBottomBar();
        if (allowKeyPress) {
            gameBoard.setOnKeyPressed(controller::handleGameKeyEvent);
        }
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
            boom.play();
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

        // build the bottombar
        BOTTOM_BAR.setLayoutX(0);
        BOTTOM_BAR.setLayoutY((double)GameView.SCALE_FACTOR * 11);
        BOTTOM_BAR.setPrefHeight(GameView.SCALE_FACTOR);
        BOTTOM_BAR.setPrefWidth((double)GameView.SCALE_FACTOR * 17);
        BOTTOM_BAR.setStyle("-fx-background-color: grey");

        // build the label
        Font customFontSmall = Font.loadFont(GameView.class.getResourceAsStream("/org/jbomberman/SfComicScriptBold-YXD2.ttf"), 25.0);
        livesLabel = new Label("Vite: " + 3);
        pointsLabel = new Label("Punti: 0");
        timerLabel = new Label("Tempo: 0");

        livesLabel.setAlignment(Pos.CENTER_LEFT);
        livesLabel.setFont(customFontSmall);
        livesLabel.setTextFill(Color.BLACK);


        //vedere se aggiungere

        // add the label to the bottombar
        BOTTOM_BAR.getChildren().addAll(livesLabel);
        // add the bottombar to the game board
        //GameView.gameBoard.getChildren().add(BOTTOM_BAR);
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
                    }
                }

                case L_PLAYER -> {
                    player = drawImage(updateInfo.getCoordinate(), BlockImage.STEVE.getImage());
                    gameBoard.getChildren().add(player);
                }
                case L_ENEMIES -> {
                    updateInfo.getArray().forEach(coordinate ->  {
                        ImageView enemy = drawImage(coordinate, BlockImage.ENEMY.getImage());
                        enemies.add(enemy);
                        gameBoard.getChildren().add(enemy);
                    });
                }


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
                        /*
                        TranslateTransition transition = new TranslateTransition(Duration.millis(400), player);
                        transition.setFromX(oldX);
                        transition.setFromY(oldY);
                        transition.setToX(newX);
                        transition.setToY(newY);

                        transition.setOnFinished(event -> controller.moved());
                        transition.play();
                         */
                    } else {
                        enemies.get(index).setLayoutX(newX);
                        enemies.get(index).setLayoutY(newY);
                    }
                }

                case U_RESPAWN -> {
                    controller.respawning(true);
                    Coordinate coordinate = updateInfo.getCoordinate();

                    PauseTransition pauseLessLife = new PauseTransition(Duration.millis(400));
                    pauseLessLife.setOnFinished(event -> {
                        player.setLayoutX(coordinate.x());
                        player.setLayoutY(coordinate.y());
                        updateLife(updateInfo.getIndex());
                        controller.respawning(false);
                    });
                    pauseLessLife.play();
                }

                case U_PU_LIFE -> doLifePowerUp(updateInfo.getIndex());

                case U_PU_BOMB -> doBombPowerUp();

                case BOMB_RELEASED -> drawBomb(updateInfo.getCoordinate());

                case PAUSE -> {
                    pause.setVisible(true);
                    pause.requestFocus();
                }


            case END_PAUSE -> {
                pause.setVisible(false);
                gameBoard.requestFocus();
            }

            case GAME_WIN -> {
                BackgroundMusic.stopMusic();
                //controller.stop();
                allowKeyPress = false;


                PauseTransition pauseGameWin = new PauseTransition(Duration.millis(400));
                pauseGameWin.setOnFinished(event -> {
                    gameWin.setVisible(true);
                });
                pauseGameWin.play();
            }

            case GAME_OVER -> {
                BackgroundMusic.stopMusic();
                //controller.stop();
                allowKeyPress = false;
                livesLabel.setText("Vite: " + 0);

                PauseTransition pauseGameOver = new PauseTransition(Duration.millis(400));
                pauseGameOver.setOnFinished(event -> {
                    gameOver.setVisible(true);
                    gameOver.requestFocus();
                });
                pauseGameOver.play();
            }

            case GAME_EXIT -> {
                //a che cazzo serve sto coso?
                BackgroundMusic.stopMusic();
                controller.stop();
                allowKeyPress = false;
                controller.quitMatch();
            }
            }
        }
    }

    private void updateLife(int index){
        livesLabel.setText("Vite: " + index);
    }
    private void doLifePowerUp(int index) {
        updateLife(index);
        PauseTransition pauseRemovePowerUp = new PauseTransition(Duration.millis(200));
        pauseRemovePowerUp.setOnFinished(event -> gameBoard.getChildren().remove(oneUp));
        pauseRemovePowerUp.play();
    }

    private void doBombPowerUp(){
    PauseTransition pauseRemovePowerUp = new PauseTransition(Duration.millis(200));
    pauseRemovePowerUp.setOnFinished(event -> gameBoard.getChildren().remove(fireImageView));
    pauseRemovePowerUp.play();

    ImageView imageView = new ImageView(BlockImage.FIRE.getImage());
    imageView.setLayoutY(SCALE_FACTOR);
    imageView.setLayoutX(SCALE_FACTOR);
    BOTTOM_BAR.setAlignment(Pos.BOTTOM_LEFT);
    HBox.setMargin(imageView, new Insets(0, 0, 10, 0));
    BOTTOM_BAR.getChildren().add(imageView);
    }

    private void destroyEntity(List<ImageView> array, int index) {
        gameBoard.getChildren().remove(array.get(index));
        array.remove(index);
    }

    private void loader(ArrayList<Coordinate> array, Image image) {
        array.forEach(coordinate -> {
            drawImage(coordinate, image);
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


        resumeButton.setOnMouseClicked(event -> controller.resume());

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
                //model.initialize();
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
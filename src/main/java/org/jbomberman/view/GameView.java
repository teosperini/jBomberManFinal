package org.jbomberman.view;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
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

import static org.jbomberman.utils.SceneManager.SCALE_FACTOR;
import static org.jbomberman.utils.SceneManager.createImageView;

public class GameView implements Observer {

    private final MainController controller;

    public final AnchorPane gameBoard;

    //END GAME PANELS
    Pane gameOver;
    Pane victory;

    //PAUSE PANELS
    Pane pause;
    Pane options;

    //IMAGE VIEWS
    ImageView player;
    ImageView puBomb;
    ImageView puLife;
    ImageView puInvincible;
    ImageView exit;

    //ARRAYS
    private final List<ImageView> randomBlocks;
    private final List<ImageView> enemies;
    private final List<ImageView> coins;
    private final List<ImageView> bombExplosion;

    private ArrayList<Image> bombs;
    //BOTTOM BAR
    private final HBox bottomBar = new HBox();
    Label livesLabel;
    Label pointsLabel;
    Label timerLabel;
    Label nameLabel;

    private int level;
    private boolean bombAnimation;

    //IMAGES
    private enum BlockImage {
        //bomb is the real bomb, fire is the power_up
        BEDROCK(BlockImage.class.getResourceAsStream("definitive/static_block.png")),
        BEDROCK2(BlockImage.class.getResourceAsStream("definitive/static_block2.png")),
        STONE(BlockImage.class.getResourceAsStream("definitive/random_block.png")),
        STONE2(BlockImage.class.getResourceAsStream("definitive/random_block2.png")),
        GRASS(BlockImage.class.getResourceAsStream("definitive/background_green.png")),
        GRASS2(BlockImage.class.getResourceAsStream("definitive/background_grey.png")),
        STEVE(BlockImage.class.getResourceAsStream("definitive/steve.png")),
        DOOR(BlockImage.class.getResourceAsStream("definitive/exit.png")),
        //BOMB(BlockImage.class.getResourceAsStream("bomb/bomb.gif")),
        ENEMY(BlockImage.class.getResourceAsStream("definitive/enemy.png")),
        ENEMY2(BlockImage.class.getResourceAsStream("definitive/steve.png")),
        FIRE(BlockImage.class.getResourceAsStream("power_up/bomb.png")),
        LIFE(BlockImage.class.getResourceAsStream("power_up/oneup.png")),
        INVINCIBLE(BlockImage.class.getResourceAsStream("power_up/resistance.png")),
        COIN(BlockImage.class.getResourceAsStream("power_up/coin.gif")),
        DESTRUCTION(BlockImage.class.getResourceAsStream("random_blocks/blocks.gif")),
        BOMB0(BlockImage.class.getResourceAsStream("bomb/bomb_0")),
        BOMB1(BlockImage.class.getResourceAsStream("bomb/bomb_1")),
        BOMB2(BlockImage.class.getResourceAsStream("bomb/bomb_2"))
        ;

        private final Image image;

        BlockImage(InputStream path) {
            this.image = new Image(path);
        }

        public Image getImage() {
            return image;
        }


    }

    //############### CONSTRUCTOR AND INITIALIZE ################//
    public GameView() {
        controller = MainController.getInstance();
        gameBoard = new AnchorPane();
        randomBlocks = new ArrayList<>();
        enemies = new ArrayList<>();
        coins = new ArrayList<>();
        bombExplosion = new ArrayList<>();
        addBottomBar();
    }

    public void initialize() {
        genInGamePanels();
        gameBoard.setOnKeyPressed(controller::handleGameKeyEvent);
    }

    //####################### PANELS #######################//
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
        pauseRestartButton.setOnMouseClicked(mouseEvent -> restart());
        pauseExitButton.setOnMouseClicked(mouseEvent -> {
            controller.endMatch();
            controller.quitMatch();
        });

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
        gameOverRestartButton.setOnMouseClicked(mouseEvent -> restart());

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
        victoryNextLevelButton.setOnMouseClicked(mouseEvent -> controller.nextLevel());

        victoryExitButton.setOnMouseClicked(mouseEvent -> controller.quitMatch());

        victory.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ESCAPE)){
                controller.quitMatch();
            }
        });
        System.out.println(level);
        if (level == 1) {
            victory.getChildren().addAll(victoryNextLevelButton, victoryExitButton);
        }
        else
            victory.getChildren().add(victoryExitButton);

        //################## GAMEBOARD ################//
        gameBoard.getChildren().addAll(pause, options , gameOver, victory);
    }

    private void restart() {
        controller.restart();
        /*
        gameBoard.getChildren().clear();
        randomBlocks.clear();
        enemies.clear();
        coins.clear();
        bombExplosion.clear();
        bottomBar.getChildren().clear();
        initialize();
         */
    }


    //#################### BOTTOM BAR ################//
    private void addBottomBar() {
        // build the bottomBar
        bottomBar.setLayoutX(0);
        bottomBar.setLayoutY((double)SCALE_FACTOR * 11);
        bottomBar.setPrefHeight(SCALE_FACTOR);
        bottomBar.setPrefWidth((double)SCALE_FACTOR * 17);
        bottomBar.setStyle("-fx-background-color: grey");

        // build the labels
        Font customFontSmall = Font.loadFont(GameView.class.getResourceAsStream("/org/jbomberman/SfComicScriptBold-YXD2.ttf"), 25.0);
        livesLabel = new Label("Lives: " + 3);
        pointsLabel = new Label("Points: " + 0);
        timerLabel = new Label("Tempo: 0");
        nameLabel = new Label();

        nameLabel.setFont(customFontSmall);
        nameLabel.setTextFill(Color.BLACK);

        livesLabel.setFont(customFontSmall);
        livesLabel.setTextFill(Color.BLACK);

        pointsLabel.setFont(customFontSmall);
        pointsLabel.setTextFill(Color.BLACK);
        pointsLabel.setAlignment(Pos.CENTER_RIGHT);

        nameLabel.setText("player: guest");
        HBox.setMargin(nameLabel, new Insets(0,0,0,20));

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

        bottomBar.getChildren().addAll(livesLabel, buttonBlocks, pointsLabel, nameLabel);
        //###############################################//

        gameBoard.getChildren().add(bottomBar);
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

        FadeTransition fadeOutTransition = new FadeTransition(Duration.millis(900), text);
        fadeOutTransition.setFromValue(1);
        fadeOutTransition.setToValue(0);
        fadeOutTransition.setOnFinished(actionEvent -> gameBoard.getChildren().remove(text));

        ParallelTransition parallelTransition  = new ParallelTransition(transition, fadeOutTransition);
        parallelTransition.play();
    }


    //####################### GETTER #######################//
    public AnchorPane getGame() {
        return gameBoard;
    }

    public void getFocus() {
        gameBoard.setVisible(true);
        gameBoard.toFront();
        gameBoard.requestFocus();
    }

    @Override
    public void update(Observable ignored, Object arg) {

        if (arg instanceof UpdateInfo updateInfo) {
            UpdateType updateType = updateInfo.getUpdateType();

            switch (updateType) {

                case LEVEL -> level = updateInfo.getIndex();

                case LOAD_MAP -> {
                    switch (updateInfo.getSubBlock()) {
                        case GROUND_BLOCKS -> {
                            if (level == 1)
                                loader(updateInfo.getArray(), BlockImage.GRASS.getImage());
                            else
                                loader(updateInfo.getArray(), BlockImage.GRASS2.getImage());
                        }

                        case STATIC_BLOCKS -> {
                            if (level == 1)
                                loader(updateInfo.getArray(), BlockImage.BEDROCK.getImage());
                            else
                                loader(updateInfo.getArray(), BlockImage.BEDROCK2.getImage());
                        }

                        case RANDOM_BLOCKS -> {
                            if (level == 1)
                                updateInfo.getArray().forEach(coordinate -> drawImageView(coordinate, BlockImage.STONE.getImage(), randomBlocks));
                            else
                                updateInfo.getArray().forEach(coordinate -> drawImageView(coordinate, BlockImage.STONE2.getImage(), randomBlocks));
                        }
                        default -> throw new IllegalStateException("Unexpected value: " + updateInfo.getIndex());
                    }
                }

                case LOAD_ENEMIES -> updateInfo.getArray().forEach(coordinate -> drawImageView(coordinate, BlockImage.ENEMY.getImage(), enemies));

                case LOAD_COINS -> updateInfo.getArray().forEach(coordinate -> drawImageView(coordinate, BlockImage.COIN.getImage(), coins));


                case LOAD_PLAYER -> player = loadItems(updateInfo.getCoordinate(), BlockImage.STEVE.getImage());

                case LOAD_EXIT -> exit = loadItems(updateInfo.getCoordinate(), BlockImage.DOOR.getImage());

                case LOAD_POWER_UP_LIFE -> puLife = loadItems(updateInfo.getCoordinate(), BlockImage.LIFE.getImage());

                case LOAD_POWER_UP_BOMB -> puBomb = loadItems(updateInfo.getCoordinate(), BlockImage.FIRE.getImage());

                case LOAD_POWER_UP_INVINCIBLE -> puInvincible = loadItems(updateInfo.getCoordinate(), BlockImage.INVINCIBLE.getImage());


                case LOAD_NAME -> {
                    if (updateInfo.getNickname() != null)
                        nameLabel.setText("player: "+ updateInfo.getNickname());
                }


                case UPDATE_BLOCK_DESTROYED -> removeImageView(randomBlocks, updateInfo.getIndex());

                case UPDATE_ENEMY_DEAD -> removeImageView(enemies, updateInfo.getIndex());

                case UPDATE_COINS -> removeImageView(coins, updateInfo.getIndex());


                case UPDATE_POSITION -> position(updateInfo.getNewCoord(), updateInfo.getOldCoord(), updateInfo.getIndex());

                case UPDATE_RESPAWN -> respawn(updateInfo.getIndex());


                case UPDATE_POINTS -> updatePoints(updateInfo.getIndex(), updateInfo.getIndex2(), updateInfo.getCoordinate());

                case UPDATE_PU_LIFE -> doLifePowerUp(updateInfo.getIndex());

                case UPDATE_PU_BOMB -> doBombPowerUp();

                case UPDATE_PU_INVINCIBLE -> doInvinciblePowerUp(updateInfo.getBoo());

                case UPDATE_BOMB_RELEASED -> {
                    BackgroundMusic.playBomb();
                    drawBomb(updateInfo.getCoordinate());
                }

                case UPDATE_EXPLOSION -> {
                    bombAnimation = false;
                    drawExplosion(updateInfo.getTriadArrayList(), 1);
                }

                case UPDATE_ENEMY_LIFE -> {
                    ImageView woundedEnemy = enemies.get(updateInfo.getIndex());
                    woundedEnemy.setImage(BlockImage.ENEMY2.getImage());
                }

                case UPDATE_GAME_WIN -> gameWin();

                case UPDATE_GAME_OVER -> gameLost(updateInfo.getCoordinate());

                default -> throw new IllegalStateException("Unexpected value: " + updateType);
            }
        }
    }

    private ImageView loadItems(Coordinate c, Image image) {
        ImageView item = createImageView(c, image);
        gameBoard.getChildren().add(item);
        return item;
    }


    private void gameLost(Coordinate c) {
        controller.endMatch();
        BackgroundMusic.playDeath();
        PauseTransition pauseGameOver = new PauseTransition(Duration.millis(400));
        gameOverAnimation(c);
        pauseGameOver.setOnFinished(event -> {
            gameOver.toFront();
            gameOver.setVisible(true);
            gameOver.requestFocus();
        });
        pauseGameOver.play();
    }

    private void gameWin() {
        controller.endMatch();
        BackgroundMusic.playSuccess();
        PauseTransition pauseGameWin = new PauseTransition(Duration.millis(400));
        pauseGameWin.setOnFinished(event -> {
            victory.toFront();
            victory.setVisible(true);
            victory.requestFocus();
        });
        pauseGameWin.play();
    }
    //#################### ANIMATION AND MOVEMENT ##################//

    private void position(Coordinate newC, Coordinate oldC, int index) {
        int oldX = oldC.x() * SCALE_FACTOR;
        int oldY = oldC.y() * SCALE_FACTOR;
        int newX = newC.x() * SCALE_FACTOR;
        int newY = newC.y() * SCALE_FACTOR;

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

    private void respawn(int index) {
        controller.moving(true);
        PauseTransition pauseRespawn = getPauseTransition();
        updateLife(index);
        pauseRespawn.play();
    }

    private PauseTransition getPauseTransition() {
        PauseTransition pauseRespawn = new PauseTransition(Duration.millis(200));
        pauseRespawn.setOnFinished(event -> {
            player.setTranslateX(0);
            player.setTranslateY(0);
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


    //##################### PAUSE ####################//
    public void pauseView(){
        pause.toFront();
        pause.setVisible(true);
        pause.requestFocus();
    }

    public void resumeView() {
        SceneManager.changePane(pause,gameBoard);
    }


    //########################## POWER UPS ########################//
    private void doLifePowerUp(int index) {
        updateLife(index);
        powerUPs(puLife);
    }

    private void doBombPowerUp(){
        powerUPs(puBomb);
    }

    private void doInvinciblePowerUp(boolean boo) {
        if (boo) {
            player.setOpacity(0.5);
            powerUPs(puInvincible);
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
        HBox.setMargin(imageView, new Insets(5, 0, 0, 10));
        bottomBar.getChildren().add(imageView);
    }


    //###################### IMAGEVIEW METHODS ######################//

    private void drawImageView(Coordinate coordinate, Image image, List<ImageView> entities) {
        ImageView imageView = createImageView(coordinate, image);
        entities.add(imageView);
        gameBoard.getChildren().add(imageView);
    }

    private void drawBomb(Coordinate coordinate) {
        bombs = new ArrayList<>(List.of(BlockImage.BOMB0.getImage(), BlockImage.BOMB1.getImage(), BlockImage.BOMB2.getImage()));
        bombAnimation = true;
        int i = 0;
        while (bombAnimation){
            ImageView tntImage = createImageView(coordinate, bombs.get(i));
            gameBoard.getChildren().add(tntImage);
            player.toFront();

            PauseTransition tnt = new PauseTransition(Duration.millis(250));
            tnt.setOnFinished(actionEvent -> {
                gameBoard.getChildren().remove(tntImage);
            });
        }
    }

    private void removeImageView(List<ImageView> array, int index) {
        gameBoard.getChildren().remove(array.get(index));
        if (array.equals(randomBlocks)){
            ImageView imageView = randomBlocks.get(index);
            destroyBlock(new Coordinate((int)imageView.getLayoutX()/SCALE_FACTOR, (int)imageView.getLayoutY()/SCALE_FACTOR), 1);
        } else if (array.equals(coins)){

            BackgroundMusic.playCoin();
        }
        array.remove(index);
    }

    private void destroyBlock(Coordinate c, int i){
        ImageView newImageView = createImageView(c, new Image(Objects.requireNonNull(GameView.class.getResourceAsStream( "random_blocks/" + i + ".png"))));

        gameBoard.getChildren().add(newImageView);
        int j = i + 1;
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(160));
        pauseTransition.setOnFinished(event -> {
            gameBoard.getChildren().remove(newImageView);
            if (j < 7) destroyBlock(c, j);
        });
        pauseTransition.play();
    }

    private void drawExplosion(ArrayList<Triad> triadArrayList, int i) {
        String path = "explosion/" + i;
        triadArrayList.forEach(triad -> {
            ImageView imageView;
            if (triad.getDirection().equals(Direction.CENTER)) {
                imageView = createImageView(triad.getCoordinate(), new Image(Objects.requireNonNull(GameView.class.getResourceAsStream( path + "/center.png"))));
            } else if (triad.isLast()) {
                imageView = createImageView(triad.getCoordinate(), new Image(Objects.requireNonNull(GameView.class.getResourceAsStream(path +"/" + triad.getDirection().getKeyCode() + "_external.png"))));
            } else {
                imageView = createImageView(triad.getCoordinate(), new Image(Objects.requireNonNull(GameView.class.getResourceAsStream(path +"/" + triad.getDirection().getKeyCode() + ".png"))));
            }
            bombExplosion.add(imageView);
            gameBoard.getChildren().add(imageView);
        });

        int j = i + 1;
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(150));
        pauseTransition.setOnFinished(event -> {
            removeExplosion();
            if (j < 4) drawExplosion(triadArrayList, j);
        });
        pauseTransition.play();
    }

    private void removeExplosion() {
        bombExplosion.forEach(imageView -> gameBoard.getChildren().remove(imageView));
    }

    private void loader(ArrayList<Coordinate> array, Image image) {
        array.forEach(coordinate -> gameBoard.getChildren().add(createImageView(coordinate, image)));
    }
}
package org.jbomberman.model;

import org.jbomberman.updatemanager.*;
import org.jbomberman.utils.*;
import javafx.animation.PauseTransition;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

import java.util.*;

public class GameModel extends Observable {

    private static final int NUMBER_OF_ENEMIES = 3;
    private static final ArrayList<Coordinate> COORDINATE_GROUND = new ArrayList<>();
    private static final ArrayList<Coordinate> COORDINATES_FIXED_BLOCKS = new ArrayList<>();
    private static final ArrayList<Coordinate> COORDINATES_RANDOM_BLOCKS = new ArrayList<>();
    private static final ArrayList<Coordinate> COORDINATE_ENEMIES = new ArrayList<>();

    private static final ArrayList<KeyCode> ENEMY_MOVES = new ArrayList<>(List.of(KeyCode.UP,KeyCode.DOWN,KeyCode.LEFT,KeyCode.RIGHT));
    // LIMITS OF THE MAP
    public final Coordinate MAX = new Coordinate(15, 9);
    public final Coordinate MIN = new Coordinate(1,1);
    // COORDINATES OF THE TNT
    private Coordinate TNT_COORDINATES;
    private int BOMB_RANGE = 1;

    //coordinate power up
    private Coordinate bombPu;
    private Coordinate lifePu;

    private int PLAYER_HP = 3;
    private Coordinate playerPosition = new Coordinate(1,1);

    // how much the character can move every time a key is pressed
    private final int MOVEMENT = 1;
    // how many random blocks are going to spawn
    private int NUM_RND_BLOCKS = 20;
    // the coordinates of the winning cell
    private Coordinate EXIT;

    public GameModel() {
        initialize();
    }

    public void initialize(){
        generateBlocks();
        powerUP();
        initializeEnemies();
        playerPosition = new Coordinate(1,1);
    }

    public void ready() {
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.L_MAP, COORDINATE_GROUND, 0));
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.L_MAP, COORDINATES_FIXED_BLOCKS, 1));
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.L_MAP, COORDINATES_RANDOM_BLOCKS, 2));
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.L_PLAYER, playerPosition));
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.L_ENEMIES, COORDINATE_ENEMIES));
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.L_PU_BOMB, bombPu));
    }

    private void powerUP() {
        Random random = new Random();
        int randomFire = random.nextInt(COORDINATES_RANDOM_BLOCKS.size());
        bombPu = COORDINATES_RANDOM_BLOCKS.get(randomFire);
        int randomLife = random.nextInt(COORDINATES_RANDOM_BLOCKS.size());
        lifePu = COORDINATES_RANDOM_BLOCKS.get(randomLife);
        if (bombPu.equals(EXIT) || lifePu.equals(EXIT)){
             powerUP();
        } else {

        }

    }


    public void generateBlocks() {
        generateFixedBlocks();
        generateRandomBlocks();
        generateRandomExit();
        generateBackground();
    }

    private void generateFixedBlocks() {
        // generates the coordinates of the internal fixed blocks

    }

    private void generateRandomBlocks() {
        Random random = new Random();
        int distanceFromBorder = 1;

        int i = 0;
        while (i < NUM_RND_BLOCKS) {
            Coordinate location = new Coordinate(
                    distanceFromBorder + random.nextInt(MAX.x() - 2 * distanceFromBorder + 1),
                    distanceFromBorder + random.nextInt(MAX.y() - 2 * distanceFromBorder + 1)
            );
            if (isValidLocation(location)) {
                COORDINATES_RANDOM_BLOCKS.add(location);
                i++;
            }
        }
    }

    private boolean isValidLocation(Coordinate c) {
        return !COORDINATES_FIXED_BLOCKS.contains(c) && (c.x() + c.y() > 3);
    }

    public void generateRandomExit() {
        Random random = new Random();
        int randomIndex = random.nextInt(COORDINATES_RANDOM_BLOCKS.size());
        EXIT = COORDINATES_RANDOM_BLOCKS.get(randomIndex);
    }

    private void generateBackground() {
        for (int x = 1; x <= 15; x += 1) {
            for (int y = 1; y < 10; y += 1) {
                COORDINATE_GROUND.add(new Coordinate(x, y));
            }
        }

        for (int x = MIN.x() + 1; x <= MAX.x(); x += 2) {
            for (int y = MIN.y() + 1; y <= MAX.y(); y += 2) {
                COORDINATES_FIXED_BLOCKS.add(new Coordinate(x, y));
            }
        }

        for (int x = 0; x <= MAX.x() + 1; x += 1) {
            for (int y = 0; y <= MAX.y() + 1; y += 1) {
                // Verifica se la coordinata è ai bordi
                if (x == 0 || x == MAX.x()+1 || y == 0 || y == MAX.y()+1) {
                    // Fai qualcosa con la coordinata ai bordi
                    // Esempio:
                    COORDINATES_FIXED_BLOCKS.add(new Coordinate(x, y));
                }
            }
        }

    }

    boolean isBombExploding = false;

    public void releaseBomb() {
        if ((TNT_COORDINATES != null) && isValidLocation(playerPosition)){
            return;
        }

        TNT_COORDINATES = playerPosition;

        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.BOMB_RELEASED));
    }

    public void bombExploded() {
        Set<Coordinate> toRemove = new HashSet<>();
        Set<Coordinate> enemiesToRemove = new HashSet<>();

        ArrayList<Triad> adjacentTernas = getCoordinates();
        adjacentTernas.add(new Triad(TNT_COORDINATES, Direction.CENTER, true));

        for (Triad terna : adjacentTernas) {
            Coordinate coord = terna.getCoordinate();

            if (playerPosition.equals(coord)) {
                lessLife();
            }

            if (COORDINATES_RANDOM_BLOCKS.contains(coord)) {
                toRemove.add(coord);
            }

            if (COORDINATE_ENEMIES.contains(coord)){
                enemiesToRemove.add(coord);
            }
        }

        toRemove.forEach(coordinate -> {
            int index = COORDINATES_RANDOM_BLOCKS.indexOf(coordinate);
            COORDINATES_RANDOM_BLOCKS.remove(coordinate);
            notifyBlockRemoved(index);
        });

        enemiesToRemove.forEach(coordinate -> {
            int index = COORDINATE_ENEMIES.indexOf(coordinate);
            COORDINATE_ENEMIES.remove(coordinate);
            notifyDeadEnemy(index);
        });

        System.out.println(adjacentTernas);
        TNT_COORDINATES = null;
        isBombExploding = false;
    }



    private void notifyDeadEnemy(int index) {
        setChanged();
        notifyObservers(new UEnemyDead(index));
    }


    private void lessLife() {
        PLAYER_HP -=1;
        if(PLAYER_HP <= 0){
            defeat();
        }
        else {
            PauseTransition respawn = new PauseTransition(Duration.millis(400));
            respawn.setOnFinished(event -> {
                playerPosition = new Coordinate(1,1);
                setChanged();
                notifyObservers(new UpdateInfo(UpdateType.U_RESPAWN, playerPosition));
            });
            respawn.play();
        }

    }

    private ArrayList<Triad> getCoordinates() {
        ArrayList<Triad> adjacentTernas = new ArrayList<>();
        boolean stopPropagationUp = false;
        boolean stopPropagationDown = false;
        boolean stopPropagationLeft = false;
        boolean stopPropagationRight = false;

        for (int distance = 1; distance <= BOMB_RANGE; distance++) {
            Coordinate adjacentCoordUp = new Coordinate(TNT_COORDINATES.x(), TNT_COORDINATES.y() - distance);
            Coordinate adjacentCoordDown = new Coordinate(TNT_COORDINATES.x(), TNT_COORDINATES.y() + distance);
            Coordinate adjacentCoordLeft = new Coordinate(TNT_COORDINATES.x() - distance, TNT_COORDINATES.y());
            Coordinate adjacentCoordRight = new Coordinate(TNT_COORDINATES.x() + distance, TNT_COORDINATES.y());

            if (!stopPropagationUp && !COORDINATES_FIXED_BLOCKS.contains(adjacentCoordUp)) {
                adjacentTernas.add(new Triad(adjacentCoordUp, Direction.UP, distance == BOMB_RANGE));
            } else {
                stopPropagationUp = true;
            }

            if (!stopPropagationDown && !COORDINATES_FIXED_BLOCKS.contains(adjacentCoordDown)) {
                adjacentTernas.add(new Triad(adjacentCoordDown, Direction.DOWN, distance == BOMB_RANGE));
            } else {
                stopPropagationDown = true;
            }

            if (!stopPropagationLeft && !COORDINATES_FIXED_BLOCKS.contains(adjacentCoordLeft)) {
                adjacentTernas.add(new Triad(adjacentCoordLeft, Direction.LEFT, distance == BOMB_RANGE));
            } else {
                stopPropagationLeft = true;
            }

            if (!stopPropagationRight && !COORDINATES_FIXED_BLOCKS.contains(adjacentCoordRight)) {
                adjacentTernas.add(new Triad(adjacentCoordRight, Direction.RIGHT, distance == BOMB_RANGE));
            } else {
                stopPropagationRight = true;
            }
        }

        return adjacentTernas;
    }


    public void powerUpBiggerExplosion(){
        BOMB_RANGE += 1;
        bombPu = null;
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.U_PU_BOMB));
    }

    public void powerUpIncreaseLife(){
        PLAYER_HP += 1;
        lifePu = null;
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.U_PU_LIFE));
    }

    /**
     * Given the key code of the key pressed by the player, changes accordingly its position,
     * checking if it found the exit or if got more power.
     * @param keyCode is the code of the key pressed by the player
     */
    public void movePlayer(KeyCode keyCode) {
        Coordinate oldPosition = playerPosition;
        Coordinate newPosition = calculateNewPosition(keyCode, playerPosition);

        if (!newPosition.equals(oldPosition) && !collision(newPosition)) {
            updatePlayerPosition(newPosition, oldPosition);

            if (newPosition.equals(EXIT) && COORDINATE_ENEMIES.isEmpty()) {
                victory();
            } else if (newPosition.equals(bombPu)) {
                powerUpBiggerExplosion();
            } else if (newPosition.equals(lifePu)){
                powerUpIncreaseLife();
            }
        }
    }

    /**
     * Starting from the current position, for the given keyCode calculates the new position on the game board
     * (without taking into account any collision).
     * @param keyCode is the code of the key pressed by the player
     * @return returns the new position of the player
     */
    private Coordinate calculateNewPosition(KeyCode keyCode, Coordinate currentPosition) {
        int deltaX = 0;
        int deltaY = 0;

        switch (keyCode) {
            case UP -> deltaY = -MOVEMENT;
            case DOWN -> deltaY = MOVEMENT;
            case LEFT -> deltaX = -MOVEMENT;
            case RIGHT -> deltaX = MOVEMENT;
            default -> {
                return playerPosition;
            }
        }

        int newX = clamp(currentPosition.x() + deltaX, MIN.x(), MAX.x());
        int newY = clamp(currentPosition.y() + deltaY, MIN.y(), MAX.y());

        return new Coordinate(newX, newY);
    }


    /**
     * if value<0 it returns 0;
     * if value>max it returns max;
     * otherwise returns value.
     */
    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Checks if the given coordinate collides with an existing fixed block or random block.
     * @param coordinate
     * @return true if the given coordinate collides with an existing fixed block or random block.
     */
    private boolean collision(Coordinate coordinate) {
        return (COORDINATES_FIXED_BLOCKS.contains(coordinate) || COORDINATES_RANDOM_BLOCKS.contains(coordinate) || (TNT_COORDINATES != null && TNT_COORDINATES.equals(coordinate)));
    }

    private void notifyBlockRemoved(int blockToRemove) {
        setChanged();
        notifyObservers(new UBlockDestroyed(blockToRemove));
    }

    private void victory() {
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.U_GAME_WIN));
        resetModel();
    }

    private void defeat() {
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.U_GAME_OVER));
        resetModel();
    }

    private void resetModel() {
        COORDINATES_RANDOM_BLOCKS.clear();
        COORDINATE_ENEMIES.clear();
        playerPosition = new Coordinate(1,1);
        PLAYER_HP = 3;
        BOMB_RANGE = 1;
    }

    public void updatePlayerPosition(Coordinate coordinate, Coordinate oldPosition) {
        //System.out.println("new position: " + coordinate + " old position: " + oldPosition);
        playerPosition = coordinate;
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.U_POSITION, oldPosition,coordinate, -1));
        controlPosition();
    }

    public void initializeEnemies() {
        Random random = new Random();
        int i = 0;
        while (i < NUMBER_OF_ENEMIES) {
            Coordinate coord = new Coordinate(random.nextInt(MAX.x()), random.nextInt(MAX.y()));

            if ((coord.x() + coord.y() > 3) && !collision(coord)) {
                COORDINATE_ENEMIES.add(coord);
                i++;
            }
        }

    }

    public void moveEnemies() {
        for (int i=0; i<COORDINATE_ENEMIES.size(); i++) {
            calculateNewEnemyPosition(i);
        }
    }

    public void calculateNewEnemyPosition(int enemyId) {
        Coordinate oldEnemyPosition = COORDINATE_ENEMIES.get(enemyId);
        Random random = new Random();

        Coordinate newEnemyPosition;
        do {
            KeyCode keyCode = ENEMY_MOVES.get(random.nextInt(ENEMY_MOVES.size()));
            newEnemyPosition = calculateNewPosition(keyCode, oldEnemyPosition);
        } while (collision(newEnemyPosition) || COORDINATE_ENEMIES.contains(newEnemyPosition));

        // the enemy didn't move: do nothing
        if (newEnemyPosition.equals(oldEnemyPosition)) return;

        COORDINATE_ENEMIES.set(enemyId, newEnemyPosition);
        updateEnemyPosition(oldEnemyPosition, newEnemyPosition, enemyId);
    }

    private void updateEnemyPosition(Coordinate oldPosition, Coordinate newPosition, int enemyId) {
        setChanged();
        notifyObservers(new UMovement(oldPosition,newPosition, enemyId));
        controlPosition();
    }

    private void controlPosition() {
        if(COORDINATE_ENEMIES.contains(playerPosition)){
            System.out.println("stessa posizione");
            lessLife();
        }
    }

    public void gameReset(){
        resetModel();
    }

    public void respawn() {

    }
}

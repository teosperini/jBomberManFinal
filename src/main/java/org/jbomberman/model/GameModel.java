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
    public final Coordinate max = new Coordinate(15, 9);
    public final Coordinate min = new Coordinate(1,1);
    // COORDINATES OF THE TNT
    private Coordinate tntCoordinates;
    private int bombRange = 1;

    //coordinate power up
    private Coordinate bombPu;
    private Coordinate lifePu;

    private int playerHp = 3;
    private Coordinate playerPosition = new Coordinate(1,1);

    // how much the character can move every time a key is pressed
    private final int movement = 1;
    // how many random blocks are going to spawn
    private final int numRndBlocks = 70;
    // the coordinates of the winning cell
    private Coordinate exit;

    private final Random random = new Random();


//############################  CONSTRUCTOR AND INITIALIZATION ############################//

    public GameModel() {
        initialize();
    }

    public void initialize(){
        generateBlocks();
        generatePowerUP();
        generateEnemies();
        playerPosition = new Coordinate(1,1);
    }

    public void generateBlocks() {
        generateBackground();
        generateRandomBlocks();
        generateRandomExit();
    }

    private void generateBackground() {
        for (int x = 1; x <= 15; x += 1) {
            for (int y = 1; y < 10; y += 1) {
                COORDINATE_GROUND.add(new Coordinate(x, y));
            }
        }

        for (int x = min.x() + 1; x <= max.x(); x += 2) {
            for (int y = min.y() + 1; y <= max.y(); y += 2) {
                COORDINATES_FIXED_BLOCKS.add(new Coordinate(x, y));
            }
        }

        for (int x = 0; x <= max.x() + 1; x += 1) {
            for (int y = 0; y <= max.y() + 1; y += 1) {
                // Verifica se la coordinata è ai bordi
                if (x == 0 || x == max.x()+1 || y == 0 || y == max.y()+1) {
                    // Fai qualcosa con la coordinata ai bordi
                    // Esempio:
                    COORDINATES_FIXED_BLOCKS.add(new Coordinate(x, y));
                }
            }
        }

    }

    private void generateRandomBlocks() {
        int i = 0;
        while (i < numRndBlocks) {
            Coordinate location = new Coordinate(
                    1 + random.nextInt(max.x()),
                    1 + random.nextInt(max.y())
            );
            if (isValidLocation(location) && !COORDINATES_FIXED_BLOCKS.contains(location)) {
                COORDINATES_RANDOM_BLOCKS.add(location);
                i++;
            }
        }
    }

    public void generateRandomExit() {
        int randomIndex = random.nextInt(COORDINATES_RANDOM_BLOCKS.size());
        exit = COORDINATES_RANDOM_BLOCKS.get(randomIndex);
    }

    private void generatePowerUP() {
        int randomFire = random.nextInt(COORDINATES_RANDOM_BLOCKS.size());
        bombPu = COORDINATES_RANDOM_BLOCKS.get(randomFire);
        int randomLife = random.nextInt(COORDINATES_RANDOM_BLOCKS.size());
        lifePu = COORDINATES_RANDOM_BLOCKS.get(randomLife);
        if (bombPu.equals(exit) || lifePu.equals(exit)){
             generatePowerUP();
        } else {

        }
    }
    
    public void generateEnemies() {
        int i = 0;
        while (i < NUMBER_OF_ENEMIES) {
            Coordinate coord = new Coordinate(random.nextInt(max.x()), random.nextInt(max.y()));

            if ((coord.x() + coord.y() > 3) && !collision(coord)) {
                COORDINATE_ENEMIES.add(coord);
                i++;
            }
        }

    }



//######################################  TNT  ######################################//

    boolean isBombExploding = false;

    public void releaseBomb() {
        if ((tntCoordinates != null) && isValidLocation(playerPosition)){
            return;
        }

        tntCoordinates = playerPosition;

        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.BOMB_RELEASED));
    }

    public void bombExploded() {
        Set<Coordinate> toRemove = new HashSet<>();
        Set<Coordinate> enemiesToRemove = new HashSet<>();

        ArrayList<Triad> adjacentTernas = getCoordinates();
        adjacentTernas.add(new Triad(tntCoordinates, Direction.CENTER, true));

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
        tntCoordinates = null;
        isBombExploding = false;
    }

    private ArrayList<Triad> getCoordinates() {
        ArrayList<Triad> adjacentTernas = new ArrayList<>();
        boolean stopPropagationUp = false;
        boolean stopPropagationDown = false;
        boolean stopPropagationLeft = false;
        boolean stopPropagationRight = false;

        for (int distance = 1; distance <= bombRange; distance++) {
            Coordinate adjacentCoordUp = new Coordinate(tntCoordinates.x(), tntCoordinates.y() - distance);
            Coordinate adjacentCoordDown = new Coordinate(tntCoordinates.x(), tntCoordinates.y() + distance);
            Coordinate adjacentCoordLeft = new Coordinate(tntCoordinates.x() - distance, tntCoordinates.y());
            Coordinate adjacentCoordRight = new Coordinate(tntCoordinates.x() + distance, tntCoordinates.y());

            if (!stopPropagationUp && !COORDINATES_FIXED_BLOCKS.contains(adjacentCoordUp)) {
                adjacentTernas.add(new Triad(adjacentCoordUp, Direction.UP, distance == bombRange));
            } else {
                stopPropagationUp = true;
            }

            if (!stopPropagationDown && !COORDINATES_FIXED_BLOCKS.contains(adjacentCoordDown)) {
                adjacentTernas.add(new Triad(adjacentCoordDown, Direction.DOWN, distance == bombRange));
            } else {
                stopPropagationDown = true;
            }

            if (!stopPropagationLeft && !COORDINATES_FIXED_BLOCKS.contains(adjacentCoordLeft)) {
                adjacentTernas.add(new Triad(adjacentCoordLeft, Direction.LEFT, distance == bombRange));
            } else {
                stopPropagationLeft = true;
            }

            if (!stopPropagationRight && !COORDINATES_FIXED_BLOCKS.contains(adjacentCoordRight)) {
                adjacentTernas.add(new Triad(adjacentCoordRight, Direction.RIGHT, distance == bombRange));
            } else {
                stopPropagationRight = true;
            }
        }

        return adjacentTernas;
    }


//####################################  POWER UPS AND LIFE  ####################################//

    private void lessLife() {
        playerHp -=1;
        if(playerHp <= 0){
            notifyDefeat();
        }
        else {
            playerPosition = new Coordinate(1,1);
            notifyLessLife();
        }
    }



//####################################  PLAYER MOVEMENT  ####################################//
    /**
     * Given the key code of the key pressed by the player, changes accordingly its position,
     * checking if it found the exit or if got more power.
     * @param keyCode is the code of the key pressed by the player
     */
    public void movePlayer(KeyCode keyCode) {
        Coordinate oldPosition = playerPosition;
        Coordinate newPosition = calculateNewPosition(keyCode, playerPosition);

        if (!newPosition.equals(oldPosition) && !collision(newPosition)) {
            playerPosition = newPosition;
            notifyPlayerPosition(newPosition, oldPosition);

            if (newPosition.equals(exit) && COORDINATE_ENEMIES.isEmpty()) {
                notifyVictory();
            } else if (newPosition.equals(bombPu)) {
                notifyPUExplosion();
            } else if (newPosition.equals(lifePu)){
                notifyPULife();
            }
        }
    }



//####################################   UTILITIES   ####################################//
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
            case UP -> deltaY = -movement;
            case DOWN -> deltaY = movement;
            case LEFT -> deltaX = -movement;
            case RIGHT -> deltaX = movement;
            default -> {
                return playerPosition;
            }
        }

        int newX = clamp(currentPosition.x() + deltaX, min.x(), max.x());
        int newY = clamp(currentPosition.y() + deltaY, min.y(), max.y());

        return new Coordinate(newX, newY);
    }
    private boolean isValidLocation(Coordinate c) {
        return !COORDINATES_FIXED_BLOCKS.contains(c) && (c.x() + c.y() > 3);
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
     * @param coordinate the coordinate to check
     * @return true if the given coordinate collides with an existing fixed block or random block.
     */
    private boolean collision(Coordinate coordinate) {
        return (COORDINATES_FIXED_BLOCKS.contains(coordinate) || COORDINATES_RANDOM_BLOCKS.contains(coordinate) || coordinate.equals(tntCoordinates));
    }

    private void controlPosition() {
        if(COORDINATE_ENEMIES.contains(playerPosition)){
            lessLife();
        }
    }



//####################################  NOTIFICATIONS  ####################################//

    public void notifyModelReady() {
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.L_MAP, COORDINATE_GROUND, 0));
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.L_MAP, COORDINATES_FIXED_BLOCKS, 1));
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.L_PU_BOMB, bombPu));
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.L_PU_LIFE, lifePu));
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.L_MAP, COORDINATES_RANDOM_BLOCKS, 2));
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.L_PLAYER, playerPosition));
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.L_ENEMIES, COORDINATE_ENEMIES));
    }
    private void notifyBlockRemoved(int blockToRemove) {
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.U_BLOCK_DESTROYED, blockToRemove));
    }

    private void notifyDeadEnemy(int index) {
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.U_ENEMY_DEAD, index));
    }

    public void notifyPlayerPosition(Coordinate coordinate, Coordinate oldPosition) {
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.U_POSITION, oldPosition,coordinate, -1));
        controlPosition();
    }

    private void notifyLessLife() {
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.U_RESPAWN, playerPosition, playerHp));
    }

    public void notifyPUExplosion(){
        bombRange += 1;
        bombPu = null;
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.U_PU_BOMB));
    }

    public void notifyPULife(){
        playerHp += 1;
        lifePu = null;
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.U_PU_LIFE));
    }

    private void notifyVictory() {
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.U_GAME_WIN));
    }

    private void notifyDefeat() {
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.U_GAME_OVER, playerPosition));
    }

    private void notifyEnemyMovement(Coordinate oldPosition, Coordinate newPosition, int enemyId) {
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.U_POSITION, oldPosition,newPosition, enemyId));
        controlPosition();
    }



//########################################  ENEMIES  ########################################//

    public void moveEnemies() {
        for (int i=0; i<COORDINATE_ENEMIES.size(); i++) {
            calculateNewEnemyPosition(i);
        }
    }

    public void calculateNewEnemyPosition(int enemyId) {
        Coordinate oldEnemyPosition = COORDINATE_ENEMIES.get(enemyId);
        int randomInt = random.nextInt(ENEMY_MOVES.size());
        Coordinate newEnemyPosition;
        int i = 0;
        do{
            newEnemyPosition = calculateNewPosition(ENEMY_MOVES.get((randomInt + i)%4), oldEnemyPosition);
            i++;

        } while((collision(newEnemyPosition) || COORDINATE_ENEMIES.contains(newEnemyPosition)) && i < 4);

        if (i != 4){
            COORDINATE_ENEMIES.set(enemyId, newEnemyPosition);
            notifyEnemyMovement(oldEnemyPosition, newEnemyPosition, enemyId);
        }
    }



//####################################  END OF THE MATCH  ####################################//

    public void gameReset() {
        COORDINATES_RANDOM_BLOCKS.clear();
        COORDINATE_ENEMIES.clear();
        playerPosition = new Coordinate(1,1);
        playerHp = 3;
        bombRange = 1;
    }
}

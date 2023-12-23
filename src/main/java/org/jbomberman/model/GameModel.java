package org.jbomberman.model;

import javafx.animation.PauseTransition;
import javafx.util.Duration;
import org.jbomberman.utils.*;
import javafx.scene.input.KeyCode;

import java.util.*;

public class GameModel extends Observable {

    private final ArrayList<Coordinate> coordinateGround = new ArrayList<>();
    private final ArrayList<Coordinate> coordinatesFixedBlocks = new ArrayList<>();
    private final ArrayList<Coordinate> coordinatesRandomBlocks = new ArrayList<>();
    private final ArrayList<Coordinate> coordinateEnemies = new ArrayList<>();
    private final ArrayList<Coordinate> coins = new ArrayList<>();

    private static final ArrayList<KeyCode> KEY_CODES = new ArrayList<>(List.of(KeyCode.UP,KeyCode.DOWN,KeyCode.LEFT,KeyCode.RIGHT));
    // LIMITS OF THE MAP
    public final Coordinate max = new Coordinate(15, 9);
    public final Coordinate min = new Coordinate(1,1);
    // COORDINATES OF THE TNT
    private Coordinate tntCoordinates;
    private int bombRange = 1;

    //coordinate exit and power up
    private Coordinate exit;
    private Coordinate bombPu;
    private Coordinate lifePu;
    private Coordinate invinciblePu;

    private int level = 1;

    private boolean playerInvincible = false;

    private int numberOfEnemies = 3;
    private int playerHp = 3;
    private Coordinate playerPosition = new Coordinate(1,1);

    // how much the character can move every time a key is pressed
    private static final int MOVEMENT = 1;
    // how many random blocks are going to spawn
    private static final int NUM_RND_BLOCKS = 20;
    // the coordinates of the winning cell

    private int points = 0;

    private final Random random = new Random();


//############################# CONSTRUCTOR AND INITIALIZATION ############################//

    public GameModel() {
        initialize();
    }

    public void initialize(){
        generateBlocks();
        generateItemsExit();
        generateEnemies();

        playerPosition = new Coordinate(1,1);
        playerHp = 3;
        bombRange = 1;
    }

    public void generateBlocks() {
        generateBackground();
        generateRandomBlocks();
    }

    public void setDifficulty(Difficulty difficulty){
        switch (difficulty){
            case EASY -> numberOfEnemies = 2;
            case NORMAL -> numberOfEnemies = 3;
            case HARD -> numberOfEnemies = 4;
            default -> numberOfEnemies = 0;
        }
    }

    private void generateBackground() {
        for (int x = 1; x <= 15; x += 1) {
            for (int y = 1; y < 10; y += 1) {
                coordinateGround.add(new Coordinate(x, y));
            }
        }

        for (int x = min.x() + 1; x <= max.x(); x += 2) {
            for (int y = min.y() + 1; y <= max.y(); y += 2) {
                coordinatesFixedBlocks.add(new Coordinate(x, y));
            }
        }

        for (int x = 0; x <= max.x() + 1; x += 1) {
            for (int y = 0; y <= max.y() + 1; y += 1) {
                //verifica se la coordinata è ai bordi
                if (x == 0 || x == max.x()+1 || y == 0 || y == max.y()+1) {
                    coordinatesFixedBlocks.add(new Coordinate(x, y));
                }
            }
        }

    }

    private void generateRandomBlocks() {
        int i = 0;
        while (i < NUM_RND_BLOCKS) {
            Coordinate location = new Coordinate(
                    1 + random.nextInt(max.x()),
                    1 + random.nextInt(max.y())
            );
            if (isValidLocation(location)) {
                coordinatesRandomBlocks.add(location);
                i++;
            }
        }
    }

    private void generateItemsExit() {
        //we can use this array to prevent the items to stack over the same position
        ArrayList<Coordinate> partial = new ArrayList<>(coordinatesRandomBlocks);

        int randomExit = random.nextInt(coordinatesRandomBlocks.size());
        exit = coordinatesRandomBlocks.get(randomExit);

        partial.remove(exit);

        int randomFire = random.nextInt(partial.size());
        bombPu = partial.get(randomFire);

        partial.remove(bombPu);

        int randomLife = random.nextInt(partial.size());
        lifePu = partial.get(randomLife);

        partial.remove(lifePu);

        int randomInvincible = random.nextInt(partial.size());
        invinciblePu = partial.get(randomInvincible);

        partial.remove(invinciblePu);


        int randomCoin1 = random.nextInt(partial.size());
        Coordinate coin1 = partial.get(randomCoin1);

        partial.remove(coin1);

        int randomCoin2 = random.nextInt(partial.size());
        Coordinate coin2 = partial.get(randomCoin2);

        partial.remove(coin2);

        int randomCoin3 = random.nextInt(partial.size());
        Coordinate coin3 = partial.get(randomCoin3);

        partial.remove(coin3);

        coins.add(coin1);
        coins.add(coin2);
        coins.add(coin3);
    }
    
    public void generateEnemies() {
        int i = 0;
        while (i < numberOfEnemies) {
            System.out.println("ayo");
            Coordinate coord = new Coordinate(random.nextInt(max.x()), random.nextInt(max.y()));

            if ((coord.x() + coord.y() > 3) && !collision(coord)) {
                coordinateEnemies.add(coord);
                i++;
            }
        }
    }



//######################################  TNT  ######################################//

    boolean isBombExploding = false;

    public void releaseBomb() {

        if (tntCoordinates != null || Objects.equals(playerPosition, new Coordinate(1, 1))){
            return;
        }
        tntCoordinates = playerPosition;


        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.UPDATE_BOMB_RELEASED, tntCoordinates));
    }

    public void explosion() {
        Set<Coordinate> blocksToRemove = new HashSet<>();
        Set<Coordinate> enemiesToRemove = new HashSet<>();

        //with getCoordinates() I get every coordinates that needs to be checked
        ArrayList<Triad> adjacentCoordinates = getCoordinates();
        adjacentCoordinates.add(new Triad(tntCoordinates, Direction.CENTER, true));

        for (Triad terna : adjacentCoordinates) {
            Coordinate coord = terna.getCoordinate();

            if (playerPosition.equals(coord)) {
                lessLife();
            }

            if (coordinatesRandomBlocks.contains(coord)) {
                blocksToRemove.add(coord);
            }

            if (coordinateEnemies.contains(coord)){
                enemiesToRemove.add(coord);
            }
        }

        blocksToRemove.forEach(coordinate -> {
            int index = coordinatesRandomBlocks.indexOf(coordinate);
            coordinatesRandomBlocks.remove(coordinate);
            notifyBlockRemoved(index);
        });

        enemiesToRemove.forEach(coordinate -> {
            int index = coordinateEnemies.indexOf(coordinate);
            coordinateEnemies.remove(coordinate);
            notifyDeadEnemy(index);
            int addedPoints = 200;
            points += addedPoints;
            notifyPoints(addedPoints, coordinate);
        });
        notifyExplosion(adjacentCoordinates);
        tntCoordinates = null;
        isBombExploding = false;
    }


    private ArrayList<Triad> getCoordinates() {
        ArrayList<Triad> adjacentCoordinate = new ArrayList<>();

        //Flags to stop the propagation of the bomb in that direction
        //I'm using these flags because you need to know if the propagation in that direction has encountered an
        //obstacle the cycle before
        boolean stopUp = false;
        boolean stopDown = false;
        boolean stopLeft = false;
        boolean stopRight = false;

        //Checking every direction, then, if the bomb range is >1, check the external blocks
        for (int distance = 1; distance <= bombRange; distance++) {
            Coordinate coordUp = new Coordinate(tntCoordinates.x(), tntCoordinates.y() - distance);
            Coordinate coordDown = new Coordinate(tntCoordinates.x(), tntCoordinates.y() + distance);
            Coordinate coordLeft = new Coordinate(tntCoordinates.x() - distance, tntCoordinates.y());
            Coordinate coordRight = new Coordinate(tntCoordinates.x() + distance, tntCoordinates.y());

            if (!stopUp && !coordinatesFixedBlocks.contains(coordUp)) {
                adjacentCoordinate.add(new Triad(coordUp, Direction.UP, distance == bombRange));
            } else {
                stopUp = true;
            }

            if (!stopDown && !coordinatesFixedBlocks.contains(coordDown)) {
                adjacentCoordinate.add(new Triad(coordDown, Direction.DOWN, distance == bombRange));
            } else {
                stopDown = true;
            }

            if (!stopLeft && !coordinatesFixedBlocks.contains(coordLeft)) {
                adjacentCoordinate.add(new Triad(coordLeft, Direction.LEFT, distance == bombRange));
            } else {
                stopLeft = true;
            }

            if (!stopRight && !coordinatesFixedBlocks.contains(coordRight)) {
                adjacentCoordinate.add(new Triad(coordRight, Direction.RIGHT, distance == bombRange));
            } else {
                stopRight = true;
            }
        }
        return adjacentCoordinate;
    }



//####################################  POWER UPS AND LIFE  ####################################//
    private void lessLife() {
        if (!playerInvincible) {
            playerHp -= 1;
            if (playerHp <= 0) {
                notifyDefeat();
            } else {
                playerPosition = new Coordinate(1, 1);
                notifyLessLife();
            }
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
            notifyPlayerPosition(newPosition, oldPosition, keyCode.getName());

            if (newPosition.equals(exit) && coordinateEnemies.isEmpty()) {
                notifyVictory();
            } else if (newPosition.equals(bombPu)){
                notifyPUExplosion();
            } else if (newPosition.equals(lifePu)){
                notifyPULife();
            } else if (newPosition.equals(invinciblePu)){
                notifyPUInvincible();
            } else {
                ArrayList<Coordinate> coinsToRemove = new ArrayList<>(coins);
                coinsToRemove.forEach(coordinate -> {
                    if (newPosition.equals(coordinate)){
                        //notifico che il player è passato sulla moneta quindi si può togliere
                        notifyCoin(coins.indexOf(coordinate));
                        coins.remove(coordinate);
                        //notifico l'aggiunta dei punti
                        int addedPoints = 400;
                        points+= addedPoints;
                        notifyPoints(addedPoints, coordinate);
                    }
                });
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
            case UP -> deltaY = -MOVEMENT;
            case DOWN -> deltaY = MOVEMENT;
            case LEFT -> deltaX = -MOVEMENT;
            case RIGHT -> deltaX = MOVEMENT;
            default -> {
                return playerPosition;
            }
        }

        int newX = clamp(currentPosition.x() + deltaX, min.x(), max.x());
        int newY = clamp(currentPosition.y() + deltaY, min.y(), max.y());

        return new Coordinate(newX, newY);
    }
    private boolean isValidLocation(Coordinate c) {
        return !coordinatesFixedBlocks.contains(c) && (c.x() + c.y() > 3)&& !coordinatesRandomBlocks.contains(c);
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
        return (coordinatesFixedBlocks.contains(coordinate) || coordinatesRandomBlocks.contains(coordinate) || coordinate.equals(tntCoordinates));
    }

    private void controlPosition() {
        if(coordinateEnemies.contains(playerPosition)){
            lessLife();
        }
    }

    public int getPoints(){
        return points;
    }



//####################################  NOTIFICATIONS  ####################################//

    public void notifyModelReady() {
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.LOAD_MAP, coordinateGround, SubMap.GROUND_BLOCKS));
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.LOAD_MAP, coordinatesFixedBlocks, SubMap.STATIC_BLOCKS));
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.LOAD_POWER_UP_BOMB, bombPu));
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.LOAD_POWER_UP_LIFE, lifePu));
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.LOAD_POWER_UP_INVINCIBLE, invinciblePu));
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.LOAD_EXIT, exit));
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.LOAD_COINS, coins));
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.LOAD_MAP, coordinatesRandomBlocks, SubMap.RANDOM_BLOCKS));
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.LOAD_PLAYER, playerPosition));
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.LOAD_ENEMIES, coordinateEnemies));

    }
    private void notifyBlockRemoved(int blockToRemove) {
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.UPDATE_BLOCK_DESTROYED, blockToRemove));
    }

    // quando viene ucciso un nemico, i punti compariranno sopra di esso
    // quando invece è il giocatore a passare sopra a un item che da punti, i punti compariranno
    // sopra il giocatore
    /**
     * notifica se il player è passato su una moneta
     * @param i
     */
    private void notifyCoin(int i) {
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.UPDATE_COINS,i));
    }

    private void notifyPoints(int currentPoints, Coordinate coordinate) {
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.UPDATE_POINTS, coordinate, points, currentPoints));
    }

    private void notifyDeadEnemy(int index) {
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.UPDATE_ENEMY_DEAD, index));
    }

    public void notifyPlayerPosition(Coordinate coordinate, Coordinate oldPosition, String name) {
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.UPDATE_POSITION, oldPosition,coordinate, name, -1));
        controlPosition();
    }

    private void notifyLessLife() {
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.UPDATE_RESPAWN, playerPosition, playerHp));
    }

    private void notifyExplosion(ArrayList<Triad> triadArrayList) {
        setChanged();
        notifyObservers(new UpdateInfo(triadArrayList, UpdateType.UPDATE_EXPLOSION));
    }

    public void notifyPUExplosion(){
        bombRange += 1;
        bombPu = null;
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.UPDATE_PU_BOMB));
    }

    public void notifyPULife(){
        playerHp += 1;
        lifePu = null;
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.UPDATE_PU_LIFE, playerHp));
    }

    public void notifyPUInvincible(){
        invinciblePu = null;
        playerInvincible = true;
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.UPDATE_PU_INVINCIBLE, true));
        PauseTransition pauseInvincible = new PauseTransition(Duration.seconds(10));
        pauseInvincible.setOnFinished(actionEvent -> {
            playerInvincible = false;
            setChanged();
            notifyObservers(new UpdateInfo(UpdateType.UPDATE_PU_INVINCIBLE, false));
        });
        pauseInvincible.play();
    }

    private void notifyVictory() {
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.UPDATE_GAME_WIN));
    }

    private void notifyDefeat() {
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.UPDATE_GAME_OVER, playerPosition));
    }

    private void notifyEnemyMovement(Coordinate oldPosition, Coordinate newPosition, int enemyId) {
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.UPDATE_POSITION, oldPosition,newPosition, enemyId));
        controlPosition();
    }




//########################################  ENEMIES  ########################################//
    public void difficulty(int i) {
        numberOfEnemies = i;
    }

    public void moveEnemies() {
        for (int i = 0; i< coordinateEnemies.size(); i++) {
            calculateNewEnemyPosition(i);
        }
    }

    public void calculateNewEnemyPosition(int enemyId) {
        Coordinate oldEnemyPosition = coordinateEnemies.get(enemyId);
        int randomInt = random.nextInt(KEY_CODES.size());
        Coordinate newEnemyPosition;
        int i = 0;
        do{
            newEnemyPosition = calculateNewPosition(KEY_CODES.get((randomInt + i)%4), oldEnemyPosition);
            i++;

        } while((collision(newEnemyPosition) || coordinateEnemies.contains(newEnemyPosition)|| !isSafeZone(newEnemyPosition)) && i < 4);

        if (i != 4){
            coordinateEnemies.set(enemyId, newEnemyPosition);
            notifyEnemyMovement(oldEnemyPosition, newEnemyPosition, enemyId);
        }
    }

    private boolean isSafeZone(Coordinate newEnemyPosition) {
        return (newEnemyPosition.x()+newEnemyPosition.y() >3);
    }
    //##################### TEST ####################//
    //TODO remove after test
    public void removeRandom() {
        ArrayList<Coordinate> array = new ArrayList<>(coordinatesRandomBlocks);
        array.forEach(coordinate -> {
                    int index = coordinatesRandomBlocks.indexOf(coordinate);
                    coordinatesRandomBlocks.remove(coordinate);
                    notifyBlockRemoved(index);
                }
        );
    }

    public void reset() {
        coordinateGround.clear();
        coordinatesFixedBlocks.clear();
        coordinatesRandomBlocks.clear();
        coordinateEnemies.clear();
        coins.clear();
        playerPosition = new Coordinate(1,1);
        bombRange = 1;
        playerInvincible = false;
        exit = null;
        bombPu = null;
        lifePu = null;
        invinciblePu = null;
        playerHp = 3;
        points = 0;
        tntCoordinates = null;
        deleteObservers();
    }

    public void setLevel(int level) {
        this.level = level;
    }

    //###############################################//
}

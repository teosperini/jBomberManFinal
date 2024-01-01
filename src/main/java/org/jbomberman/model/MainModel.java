package org.jbomberman.model;

import javafx.animation.PauseTransition;
import javafx.util.Duration;
import org.jbomberman.utils.*;
import javafx.scene.input.KeyCode;

import java.util.*;
import java.util.stream.Collectors;

public class MainModel extends Observable {
    // LIMITS OF THE MAP
    public static final int X_MAX = 15;
    public static final int Y_MAX = 9;
    public static final Coordinate MAX = new Coordinate(X_MAX, Y_MAX);
    public static final Coordinate MIN = new Coordinate(1,1);

    private int numberOfEnemies = 3;

    public static final int NUMBER_OF_COINS = 3;

    // how much the character can move every time a key is pressed
    private static final int MOVEMENT = 1;
    // how many random blocks are going to spawn
    private static final int NUM_RND_BLOCKS = 20;


    private final ArrayList<Coordinate> coordinateGround = new ArrayList<>();

    // the coordinates of the fixed blocks
    private final ArrayList<Coordinate> coordinatesFixedBlocks = new ArrayList<>();

    // the coordinates of the random blocks (the blocks that can be destroyed)
    private final ArrayList<Coordinate> coordinatesRandomBlocks = new ArrayList<>();

    private List<Coordinate>  freePositions;

    // the coordinates of the exit door
    private Coordinate exit;

    // the coordinates of the high potential bomb
    private Coordinate bombPu;

    // the coordinates of the extra life
    private Coordinate lifePu;

    // the coordinates of the harmor
    private Coordinate invinciblePu;


    // the coordinates of the enemies
    private final ArrayList<Coordinate> coordinateEnemies = new ArrayList<>();
    private final ArrayList<Integer> enemiesHp = new ArrayList<>();

    // the coordinates of the coins
    private final ArrayList<Coordinate> coins = new ArrayList<>();

    private static final ArrayList<KeyCode> KEY_CODES = new ArrayList<>(List.of(KeyCode.UP,KeyCode.DOWN,KeyCode.LEFT,KeyCode.RIGHT));

    // COORDINATES OF THE TNT
    private Coordinate tntCoordinates;
    private int bombRange = 1;


    private int level = 1;

    private boolean playerInvincible = false;

    private int playerHp = 3;
    private Coordinate playerPosition = new Coordinate(1,1);


    private int points = 0;

    private final Random random = new Random();

    private String nickname;
    private boolean doorOpen = false;

//############################# CONSTRUCTOR AND INITIALIZATION ############################//

    public MainModel() {
        initialize();
    }

    public void initialize(){
        generateBlocks();
        generateItemsAndExit();
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
        // this is the green ground (1 .. X_MAX, 1 .. Y_MAX)
        for (int x = MIN.x(); x <= MAX.x(); x += 1) {
            for (int y = MIN.y(); y <= MAX.y(); y += 1) {
                coordinateGround.add(new Coordinate(x, y));
            }
        }

        // at the beginning the free positions are all the green ground positions, except the ones of the first corner
        freePositions = coordinateGround.stream().filter(p -> p.x() + p.y() > 3).collect(Collectors.toList());

        // this generates the fixed checkerboard blocks, removing the corresponding positions from the free positions list
        for (int x = MIN.x() + 1; x <= MAX.x(); x += 2) {
            for (int y = MIN.y() + 1; y <= MAX.y(); y += 2) {
                var fixedBlock = new Coordinate(x, y);
                coordinatesFixedBlocks.add(fixedBlock);
                freePositions.remove(fixedBlock);
            }
        }

        // this generates the fixed blocks on the edges
        for (int x = 0; x <= MAX.x() + 1; x += 1) {
            for (int y = 0; y <= MAX.y() + 1; y += 1) {
                //verifica se la coordinata è ai bordi
                if (x == 0 || x == MAX.x()+1 || y == 0 || y == MAX.y()+1) {
                    coordinatesFixedBlocks.add(new Coordinate(x, y));
                }
            }
        }
    }

    /**
     * Generate NUM_RND_BLOCKS random blocks in the range 1 .. XMAX-1, 1 .. YMAX-1;
     * the corresponding coordinates are taken from the free positions list and put in the coordinatesRandomBlocks list.
     */
    private void generateRandomBlocks() {
        for (int i=0; i<NUM_RND_BLOCKS; i++) {
            Coordinate rndBlock = freePositions.remove(random.nextInt(freePositions.size()-1));
            coordinatesRandomBlocks.add(rndBlock);
        }
    }

    private void generateItemsAndExit() {
        // the items are put behind the random blocks; we can use this array also
        // to prevent the items to stack over the same position
        ArrayList<Coordinate> availableCoordinates = new ArrayList<>(coordinatesRandomBlocks);

        int randomExit = random.nextInt(coordinatesRandomBlocks.size());
        exit = coordinatesRandomBlocks.get(randomExit);
        availableCoordinates.remove(exit);

        int randomFire = random.nextInt(availableCoordinates.size());
        bombPu = availableCoordinates.get(randomFire);
        availableCoordinates.remove(bombPu);

        int randomLife = random.nextInt(availableCoordinates.size());
        lifePu = availableCoordinates.get(randomLife);
        availableCoordinates.remove(lifePu);

        int randomInvincible = random.nextInt(availableCoordinates.size());
        invinciblePu = availableCoordinates.get(randomInvincible);
        availableCoordinates.remove(invinciblePu);

        // generate the coins
        for (int i = 0; i < NUMBER_OF_COINS; i++) {
            int randomCoin = random.nextInt(availableCoordinates.size());
            Coordinate coin = availableCoordinates.get(randomCoin);
            availableCoordinates.remove(coin);
            coins.add(coin);
        }
    }

    public void generateEnemies() {
        for (int i=0; i<numberOfEnemies; i++) {
            var enemy = freePositions.remove(random.nextInt(freePositions.size()-1));
            coordinateEnemies.add(enemy);
            // the number of life of an enemy depends upon the level
            enemiesHp.add(level);
        }
        System.out.println(enemiesHp);
    }



//######################################  TNT  ######################################//

    boolean isBombExploding = false;

    public boolean releaseBomb() {
        if (tntCoordinates != null || Objects.equals(playerPosition, new Coordinate(1, 1))){
            return false;
        }

        tntCoordinates = playerPosition;

        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.UPDATE_BOMB_RELEASED, tntCoordinates));
        return true;
    }

    public void explodeBomb() {
        Set<Coordinate> blocksToRemove = new HashSet<>();
        Set<Coordinate> enemiesToRemove = new HashSet<>();
        Set<Coordinate> enemiesHpToRemove = new HashSet<>();

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

            if (coordinateEnemies.contains(coord)) {
                int enemyIndex = coordinateEnemies.indexOf(coord);

                    enemiesHp.set(enemyIndex, enemiesHp.get(enemyIndex)-1);
                    if (enemiesHp.get(enemyIndex) == 0) {
                        enemiesToRemove.add(coord);
                        enemiesHp.remove(enemyIndex);
                    } else
                        enemiesHpToRemove.add(coord);
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


        enemiesHpToRemove.forEach(coordinate -> {
            int index = coordinateEnemies.indexOf(coordinate);
            notifyLessLifeEnemy(index);
            System.out.println(enemiesHp);
        });

        notifyExplosion(adjacentCoordinates);
        tntCoordinates = null;
        isBombExploding = false;

        openTheDoor();
    }

    private void openTheDoor() {
        if (!doorOpen && coordinateEnemies.isEmpty()){
            notifyOpenedDoor();
            doorOpen = true;
        }
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

        int newX = clamp(currentPosition.x() + deltaX, MIN.x(), MAX.x());
        int newY = clamp(currentPosition.y() + deltaY, MIN.y(), MAX.y());

        return new Coordinate(newX, newY);
    }
    /**
     * A coordinate is valid if it is not already occupied by a fixed block or by another random block. Furthermore we
     * should leave cleared the positions that are adiacent to the origin, where the player is initially positioned.
     * @param c: the coordinate to evaluate
     * @return if it is a valid location
     */
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
        notifyObservers(new UpdateInfo(UpdateType.LEVEL, level));
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.LOAD_MAP, SubMap.GROUND_BLOCKS, coordinateGround));
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.LOAD_MAP, SubMap.STATIC_BLOCKS, coordinatesFixedBlocks));
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
        notifyObservers(new UpdateInfo(UpdateType.LOAD_MAP, SubMap.RANDOM_BLOCKS, coordinatesRandomBlocks));
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.LOAD_PLAYER, playerPosition));
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.LOAD_ENEMIES, coordinateEnemies));
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.LOAD_NAME, nickname));
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

    private void notifyLessLifeEnemy(int index) {
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.UPDATE_ENEMY_LIFE, index));
    }

    private void notifyOpenedDoor() {
        setChanged();
        notifyObservers(new UpdateInfo(UpdateType.UPDATE_DOOR));
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
        enemiesHp.clear();
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
        doorOpen = false;
        deleteObservers();
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel(){
        return level;
    }

    public void setShownNickname(String nickname) {
        this.nickname = nickname;
    }
    //###############################################//
}
package ChaosAI.knowledgebase.locationbased;

import ChaosAI.knowledgebase.KnowledgeBaseConfig;
import ChaosAI.utils.FieldTypes;
import ChaosAI.utils.GameState;
import ChaosAI.utils.Position;
import core.game.Observation;
import ontology.Types;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by user_ms on 18.05.16.
 */
public class GameField {

    // game field vars
    private final int BLOCK_SIZE;
    private final int HEIGHT;
    private final int WIDTH;
    private final int BLOCK_COUNT;
    private GameBlock[][] gameField;
    private ArrayList<Types.ACTIONS> availableActions;

    // -------------------------------------------------------------------------------------------------------
    // *** GameField ***
    // -------------------------------------------------------------------------------------------------------
    public GameField(GameState gameState) {
        // Initialize gameField size
        this.BLOCK_SIZE = gameState.getBlockSize();
        this.HEIGHT = gameState.getWorldDimension().height / BLOCK_SIZE;
        this.WIDTH = gameState.getWorldDimension().width / BLOCK_SIZE;
        this.BLOCK_COUNT = HEIGHT * WIDTH;
        this.gameField = new GameBlock[WIDTH][HEIGHT];
        for(int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                gameField[x][y] = new GameBlock();
            }
        }

        this.availableActions = gameState.getAvailableActions();

        // mark all fields
        //readInWholeStateObservation(gameState);
    }

    // -------------------------------------------------------------------------------------------------------
    // *** Public getters ***
    // -------------------------------------------------------------------------------------------------------
    public int getBlockSize() {
        return BLOCK_SIZE;
    }
    public int getHeight() {
        return HEIGHT;
    }
    public int getWidth() {
        return WIDTH;
    }
    public int getBlockCount() {
        return BLOCK_COUNT;
    }
    public GameBlock getGameBlockAt(Position position) {
        return gameField[position.x][position.y];
    }
    public ArrayList<Position> getPositionsOfSpriteIDs(HashSet<Integer> spriteIDs) {
        ArrayList<Position> positions = new ArrayList<>(KnowledgeBaseConfig.ARRAYLIST_CAPACITY);
        for(int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                for(int sprite : spriteIDs) {
                    if(gameField[x][y].getSpriteIds().contains(sprite))
                        positions.add(new Position(x, y));
                }
            }
        }
        return positions;
    }

    // -------------------------------------------------------------------------------------------------------
    // *** readInWholeStateObservation ***
    // -------------------------------------------------------------------------------------------------------
    private void readInWholeStateObservation(GameState gameState) {
        ArrayList<Observation>[][] observationGrid = gameState.getObservationGrid();
        for(int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                gameField[x][y].setObservations(observationGrid[x][y]);
            }
        }
    }

    // -------------------------------------------------------------------------------------------------------
    // *** Update state ***
    // -------------------------------------------------------------------------------------------------------
    public void update(GameState gameState) {
        // TODO maybe more efficiency needed
        //readInWholeStateObservation(gameState);
    }

    // -------------------------------------------------------------------------------------------------------
    // *** positionIsWithinGameField ***
    // -------------------------------------------------------------------------------------------------------
    public boolean positionIsWithinGameField(Position pos) {
        return !(pos.x < 0 || pos.x >= WIDTH || pos.y < 0 || pos.y >= HEIGHT);
        //return (pos.x >= 0 && pos.y >= 0 && pos.x < WIDTH && pos.y < HEIGHT);
    }

    // -------------------------------------------------------------------------------------------------------
    // *** getAvailableActions ***
    // -------------------------------------------------------------------------------------------------------
    public ArrayList<Types.ACTIONS> getAvailableActions() {
        return availableActions;
    }

    public void addTypeObserved(int type, int x, int y) {
        this.gameField[x][y].addTypeLearned(type);
    }

    public void clearObserved(int x, int y) {
        this.gameField[x][y].clearLearned();
    }

    // -------------------------------------------------------------------------------------------------------
    // *** getObservationsFromSpriteIdAndFieldType ***
    // -------------------------------------------------------------------------------------------------------
    public ArrayList<Observation> getObservationsFromSpriteIDsAndFieldType(HashSet<Integer> spriteId, int fieldType) {
        ArrayList<Observation> result = new ArrayList<>(100);
        for(int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                for (Observation observation : gameField[x][y].getObservations()) {
                    if(spriteId.contains(observation.itype) && observation.category == fieldType) {
                        result.add(observation);
                        gameField[x][y].addTypeLearned(FieldTypes.TYPE_ENEMY);
                    }
                }
            }
        }
        return result;
    }

    // -------------------------------------------------------------------------------------------------------
    // *** prettyPrint ***
    // -------------------------------------------------------------------------------------------------------
    public void prettyPrint() {
        // needed?
    }

}

package ChaosAI.heuristics.stateEvaluation.base;

import java.awt.Dimension;
import java.util.Arrays;

import ChaosAI.heuristics.HeuristicBase;
import ChaosAI.utils.GameState;
import ChaosAI.utils.Position;
import tools.Vector2d;

/**
 * Created by Chris on 13.05.2016.
 */
public abstract class HeatMapBase extends HeuristicBase {
    //TODO for debugging purpose set to public before Upload, set to protected
    public final int BASIC_HEAT;
    public int maxKnownHeat;
    protected final int height;
    protected final int width;

    private final int DELAY = 30;

    private int[][] coolDelayMap;
    private int[][] heatMap;
    public HeatMapBase(GameState gameState, int basicHeat){
        BASIC_HEAT = basicHeat;
        maxKnownHeat = basicHeat;
        //TODO Maybe subject to change
        Dimension dim = gameState.getWorldDimension();
        int blockSize =  gameState.getBlockSize();
        height = dim.height/blockSize;
        width = dim.width/blockSize;
        heatMap = new int[width][height];
        coolDelayMap = new int[width][height];
        for(int[] a : heatMap)
            Arrays.fill(a, basicHeat);
    }

    protected int getHeatValue(Position position) {
        return heatMap[position.x][position.y];
    }

    /**
     * @Depricated
     * TODO Grab from Knowlage Base
     * @param gameState
     * @return
     */
    //@Deprecated
    protected Position getPlayerPosition(GameState gameState){
        //TODO Grab from KnowledgeBase
        Vector2d avPos = gameState.getAvatarPosition();
        int blockSize = gameState.getBlockSize();
        //TODO add usefull Information here
        Position p = new Position((int)(avPos.x/blockSize),(int)(avPos.y/blockSize));
        return p;
    }
    protected void heatPosition(Position position, int value){
        addToPosition(heatMap,position, value);
        setPosition(coolDelayMap,position,DELAY);
    }
    protected void coolField(int value){

        for(int x = 0; x < heatMap.length; x++)
            for(int y = 0; y < heatMap[0].length; y++) {
                Position p = new Position(x,y);
                if(coolDelayMap[x][y] > 0)
                    coolDelayMap[x][y]--;
                else
                    addToPosition(heatMap,p,value*-1);
            }
        //changeField(value*-1);
    }
    private void setPosition(int[][]field, Position position, int value){
        int newHeat = value;
        if(newHeat < BASIC_HEAT)
            newHeat = BASIC_HEAT;
        else if(newHeat > maxKnownHeat)
            maxKnownHeat = newHeat;
        heatMap[position.x][position.y] = newHeat;
    }
    private void addToPosition(int[][]field, Position position, int value){
        int newHeat = heatMap[position.x][position.y] + value;
        if(newHeat < BASIC_HEAT)
            newHeat = BASIC_HEAT;
        else if(newHeat > maxKnownHeat)
            maxKnownHeat = newHeat;
        heatMap[position.x][position.y] = newHeat;
    }
    private void changeField(int value){
        for(int x = 0; x < heatMap.length; x++)
            for(int y = 0; y < heatMap[0].length; y++) {
                Position p = new Position(x,y);
                addToPosition(heatMap,p,value);
            }
    }
    /*****************************************************************************/
    //evaluate(GameState gameState)
    //updateFields(GameState gameState)
    /*****************************************************************************/
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(int[] x : heatMap){
            for (int y : x){
                sb.append(" ").append(y);
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    public int[][] getField(Class<? extends HeatMapBase> reqClass){
        return this.heatMap;
    }
    public boolean insideField(Position position){
        return !(position.x < 0 || position.x >= width || position.y < 0 || position.y >= height);
    }
}

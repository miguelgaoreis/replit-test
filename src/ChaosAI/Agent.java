package ChaosAI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashSet;

import ChaosAI.algorithm.BaseAlgorithm;
import ChaosAI.algorithm.aStar_old.AStar;
import ChaosAI.algorithm.bfs.BestFirstSearch;
import ChaosAI.algorithm.mcts.rewrite.CarloTree;
import ChaosAI.heuristics.HeuristicBase;
import ChaosAI.heuristics.HeuristicalEvaluater;
import ChaosAI.heuristics.stateEvaluation.MoveHistoryHeatMap;
import ChaosAI.heuristics.stateEvaluation.base.HeatMapBase;
import ChaosAI.knowledgebase.KnowledgeBase;
import ChaosAI.utils.FieldTypes;
import ChaosAI.utils.GameState;
import ChaosAI.utils.Position;
import ChaosAI.utils.StochasticException;
import ChaosAI.utils.Timer;
import ChaosAI.utils.Util;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.ErrorGeneration;
import javax.swing.*;

/**
 * Created by chris on 20.04.16.
 */
public class Agent extends AbstractPlayer {

    public static boolean DEBUG = false;
    //public static JFrame frame = new JFrame();

    //needs always to be false here!
    public static boolean LEARN = false;
    public static KnowledgeBase knowledgeBase;
    public static Timer timer;

    private Class toDrawFrom = MoveHistoryHeatMap.class;
    private int blockSize = 0;
    private int height = 0;
    private int width = 0;
    //For Tests all set to public
    public BaseAlgorithm algorithm;
    public HeuristicBase heuristic;

    //For garbage collector to activate ever X acts.
    private int GC_COUNTER = 30;
    //Counter of acts for garbage collector call.
    private int counter  = 0;

    private ErrorGeneration errorGenerator;

    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer, String error, String replayer){
        GameState gameState = new GameState(stateObs);
        this.errorGenerator = new ErrorGeneration(error ,stateObs.getAvailableActions(true),replayer);
        this.timer = new Timer(elapsedTimer);
        knowledgeBase = new KnowledgeBase(gameState);
        heuristic = new HeuristicalEvaluater(gameState);


        LEARN = true;
        if(knowledgeBase.getIsDeterministic()) {
            //algorithm = new Beam(gameState, heuristic);
            //System.out.println("Using BeamSearch");
            algorithm = new AStar(gameState, heuristic);
            System.out.println("Using AStar");
        } else {
            algorithm = new CarloTree(gameState, heuristic);
            System.out.println("Using MCTS");
        }
        try {
          algorithm.updateFields(gameState);
        } catch (StochasticException se) {
            System.out.println("Switching to MCTS");
            algorithm = new CarloTree(gameState, heuristic);
            algorithm.updateFields(gameState);
        }

        algorithm.compute();
        /*******DEBUGGING*CALLS***************/
        algorithm.resetSteps();
        if(DEBUG) {
            blockSize = gameState.getBlockSize();
            Dimension d = gameState.getWorldDimension();
            height = d.height/blockSize;
            width = d.width/blockSize;
        }
    }
    private int kbLearnedCheckInterval = 1;
    private int kbLearnedCheckCounter = 0;
    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        //GarbageCollector counting
        if(counter == GC_COUNTER) {
            System.gc();
            System.out.println("GC");
        }
        counter = (counter +1)%GC_COUNTER;
        //KnowlegeBase on/off switch
        boolean learnedSomethingLastRound = knowledgeBase.getLearnedSomethingNewSinceLastCall();
        if(learnedSomethingLastRound) {
            LEARN = true;
            kbLearnedCheckCounter = 0;
            kbLearnedCheckInterval = 1;
        }else if(kbLearnedCheckCounter == kbLearnedCheckInterval){
            LEARN = true;
            System.out.println("Learn On after: " + kbLearnedCheckInterval);
            kbLearnedCheckInterval *= 2;
            kbLearnedCheckCounter = 0;
        }else
            kbLearnedCheckCounter++;

        timer.updateTimer(elapsedTimer);
        GameState gameState = new GameState(stateObs);

        knowledgeBase.updateRealState(gameState);

        try {
          algorithm.updateFields(gameState);
        } catch (StochasticException se) {
            System.out.println("Switching to MCTS");
            algorithm = new CarloTree(gameState, heuristic);
          algorithm.updateFields(gameState);
        }
        algorithm.compute();
        Types.ACTIONS action = algorithm.getNextMove();
        LEARN = false;

        action = this.errorGenerator.generate_error(action);

        return action;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void draw(Graphics2D g){
        if(!DEBUG)
            return;
        HeatMapBase h = null;
        try {
            h = (HeatMapBase) heuristic.getHeuristicByClass(toDrawFrom);
        }catch (Exception e) {
            return;
        }
        int[][] heatField = h.getField(toDrawFrom);
        int minHeat = h.BASIC_HEAT;
        int maxHeat = h.maxKnownHeat;
        drawPath(g);

        for(int y = 0; y < height; y++)
            for(int x = 0; x < width; x++) {
                drawKBField(g, new Position(x, y));
                drawHeatMap(g,heatField,x,y,minHeat,maxHeat);
            }
    }
    private void drawHeatMap(Graphics2D g, int[][] heatField,int x, int y,int minHeat, int maxHeat){
        int fValue = heatField[x][y];
        if(fValue == 0)
            return;
        int val = (int)(Util.normalise(fValue,minHeat,maxHeat)*255);
        val = 255 - val;
        g.setColor(new Color(255,val,0,fValue == minHeat ? 0 : 100));
        int xPos = x * blockSize;
        int yPos = y * blockSize;
        g.fillRect(xPos,yPos,blockSize,blockSize);
    }
    private void drawKBField(Graphics2D g, Position position){
        ArrayList<Integer> fieldTypes = knowledgeBase.getGameBlockAt(position).getTypesCombined();

        if(fieldTypes.size() == 0)
            return;

        StringBuilder sb = new StringBuilder();

        for(int fValue : knowledgeBase.getGameBlockAt(position).getTypesCombined()) {
            switch(fValue) {
                // BEGIN framework defined types
                case FieldTypes.TYPE_AVATAR:
                    sb.append("A");
                    break;
                case FieldTypes.TYPE_RESOURCE:
                    sb.append("R");
                    break;
                case FieldTypes.TYPE_PORTAL:
                    sb.append("P");
                    break;
                case FieldTypes.TYPE_NPC:
                    sb.append("N");
                    break;
                case FieldTypes.TYPE_STATIC:
                    sb.append("S");
                    break;
                case FieldTypes.TYPE_FROMAVATAR:
                    sb.append("F");
                    break;
                case FieldTypes.TYPE_MOVABLE:
                    sb.append("M");
                    break;
                // BEGIN self defined types
                case FieldTypes.TYPE_WALL:
                    sb.append("W");
                    break;
                default:
                    System.out.println("Should not happen! :D");
                    sb.append("#");
                    break;
            }
        }
        int xPos = position.x * blockSize;
        int yPos = position.y * blockSize;

        // draw red strings
        g.setColor(Color.RED);
        xPos = xPos + blockSize / 2;
        yPos = yPos + blockSize / 2;
        g.drawString(sb.toString(), xPos, yPos);
    }

    private void drawPath(Graphics2D g) {
        g.setColor(new Color(0, 113, 255, 135));

        if(Agent.knowledgeBase.currentPath != null) {
            for(Position p: Agent.knowledgeBase.currentPath) {
                g.fillRect(p.x*blockSize, p.y*blockSize, blockSize, blockSize);
            }
        }
    }
}

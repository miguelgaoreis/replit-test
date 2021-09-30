package ChaosAI.algorithm;

import ChaosAI.Agent;
import ChaosAI.heuristics.HeuristicBase;
import ChaosAI.utils.GameState;
import ChaosAI.utils.OutOfTimeException;
import ontology.Types;

import java.util.ArrayList;

/**
 * Created by Chris on 10.05.2016.
 */
public abstract class BaseAlgorithm {

    private double totalSteps = 0;
    private int acts = 0;

    protected final ArrayList<Types.ACTIONS> knownMovesWithNil;
    protected final ArrayList<Types.ACTIONS> knownMoves;

    protected GameState currentState;
    protected HeuristicBase evaluationHeuristic;

    public BaseAlgorithm(GameState gameState, HeuristicBase heuristic){
        this.currentState = gameState;
        this.evaluationHeuristic = heuristic;

        //TODO pull from KnowledgeBase when implemented
        knownMoves = gameState.getAvailableActions();
        knownMovesWithNil = gameState.getAvailableActions(true);
    }
    public void resetSteps() {
        totalSteps = 0;
        acts = 0;
    }

    public void updateFields(GameState gameState){
        this.currentState = gameState;
        evaluationHeuristic.updateFields(gameState);
    }
    public void compute(){
        int counter = 0;
        if(currentState.isGameOver())
            return;
        try{
            while(true) {
                Agent.timer.checkComputeTimer();
                long timeBefore = Agent.timer.elapsedMillis();
                computeOnce();
                long duration = Agent.timer.elapsedMillis() - timeBefore;

                Agent.timer.updateOnCompute(duration);
                counter++;
            }
        }
        catch (OutOfTimeException oot){
            //Intended behaviour
        }
        totalSteps += counter;
        acts++;
        double avgSteps = totalSteps/acts;
        if(Agent.DEBUG) {
            System.out.println("ADV: " + GameState.advancesDone);
            GameState.advancesDone = 0;
            //System.out.println("Steps now: " + counter + "\t| " + "AvgSteps: " + avgSteps);
        }
    }
    public abstract void computeOnce();
    public abstract Types.ACTIONS getNextMove();
}

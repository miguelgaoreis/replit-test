package ChaosAI.algorithm.mcts.roullout;

import ChaosAI.heuristics.HeuristicBase;
import ChaosAI.utils.GameState;
import ontology.Types;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Chris on 15.05.2016.
 */
public class FullyRandomRollout extends RolloutStrategy {
    private ArrayList<Types.ACTIONS> possibleMoves = null;
    private Random rng;
    public FullyRandomRollout(GameState gameState, HeuristicBase heuristic){
        super(7,heuristic);
        possibleMoves = gameState.getAvailableActions();
        rng = new Random();
    }
    @Override
    protected Types.ACTIONS getNextMove(GameState gameState) {
        Types.ACTIONS action = possibleMoves.get(rng.nextInt(possibleMoves.size()));
        return action;
    }
}

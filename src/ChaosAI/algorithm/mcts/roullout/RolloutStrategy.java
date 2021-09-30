package ChaosAI.algorithm.mcts.roullout;

import ChaosAI.heuristics.HeuristicBase;
import ChaosAI.utils.GameState;
import ontology.Types;

/**
 * Created by Chris on 15.05.2016.
 */
public abstract class RolloutStrategy {

    protected int ROLLOUT_DEPTH = 7;
    private HeuristicBase heuristic;

    public RolloutStrategy(int depth, HeuristicBase heuristic){
        this.ROLLOUT_DEPTH = depth;
        this.heuristic = heuristic;
    }
    public double executeRollout(GameState gameState){
        int advancesDone = 0;
        double rolloutValue = 0;

        for(int i = 0; i < ROLLOUT_DEPTH; i++){
            Types.ACTIONS action = getNextMove(gameState.copy());
            gameState.advance(action);
            rolloutValue += heuristic.evaluate(gameState);
            advancesDone++;
            if(gameState.isGameOver())
                break;
        }
        double avgStateValue = rolloutValue / advancesDone;
        return avgStateValue;
    }

    protected abstract Types.ACTIONS getNextMove(GameState gameState);
}

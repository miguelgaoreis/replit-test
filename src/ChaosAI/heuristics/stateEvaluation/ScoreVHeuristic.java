package ChaosAI.heuristics.stateEvaluation;

import ChaosAI.heuristics.HeuristicBase;
import ChaosAI.utils.GameState;
import ChaosAI.utils.Util;

/**
 * Created by Chris on 12.05.2016.
 */
public class ScoreVHeuristic extends HeuristicBase {
    private double MIN_BOUND = 0;
    private double MAX_BOUND = 0;

    @Override
    public double evaluate(GameState gameState) {
        double score = gameState.getGameScore();
        if(score > MAX_BOUND)
            MAX_BOUND = score;
        if(score < MAX_BOUND)
            MIN_BOUND = score;
        return Util.normalise(score,MIN_BOUND-1,MAX_BOUND+1);
    }
}

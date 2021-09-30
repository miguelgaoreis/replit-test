package ChaosAI.heuristics.stateEvaluation;

import ChaosAI.heuristics.HeuristicBase;
import ChaosAI.utils.GameState;
import ontology.Types;

/**
 * Created by Chris on 12.05.2016.
 */
public class GameOverHeuristic extends HeuristicBase {
    @Override
    public double evaluate(GameState pStateObservation) {
        if(!pStateObservation.isGameOver())
            return 0.5;
        if(pStateObservation.getGameWinner()== Types.WINNER.PLAYER_WINS)
            return 1;
        return 0;
    }
}

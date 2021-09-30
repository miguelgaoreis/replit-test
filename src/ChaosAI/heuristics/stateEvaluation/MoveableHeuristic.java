package ChaosAI.heuristics.stateEvaluation;

import java.util.ArrayList;

import ChaosAI.heuristics.HeuristicBase;
import ChaosAI.utils.GameState;
import core.game.Observation;

/**
 * Created by Adrian on 10.05.2016.
 */
public class MoveableHeuristic extends HeuristicBase {

	protected static int maxMovables = 0;
	
    @Override
    public double evaluate(GameState pStateObservation) {
        double score = 1;
        ArrayList<Observation>[] moveables = pStateObservation.getMovablePositions();

        if (moveables != null) {
            int moveabeleCounter = moveables.length;

            if (moveabeleCounter > maxMovables) {
                maxMovables = moveabeleCounter;
            }
            if (maxMovables > 0) {
                score *= (1 - (moveabeleCounter / maxMovables));
            }
        }
        return score;
    }
}

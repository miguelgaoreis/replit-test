package ChaosAI.heuristics.evaluation;

import core.game.StateObservation;
import ontology.Types;

/**
 * Created by Chris on 29.04.2016.
 */
public class SimpleRating extends StateEvaluationHeuristic {

    @Override
    public double evaluate(StateObservation so) {
        double score = so.getGameScore();
        if(so.isGameOver())
            if(so.getGameWinner()== Types.WINNER.PLAYER_WINS)
                score+= 100;
            else if(so.getGameWinner() == Types.WINNER.PLAYER_LOSES)
                score = -100;
        return score;
    }
}

package ChaosAI.algorithm.aStar;

import ChaosAI.utils.CacheableGameState;
import ChaosAI.utils.CacheableNode;
import ChaosAI.utils.GameState;
import ontology.Types;

/**
 * Created by Blindguard on 17.05.2016.
 */
public class AStarHeuristic {
    public static double evaluate(GameState last, GameState next) {
        return AStarHeuristic.evaluate(next);
    }

    public static double evaluate(GameState next) {
        double score = 0;
        score += next.getGameScore();

        if (next.isGameOver() && next.getGameWinner() == Types.WINNER.PLAYER_WINS)
            score += 1000;

        if (next.isGameOver() && next.getGameWinner() == Types.WINNER.PLAYER_LOSES)
            score += -1000;

        return score;
    }
}

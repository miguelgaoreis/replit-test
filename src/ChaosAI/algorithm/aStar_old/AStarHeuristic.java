package ChaosAI.algorithm.aStar_old;

import ChaosAI.utils.CacheableGameState;
import ChaosAI.utils.CacheableNode;
import ontology.Types;

/**
 * Created by Blindguard on 17.05.2016.
 */
public class AStarHeuristic {
    public static double evaluate(CacheableNode n, CacheableGameState last, CacheableGameState next) {
        return AStarHeuristic.evaluate(n, next);
    }

    public static double evaluate(CacheableNode n, CacheableGameState next) {
        double score = 0;
        score += next.gameScore;

        if (next.isGameOver && next.gameWinner == Types.WINNER.PLAYER_WINS)
            score += 1000;

        if (next.isGameOver && next.gameWinner == Types.WINNER.PLAYER_LOSES)
            score += -1000;

        return score;
    }
}

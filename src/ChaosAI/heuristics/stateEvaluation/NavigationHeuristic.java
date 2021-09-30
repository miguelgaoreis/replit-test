package ChaosAI.heuristics.stateEvaluation;

import ChaosAI.Agent;
import ChaosAI.heuristics.HeuristicBase;
import ChaosAI.utils.GameState;
import ChaosAI.utils.Position;

/**
 * Created by blindguard on 09/06/16.
 */
public class NavigationHeuristic extends HeuristicBase {
    public double evaluate(GameState gameState) {
        double score = 0.000001;

        if(Agent.knowledgeBase.currentPath != null) {
            Position avatarPos = new Position(gameState.getAvatarPosition(), gameState.getBlockSize());
            int pathSize = Agent.knowledgeBase.currentPath.size();
            int index = Agent.knowledgeBase.currentPath.indexOf(avatarPos);

            if(index != -1) {
                index = pathSize - index + 1;
                index = index - (index % 5);
                if(index != 0)
                    score = (double) index / (double) pathSize;
            }
        }

        return score;
    }
}

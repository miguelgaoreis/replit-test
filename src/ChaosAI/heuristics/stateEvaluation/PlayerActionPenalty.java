
package ChaosAI.heuristics.stateEvaluation;

import ChaosAI.Agent;
import ChaosAI.heuristics.HeuristicBase;
import ChaosAI.knowledgebase.KnowledgeBase;
import ChaosAI.utils.GameState;
import ChaosAI.utils.Position;
import tools.Vector2d;

/**
 * Created by Adrian on 25.05.2016.
 */

public class PlayerActionPenalty extends HeuristicBase {
	@Override
	public double evaluate(GameState pGameState) {
		Vector2d position = getPlayerPosition(pGameState);
		Position avatarPosition = new Position((int)position.x, (int)position.y);
		int amountAvailableActions = Agent.knowledgeBase.getActionsNotBlockedByWall(avatarPosition).size();
		int amountAllActions = pGameState.getAvailableActions().size();

		return amountAllActions != 0 ? amountAvailableActions / amountAllActions : 1;
	}

	private Vector2d getPlayerPosition(GameState pGameState) {
		Vector2d avatarPosition = pGameState.getAvatarPosition();
		int blockSize = pGameState.getBlockSize();
		Vector2d v = new Vector2d();
		v.x = avatarPosition.x / blockSize;
		v.y = avatarPosition.y / blockSize;
		return v;
	}
}

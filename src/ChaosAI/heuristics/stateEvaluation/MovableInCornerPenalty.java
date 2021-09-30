package ChaosAI.heuristics.stateEvaluation;

import ChaosAI.Agent;
import ChaosAI.heuristics.HeuristicBase;
import ChaosAI.utils.GameState;
import ChaosAI.utils.Position;
import core.game.Observation;
import ontology.Types.ACTIONS;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Adrian on 15.05.2016.
 */
public class MovableInCornerPenalty extends HeuristicBase {
	private static ArrayList<ACTIONS> directions = new ArrayList<>(Arrays.asList(ACTIONS.ACTION_DOWN,ACTIONS.ACTION_LEFT,ACTIONS.ACTION_RIGHT,ACTIONS.ACTION_UP));

	@Override
	public double evaluate(GameState gameState) {
		ArrayList<Observation>[] movables = gameState.getMovablePositions();
		double score = 0.00000000000000000001;
		int counter = 0;
		if (movables != null) {
			for (ArrayList<Observation> movable : movables) {
				counter += movable.size();
				for (Observation observation : movable) {
					//Maybe reduce iterationload
					Position currentPosition = new Position(observation.position, gameState.getBlockSize());
					int amountWalls = Agent.knowledgeBase.getActionsNotBlockedByWall(currentPosition,directions).size();
					if (amountWalls >= 3)
						score += 0;
					else if (amountWalls == 2)
						score += 0.1;
					else if (amountWalls == 1)
						score += 0.25;
					else
						score += 1;
				}
			}
			if (counter > 0) {
				return score / counter;
			}
		}
		return 1;
	}
}
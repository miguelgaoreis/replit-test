package ChaosAI.heuristics.stateEvaluation;

import java.util.ArrayList;

import ChaosAI.Agent;
import ChaosAI.heuristics.HeuristicBase;
import ChaosAI.utils.GameState;

/**
 * Created by Adrian on 10.05.2016.
 */
public class DestroyableHeuristic extends HeuristicBase {

	protected static int maxObjects = 0;

	@Override
	public double evaluate(GameState pStateObservation) {

		ArrayList<Object> objects = Agent.knowledgeBase.getDestroyableObjects();
		int amountObjects = 0;

		if (objects != null && !objects.isEmpty()) {
			amountObjects = objects.size();
			if (amountObjects > maxObjects) {
				maxObjects = amountObjects;
			}
			if (maxObjects > 0) {
				return 1 - (amountObjects / maxObjects);
			}
		}
		return 1;
	}
}

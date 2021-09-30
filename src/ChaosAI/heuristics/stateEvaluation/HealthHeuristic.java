package ChaosAI.heuristics.stateEvaluation;

import ChaosAI.Agent;
import ChaosAI.heuristics.HeuristicBase;
import ChaosAI.utils.GameState;

/**
 * Created by Adrian on 10.05.2016.
 */
public class HealthHeuristic extends HeuristicBase {

	@Override
	public double evaluate(GameState pStateObservation) {
		double score = 1;
		if (Agent.knowledgeBase.getStartingHealthPoints() == 0) {
			return score;
		}
		double healthPoints = pStateObservation.getAvatarHealthPoints();
		double limitedHealthPoints = pStateObservation.getAvatarLimitHealthPoints();
		double maxHealthPoints = pStateObservation.getAvatarMaxHealthPoints();

		if (maxHealthPoints != 0) {
			if (healthPoints != 0 && maxHealthPoints > 0) {
				score *= (healthPoints / maxHealthPoints);
			}
			if (limitedHealthPoints != 0 && limitedHealthPoints > 0) {
				score *= (maxHealthPoints / limitedHealthPoints);
			}
		}
		return score;
	}
}

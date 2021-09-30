package ChaosAI.heuristics.stateEvaluation;

import ChaosAI.heuristics.HeuristicBase;
import ChaosAI.utils.GameState;
import ontology.Types.WINNER;

/**
 * Created by Adrian on 10.05.2016.
 */
public class ScoreHeuristic extends HeuristicBase {

	protected static double maxScore = 0;
	protected static double minTicks = 0;

	@Override
	public double evaluate(GameState pStateObservation) {
		double score = 1;
		double gameScore = pStateObservation.getGameScore();
		double gameTicks = pStateObservation.getGameTick();

		if (gameScore > maxScore) {
			maxScore = gameScore;
		}

		if (pStateObservation.getGameWinner() == WINNER.PLAYER_LOSES) {
			score *= 0.1;

			if (gameTicks < minTicks) {
				minTicks = gameTicks;
			}
		}
		if (maxScore > 0) {
			score *= (gameScore / maxScore);
		}
		if (gameTicks > 0) {
			score = (float) ((score * 1.9f) + ((minTicks / gameTicks) * 0.1f) / 2f);
		}
		if (pStateObservation.getGameWinner() == WINNER.PLAYER_WINS) {
			score *= 2;
			if (score > 1) {
				score = 1;
			}
		}

		return score;
	}

}

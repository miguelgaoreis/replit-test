package ChaosAI.heuristics.stateEvaluation;

import java.util.ArrayList;

import ChaosAI.heuristics.HeuristicBase;
import ChaosAI.utils.GameState;
import core.game.Observation;

/**
 * Created by Adrian on 10.05.2016.
 */
public class NpcHeuristic extends HeuristicBase {

	protected static int maxNPCs = 0;
	
	@Override
	public double evaluate(GameState pGameState) {
		ArrayList<Observation>[] npcs = pGameState.getNPCPositions();
		if (npcs != null) {
			int npcCounter = npcs.length;

			if (npcCounter > maxNPCs) {
				maxNPCs = npcCounter;
			}
			if (maxNPCs > 0) {
				return 1 - (npcCounter / maxNPCs);
			}
		}
		return 1;
	}
}

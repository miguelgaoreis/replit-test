package ChaosAI.heuristics;

import ChaosAI.heuristics.stateEvaluation.*;
import ChaosAI.utils.GameState;
import ontology.Types;

import java.util.ArrayList;

/**
 * Created by Chris on 12.05.2016.
 */
public class HeuristicalEvaluater extends HeuristicBase {

	private ArrayList<Entry> heuristics = new ArrayList<>();
	
	public HeuristicalEvaluater(GameState gameState) {
		heuristics.add(new Entry(new ScoreVHeuristic(), 1));
		heuristics.add(new Entry(new GameOverHeuristic(), 0.8));
		heuristics.add(new Entry(new MoveHistoryHeatMap(gameState), 0.1));
		heuristics.add(new Entry(new NavigationHeuristic(), 0.8));
		//heuristics.add(new Entry(new ClosestNPCHeuristic(gameState), 0.3));
		//heuristics.add(new Entry(new PheromonHeuristic(gameState), 0.2));
		//heuristics.add(new Entry(new MovableInCornerPenalty(), 0.8));
		//heuristics.add(new Entry(new DestroyableHeuristic(), 0.3));
		//heuristics.add(new Entry(new MoveableHeuristic(), 0.1));
		//heuristics.add(new Entry(new EnemyDistanceHeuristic(), 0.2));
		//heuristics.add(new Entry(new GoalDistanceHeuristic(), 0.1));
		//heuristics.add(new Entry(new HealthHeuristic(), 0.25));
		//heuristics.add(new Entry(new NpcHeuristic(), 0.25));
		//FIXME (divide by zero) heuristics.add(new Entry(new PlayerActionPenalty(), 0.33));

	}

	@Override
	public void updateFields(GameState so) {
		for (Entry entry : heuristics)
			entry.heuristic.updateFields(so);
	}

	@Override
	public double evaluate(GameState pStateObservation) {
		if (pStateObservation.isGameOver() && pStateObservation.getGameWinner() == Types.WINNER.PLAYER_LOSES) {
			return 0;
		}
		double heuristicWeight = 0;
		double heuristicSum = 0;

		for(int i = 0; i < heuristics.size(); i++){
			Entry entry = heuristics.get(i);
			heuristicWeight += entry.weight;
			if(entry.weight == 0)
				continue;
			double heuristicValue = entry.heuristic.evaluate(pStateObservation) * entry.weight;
			if (heuristicValue > 1 || heuristicValue <= 0)
				throw new IllegalStateException("Evaluation not between 0(excluding 0) and 1. Crashing Heuristic: "+entry.heuristic.getClass().toString() + " Returned: " + heuristicValue);
			heuristicSum += heuristicValue;
		}
		if(heuristicWeight == 0) {
			throw new IllegalStateException("No heuristics included or all wights are 0!");
		}
		return heuristicSum / heuristicWeight;
	}

	@Override
	@SuppressWarnings("unchecked")
	public HeuristicBase getHeuristicByClass(Class reqClass) {
		for (Entry entry : heuristics)
			if (entry.heuristic.getClass() == reqClass)
				return entry.heuristic;
		return super.getHeuristicByClass(reqClass);
	}

	private class Entry {
		private final HeuristicBase heuristic;
		private double weight;

		private Entry(HeuristicBase h, double value) {
			this.heuristic = h;
			this.weight = value;
		}

	}

}

package ChaosAI.heuristics.stateEvaluation;

import java.util.ArrayList;

import ChaosAI.Agent;
import ChaosAI.heuristics.HeuristicBase;
import ChaosAI.utils.GameState;
import ChaosAI.utils.Position;
import tools.Vector2d;

/**
 * Created by Adrian on 11.05.2016.
 */
public class ScoreDistanceHeuristic extends HeuristicBase {

	protected static Double minDistance = null;

	@Override
	public double evaluate(GameState pGameState) {
		Position playerPos = getPlayerPosition(pGameState);
		ArrayList<Position> goalPositions = null;//Agent.knowledgeBase.getScoreIncreasingSpritePositions();
		if (goalPositions != null && !goalPositions.isEmpty()) {
			return calculateMinDistance2D(playerPos, goalPositions);
		}
		return 0;
	}

	private Position getPlayerPosition(GameState pGameState) {
		Vector2d avPos = pGameState.getAvatarPosition();
		int blockSize = pGameState.getBlockSize();
		return new Position((int) (avPos.x / blockSize), (int) (avPos.y / blockSize));
	}

	private double calculateMinDistance2D(Position pPosition, ArrayList<Position> pGoalPositions) {
		Double min = null;
		for (Position vector2d : pGoalPositions) {
			double distance = calculateDistance2D(pPosition, vector2d);
			if (min == null || distance < min) {
				min = distance;
			}
			if (minDistance == null || distance < minDistance) {
				minDistance = distance;
			}
		}
		if (min != null && min > 0) {
			return minDistance / min;
		}
		return 1;
	}

	private double calculateDistance2D(Position pPosition, Position pGoalPositions) {
		double diffX = Math.abs(pPosition.x - pGoalPositions.x);
		double diffY = Math.abs(pPosition.y - pGoalPositions.y);
		return diffX + diffY;
	}
}

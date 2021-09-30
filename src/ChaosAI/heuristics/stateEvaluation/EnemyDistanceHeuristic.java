package ChaosAI.heuristics.stateEvaluation;

import java.util.ArrayList;

import ChaosAI.Agent;
import ChaosAI.heuristics.HeuristicBase;
import ChaosAI.utils.GameState;
import ChaosAI.utils.Position;
import core.game.Observation;
import tools.Vector2d;

/**
 * Created by Adrian on 11.05.2016.
 */
public class EnemyDistanceHeuristic extends HeuristicBase {

	protected double maxSpeed = 0;

	@Override
	public double evaluate(GameState pGameState) {
		try {
			Position playerPosition = getPlayerPosition(pGameState);
			Position enemy = getNearestEnemy(playerPosition);
			if (enemy != null) {
				double distance = Math.abs(enemy.x - playerPosition.x)
						+ Math.abs(enemy.y - playerPosition.y);
				double enemySpeed = 0;//Agent.knowledgeBase.getSpeedOfEnemy(enemy);
				if (enemySpeed > maxSpeed) {
					maxSpeed = enemySpeed;
				}
				if (enemySpeed >= distance) {
					return 0;
				}
				if (distance > 0) {
					return 1 - (enemySpeed / distance);
				}
			}
			return 0;
		} catch (Exception e) {
			return 1;
		}
	}

	private Position getNearestEnemy(Position pPlayerPosition) {
		ArrayList<Position> enemies = null;//Agent.knowledgeBase.getEnemyPositions();
		Position nearestEnemy = null;
		Double minDistance = null;

		if (enemies != null && !enemies.isEmpty()) {
			for (Position enemy : enemies) {
				double distance = Math.abs(enemy.x - pPlayerPosition.x)
						+ Math.abs(enemy.y - pPlayerPosition.y);
				if (minDistance == null || minDistance > distance) {
					minDistance = distance;
					nearestEnemy = enemy;
				}
			}
			return nearestEnemy;
		}
		return null;
	}

	private Position getPlayerPosition(GameState gameState) {
		Vector2d avPos = gameState.getAvatarPosition();
		int blockSize = gameState.getBlockSize();
		return new Position((int) (avPos.x / blockSize), (int) (avPos.y / blockSize));
	}
}

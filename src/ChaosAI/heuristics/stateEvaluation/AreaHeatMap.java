package ChaosAI.heuristics.stateEvaluation;

import java.util.ArrayList;

import ChaosAI.heuristics.stateEvaluation.base.HeatMapBase;
import ChaosAI.utils.GameState;
import ChaosAI.utils.Position;
import ChaosAI.utils.Util;
import core.game.Observation;
import tools.Vector2d;

/**
 * Created by Adrian on 01.06.2016.
 */
public class AreaHeatMap extends HeatMapBase {

	protected final int					COOLDOWN_DELAY	= 100;

	protected final static int			BASE_PHEROMON	= 1;

	protected final int					POS_VALUE		= 25;

	protected final int					NEG_VALUE		= 1;

	private float						mMaxPheromon	= 1;

	private int							mDelayCounter	= 0;

	private int							divisions		= 4;

	private ArrayList<Observation>[][]	observationGrid;

	private int[][]						mHeatMap;

	public AreaHeatMap(GameState pGameState) {
		super(pGameState, BASE_PHEROMON);
		observationGrid = pGameState.getObservationGrid();
		mHeatMap = new int[divisions][divisions];
	}

	public void updateFields(GameState pGameState) {
		if (divisions != 0) {
			Position playerPos = getPlayerPosition(pGameState);
			addHeat(playerPos);
			if (mDelayCounter < COOLDOWN_DELAY) {
				mDelayCounter++;
			} else {
				reduceHeat();
			}
		}
	}

	@Override
	public double evaluate(GameState pGameState) {
		Position playerPos = getPlayerPosition(pGameState);
		float pheromonValue = getValue(playerPos);
		return 1 - Util.normalise(pheromonValue, 0, mMaxPheromon);
	}

	private float getValue(Position pPlayerPosition) {
		int xPosition = pPlayerPosition.x / (observationGrid.length / divisions);
		int yPosition = pPlayerPosition.y / (observationGrid[0].length / divisions);
		if(xPosition == divisions) {
			xPosition--;
		}
		if(yPosition == divisions) {
			yPosition--;
		}
		return mHeatMap[xPosition][yPosition];
	}

	protected Position getPlayerPosition(GameState pGameState) {
		Vector2d avatarPosition = pGameState.getAvatarPosition();
		int blockSize = pGameState.getBlockSize();
		// TODO add usefull Information here
		Position p = new Position((int) (avatarPosition.x / blockSize), (int) (avatarPosition.y / blockSize));
		return p;
	}

	private void addHeat(Position pPosition) {
		int xPosition = pPosition.x / (observationGrid.length / divisions);
		int yPosition = pPosition.y / (observationGrid[0].length / divisions);
		if(xPosition == divisions) {
			xPosition--;
		}
		if(yPosition == divisions) {
			yPosition--;
		}
		int pheromonValue = mHeatMap[xPosition][yPosition] + POS_VALUE;

		if (pheromonValue > mMaxPheromon) {
			mMaxPheromon = pheromonValue;
		}
		try {
			mHeatMap[xPosition][yPosition] = pheromonValue;
		} catch (IndexOutOfBoundsException e) { // if player has won the game or died
		}
	}

	private void reduceHeat() {
		for (int x = 0; x < divisions; x++) {
			for (int y = 0; y < divisions; y++) {
				if (mHeatMap[x][y] > 0) {
					mHeatMap[x][y] -= NEG_VALUE;
				}
			}
		}
	}
}

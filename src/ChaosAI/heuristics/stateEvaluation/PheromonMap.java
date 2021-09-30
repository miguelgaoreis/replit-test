package ChaosAI.heuristics.stateEvaluation;

import java.util.ArrayList;

import ChaosAI.heuristics.stateEvaluation.base.HeatMapBase;
import ChaosAI.utils.GameState;
import ChaosAI.utils.Position;
import ChaosAI.utils.Util;
import core.game.Observation;
import tools.Vector2d;

/**
 * Created by Adrian on 13.05.2016.
 */
public class PheromonMap extends HeatMapBase {

	protected final int			COOLDOWN_DELAY	= 100;

	protected final static int	BASE_PHEROMON	= 1;

	protected final int			POS_VALUE		= 10;

	protected final int			NEG_VALUE		= 1;

	protected final int			RANGE			= 2;

	private float				mMaxPheromon	= 1;

	private int					mDelayCounter	= 0;

	private float[][]			mPheromonMap;

	public PheromonMap(GameState pGameState) {
		super(pGameState, BASE_PHEROMON);
		ArrayList<Observation>[][] obs = pGameState.getObservationGrid();
		mPheromonMap = new float[obs.length][obs[0].length];
	}

	public void updateFields(GameState pGameState) {
		Position playerPos = getPlayerPosition(pGameState);
		addPheromon(playerPos);
		if (mDelayCounter < COOLDOWN_DELAY) {
			mDelayCounter++;
		} else {
			reducePheromon();
		}
	}

	@Override
	public double evaluate(GameState pGameState) {
		Position playerPos = getPlayerPosition(pGameState);
		float pheromonValue = getPheromonValue(playerPos);
		return 1 - Util.normalise(pheromonValue, 0, mMaxPheromon);
	}

	private float getPheromonValue(Position pPlayerPosition) {
		return mPheromonMap[pPlayerPosition.x][pPlayerPosition.y];
	}

	protected Position getPlayerPosition(GameState pGameState) {
		Vector2d avatarPosition = pGameState.getAvatarPosition();
		int blockSize = pGameState.getBlockSize();
		// TODO add usefull Information here
		Position p = new Position((int) (avatarPosition.x / blockSize), (int) (avatarPosition.y / blockSize));
		return p;
	}

	private void addPheromon(Position pPosition) {
		for (int x = -RANGE; x < RANGE; x++) {
			for (int y = -RANGE; y < RANGE; y++) {
				int range = Math.abs(x) + Math.abs(y);
				if (range < RANGE && range > 0) {
					try {
						float pheromonValue = mPheromonMap[pPosition.x + x][pPosition.y + y] + (POS_VALUE / range);
						if (pheromonValue > mMaxPheromon) {
							mMaxPheromon = pheromonValue;
						}
						mPheromonMap[pPosition.x + x][pPosition.y + y] = pheromonValue;
					} catch (IndexOutOfBoundsException e) { // pheromons out of board
						// Do nothing...
					}
				}
			}
		}
		try {
			mPheromonMap[pPosition.x][pPosition.y] += POS_VALUE;
		} catch (IndexOutOfBoundsException e) { // if player has won the game or died
		}
	}

	private void reducePheromon() {
		for (int x = 0; x < mPheromonMap.length; x++) {
			for (int y = 0; y < mPheromonMap[0].length; y++) {
				if (mPheromonMap[x][y] > 0) {
					mPheromonMap[x][y] /= NEG_VALUE;
				}
			}
		}
	}
}

package ChaosAI.heuristics.stateEvaluation;

import ChaosAI.heuristics.stateEvaluation.base.HeatMapBase;
import ChaosAI.utils.GameState;
import ChaosAI.utils.Position;
import ChaosAI.utils.Util;

/**
 * Created by Chris on 13.05.2016.
 */
public class MoveHistoryHeatMap extends HeatMapBase {

    public MoveHistoryHeatMap(GameState gameState) {
        super(gameState, 1);
    }


    @Override
    public double evaluate(GameState pGameState) {
        Position p = getPlayerPosition(pGameState);
        if(!insideField(p))
            return 0 + Util.normalise(Double.MIN_VALUE+1,Double.MIN_VALUE,Double.MAX_VALUE);
        int heatValue = getHeatValue(p);
        return 1 - Util.normalise(heatValue,0,maxKnownHeat+1);
    }

    @Override
    public void updateFields(GameState pGameState) {
//        if(delayCounter < COOLDOWN_DELAY)
//            delayCounter++;
//        else
            coolField(1);
        Position playerPos = getPlayerPosition(pGameState);
        heatPosition(playerPos, 10);
    }
}

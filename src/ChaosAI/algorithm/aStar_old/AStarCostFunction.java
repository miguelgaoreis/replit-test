package ChaosAI.algorithm.aStar_old;

import ChaosAI.Agent;
import ChaosAI.utils.CacheableGameState;
import ChaosAI.utils.CacheableNode;
import ChaosAI.utils.Position;
import core.game.Observation;
import core.game.StateObservation;
import ontology.Types;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Blindguard on 14.05.2016.
 */
public class AStarCostFunction {
    private static ArrayList<Types.ACTIONS> directions = new ArrayList<>(Arrays.asList(Types.ACTIONS.ACTION_DOWN, Types.ACTIONS.ACTION_LEFT, Types.ACTIONS.ACTION_RIGHT, Types.ACTIONS.ACTION_UP));

    public double evaluate(CacheableNode n, CacheableGameState last, CacheableGameState next) {
        /*StateObservation so = next.getGameState(n);
        ArrayList<Observation>[] movables = so.getMovablePositions();
        int counter = 0;

        if(movables != null) {
            for(ArrayList<Observation> obsList: movables) {
                for(Observation obs: obsList) {
                    counter++;
                    Position pos = new Position(obs.position, so.getBlockSize());

                    int wallCount = 4 - Agent.knowledgeBase.getActionsNotBlockedByWall(pos, directions).size();
                }
            }
        }*/

        return 0.1;
    }
}

package ChaosAI.heuristics.stateEvaluation;

import ChaosAI.heuristics.HeuristicBase;
import ChaosAI.utils.GameState;
import ChaosAI.utils.Position;
import core.game.Game;
import core.game.Observation;
import ChaosAI.utils.Util;

import java.util.ArrayList;

/**
 * Created by blindguard on 31/05/16.
 */
public class ClosestNPCHeuristic extends HeuristicBase {
    public int minValue = 1;
    public int maxValue = -1;

    public ClosestNPCHeuristic(GameState gs) {
        this.calcMax(gs);
    }

    public double evaluate(GameState gs) {
        double value = 1e-6;
        ArrayList<Observation>[] npcs = gs.getNPCPositions();
        Position avatar = new Position(gs.getAvatarPosition(), gs.getBlockSize());
        Observation closestNPC = null;
        int closestDistance = 0;

        if(npcs != null) {
            for(ArrayList<Observation> obsList: npcs) {
                for(Observation obs: obsList) {
                    Position p = new Position(obs.position, gs.getBlockSize());
                    int newDistance = Util.manhattanDistance(avatar, p);
                    if(newDistance < closestDistance) {
                        closestDistance = newDistance;
                        closestNPC = obs;
                    }
                }
            }
        }

        double d =  1 - Util.normalise(closestDistance, minValue, maxValue);
        if(d < 0)
            System.out.println("WAT");
        return d;
    }

    public void calcMax(GameState gs) {
        int width = gs.getWorldDimension().width;
        int height = gs.getWorldDimension().height;

        maxValue = Util.manhattanDistance(new Position(0, 0), new Position(width-1, height-1));
    }
}
